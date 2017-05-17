package com.happening.poc_happening.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.happening.poc_happening.MyApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Layer {

    private String TAG = getClass().getSimpleName();
    private boolean d = true;

    public static final String ADVERTISE_UUID = "11111111-0000-0000-0000-000ad7e9415f";
    public static final String SERVICE_UUID = "11111111-0000-0000-0000-000005e971cf";
    public static final String CHARACTERISTIC_UUID = "11111111-0000-0000-00c8-a9ac4e91541c";
    public static final String USERINFO_UUID = "11111111-0000-0000-0000-000005371970";

    private static Layer instance = null;

    private int userID = 0;
    private Context context = null;
    private BluetoothManager bluetoothManager = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private ScannerCallback scannerCallback = new ScannerCallback();

    private List<Handler> handlers = new ArrayList<>();
    private ArrayList<Device> scannedDevices = new ArrayList<>();
    private Server acceptor = null;

    public static Layer getInstance() {
        if (instance == null)
            instance = new Layer();
        return instance;
    }

    private Layer() {
        context = MyApp.getAppContext();
        this.bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = bluetoothManager.getAdapter();
        this.userID = generateUserID();
        Log.i(TAG, "*********************** I am " + bluetoothAdapter.getName() + " | " + generateUserID() + " ***********************");
    }


    private int generateUserID() {
        // TODO: Return existing ID from database
        return UUID.randomUUID().hashCode();
    }

    public ArrayList<Device> getScannedDevices() {
        return scannedDevices;
    }

    public int getUserID() {
        return userID;
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

    public boolean isEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public void enableAdapter() {
        bluetoothAdapter.enable();
    }

    public void disableAdapter() {
        bluetoothAdapter.disable();
    }

    public void startScan() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        context.registerReceiver(scannerCallback, filter);
        bluetoothAdapter.startDiscovery();
        if (d) Log.d(TAG, "Started Scanner");
    }

    public void stopScan() {
        if (d) Log.d(TAG, "Stopped Scanner");
        bluetoothAdapter.cancelDiscovery();
        context.unregisterReceiver(scannerCallback);
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
        if (acceptor == null) {
            acceptor = new Server();
            acceptor.start();
        }
    }

    public void stopAcceptor() {
        if (acceptor != null) {
            acceptor.interrupt();
            acceptor.cancel();
            acceptor = null;
        }
    }

    public void addNewScan(BluetoothDevice device) {
        Device scannedDevice = new Device(device);
        if (isMacAddressInScannedDevices(scannedDevice)) {
            return;
        }

//        scannedDevice.fetchSdpList();

        this.scannedDevices.add(scannedDevice);
        if (d) Log.d(TAG, "addNewScan - Yes added it (" + scannedDevice.toString() + ")");

        notifyHandlers(1);
    }

    public void fetchedUUIDsFor(Device scannedDevice) {

        scannedDevice.changeState(Device.STATE.FETCHED);

        ArrayList<UUID> fetchedUuids = scannedDevice.getFetchedUuids();
        if (!fetchedUuids.contains(UUID.fromString(SERVICE_UUID))) {
            scannedDevice.changeState(Device.STATE.IGNORE);
        } else{
            // TODO: 16.05.17 Connectible

        }

        notifyHandlers(1);

    }

    public void fetchedUUIDsFailedFor(Device scannedDevice) {
        scannedDevice.changeState(Device.STATE.FETCHING_FAILED);
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
        // TODO: 16.05.17 handle clean shutdown
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
        // TODO: 16.05.17 handle io streams
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
        // TODO: 16.05.17 handle io streams
    }
}