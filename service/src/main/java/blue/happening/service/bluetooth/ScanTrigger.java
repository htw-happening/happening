package blue.happening.service.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.os.ParcelUuid;
import android.provider.CallLog;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import blue.happening.service.MainActivity;

class ScanTrigger {

    private String TAG = getClass().getSimpleName();
    private boolean d = true;

    private Context context = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothLeScanner bluetoothLeScanner = null;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser = null;

    private ScanCallback scanCallback = new ScanCallback();
    private AdvertiseCallback advertiseCallback = new AdvertiseCallback();

    ScanTrigger() {
        context = MainActivity.getContext();
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = bluetoothManager.getAdapter();
        this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        this.bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
    }

    private boolean isAdvertisingSupported() {
        return bluetoothAdapter.isMultipleAdvertisementSupported() &&
                bluetoothAdapter.isOffloadedFilteringSupported() &&
                bluetoothAdapter.isOffloadedScanBatchingSupported();
    }

    void startLeScan() {
        if (d) Log.d(TAG, "Starting Scanner");
        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scanSettingsBuilder
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                    .setMatchMode(ScanSettings.MATCH_MODE_STICKY);
        } else {
            scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        }
        ScanSettings scanSettings = scanSettingsBuilder.build();

        ParcelUuid serviceUuid = ParcelUuid.fromString(Layer.SERVICE_UUID);
        ScanFilter.Builder scanFilterBuilder = new ScanFilter.Builder();
        scanFilterBuilder.setServiceUuid(serviceUuid);
        ScanFilter scanFilter = scanFilterBuilder.build();
        List<ScanFilter> scanFilters = new ArrayList<>();
        scanFilters.add(scanFilter);

        bluetoothLeScanner.stopScan(scanCallback);
        bluetoothLeScanner.flushPendingScanResults(scanCallback);
        bluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback);
    }

    void stopLeScan() {
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.flushPendingScanResults(scanCallback);
            bluetoothLeScanner.stopScan(scanCallback);
            if (d) Log.d(TAG, "Stopped Scanner");
        }
    }

    void startAdvertising() {
        if (isAdvertisingSupported()) {
            if (d) Log.d(TAG, "Starting Advertiser");

            String macAddress = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");

            AdvertiseSettings.Builder advertiseSettingsBuilder = new AdvertiseSettings.Builder();
            AdvertiseSettings advertiseSettings = advertiseSettingsBuilder
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                    .setConnectable(true)
                    .build();

            ParcelUuid serviceUuid = ParcelUuid.fromString(Layer.SERVICE_UUID);
            ParcelUuid addressUuid = ParcelUuid.fromString(Layer.ADVERTISE_UUID);

            AdvertiseData advertiseData = new AdvertiseData.Builder()
                    .addServiceUuid(serviceUuid)
                    .build();

            AdvertiseData extraData = new AdvertiseData.Builder()
                    .addServiceData(serviceUuid, macAddress.getBytes())
                    .build();

            bluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, extraData, advertiseCallback);

            if (d) Log.d(TAG, "Started Advertising");
        }
    }

    void stopAdvertising() {
        if (bluetoothLeAdvertiser != null) {
            bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
            if (d) Log.d(TAG, "Stopped Advertising");
        }
    }

    private void addNewLeScanResult(BluetoothDevice device, String macAddress) {

        if (!BluetoothAdapter.checkBluetoothAddress(macAddress)) return; // TODO: 30.05.17 Maybe later trigger EDR scan

        Layer.getInstance().addNewScan(macAddress);
    }

    private class ScanCallback extends android.bluetooth.le.ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            String macAddress = "";
            if (result.getScanRecord() != null) {
                byte[] resultBytes = result.getScanRecord().getServiceData(ParcelUuid.fromString(Layer.RANDOM_READ_UUID));
                macAddress = new String(resultBytes, Charset.defaultCharset());
            }
            //if (d) Log.d(TAG, "Scanned mac address is " + macAddress);
            addNewLeScanResult(result.getDevice(), macAddress);
        }
    }

    private class AdvertiseCallback extends android.bluetooth.le.AdvertiseCallback {

        private String TAG = getClass().getSimpleName();
        private boolean d = true;

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

}
