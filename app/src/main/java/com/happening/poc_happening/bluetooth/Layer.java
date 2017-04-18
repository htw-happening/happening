package com.happening.poc_happening.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;

import com.happening.poc_happening.MyApp;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Layer {

    private String TAG = getClass().getSimpleName();
    private boolean d = true;

    private static Layer instance = null;

    public static final String ADVERTISE_UUID = "11111111-0000-0000-0000-000ad7e9415e";
    public static final String SERVICE_UUID = "11111111-0000-0000-0000-000005e971ce";
    public static final String CHARACTERISTIC_UUID = "11111111-0000-0000-00c8-a9ac4e91541c";
    public static final String USERINFO_UUID = "11111111-0000-0000-0000-000005371970";

    public static final int DEFAULT_MTU_BYTES = 128;

    private Context context = null;

    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothGattServer mBluetoothGattServer = null;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;
    private BluetoothGattService gattService = null;

    private BluetoothGatt bluetoothGatt = null;

    private BluetoothLeScanner mBluetoothLeScanner = null;

    private BluetoothGattServerCallback bluetoothGattServerCallback = null;
    private ScanCallback mScanCallback = new ScanCallback();
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback();

    private List<Handler> handlers = new ArrayList<>();
    private ArrayList<ScanResult> scanResults = new ArrayList<>();

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
        Log.i(TAG, "I am " + mBluetoothAdapter.getName());
    }



