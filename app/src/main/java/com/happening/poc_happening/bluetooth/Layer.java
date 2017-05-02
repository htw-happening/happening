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
import java.nio.ByteOrder;
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

//    private Connector connector = null;

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
        this.userID = getID();
        this.connectedDevices = new ArrayList<>();
        Log.i(TAG, "I am " + mBluetoothAdapter.getName() + " - " + this.userID);
    }


    private String getID() {
        // TODO: Return existing ID from database
        return UUID.randomUUID().toString();
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
        AdvertiseSettings advertiseSettings = advertiseSettingsBuilder
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(true)
                .build();

        int userHash = 12345678; //UUID.randomUUID().hashCode();
        byte[] userId = intToByte(userHash);

        Log.d(TAG, "Test 1.1: " + Arrays.toString(userId));
        Log.d(TAG, "Test 1.1: " + toBinary(userId));

        ParcelUuid advertiseUuid = ParcelUuid.fromString(ADVERTISE_UUID);
        ParcelUuid userUuid = ParcelUuid.fromString(ADVERTISE_UUID);
        AdvertiseData.Builder advertiseDataBuilder = new AdvertiseData.Builder();
        AdvertiseData.Builder userDataBuilder = new AdvertiseData.Builder();

        AdvertiseData advertiseData = advertiseDataBuilder
                .addServiceUuid(advertiseUuid)
                .addServiceData(userUuid, userId)
                .build();

//        AdvertiseData userData = userDataBuilder
//                .addServiceUuid(userUuid)
//                .addServiceData(userUuid, userId)
//                .build();

//        advertiseDataBuilder
////                .addServiceData(advertiseUuid, "TEST".getBytes())
//                .addServiceUuid(advertiseUuid)
//                .setIncludeTxPowerLevel(true);
//        AdvertiseData advertiseData = advertiseDataBuilder.build();
//
//
//        AdvertiseData.Builder userDataBuilder = new AdvertiseData.Builder();
//        ParcelUuid userUuid = ParcelUuid.fromString(USERINFO_UUID);
//        userDataBuilder
//                .addServiceUuid(userUuid)
//                .addServiceData(userUuid, userId)
//                .setIncludeTxPowerLevel(true);
//        AdvertiseData userData = advertiseDataBuilder.build();

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
//        UUID userinfoUuid = UUID.fromString(USERINFO_UUID);

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

//        BluetoothGattCharacteristic userinfo = new BluetoothGattCharacteristic(
//                userinfoUuid,
//                BluetoothGattCharacteristic.PROPERTY_BROADCAST |
//                        BluetoothGattCharacteristic.PROPERTY_READ |
//                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
//                BluetoothGattCharacteristic.PERMISSION_READ);
//        userinfo.setValue(userID);
//
//        gattService.addCharacteristic(userinfo);

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
//        mBluetoothLeScanner.startScan(mScanCallback);
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



        int userId = 0;
        ScanRecord scanRecord = scanResult.getScanRecord();





        Log.d(TAG, "Bla " + Arrays.toString(scanRecord.getBytes()));
        Log.d(TAG, "Bla " + toBinary(scanRecord.getBytes()));
        if (scanRecord != null) {
            Map<ParcelUuid, byte[]> serviceData = scanRecord.getServiceData();
            if (serviceData == null) return;
            ParcelUuid test1 = ParcelUuid.fromString("00001111-0000-1000-8000-00805f9b34fb");
            if (scanRecord.getServiceData(test1) != null) {
                byte[] userInfo = scanRecord.getServiceData(test1);
                userId = bytesToInt(userInfo);
                Log.d(TAG, "Test 1: " + Arrays.toString(userInfo));
                Log.d(TAG, "Test 1: " + toBinary(userInfo));
                Log.d(TAG, "Test 1: " + userId);
            }
        }

        Device scannedDevice = new Device(scanResult.getDevice(), Integer.toString(userId));
        for (Device device: scannedDevices){
            if (device.hasSameMacAddress(scannedDevice)){
                return;
            }
        }
        if (d) Log.d(TAG, "addNewScan to scanned Devices ("+scannedDevice.getBluetoothDevice().getAddress()+")");
        this.scannedDevices.add(scannedDevice);
        notifyHandlers(1);


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
//        this.connector.addDevice(scannedDevice);
    }

    public void stopConnector(){
//        if (connector != null){
//            connector.interrupt();
//            connector = null;
//        }
    }

    public void startConnector(){
//        if (connector == null){
//            connector = new Connector();
//            connector.start();
//        }
    }

    private void startWriter(){
        if (writerTimer != null){
            return;
        }
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //if (d) Log.d(TAG, "Writer Trigger");
                BluetoothGattCharacteristic bluetoothGattCharacteristic = gattService.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID));
                if (bluetoothGattCharacteristic == null) return;
                bluetoothGattCharacteristic.setValue(String.valueOf(System.currentTimeMillis()));
                if (d) Log.d(TAG, "Writer - Changed Value");
            }
        };
        writerTimer = new Timer();
        writerTimer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    private void stopWriter(){
        if (writerTimer == null){
            return;
        }
        writerTimer.cancel();
        writerTimer = null;
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

    public void parseAdvertisementPacket(final byte[] scanRecord) {

        byte[] advertisedData = Arrays.copyOf(scanRecord, scanRecord.length);
        ArrayList<UUID> uuids = new ArrayList<>();

        int offset = 0;
        while (offset < (advertisedData.length - 2)) {
            int len = advertisedData[offset++];
            if (len == 0)
                break;

            int type = advertisedData[offset++];
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (len > 1) {
                        int uuid16 = advertisedData[offset++] & 0xFF;
                        uuid16 |= (advertisedData[offset++] << 8);
                        len -= 2;
                        uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", uuid16)));
                    }
                    break;
                case 0x06:// Partial list of 128-bit UUIDs
                case 0x07:// Complete list of 128-bit UUIDs
                    // Loop through the advertised 128-bit UUID's.
                    while (len >= 16) {
                        try {
                            // Wrap the advertised bits and order them.
                            ByteBuffer buffer = ByteBuffer.wrap(advertisedData,
                                    offset++, 16).order(ByteOrder.LITTLE_ENDIAN);
                            long mostSignificantBit = buffer.getLong();
                            long leastSignificantBit = buffer.getLong();
                            uuids.add(new UUID(leastSignificantBit,
                                    mostSignificantBit));
                        } catch (IndexOutOfBoundsException e) {
                            // Defensive programming.
                            Log.e(TAG, e.toString());
                            continue;
                        } finally {
                            // Move the offset to read the next uuid.
                            offset += 15;
                            len -= 16;
                        }
                    }
                    break;
                case 0xFF:  // Manufacturer Specific Data
                    Log.d(TAG, "Manufacturer Specific Data size:" + len +" bytes" );
//                    while (len > 1) {
//                        if(i < 32) {
//                            MfgData[i++] = advertisedData[offset++];
//                        }
//                        len -= 1;
//                    }
//                    Log.d(TAG, "Manufacturer Specific Data saved." + MfgData.toString());
                    break;
                default:
                    offset += (len - 1);
                    break;
            }
        }
    }

    //endregion
}