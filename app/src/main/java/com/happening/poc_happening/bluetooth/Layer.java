package com.happening.poc_happening.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import com.happening.poc_happening.MyApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.UUID;

public class Layer {

    private String TAG = getClass().getSimpleName();
    private boolean d = true;

    private static Layer instance = null;

    public static final String ADVERTISE_UUID = "11111111-0000-0000-0000-000ad7e9415e";
    public static final String SERVICE_UUID = "11111111-0000-0000-0000-000005e971ce";
    public static final String CHARACTERISTIC_UUID = "11111111-0000-0000-00c8-a9ac4e91541c";
    public static final String USERINFO_UUID = "11111111-0000-0000-0000-000005371970";

    private String userID = null;
    private Context context = null;

    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothGattServer bluetoothGattServer = null;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;
    private BluetoothGattService gattService = null;

    private BluetoothLeScanner mBluetoothLeScanner = null;

    private BluetoothGattServerCallback bluetoothGattServerCallback = null;
    private ScanCallback mScanCallback = new ScanCallback();
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback();

    private List<Handler> handlers = new ArrayList<>();
    private ArrayList<Device> scannedDevices = new ArrayList<>();
    private ArrayList<Device> connectedDevices = new ArrayList<>();

    private Connector connector = null;

    private Timer readerTimer;
    private Timer writerTimer;

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
        this.userID = getID().toString();
        this.connectedDevices = new ArrayList<>();
        Log.i(TAG, "I am " + mBluetoothAdapter.getName());
    }


    private UUID getID() {
        // TODO: Return existing ID from database
        return UUID.randomUUID();
    }

    public ArrayList<Device> getConnectedDevices() {
        return connectedDevices;
    }

    public ArrayList<Device> getScannedDevices() {
        return scannedDevices;
    }

    public BluetoothGattServer getBluetoothGattServer() {
        return bluetoothGattServer;
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

    /*
    public void disconnectDevice(DeviceModel deviceModel) {
        if (deviceModel.isConnected()) {
            deviceModel.setTargetState(BluetoothProfile.STATE_DISCONNECTED);
            if (Objects.equals(deviceModel.getType(), "client")) {
                bluetoothGattServer.cancelConnection(deviceModel.getBluetoothDevice());
            } else if (Objects.equals(deviceModel.getType(), "server")) {
                deviceModel.getBluetoothGatt().disconnect();
            }
            Log.i("GATT", "Disconnecting " + deviceModel.getAddress());
        } else {
            Log.i("GATT", "Cannot disconnect state " + deviceModel.getCurrentState() + " gatt " + deviceModel.getBluetoothGatt());
        }
    }
    */

    public boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public boolean isAdvertisingSupported() {
        return mBluetoothAdapter.isMultipleAdvertisementSupported() &&
                mBluetoothAdapter.isOffloadedFilteringSupported() &&
                mBluetoothAdapter.isOffloadedScanBatchingSupported();
    }

    //region Operations

    public void enableAdapter() {
        mBluetoothAdapter.enable();
    }

    public void disableAdapter() {
        mBluetoothAdapter.disable();
    }

    public void startAdvertising() {
        if (d) Log.d(TAG, "Starting Advertiser");
        AdvertiseSettings.Builder advertiseSettingsBuilder = new AdvertiseSettings.Builder();
        advertiseSettingsBuilder
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(true);
        AdvertiseSettings advertiseSettings = advertiseSettingsBuilder.build();

        AdvertiseData.Builder advertiseDataBuilder = new AdvertiseData.Builder();
        ParcelUuid advertiseUuid = ParcelUuid.fromString(ADVERTISE_UUID);
        advertiseDataBuilder
                .addServiceUuid(advertiseUuid)
                .setIncludeTxPowerLevel(true);
        AdvertiseData advertiseData = advertiseDataBuilder.build();

        mBluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, mAdvertiseCallback);

        if (d) Log.d(TAG, "Started Advertising");
    }

    public void stopAdvertising() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
            if (d) Log.d(TAG, "Stopped Advertising");
        }
    }

    public void createGattServer() {
        if (d) Log.d(TAG, "Starting GattServer");
        UUID serviceUuid = UUID.fromString(SERVICE_UUID);
        UUID characteristicUuid = UUID.fromString(CHARACTERISTIC_UUID);
        UUID userinfoUuid = UUID.fromString(USERINFO_UUID);

        gattService = new BluetoothGattService(
                serviceUuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                characteristicUuid,
                BluetoothGattCharacteristic.PROPERTY_BROADCAST |
                        BluetoothGattCharacteristic.PROPERTY_WRITE |
                        BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE |
                        BluetoothGattCharacteristic.PROPERTY_READ |
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ |
                        BluetoothGattCharacteristic.PERMISSION_WRITE);
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        characteristic.setValue("n/a".getBytes());

        gattService.addCharacteristic(characteristic);

        BluetoothGattCharacteristic userinfo = new BluetoothGattCharacteristic(
                userinfoUuid,
                BluetoothGattCharacteristic.PROPERTY_BROADCAST |
                        BluetoothGattCharacteristic.PROPERTY_READ |
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);
        userinfo.setValue(userID);

        gattService.addCharacteristic(userinfo);

        bluetoothGattServerCallback = new BluetoothGattServerCallback();

        bluetoothGattServer = mBluetoothManager.openGattServer(context, bluetoothGattServerCallback);

        bluetoothGattServer.addService(gattService);
        //startWriter();TODO
        if (d) Log.d(TAG, "Started Gattserver");
    }

    public void stopGattServer() {
        //stopWriter();TODO
        if (bluetoothGattServer != null) {
            for (BluetoothDevice bluetoothDevice: bluetoothGattServer.getConnectedDevices() ) {
                bluetoothGattServer.cancelConnection(bluetoothDevice);
            }

            bluetoothGattServer.clearServices();
            bluetoothGattServer.close();
            if (d) Log.d(TAG, "Stopped Gattserver");
        }
    }



    public void startScan() {
        if (d) Log.d(TAG, "Starting Scanner");
        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scanSettingsBuilder
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
        } else {
            scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        }
        ScanSettings scanSettings = scanSettingsBuilder.build();

        ParcelUuid advertiseUuid = ParcelUuid.fromString(ADVERTISE_UUID);
        ScanFilter.Builder scanFilterBuilder = new ScanFilter.Builder();
        scanFilterBuilder.setServiceUuid(advertiseUuid);
        ScanFilter scanFilter = scanFilterBuilder.build();
        List<ScanFilter> scanFilters = new ArrayList<>();
        scanFilters.add(scanFilter);

        mBluetoothLeScanner.stopScan(mScanCallback);
        mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
        mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);

        startConnector();

        if (d) Log.d(TAG, "Started Scanner");
    }

    public void stopScan() {
        if (mBluetoothLeScanner != null) {
            mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
            mBluetoothLeScanner.stopScan(mScanCallback);
            if (d) Log.d(TAG, "Stopped Scanner");
        }

        stopConnector();
    }

