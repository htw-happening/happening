package blue.happening.service.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import blue.happening.service.MainActivity;

public class Layer {

    private String TAG = getClass().getSimpleName();
    private boolean d = true;

    static final String SERVICE_UUID = "11111111-0000-0000-0000-000005e971cf";
    static final String RANDOM_READ_UUID = "00001111-0000-1000-8000-00805f9b34fb";

    @SuppressLint("StaticFieldLeak")
    private static Layer instance = null;

    private Context context = null;

    private BluetoothAdapter bluetoothAdapter = null;
    private ILayerCallback layerCallback;
    private PairingRequest pairingRequest;
    private IDeviceFinder deviceFinder;

    private List<Handler> handlers;
    private ArrayList<Device> scannedDevices;
    private Server acceptor = null;
    private AutoConnectSink connectSink = null;
    private String macAddress = "";
    private boolean autoConnect = true;

    public Context getContext() {
        return context;
    }

    public static Layer getInstance() {
        if (instance == null)
            instance = new Layer();
        return instance;
    }

    private Layer() {
        this.context = MainActivity.getContext();
        this.scannedDevices = new ArrayList<>();
        this.handlers = new ArrayList<>();
        BluetoothManager bluetoothManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = bluetoothManager.getAdapter();
        this.macAddress = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
        Log.i(TAG, "*********************** I am " + bluetoothAdapter.getName() + " | " + macAddress + " ***********************");

    }

    public void setAutoConnect(boolean value){
        this.autoConnect = value;
    }

    public void start(){
        // TODO: 06.06.17 check autoconnect bool
        this.deviceFinder = new LeDeviceFinder();
        this.deviceFinder.registerCallback(this);
        this.deviceFinder.start();
        this.pairingRequest = new PairingRequest();
        this.context.registerReceiver(pairingRequest, new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST));
        this.connectSink = new AutoConnectSink();
        this.connectSink.start();
        this.acceptor = new Server();
        this.acceptor.start();
        // TODO: 06.06.17 bl stack aufräumen
        notifyHandlers(1);

    }


    public void shutdown() {
        if (deviceFinder != null){
            this.deviceFinder.stop();
        }
        if (acceptor != null) {
            acceptor.cancel();
        }
        for (Device device : scannedDevices) {
            if (device != null && device.getState() == Device.STATE.CONNECTED) {
                device.connection.shutdown();
            }
        }
        context.unregisterReceiver(pairingRequest);
        connectSink.interrupt();
        handlers.clear();
        // TODO: 06.06.17 aufräumen
        notifyHandlers(1);

    }

    public void connectTo(Device device){
        connectSink.addDevice(device);
    }

    public void disconnectFrom(Device device){
        device.disconnect();
    }

    public void reset(){
        shutdown();
        start();
    }

    public String getMacAddress() {
        return macAddress;
    }

    public ArrayList<Device> getDevices() {
        return scannedDevices;
    }

    public void registerLayerCallback(ILayerCallback layerCallback) {
        this.layerCallback = layerCallback;
    }

    ILayerCallback getLayerCallback() {
        return layerCallback;
    }

    void notifyHandlers(int code) {
        for (Handler handler : handlers) {
            handler.obtainMessage(code).sendToTarget();
        }
    }

    public void addHandler(Handler handler) {
        if (!handlers.contains(handler)) {
            handlers.add(handler);
        }
    }

    public void removeHandler(Handler handler) {
        if (handlers.contains(handler)) {
            handlers.remove(handler);
        }
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public void enableBluetooth() {
        bluetoothAdapter.enable();
    }

    public void disableBluetooth() {
        bluetoothAdapter.disable();
    }

    public int getNumOfConnectedDevices() {
        int num = 0;
        for (Device device : scannedDevices) {
            if (device.getState() == Device.STATE.CONNECTED) {
                num++;
            }
        }
        return num;
    }

    void addNewScan(String macAddress) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
        Device scannedDevice = getDeviceByMac(device);
        if (!isMacAddressAlreadyInList(scannedDevice, connectSink.getSink())
                && scannedDevice.getState() != Device.STATE.CONNECTING
                && scannedDevice.getState() != Device.STATE.SCHEDULED
                && scannedDevice.getState() != Device.STATE.CONNECTED) {
            if (d) Log.d(TAG, "addNewScan - Yes added to sink (" + scannedDevice.toString() + ")");
            connectSink.addDevice(scannedDevice);
        }
        if (!isMacAddressAlreadyInList(scannedDevice, scannedDevices)) {
            if (d) Log.d(TAG, "addNewScan - Yes added to list (" + scannedDevice.toString() + ")");
            scannedDevices.add(scannedDevice);
        }
        notifyHandlers(1);
    }

    private boolean isMacAddressAlreadyInList(Device device, Collection<Device> collection) {
        for (Device aDevice : collection) {
            if (device.hasSameMacAddress(aDevice))
                return true;
        }
        return false;
    }

    private Device getDeviceByMac(BluetoothDevice device) {
        for (Device aDevice : scannedDevices) {
            if (device.getAddress().equals(aDevice.getAddress()))
                return aDevice;
        }
        return new Device(device);
    }

    void receivedData(byte[] data, Device device) {
        if (d) Log.d(TAG, "Received Data " + Arrays.toString(data) + " from " + device);
        for (Handler handler : handlers) {
            Message msg = handler.obtainMessage(666);
            Bundle bundle = new Bundle();
            bundle.putByteArray("data", data);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    void connectionLost(Device device) {
        device.changeState(Device.STATE.DISCONNECTED);
        if (layerCallback != null) {
            layerCallback.onDeviceRemoved(device);
        }
    }

    private class Server extends Thread {

        BluetoothServerSocket serverSocket = null;

        Server() {
        }

        public void run() {
            if (d) Log.d(TAG, "Server is running");
            setName("Server");
            BluetoothSocket socket;
            try {
                while (!interrupted()) {
                    serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("Happening", UUID.fromString(SERVICE_UUID));

                    if (d) Log.d(TAG, "About to wait, accepting for a client (Blocking Call)");
                    socket = serverSocket.accept();
                    if (socket != null) {
                        connectedToClient(socket, socket.getRemoteDevice());
                    }
                    serverSocket.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "accept() has been interrupted, cause: " + e.getMessage());
            }
            if (d) Log.i(TAG, "Server stopped");
        }

        void cancel() {
            interrupt();
            if (d) Log.d(TAG, "cancel()");
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "close of server failed", e);
                }
            }
        }
    }

    void connectedToServer(BluetoothSocket socket, Device device) {
        if (device.getState() == Device.STATE.CONNECTED) {
            return;
        }
        device.changeState(Device.STATE.CONNECTED);
        device.resetTrials();
        device.connection = new Connection(device, socket);
        if (layerCallback != null) {
            layerCallback.onDeviceAdded(device);
        }

    }

    private void connectedToClient(BluetoothSocket socket, BluetoothDevice bluetoothDevice) {
        Device device;
        if (isMacAddressAlreadyInList(new Device(bluetoothDevice), scannedDevices)) {
            device = getDeviceByMac(bluetoothDevice);
            device.resetTrials();
            if (device.getState() == Device.STATE.CONNECTED) {
                return;
            }

        } else {
            device = new Device(bluetoothDevice);
            scannedDevices.add(device);
        }
        device.changeState(Device.STATE.CONNECTED);
        device.connection = new Connection(device, socket);
        if (layerCallback != null) {
            layerCallback.onDeviceAdded(device);
        }

    }
}