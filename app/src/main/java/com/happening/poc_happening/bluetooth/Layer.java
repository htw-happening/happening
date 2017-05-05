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
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.util.SparseArray;

import com.happening.poc_happening.MyApp;

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

    private static Layer instance = null;

    public static final String ADVERTISE_UUID = "11111111-0000-0000-0000-000ad7e9415f";
    public static final String SERVICE_UUID = "11111111-0000-0000-0000-000005e971cf";
    public static final String CHARACTERISTIC_UUID = "11111111-0000-0000-00c8-a9ac4e91541c";
    public static final String USERINFO_UUID = "11111111-0000-0000-0000-000005371970";

    private int userID = 0;
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

    private Timer scanTimer;

    // for analysing uptime
    public int counter;
    public long startTimestamp;

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
        this.connectedDevices = new ArrayList<>();
        this.counter = 0;
        this.startTimestamp = System.currentTimeMillis();
        startConnector();
        Log.i(TAG, "*********************** I am " + mBluetoothAdapter.getName() + " | " + generateUserID() + " ***********************");
    }


    private int generateUserID() {
        // TODO: Return existing ID from database
        return UUID.randomUUID().hashCode();
    }

    public ArrayList<Device> getConnectedDevices() {
        return connectedDevices;
    }

    public ArrayList<Device> getScannedDevices() {
        return scannedDevices;
    }

    public int getUserID() {
        return userID;
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
        AdvertiseSettings advertiseSettings = advertiseSettingsBuilder
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(true)
                .build();

        int userHash = getUserID();
        byte[] userId = intToByte(userHash);

        Log.d(TAG, "Start Adevertising with id: " + userHash);
        Log.d(TAG, "Start Adevertising with id as bytearray: " + Arrays.toString(userId));
        Log.d(TAG, "Start Adevertising with id as binary: " + toBinary(userId));

        ParcelUuid advertiseUuid = ParcelUuid.fromString(ADVERTISE_UUID);
        ParcelUuid userUuid = ParcelUuid.fromString(ADVERTISE_UUID);
        AdvertiseData.Builder advertiseDataBuilder = new AdvertiseData.Builder();
        AdvertiseData.Builder userDataBuilder = new AdvertiseData.Builder();

        AdvertiseData advertiseData = advertiseDataBuilder
                .addServiceUuid(advertiseUuid)
                .addServiceData(userUuid, userId)
                .build();

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
        UUID serviceUuid = UuidFactory.getServiceUuid(getUserID());
        Log.d(TAG, serviceUuid.toString());
        UUID characteristicUuid = UUID.fromString(CHARACTERISTIC_UUID);

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

        bluetoothGattServerCallback = new BluetoothGattServerCallback();
        bluetoothGattServer = mBluetoothManager.openGattServer(context, bluetoothGattServerCallback);
        bluetoothGattServer.addService(gattService);
        startWriter();

        if (d) Log.d(TAG, "Started Gattserver");
    }

    public void stopGattServer() {
        stopWriter();
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



        if (d) Log.d(TAG, "Started Scanner");
    }

    public void stopScan() {
        if (mBluetoothLeScanner != null) {
            mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
            mBluetoothLeScanner.stopScan(mScanCallback);
            if (d) Log.d(TAG, "Stopped Scanner");
        }


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

    public void scanOneSecond() {

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                stopScan();
            }
        };

        this.scanTimer = new Timer();
        scanTimer.schedule(timerTask, 1000);
        startScan();
    }

    public void setAutoConnect(boolean b) {
        if (b) startConnector();
        if (!b) stopConnector();
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

//        MyScanResult myScanResult = new MyScanResult(scanResult);
//        printScan(scanResult);

        int userId = 0;
        ScanRecord scanRecord = scanResult.getScanRecord();

//        Log.d(TAG, "Scanrecord raw " + Arrays.toString(scanRecord.getBytes()));
//        Log.d(TAG, "Scanrecord raw " + toBinary(scanRecord.getBytes()));
        if (scanRecord != null) {
            Map<ParcelUuid, byte[]> serviceData = scanRecord.getServiceData();
            if (serviceData == null) return;
            ParcelUuid test1 = ParcelUuid.fromString("00001111-0000-1000-8000-00805f9b34fb");
            if (scanRecord.getServiceData(test1) != null) {
                byte[] userInfo = scanRecord.getServiceData(test1);
                userId = bytesToInt(userInfo);
//                Log.d(TAG, "Scanrecord userInfo: " + Arrays.toString(userInfo));
//                Log.d(TAG, "Scanrecord userInfo: " + toBinary(userInfo));
//                Log.d(TAG, "Scanrecord userInfo: " + userId);
                if (userId == 0) return;
            }
        }

        Device scannedDevice = new Device(scanResult.getDevice(), userId);

        if (isMacAdressInScannedDevices(scannedDevice)){
            // Ignore - this is just a duplicate with the same mac - 1 sec Scan
//            if (d) Log.d(TAG, "addNewScan - do not add, its just a duplicate");
            return;
        }


        if (!isUserIdInScannedDevices(scannedDevice)){
            // this is a fresh new device, nice
            if (d) Log.d(TAG, "addNewScan - This is Fresh NEW Never Seen Device ("+scannedDevice.toString()+")");
            this.connector.addDevice(scannedDevice); //--> will automatically connect()
        }else{
            // this is a shadow device. We are already connected (or some other state) to this device
            // with an old mac address - lets try disconnect and fresh connect to the new one
            if (d) Log.d(TAG, "addNewScan - This is Shadow Device with a new MAC ("+scannedDevice.toString()+")");

            if (d) Log.d(TAG, "addNewScan - Disconnect old ones");
            List<Device> oldDevices = getShadowDuplicates(scannedDevice);
            for (Device oldDevice : oldDevices) {
                oldDevice.disconnect();
                oldDevice.changeState(Device.STATE.SHADOW);
            }
            if (d) Log.d(TAG, "addNewScan - add shadow device to sink ("+scannedDevice.toString()+")");
            this.connector.addDevice(scannedDevice);
        }

        // we added to the scanned devices list, cause we have to do stuff
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

    private void startWriter(){
//        if (writerTimer != null){
//            return;
//        }
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                //if (d) Log.d(TAG, "Writer Trigger");
//                BluetoothGattCharacteristic bluetoothGattCharacteristic = gattService.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID));
//                if (bluetoothGattCharacteristic == null) return;
//                bluetoothGattCharacteristic.setValue(String.valueOf(System.currentTimeMillis()));
//                if (d) Log.d(TAG, "Writer - Changed Value");
//            }
//        };
//        writerTimer = new Timer();
//        writerTimer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    private void stopWriter(){
//        if (writerTimer == null){
//            return;
//        }
//        writerTimer.cancel();
//        writerTimer = null;
    }

    public double calcUpTime(){
        if (counter == 0) return 0;
        long now = System.currentTimeMillis();
        int diff = (int) (now/1000 - startTimestamp/1000);
        if (diff == 0) return 0;
        return (counter/new Double(diff)*100);
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

    public byte[] fromBinary( String s )
    {
        int sLen = s.length();
        byte[] toReturn = new byte[(sLen + Byte.SIZE - 1) / Byte.SIZE];
        char c;
        for( int i = 0; i < sLen; i++ )
            if( (c = s.charAt(i)) == '1' )
                toReturn[i / Byte.SIZE] = (byte) (toReturn[i / Byte.SIZE] | (0x80 >>> (i % Byte.SIZE)));
            else if ( c != '0' )
                throw new IllegalArgumentException();
        return toReturn;
    }

    //endregion
}