/*
    public void broadcastMessage(String message) {
        Log.i("BROADCAST", "broadcast message" + message);
        logger.info("Broadcast Message: " + message);

        synchronized (devicePool.getConnectedDevices()) {
            for (DeviceModel deviceModel : devicePool.getConnectedDevices()) {
                try {

                    Log.i("BROADCAST", "Device " + deviceModel.getAddress());
                    BluetoothGatt bluetoothGatt = deviceModel.getBluetoothGatt();
                    if (Objects.equals(deviceModel.getType(), "client")) continue;
                    BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(SERVICE_UUID));
                    BluetoothGattCharacteristic characteristic = bluetoothGattService.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID));
                    characteristic.setValue(message.getBytes());
                    bluetoothGatt.writeCharacteristic(characteristic);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), e.toString());
                }
            }
        }
        Log.i("BROADCAST", "Done");
    }

    //endregion

    */

    //region Callbacks


    public class ScanCallback extends android.bluetooth.le.ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //if (d) Log.d(TAG, "ScanCallback - onScanResult ("+result.getDevice().getAddress()+" type: "+callbackType+")");
            addNewScan(result);
        }
    }

    // verifying new devices through MAC Address (not necessary NEW - see userInfoUUID) // lack of changing MACs
    // changing MAC every 15 minutes (exactly 15 mins!!)
    private void addNewScan(ScanResult scanResult){
        Device scannedDevice = new Device(scanResult.getDevice());
        for (Device device: scannedDevices){
            if (device.hasSameMacAddress(scannedDevice)){
                return;
            }
        }
        if (d) Log.d(TAG, "addNewScan to scanned Devices ("+scannedDevice.getBluetoothDevice().getAddress()+")");
        this.scannedDevices.add(scannedDevice);
        this.connector.addDevice(scannedDevice);
    }

    public void stopConnector(){
        if (connector != null){
            connector.interrupt();
            connector = null;
        }
    }

    public void startConnector(){
        if (connector == null){
            connector = new Connector();
            connector.start();
        }
    }

//    private void startWriter(){
//        if (writerTimer != null){
//            return;
//        }
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                if (d) Log.d(TAG, "Writer Trigger");
//                BluetoothGattCharacteristic bluetoothGattCharacteristic = gattService.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID));
//                if (bluetoothGattCharacteristic == null) return;
//                bluetoothGattCharacteristic.setValue(String.valueOf(System.currentTimeMillis()));
//                if (d) Log.d(TAG, "Writer - Changed Value");
//            }
//        };
//        writerTimer = new Timer();
//        writerTimer.scheduleAtFixedRate(timerTask, 1000, 1000);
//    }
//
//    private void stopWriter(){
//        if (writerTimer == null){
//            return;
//        }
//        writerTimer.cancel();
//        writerTimer = null;
//    }
//
//    private void startReader(){
//        if (readerTimer != null) return;
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                if (d) Log.d(TAG, "Reader Trigger");
//                if (bluetoothGatt == null) return;
//                if (bluetoothGattCharacteristic == null) return;
//                bluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);
//
//            }
//        };
//        readerTimer = new Timer();
//        readerTimer.scheduleAtFixedRate(timerTask, 1000, 1000);
//    }
//
//    private void stopReader(){
//        if (readerTimer == null){
//            return;
//        }
//        readerTimer.cancel();
//        readerTimer = null;
//    }

    //endregion
}