package blue.happening.service.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import blue.happening.service.MainActivity;

public class Layer {

    private String TAG = getClass().getSimpleName();
    private boolean d = true;

    public static final String ADVERTISE_UUID = "11111111-0000-0000-0000-000ad7e9415f";
    public static final String SERVICE_UUID = "11111111-0000-0000-0000-000005e971cf";
    public static final String CHARACTERISTIC_UUID = "11111111-0000-0000-00c8-a9ac4e91541c";
    public static final String USERINFO_UUID = "11111111-0000-0000-0000-000005371970";
    public static final String RANDOM_READ_UUID = "00001111-0000-1000-8000-00805f9b34fb";
    private final ScanTrigger scanTrigger;

    private static Layer instance = null;

    private Context context = null;
    private BluetoothManager bluetoothManager = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private ILayerCallback layerCallback;

    private List<Handler> handlers = new ArrayList<>();
    private ArrayList<Device> scannedDevices = new ArrayList<>();
    private Server acceptor = null;
    private AutoConnectSink connectSink = null;
    private String macAddress = "";

    public Context getContext() {
        return context;
    }

    public static Layer getInstance() {
        if (instance == null)
            instance = new Layer();
        return instance;
    }

    private Layer() {
        context = MainActivity.getContext();
        this.scanTrigger = new ScanTrigger();
        this.bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = bluetoothManager.getAdapter();
        this.connectSink = new AutoConnectSink();
        this.connectSink.start();
        macAddress = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
        Log.i(TAG, "*********************** I am " + bluetoothAdapter.getName() + " | " + macAddress + " ***********************");
    }

    public String getMacAddress() {
        return macAddress;
    }

    public ArrayList<Device> getScannedDevices() {
        return scannedDevices;
    }

    public ILayerCallback getLayerCallback() {
        return layerCallback;
    }

    public void notifyHandlers(int code) {
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

    public void registerLayerCallback(ILayerCallback layerCallback){
        this.layerCallback = layerCallback;
    }

    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public void enableAdapter() {
        bluetoothAdapter.enable();
    }

    public void disableAdapter() {
        bluetoothAdapter.disable();
    }

    public void startScanTrigger() {
        scanTrigger.startLeScan();
    }

    public void stopScanTrigger() {
        scanTrigger.stopLeScan();
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

    public void createAcceptor() {
        scanTrigger.startAdvertising();
        if (acceptor == null) {
            acceptor = new Server();
            acceptor.start();
        }
    }

    public void stopAcceptor() {
        scanTrigger.stopAdvertising();
        if (acceptor != null) {
            acceptor.interrupt();
            acceptor.cancel();
            acceptor = null;
        }
    }

    public void addNewScan(String macAddress) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
        Device scannedDevice = new Device(device);
        if (isMacAddressInScannedDevices(scannedDevice)) {
            return;
        }
        this.scannedDevices.add(scannedDevice);
        this.connectSink.addDevice(scannedDevice);
        if (d) Log.d(TAG, "addNewScan - Yes added it (" + scannedDevice.toString() + ")");
        notifyHandlers(1);
//        scannedDevice.delayedConnectDevice();
    }

    private boolean isMacAddressInScannedDevices(Device device) {
        for (Device aDevice : scannedDevices) {
            if (device.hasSameMacAddress(aDevice))
                return true;
        }
        return false;
    }

    public Device getDeviceByMac(BluetoothDevice device) {
        for (Device aDevice : scannedDevices) {
            if (device.getAddress().equals(aDevice.getAddress()))
                return aDevice;
        }
        return new Device(device);
    }

    public void shutdown() {
        for (Device device : scannedDevices) {
            if (device.getState() == Device.STATE.CONNECTED){
                device.connection.shutdown();
            }
        }
        stopAcceptor();
        stopScanTrigger();
        connectSink.interrupt();

    }

    public void receivedData(byte[] data, Device device) {
        if (d) Log.d(TAG, "Received Data " + Arrays.toString(data) + " from "+ device);
        for (Handler handler : handlers) {
            Message msg = handler.obtainMessage(666);
            Bundle bundle = new Bundle();
            bundle.putByteArray("data", data);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    public void connectionLost(Device device) {
        device.changeState(Device.STATE.DISCONNECTED);
        if (layerCallback != null){
            layerCallback.onDeviceRemoved(device);
        }
    }

    private class Server extends Thread {

        BluetoothServerSocket serverSocket = null;

        public Server() {
        }

        public void run() {
            if (d) Log.d(TAG, "Server is running");
            setName("Server");
            BluetoothSocket socket = null;
            try {
                while (!interrupted()) {
                    serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("Happening", UUID.fromString(SERVICE_UUID));

                    if (d) Log.d(TAG, "About to wait, accepting for a client");
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

        public void cancel() {
            if (d) Log.d(TAG, "stop " + this);
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "close of server failed", e);
                }
            }
        }
    }

    public void connectedToServer(BluetoothSocket socket, Device device) {
        if (device.getState() == Device.STATE.CONNECTED){
            // TODO: 16.05.17 do we have to close the connection manually | will this cause side effects?
            return;
        }
        device.changeState(Device.STATE.CONNECTED);
        device.connection = new Connection(device, socket);
        if (layerCallback != null){
            layerCallback.onDeviceAdded(device);
        }

    }

    public void connectedToClient(BluetoothSocket socket, BluetoothDevice bluetoothDevice) {
        Device device = null;
        //checking if BluetoothDevice is in scannedDevices
        if (isMacAddressInScannedDevices(new Device(bluetoothDevice))){
            // get it and use it
            device = getDeviceByMac(bluetoothDevice);

            //checking if an Connection already exists
            if (device.getState() == Device.STATE.CONNECTED){
                // TODO: 16.05.17 do we have to close the connection manually | will this cause side effects?
                return;
            }

        } else {
            // create a new one
            device = new Device(bluetoothDevice);
            scannedDevices.add(device);
        }

        device.changeState(Device.STATE.CONNECTED);
        device.connection = new Connection(device, socket);
        if (layerCallback!=null) {
            layerCallback.onDeviceAdded(device);
        }

    }
}