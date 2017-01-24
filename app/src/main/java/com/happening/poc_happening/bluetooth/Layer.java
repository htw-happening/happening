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
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleConnection;
import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.RxBleScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import rx.Subscription;
import rx.functions.Action1;

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

    private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;

    RxBleClient rxBleClient;

    Subscription scanSubscription;

    Subscription subscription;

    RxBleConnection rxBleConnection;

    private List<Handler> handlers = new ArrayList<>();
    private Context context = null;

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback();

    private boolean autoConnect = false;

    public static Layer getInstance() {
        if (instance == null)
            instance = new Layer();
        return instance;
    }

    private Layer() {
        context = MyApp.getAppContext();
        rxBleClient = RxBleClient.create(context);
        this.mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = mBluetoothManager.getAdapter();
        this.mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        Log.i("SELF", mBluetoothAdapter.getName());
    }

    public int getNumOfConnectedDevices() {
        return -1;
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
            mBluetoothGattServer.clearServices();
            mBluetoothGattServer.close();
        }
    }

    public void broadcastMessage(String message) {
        Log.i("BROADCAST", "broadcast message" + message);

        //TODO

        Log.i("BROADCAST", "Done");
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    public void startScan() {
        Log.d("Client", "Scanner started");
        scanSubscription = rxBleClient.scanBleDevices(UUID.fromString(ADVERTISE_UUID))
            .subscribe(
                new Action1<RxBleScanResult>() {
                       @Override
                       public void call(RxBleScanResult rxBleScanResult) {
                           Log.d("Client", "Scanner Callback - Found "+rxBleScanResult.getBleDevice().getMacAddress());
                           connect(rxBleScanResult.getBleDevice().getMacAddress());

                       }
                   },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("Client", "Scanner Callback - Error "+throwable.toString());
                    }
                }
            );

    }

    public void stopScan() {
        scanSubscription.unsubscribe();
        Log.d("Client", "Scanner stopped");
    }

    public void connect(String macAddress){
        Log.d("Client", "Start Connecting to a new Device "+macAddress);
        RxBleDevice device = rxBleClient.getBleDevice(macAddress);

        subscription = device.establishConnection(context, false) // <-- autoConnect flag
            .subscribe(
                new Action1<RxBleConnection>() {
                    @Override
                    public void call(RxBleConnection connection) {
                        // All GATT operations are done through the rxBleConnection.
                        rxBleConnection = connection;
                        Log.d("Client", "Connecting Successful");
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("Client", "Connecting failed "+throwable.toString());
                    }
                }
            );
    }

    void disconnect(Subscription subscription){
        subscription.unsubscribe();
    }

    //endregion

    //region Callbacks

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
            Log.i("Server", "CHAR_READ - value: " + new String(characteristic.getValue()));
            if (mBluetoothGattServer != null)
                mBluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getStringValue(0).getBytes());
        }

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("Server", "CONN_CHANGE - Device connected " + device.getAddress());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i("Server", "CONN_CHANGE - Device disconnected " + device.getAddress());
            } else {
                Log.i("Server", "CONN_CHANGE - State changed to " + newState + " "+ device.getAddress());
            }
            notifyHandlers(DEVICE_POOL_UPDATED);
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            Log.i("Server", "SERVICE_ADDED" + status + " " + service.getUuid().toString());
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            Log.i("Server", "CHAR_WRITE_REQUEST - device: " + device.getAddress() + " preparedWrite: " + preparedWrite + " responseNeeded: " + responseNeeded);
            String message = new String(value);
            notifyHandlers(MESSAGE_RECEIVED, message, device.getAddress());
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            Log.i("Server", "NOTIFICATION_SENT: " + device.getAddress() + " status: " + status);
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            Log.i("Server", "MTU_CHANGE - device: " + device.getAddress() + " mtu: " + mtu);
        }
    }
    //endregion
}