/*
    public void disconnectDevice(DeviceModel deviceModel) {
        if (deviceModel.isConnected()) {
            deviceModel.setTargetState(BluetoothProfile.STATE_DISCONNECTED);
            if (Objects.equals(deviceModel.getType(), "client")) {
                mBluetoothGattServer.cancelConnection(deviceModel.getBluetoothDevice());
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
        userinfo.setValue((mBluetoothAdapter.getName() != null ? mBluetoothAdapter.getName() : "n/a").getBytes());

        gattService.addCharacteristic(userinfo);

        bluetoothGattServerCallback = new BluetoothGattServerCallback();

        mBluetoothGattServer = mBluetoothManager.openGattServer(context, bluetoothGattServerCallback);

        mBluetoothGattServer.addService(gattService);
        startWriter();
        if (d) Log.d(TAG, "Started Gattserver");
    }

    public void stopGattServer() {
        stopWriter();
        if (mBluetoothGattServer != null) {
            for (BluetoothDevice bluetoothDevice: mBluetoothGattServer.getConnectedDevices() ) {
                mBluetoothGattServer.cancelConnection(bluetoothDevice);
            }

            mBluetoothGattServer.clearServices();
            mBluetoothGattServer.close();
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

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    //endregion

    */

    //region Callbacks


    public class AdvertiseCallback extends android.bluetooth.le.AdvertiseCallback {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            if (d) Log.d(TAG, "AdvertiseCallback - onStartSuccess");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            if (d) Log.d(TAG, "AdvertiseCallback - onStartFailure (error: " + errorCode+")");
        }
    }

    public class BluetoothGattServerCallback extends android.bluetooth.BluetoothGattServerCallback {

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            if (d) Log.d(TAG, "BluetoothGattServerCallback - onCharacteristicReadRequest (read: " + new String(characteristic.getValue()) +")");
            if (mBluetoothGattServer != null)
                mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getStringValue(0).getBytes());
        }

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if (d) Log.d(TAG, "BluetoothGattServerCallback - onConnectionStateChange (STATE_CONNECTED)");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (d) Log.d(TAG, "BluetoothGattServerCallback - onConnectionStateChange (STATE_DISCONNECTED)");

            } else {
                if (d) Log.d(TAG, "BluetoothGattServerCallback - onConnectionStateChange (status: " + status + "; newStatus: " + newState + ")");
            }

        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            if (d) Log.d(TAG, "BluetoothGattServerCallback - onServiceAdded (status " + status + ")");
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            String message = new String(value);
            if (d) Log.d(TAG, "BluetoothGattServerCallback - onCharacteristicWriteRequest (preparedWrite " + preparedWrite + "; message " + message + ")");

        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            if (d) Log.d(TAG, "BluetoothGattServerCallback - onNotificationSent (status " + status + ")");
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            if (d) Log.d(TAG, "BluetoothGattServerCallback - onMtuChanged (mtu " + mtu + ")");

        }
    }

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
        for (ScanResult aScanResult: scanResults) {
            if (aScanResult.getDevice().getAddress().equals(scanResult.getDevice().getAddress())){
                return;
            }
        }
        scanResults.add(scanResult);
        if (d) Log.d(TAG, "ScanCallback - addNewScan to scanResults ("+scanResult.getDevice().getAddress()+")");
        connectDevice(scanResult.getDevice());

    }

    public void connectDevice(BluetoothDevice bluetoothDevice) {
        if (d) Log.d(TAG, "Connecting to Device (" + bluetoothDevice.getAddress() + ")");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothGatt = bluetoothDevice.connectGatt(context, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            bluetoothGatt = bluetoothDevice.connectGatt(context, false, mGattCallback);
        }
    }

    public void delayedConnectDevice(final BluetoothDevice bluetoothDevice) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                connectDevice(bluetoothDevice);
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 2000);
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            BluetoothDevice bluetoothDevice = gatt.getDevice();
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    if (d)
                        Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange (STATE_CONNECTED)");
                    boolean mtuSuccess = gatt.requestMtu(DEFAULT_MTU_BYTES);
                    if (d)
                        Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange - connected and requesting mtu");
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    if (d)
                        Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange (STATE_DISCONNECTED)");
                    gatt.close();
                    if (status == 133) {
                        // do not retry connecting - seems to be an old mac address
                        Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange (GATT_FAILURE) --> Do not reconnect!!");

                    }else{
                        delayedConnectDevice(bluetoothDevice);
                    }
                    break;
                default:
                    if (d)
                        Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange (other state " + status + ")");
                    break;
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            if (d) Log.d(TAG, "BluetoothGattCallback - onMtuChanged (mtu " + mtu + ")");
            boolean discovering = gatt.discoverServices();
            if (d)
                Log.d(TAG, "BluetoothGattCallback - onMtuChanged - start discovering services (" + discovering + ")");

        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (d) Log.d(TAG, "BluetoothGattCallback - onReadRemoteRssi (rssi " + rssi + ")");
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            switch (status) {
                case BluetoothGatt.GATT_SUCCESS:
                    if (d)
                        Log.d(TAG, "BluetoothGattCallback - onServicesDiscovered (GATT_SUCCESS)");

                    UUID serviceUuid = UUID.fromString(Layer.SERVICE_UUID);
                    UUID characteristicUuid = UUID.fromString(Layer.CHARACTERISTIC_UUID);
                    UUID userinfoUuid = UUID.fromString(Layer.USERINFO_UUID);

                    BluetoothGattService service = gatt.getService(serviceUuid);

                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUuid);
                    gatt.setCharacteristicNotification(characteristic, true);
                    gatt.readCharacteristic(characteristic);

                    BluetoothGattCharacteristic userinfo = service.getCharacteristic(userinfoUuid);
                    gatt.setCharacteristicNotification(userinfo, true);
                    gatt.readCharacteristic(userinfo);
                    break;
                case BluetoothGatt.GATT_FAILURE:
                    if (d)
                        Log.d(TAG, "BluetoothGattCallback - onServicesDiscovered (GATT_FAILURE)");
                    break;
                default:
                    if (d)
                        Log.d(TAG, "BluetoothGattCallback - onServicesDiscovered (status " + status + ")");
                    break;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i("CHAR_CHANGE", "string value: " + characteristic.getStringValue(0));
            if (d)
                Log.d(TAG, "BluetoothGattCallback - onCharacteristicChanged (characteristic " + characteristic.getStringValue(0) + ")");

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (d)
                Log.d(TAG, "BluetoothGattCallback - onCharacteristicRead (characteristic " + characteristic.getStringValue(0) + ", status " + status + ")");

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (d)
                Log.d(TAG, "BluetoothGattCallback - onCharacteristicWrite (characteristic " + characteristic.getStringValue(0) + ", status " + status + ")");
        }

    };

    private void startWriter(){
        if (writerTimer != null){
            return;
        }
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (d) Log.d(TAG, "Writer Trigger");
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

    private void startReader(){
        if (readerTimer != null) return;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (d) Log.d(TAG, "Reader Trigger");
//                bluetoothGatt.readCharacteristic()

            }
        };
        readerTimer = new Timer();
        readerTimer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    private void stopReader(){
        if (readerTimer == null){
            return;
        }
        readerTimer.cancel();
        readerTimer = null;
    }

    //endregion
}