package com.happening.poc_happening.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;

import com.happening.poc_happening.MainActivity;
import com.happening.poc_happening.MyApp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Layer {

    private static Layer instance = null;

    public static final String ADVERTISE_UUID = "11111111-0000-0000-0000-000ad7e9415e";
    public static final String SERVICE_UUID = "11111111-0000-0000-0000-000005e971ce";
    public static final String CHARACTERISTIC_UUID = "11111111-0000-0000-00c8-a9ac4e91541c";
    public static final String USERINFO_UUID = "11111111-0000-0000-0000-000005371970";

    public static final int DEFAULT_MTU_BYTES = 128;
    public static final int DEVICE_POOL_UPDATED = 1;
    public static final int MESSAGE_RECEIVED = 2;

    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothGattServer mBluetoothGattServer = null;

    private BluetoothLeScanner mBluetoothLeScanner = null;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;

    private DevicePool devicePool = new DevicePool();
    private List<Handler> handlers = new ArrayList<>();
    private Context context = null;

    private ScanCallback mScanCallback = new ScanCallback();
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback();

    private boolean autoConnect = false;

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
        Log.i("SELF", mBluetoothAdapter.getName());
    }

    public int getNumOfConnectedDevices() {
        return devicePool.getConnectedDevices().size();
    }

    private void notifyHandlers(int code) {
        for (Handler handler : handlers) {
            handler.obtainMessage(code).sendToTarget();
        }
    }

    private void notifyHandlers(int code, String content, String author) {
        for (Handler handler : handlers) {
            Message message = handler.obtainMessage(code);
            Bundle bundle = new Bundle();
            bundle.putString("content", content);
            bundle.putString("author", author);
            message.setData(bundle);
            handler.sendMessage(message);
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

    public DevicePool getDevicePool() {
        return devicePool;
    }

    public void connectDevice(DeviceModel deviceModel) {
        if (deviceModel.getBluetoothGatt() == null) {
            BluetoothDevice bluetoothDevice = deviceModel.getBluetoothDevice();
            BluetoothGatt bluetoothGatt;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bluetoothGatt = bluetoothDevice.connectGatt(context, false, new BluetoothGattCallback(), BluetoothDevice.TRANSPORT_LE);
            } else {
                bluetoothGatt = bluetoothDevice.connectGatt(context, false, new BluetoothGattCallback());
            }
            deviceModel.setBluetoothGatt(bluetoothGatt);
            Log.i("GATT", "Opening new gatt " + deviceModel.getAddress());
        } else if (deviceModel.isDisconnected()) {
            boolean success = deviceModel.getBluetoothGatt().connect();
            Log.i("GATT", "Connecting via open gatt " + deviceModel.getAddress() + (success ? " success" : "fail"));
        } else {
            Log.i("GATT", "Cannot connect state " + deviceModel.getCurrentState() + " gatt " + deviceModel.getBluetoothGatt());
        }
    }

    public void disconnectDevice(DeviceModel deviceModel) {
        if (deviceModel.isConnected()) {
            deviceModel.setTargetState(BluetoothProfile.STATE_DISCONNECTED);
            if (deviceModel.getType() == "client") {
                mBluetoothGattServer.cancelConnection(deviceModel.getBluetoothDevice());
            } else if (deviceModel.getType() == "server") {
                deviceModel.getBluetoothGatt().disconnect();
            }
            deviceModel.getBluetoothGatt().getDevice().getType();
            Log.i("GATT", "Disconnecting " + deviceModel.getAddress());
        } else {
            Log.i("GATT", "Cannot disconnect state " + deviceModel.getCurrentState() + " gatt " + deviceModel.getBluetoothGatt());
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

    //region Operations

    public void enableAdapter() {
        mBluetoothAdapter.enable();
    }

    public void disableAdapter() {
        mBluetoothAdapter.disable();
    }

    public void startAdvertising() {
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
    }

    public void stopAdvertising() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
        }
    }

    public void startScan() {
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
        ScanFilter.Builder scanFilterBuilder = new ScanFilter.Builder();

        ParcelUuid advertiseUuid = ParcelUuid.fromString(ADVERTISE_UUID);

        scanFilterBuilder.setServiceUuid(advertiseUuid);

        ScanFilter scanFilter = scanFilterBuilder.build();
        List<ScanFilter> scanFilters = new ArrayList<>();
        scanFilters.add(scanFilter);

        mBluetoothLeScanner.stopScan(mScanCallback);
        mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);
    }

    public void stopScan() {
        if (mBluetoothLeScanner != null) {
            mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
            mBluetoothLeScanner.stopScan(mScanCallback);
        }
    }

    public void createGattServer() {
        UUID serviceUuid = UUID.fromString(SERVICE_UUID);
        UUID characteristicUuid = UUID.fromString(CHARACTERISTIC_UUID);
        UUID userinfoUuid = UUID.fromString(USERINFO_UUID);

        BluetoothGattService gattService = new BluetoothGattService(
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

        mBluetoothGattServer = mBluetoothManager.openGattServer(context, new BluetoothGattServerCallback());

        mBluetoothGattServer.addService(gattService);
    }

    public void stopGattServer() {
        if (mBluetoothGattServer != null) {
            for (DeviceModel deviceModel : devicePool.getConnectedDevices()) {
                disconnectDevice(deviceModel);
            }
            mBluetoothGattServer.clearServices();
            mBluetoothGattServer.close();
        }
    }

    public void broadcastMessage(String message) {
        Log.i("BROADCAST", "broadcast message" + message);

        synchronized (devicePool.getConnectedDevices()) {
            for (DeviceModel deviceModel : devicePool.getConnectedDevices()) {
                Log.i("BROADCAST", "Device " + deviceModel.getAddress());
                BluetoothGatt bluetoothGatt = deviceModel.getBluetoothGatt();
                if (deviceModel.getType() == "client") continue;
                BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(SERVICE_UUID));
                BluetoothGattCharacteristic characteristic = bluetoothGattService.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID));
                characteristic.setValue(message.getBytes());
                bluetoothGatt.writeCharacteristic(characteristic);
            }
        }
        Log.i("BROADCAST", "Done");
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    //endregion

    //region Callbacks

    public class ScanCallback extends android.bluetooth.le.ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            DeviceModel deviceModel = new DeviceModel(result);
            if (!devicePool.contains(deviceModel)) {
                Log.i("SCAN_CALLBACK", "new device found " + result);
                devicePool.add(deviceModel);
                notifyHandlers(DEVICE_POOL_UPDATED);
                if (autoConnect) connectDevice(deviceModel);
            }
        }
    }

    public class AdvertiseCallback extends android.bluetooth.le.AdvertiseCallback {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.i("ADV_CALLBACK", "advertising started");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.i("ADV_CALLBACK", "advertising error " + errorCode);
        }
    }

    public class BluetoothGattServerCallback extends android.bluetooth.BluetoothGattServerCallback {

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            Log.i("CHAR_READ", "character read " + new String(characteristic.getValue()));
            Log.i("CHAR_READ", "server " + mBluetoothGattServer);
            if (mBluetoothGattServer != null)
                mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getStringValue(0).getBytes());
        }

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("CONN_CHANGE", "Set device connected " + device.getAddress());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("CONN_CHANGE", "Set device disconnected " + device.getAddress());
            } else {
                Log.i("CONN_CHANGE", "State changed to " + newState);
            }
            DeviceModel deviceModel = new DeviceModel(device);
            if (!devicePool.contains(deviceModel)) {
                devicePool.add(deviceModel);
            }
            devicePool.changeState(device, newState);
            notifyHandlers(DEVICE_POOL_UPDATED);
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            Log.i("SERVICE_ADD", "service added " + status + " " + service.getUuid().toString());
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            Log.i("CHAR_WRITE_REQUEST:", "device: " + device.getAddress() + " preparedWrite: " + preparedWrite + " responseNeeded: " + responseNeeded);
            String message = new String(value);
            notifyHandlers(MESSAGE_RECEIVED, message, device.getAddress());
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            Log.i("NOTIFICATION", "device: " + device.getAddress() + " status: " + status);
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            Log.i("MTU_CHANGE", "device: " + device.getAddress() + " mtu: " + mtu);
        }
    }

    public class BluetoothGattCallback extends android.bluetooth.BluetoothGattCallback {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            BluetoothDevice bluetoothDevice = gatt.getDevice();
            DeviceModel device = devicePool.getModelByDevice(bluetoothDevice);
            devicePool.changeState(device, newState);

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    boolean mtuSuccess = gatt.requestMtu(DEFAULT_MTU_BYTES);
                    Log.i("CONN_CHANGE", "connected and requesting mtu " + mtuSuccess);
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.i("CONN_CHANGE", "state disconnected");
                    if (device.getTargetState() == BluetoothProfile.STATE_CONNECTED) {
                        boolean success = gatt.connect();
                        if (success) break;
                    }
                    gatt.close();
                    device.setBluetoothGatt(null);
                    if (device.getTargetState() == BluetoothProfile.STATE_CONNECTED) {
                        connectDevice(device);
                    }
                    break;
                default:
                    Log.i("CONN_CHANGE", "connection state changed " + newState);
                    break;
            }
            notifyHandlers(DEVICE_POOL_UPDATED);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            Log.i("MTU_CHANGE", "mtu changed to " + mtu);
            boolean discovering = gatt.discoverServices();
            Log.i("MTU_CHANGE", "discover start success " + discovering);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            switch (status) {
                case BluetoothGatt.GATT_SUCCESS:
                    Log.i("SERVICE_DISCO", "services discovered");
                    UUID serviceUuid = UUID.fromString(Layer.SERVICE_UUID);
                    UUID characteristicUuid = UUID.fromString(Layer.CHARACTERISTIC_UUID);
                    UUID userinfoUuid = UUID.fromString(Layer.USERINFO_UUID);

                    Log.i("SERVICE_DISCO", "triggered");

                    BluetoothGattService service = gatt.getService(serviceUuid);

                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUuid);
                    gatt.setCharacteristicNotification(characteristic, true);
                    gatt.readCharacteristic(characteristic);

                    BluetoothGattCharacteristic userinfo = service.getCharacteristic(userinfoUuid);
                    gatt.setCharacteristicNotification(userinfo, true);
                    gatt.readCharacteristic(userinfo);

                    break;
                case BluetoothGatt.GATT_FAILURE:
                    Log.e("SERVICE_DISCO", "service discovery failed");
                    break;
                default:
                    Log.e("SERVICE_DISCO", "no service discovered " + status);
                    break;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i("CHAR_CHANGE", "string value: " + characteristic.getStringValue(0));
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("CHAR_READ", "string value: " + characteristic.getStringValue(0) + " status: " + status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("CHAR_WRITE", "string value: " + characteristic.getStringValue(0) + " status: " + status);
        }
    }
    //endregion
}