package blue.happening.service.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import blue.happening.MyApplication;

public class Layer extends blue.happening.mesh.Layer {

    static final String SERVICE_UUID = "11111111-0000-0000-0000-000005e971cf";
    static final String RANDOM_READ_UUID = "00001111-0000-1000-8000-00805f9b34fb";
    @SuppressLint("StaticFieldLeak")
    private static Layer instance = null;
    private String TAG = getClass().getSimpleName();
    private boolean d = true;
    private Context context = null;

    private BluetoothAdapter bluetoothAdapter = null;
    private PairingRequest pairingRequest;
    private IDeviceFinder deviceFinder;

    private ArrayList<Device> scannedDevices;
    private Server acceptor = null;
    private ServerManager serverManager = null;
    private AutoConnectSink connectSink = null;
    private String macAddress = "";
    private boolean autoConnect = true;
    private BluetoothStateReceiver bluetoothStateReceiver;

    private Layer() {
        this.context = MyApplication.getAppContext();
        this.scannedDevices = new ArrayList<>();
        BluetoothManager bluetoothManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = bluetoothManager.getAdapter();
        this.macAddress = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
        bluetoothStateReceiver = new BluetoothStateReceiver();
        Log.i(TAG, "*********************** I am " + bluetoothAdapter.getName() + " | " + macAddress + " ***********************");

    }

    public static Layer getInstance() {
        if (instance == null)
            instance = new Layer();
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public void setAutoConnect(boolean value) {
        this.autoConnect = value;
    }

    public void start() {
        // TODO: 06.06.17 check autoconnect bool
        if (isAdvertisingSupported()) {
            Log.d(TAG, "start: isAdvertisingSupported TRUE");
            this.deviceFinder = new LeDeviceFinder();
        } else {
            Log.d(TAG, "start: isAdvertisingSupported FALSE");
            this.deviceFinder = new EdrDeviceFinder();
        }
        this.deviceFinder.registerCallback(this);
        this.deviceFinder.start();
        this.pairingRequest = new PairingRequest();
        this.context.registerReceiver(pairingRequest, new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST));
        this.connectSink = new AutoConnectSink();
        this.connectSink.start();
        this.acceptor = new Server();
        this.acceptor.start();
        bluetoothStateReceiver.start();
        serverManager = new ServerManager();
        serverManager.start();
        // TODO: 06.06.17 bl stack aufräumen

    }


    public void shutdown() {
        if (deviceFinder != null) {
            this.deviceFinder.stop();
        }
        if (acceptor != null) {
            acceptor.cancel();
        }
        serverManager.stop();
        for (Device device : scannedDevices) {
            if (device != null && device.getState() == Device.STATE.CONNECTED) {
                device.connection.shutdown();
            }
        }
        context.unregisterReceiver(pairingRequest);
        bluetoothStateReceiver.stop();
        connectSink.interrupt();
        // TODO: 06.06.17 aufräumen
        this.scannedDevices.clear();
    }

    public void reset() {
        Log.d(TAG, "reset");
        shutdown();
        start();
    }

    public String getMacAddress() {
        return macAddress;
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
    }

    private boolean isMacAddressAlreadyInList(Device device, Collection<Device> collection) {
        for (Device aDevice : collection) {
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

    void connectionLost(Device device) {
        device.changeState(Device.STATE.DISCONNECTED);
        if (getLayerCallback() != null) {
            getLayerCallback().onDeviceRemoved(device);
        }
    }

    void connectedToServer(BluetoothSocket socket, Device device) {
        if (device.getState() == Device.STATE.CONNECTED) {
            return;
        }
        device.changeState(Device.STATE.CONNECTED);
        device.resetTrials();
        device.connection = new Connection(device, socket);
        if (getLayerCallback() != null) {
            getLayerCallback().onDeviceAdded(device);
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
        if (getLayerCallback() != null) {
            getLayerCallback().onDeviceAdded(device);
        }
    }

    public boolean isAdvertisingSupported() {
        return bluetoothAdapter.isMultipleAdvertisementSupported() &&
                bluetoothAdapter.isOffloadedFilteringSupported() &&
                bluetoothAdapter.isOffloadedScanBatchingSupported();
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

    private class ServerManager {

        private Timer timer;
        private TimerTask timerTask;
        private boolean running;
        private static final int DELAY = 60000;

        ServerManager() {
            Log.d(TAG, "ServerManager: created");
        }

        void start() {
            Log.d(TAG, "start: started");
            running = true;
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "run: restarting ACCEPTOR");
                    if (acceptor != null) {
                        acceptor.cancel();
                    }
                    acceptor = new Server();
                    acceptor.start();

                }
            };
            timer.scheduleAtFixedRate(timerTask, DELAY, DELAY);
        }

        void stop() {
            Log.d(TAG, "stop: ");
            running = false;
            timerTask.cancel();
            timer.cancel();
        }
    }
}