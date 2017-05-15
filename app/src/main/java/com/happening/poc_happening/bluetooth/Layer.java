package com.happening.poc_happening.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.util.SparseArray;

import com.happening.poc_happening.MyApp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;
    private BluetoothGattService gattService = null;
    private BluetoothLeScanner mBluetoothLeScanner = null;
    private ScannerCallback scannerCallback = new ScannerCallback();
    private List<Handler> handlers = new ArrayList<>();
    private ArrayList<Device> scannedDevices = new ArrayList<>();

    private Acceptor acceptor = null;


    public static Layer getInstance() {
        if (instance == null)
            instance = new Layer();
        return instance;
    }

    private Layer() {
        context = MyApp.getAppContext();
        this.mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = mBluetoothManager.getAdapter();
        this.mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        this.mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        this.userID = generateUserID();
        Log.i(TAG, "*********************** I am " + mBluetoothAdapter.getName() + " | " + generateUserID() + " ***********************");
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
        return mBluetoothAdapter.isEnabled();
    }

    public boolean isAdvertisingSupported() {
        return mBluetoothAdapter.isMultipleAdvertisementSupported() &&
                mBluetoothAdapter.isOffloadedFilteringSupported() &&
                mBluetoothAdapter.isOffloadedScanBatchingSupported();
    }

    public void enableAdapter() {
        mBluetoothAdapter.enable();
    }

    public void disableAdapter() {
        mBluetoothAdapter.disable();
    }

    public void startScan() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        context.registerReceiver(scannerCallback, filter);
        mBluetoothAdapter.startDiscovery();
        if (d) Log.d(TAG, "Started Scanner");
    }

    public void stopScan() {
        if (d) Log.d(TAG, "Stopped Scanner");
        mBluetoothAdapter.cancelDiscovery();
        context.unregisterReceiver(scannerCallback);
    }

    public int getNumOfConnectedDevices() {
        int num = 0;
        for (Device device : scannedDevices) {
            if (device.getState() == Device.STATE.CONNECTED){
                num++;
            }
        }
        return num;
    }

    public void createAcceptor() {
        if (acceptor == null){
            acceptor = new Acceptor();
            acceptor.start();
        }
    }

    public void stopAcceptor() {
        if (acceptor != null){
            acceptor.interrupt();
            acceptor.cancel();
            acceptor = null;
        }
    }

    public void addNewScan(BluetoothDevice device){
        Device scannedDevice = new Device(device, 0);
        if (isMacAdressInScannedDevices(scannedDevice)){
            return;
        }
        this.scannedDevices.add(scannedDevice);
        if (d) Log.d(TAG, "addNewScan - Yes added it ("+scannedDevice.toString()+")");

        notifyHandlers(1);
    }

    private boolean isMacAdressInScannedDevices (Device device) {
        for (Device aDevice: scannedDevices){
            if (device.hasSameMacAddress(aDevice))
                return true;
        }
        return false;
    }

    private boolean isUserIdInScannedDevices (Device device) {
        for (Device aDevice: scannedDevices){
            if (device.hasSameUserId(aDevice))
                return true;
        }
        return false;
    }

    private List<Device> getShadowDuplicates(Device device){
        List<Device> devices = new ArrayList<>();
        for (Device aDevice: scannedDevices) {
            if (aDevice.hasSameUserId(device) && !(aDevice.hasSameMacAddress(device))){
                devices.add(aDevice);
            }
        }
        return devices;
    }

    private void printScan(ScanResult scanResult) {
        if (d) Log.d("ScanResult", "scanResult.getScanRecord().getDeviceName() " + scanResult.getScanRecord().getDeviceName());
        if (d) Log.d("ScanResult", "scanResult.getScanRecord().getAdvertiseFlags() " + scanResult.getScanRecord().getAdvertiseFlags());
        Map<ParcelUuid, byte[]> serviceData = scanResult.getScanRecord().getServiceData();
        if (d) Log.d("ScanResult", "scanResult.getScanRecord().getServiceData() SIZE: " +serviceData.size());
        for (Map.Entry<ParcelUuid, byte[]> entry : serviceData.entrySet()) {
            ParcelUuid key = entry.getKey();
            byte[] value = entry.getValue();
            if (d) Log.d("ScanResult", "scanResult.getScanRecord().getServiceData() " + key.getUuid().toString() + " " + value);
        }
        List<ParcelUuid> parcelUuids = scanResult.getScanRecord().getServiceUuids();
        if (parcelUuids == null) return;
        if (d) Log.d("ScanResult", "scanResult.getScanRecord().getServiceUuids() SIZE: " +parcelUuids.size());
        for (ParcelUuid parcelUuid: parcelUuids){
            if (d) Log.d("ScanResult", "scanResult.getScanRecord().getServiceUuids() " +parcelUuid.getUuid().toString());
        }
        SparseArray<byte[]> sparseArray = scanResult.getScanRecord().getManufacturerSpecificData();
        if (d) Log.d("ScanResult", "scanResult.getScanRecord().getManufacturerSpecificData() SIZE: " +sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++){
            if (d) Log.d("ScanResult", "scanResult.getScanRecord().getManufacturerSpecificData() " + sparseArray.get(i));
        }
    }

    public static int bytesToInt (byte[] bytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return byteBuffer.getInt();
    }

    public static byte[] intToByte (int number){
        return ByteBuffer.allocate(4).putInt(number).array();
    }

    public String toBinary( byte[] bytes )
    {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }


    private class Acceptor extends Thread {

        BluetoothServerSocket serverSocket = null;

        public Acceptor() {
        }

        public void run() {
            if(d) Log.d(TAG, "Acceptor is running");
            setName("Acceptor");
            BluetoothSocket socket = null;
            try {
                while(!interrupted()){
                    serverSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("Happening", UUID.fromString(SERVICE_UUID) );

                    if(d) Log.d(TAG,"About to wait, accepting for a client");
                    socket = serverSocket.accept();
                    if (socket != null) {
                        connected(socket, new Device(socket.getRemoteDevice(), 0)); //TODO userID
                    }
                    serverSocket.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "accept() has been interrupted, cause: " + e.getMessage());
            }
            if(d) Log.i(TAG, "Acceptor stopped");
        }
        
        public void cancel() {
            if(d) Log.d(TAG, "stop " + this);
            if (serverSocket!=null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "close of server failed", e);
                }
            }
        }
    }

    /**
     * Called form Acceptor and Connector
     * @param socket
     */
    public void connected(BluetoothSocket socket, Device device) {
        device.changeState(Device.STATE.CONNECTED);
    }
}