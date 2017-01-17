package com.happening.poc.poc_happening.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
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

import com.happening.poc.poc_happening.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Layer {

    private static Layer instance = null;

    public static final String ADVERTISE_UUID = "11111111-0000-0000-0000-000ad7e9415e";
    public static final String SERVICE_UUID = "11111111-0000-0000-0000-000005e971ce";
    public static final String CHARACTERISTIC_UUID = "11111111-0000-0000-00c8-a9ac4e91541c";
    public static final String DESCRIPTOR_UUID = "00002902-0000-0000-00c8-a9ac4e91541c";

    public static final int DEFAULT_MTU_BYTES = 128;
    public static final int DEVICE_POOL_UPDATED = 1;
    public static final int MESSAGE_RECEIVED = 2;
    public static final int MESSAGE_SENT = 3;

    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothGattServer mBluetoothGattServer = null;

    private BluetoothLeScanner mBluetoothLeScanner = null;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;

    private DevicePool devicePool = new DevicePool();
    private List<Handler> handlers = new ArrayList<Handler>();
    private Context context = null;

    private ScanCallback mScanCallback = new ScanCallback();
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback();
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback();
    private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback();

    public static Layer getInstance() {
        if (instance == null)
            instance = new Layer();
        return instance;
    }

    private Layer() {
        context = MainActivity.getContext();
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

    public void connectDevice(DeviceModel device) {
        BluetoothDevice bluetoothDevice = device.getBluetoothDevice();
        BluetoothGatt bluetoothGatt = null;

        if (device.isConnected()) {
            Log.d("GATT", "Already connected");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //TODO new GattCallback
                bluetoothGatt = bluetoothDevice.connectGatt(context, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
            } else {
                bluetoothGatt = bluetoothDevice.connectGatt(context, false, mGattCallback);
            }
            Log.d("GATT", "Connecting");
            device.setBluetoothGatt(bluetoothGatt);
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
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(true);
        AdvertiseData advertiseData = advertiseDataBuilder.build();

        mBluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, mAdvertiseCallback);
    }

    public void stopAdvertising() {
        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
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
        devicePool.clear();
        mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);
    }

    public void stopScan() {
        mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
        mBluetoothLeScanner.stopScan(mScanCallback);
        devicePool.clear();
    }

    public void createGattServer() {
        UUID serviceUuid = UUID.fromString(SERVICE_UUID);
        UUID characteristicUuid = UUID.fromString(CHARACTERISTIC_UUID);

        BluetoothGattService gattService = new BluetoothGattService(
                serviceUuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic gattCharacteristic = new BluetoothGattCharacteristic(
                characteristicUuid,
                BluetoothGattCharacteristic.PROPERTY_BROADCAST |
                        BluetoothGattCharacteristic.PROPERTY_WRITE |
                        BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE |
                        BluetoothGattCharacteristic.PROPERTY_READ |
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ |
                        BluetoothGattCharacteristic.PERMISSION_WRITE);
        gattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

        gattCharacteristic.setValue("moep".getBytes());

        BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(UUID.fromString(DESCRIPTOR_UUID),
                BluetoothGattDescriptor.PERMISSION_READ);

        gattCharacteristic.addDescriptor(descriptor);
        gattService.addCharacteristic(gattCharacteristic);

        mBluetoothGattServer = mBluetoothManager.openGattServer(context, mGattServerCallback);

        mBluetoothGattServer.addService(gattService);
    }

    public void stopGattServer() {
        mBluetoothGattServer.close();
    }

    public void broadcastMessage(String message) {
        Log.d("BROADCAST", "braodcast message"+message);
        for (DeviceModel deviceModel: devicePool.getConnectedDevices()) {

            Log.d("BROADCAST", "Device "+deviceModel.getAddress());
            BluetoothGatt bluetoothGatt = deviceModel.getBluetoothGatt();
            BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(SERVICE_UUID));
            BluetoothGattCharacteristic characteristic = bluetoothGattService.getCharacteristic(UUID.fromString(CHARACTERISTIC_UUID));
            characteristic.setValue(message.getBytes());
            bluetoothGatt.writeCharacteristic(characteristic);
        }
        Log.d("BROADCAST", "Done");
    }

    //endregion

    //region Callbacks

    public class ScanCallback extends android.bluetooth.le.ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //Log.d("SCAN_CALLBACK", "result found " + result);
            DeviceModel deviceModel = new DeviceModel(result);
            if (!devicePool.contains(deviceModel)) {
                devicePool.add(deviceModel);
                notifyHandlers(DEVICE_POOL_UPDATED);
            }
        }
    }

    public class AdvertiseCallback extends android.bluetooth.le.AdvertiseCallback {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.d("ADV_CALLBACK", "advertising started");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.d("ADV_CALLBACK", "advertising error " + errorCode);
        }
    }

    public class BluetoothGattServerCallback extends android.bluetooth.BluetoothGattServerCallback {

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            Log.d("CHAR_READ", "character read " + new String(characteristic.getValue()));
            Log.d("CHAR_READ", "server " + mBluetoothGattServer);
            if (mBluetoothGattServer != null)
                mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, (characteristic.getStringValue(0) + "Peter").getBytes());
        }

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            Log.d("CONN_CHANGE", "state changed to " + newState);
            devicePool.changeState(device, newState);

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("CONN_CHANGE", "Added a Device to List " + device.getAddress());
                notifyHandlers(DEVICE_POOL_UPDATED);
            }
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("CONN_CHANGE", "Removed a Device from List " + device.getAddress());
                notifyHandlers(DEVICE_POOL_UPDATED);
            }
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            Log.d("SERVICE_ADD", "service added " + status + " " + service.getUuid().toString());
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            Log.d("CHAR_WRITE_REQUEST:", "device: " + device.getAddress() + " preparedWrite: " + preparedWrite + " responseNeeded: " + responseNeeded);
            String message =  new String(value);
            notifyHandlers(MESSAGE_RECEIVED, message, device.getAddress());
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            Log.d("NOTIFICATION", "device: " + device.getAddress() + " status: " + status);
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            Log.d("MTU_CHANGE", "device: " + device.getAddress() + " mtu: " + mtu);
        }
    }

    public class BluetoothGattCallback extends android.bluetooth.BluetoothGattCallback {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.d("CONN_CHANGE", "state connected");
                    boolean mtuSuccess = gatt.requestMtu(DEFAULT_MTU_BYTES);
                    Log.d("CONN_CHANGE", "mtu request success " + mtuSuccess);
                    notifyHandlers(DEVICE_POOL_UPDATED);
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.d("CONN_CHANGE", "state disconnected");
                    notifyHandlers(DEVICE_POOL_UPDATED);
                    gatt.close();
                    break;
                default:
                    Log.d("CONN_CHANGE", "connection state changed " + newState);
                    break;
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            Log.d("MTU_CHANGE", "mtu changed to " + mtu);
            boolean discovering = gatt.discoverServices();
            Log.d("MTU_CHANGE", "discover start success " + discovering);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            switch (status) {
                case BluetoothGatt.GATT_SUCCESS:
                    Log.d("SERVICE_DISCO", "services discovered");
                    UUID serviceUuid = UUID.fromString(Layer.SERVICE_UUID);
                    UUID characteristicUuid = UUID.fromString(Layer.CHARACTERISTIC_UUID);

                    BluetoothGattService service = gatt.getService(serviceUuid);
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUuid);

                    Log.d("SERVICE_DISCO", "triggered");

                    gatt.setCharacteristicNotification(characteristic, true);

                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(DESCRIPTOR_UUID));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                    gatt.writeDescriptor(descriptor);
                    gatt.readCharacteristic(characteristic);

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
            Log.d("CHAR_CHANGE", "string value: " + characteristic.getStringValue(0));
//            DeviceModel device = devicePool.getModelByDevice(gatt.getDevice());
//            notifyHandlers(MESSAGE_RECEIVED, characteristic.getStringValue(0), device.getAddress());
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d("CHAR_READ", "string value: " + characteristic.getStringValue(0) + " status: " + status);
            // notifyHandlers(MESSAGE_RECEIVED, characteristic.getStringValue(0));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d("CHAR_WRITE", "string value: " + characteristic.getStringValue(0) + " status: " + status);
            // notifyHandlers(MESSAGE_SENT, characteristic.getStringValue(0), "n/a");
        }
    }

    //endregion
}
