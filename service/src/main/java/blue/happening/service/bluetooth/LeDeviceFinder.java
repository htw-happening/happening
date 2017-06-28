package blue.happening.service.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.os.ParcelUuid;
import android.util.Log;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import blue.happening.MyApplication;

class LeDeviceFinder implements IDeviceFinder {

    private String TAG = getClass().getSimpleName();
    private boolean d = true;

    private Context context = null;
    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothLeScanner bluetoothLeScanner = null;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser = null;

    private ScanCallback scanCallback = new ScanCallback();
    private AdvertiseCallback advertiseCallback = new AdvertiseCallback();
    private Layer layer;

    private AdvertiseManager advertiseManager;
    private ScanManager scanManager;
    private CombinedAdvScanManager combinedAdvScanManager;

    @Override
    public void registerCallback(Layer layer) {
        this.layer = layer;
    }

    @Override
    public void start() {
        startAdvertising();
        startLeScan();
//        advertiseManager.start();
//        scanManager.start();
        combinedAdvScanManager.start();
    }

    @Override
    public void stop() {
        stopAdvertising();
        stopLeScan();
//        advertiseManager.stop();
//        scanManager.stop();
        combinedAdvScanManager.stop();
    }

    LeDeviceFinder() {
        context = MyApplication.getAppContext();
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = bluetoothManager.getAdapter();
        this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        this.bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
//        advertiseManager = new AdvertiseManager();
//        scanManager = new ScanManager();
        combinedAdvScanManager = new CombinedAdvScanManager();
    }

    private boolean isAdvertisingSupported() {
        return bluetoothAdapter.isMultipleAdvertisementSupported() &&
                bluetoothAdapter.isOffloadedFilteringSupported() &&
                bluetoothAdapter.isOffloadedScanBatchingSupported();
    }

    private void startLeScan() {
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

    private void stopLeScan() {
        if (bluetoothLeScanner != null) {
            bluetoothLeScanner.flushPendingScanResults(scanCallback);
            bluetoothLeScanner.stopScan(scanCallback);
            if (d) Log.d(TAG, "Stopped Scanner");
        }
    }

    private void startAdvertising() {
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

    private void stopAdvertising() {
        if (bluetoothLeAdvertiser != null) {
            bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
            if (d) Log.d(TAG, "Stopped Advertising");
        }
    }

    private void addNewLeScanResult(BluetoothDevice device, String macAddress) {
        if (!BluetoothAdapter.checkBluetoothAddress(macAddress)) return;
        if (layer != null) {
            layer.addNewScan(macAddress);
        }
    }


    private class ScanCallback extends android.bluetooth.le.ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            String macAddress = "";
            if (result.getScanRecord() != null) {
                byte[] resultBytes = result.getScanRecord().getServiceData(ParcelUuid.fromString(Layer.RANDOM_READ_UUID));
                if (resultBytes == null) return;
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
            if (d) Log.d(TAG, "AdvertiseCallback - onStartFailure (error: " + errorCode + ")");
        }

    }

    private class CombinedAdvScanManager {
        private static final int ACTIVE_TIME = 3000;
        private static final int INACTIVE_TIME = 2000;
        private Timer timer;
        private TimerTask timerTask;

        CombinedAdvScanManager(){
        }

        void start(){
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "run: CombinedAdvScanManager - Timer triggered");
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            layer.state = Layer.STATE.SCANNING;
                            startAdvertising();
                            startLeScan();
                            try {
                                Thread.sleep(ACTIVE_TIME);
                            } catch (InterruptedException e) {
                                Log.e(TAG, "run: " + e.toString());
                                stop();
                            } finally {
                            }
                            stopAdvertising();
                            stopLeScan();
                            layer.state = Layer.STATE.WRITING;

                        }
                    });
                    thread.start();

                }
            };
            timer.scheduleAtFixedRate(timerTask, ACTIVE_TIME+INACTIVE_TIME, ACTIVE_TIME+INACTIVE_TIME);
        }

        void stop(){
            Log.d(TAG, "stop: AdvertiseManager");
            timerTask.cancel();
            timer.cancel();
        }
    }

    private class AdvertiseManager {
        private static final int ACTIVE_TIME = 30000;
        private static final int INACTIVE_TIME = 10000;
        private Timer timer;
        private TimerTask timerTask;

        AdvertiseManager(){
        }

        void start(){
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "run: AdvertiseManager - Timer triggered");
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startAdvertising();
                            try {
                                Thread.sleep(ACTIVE_TIME);
                            } catch (InterruptedException e) {
                                Log.e(TAG, "run: " + e.toString());
                                stop();
                            } finally {
                            }
                            stopAdvertising();
                        }
                    });
                    thread.start();

                }
            };
            timer.scheduleAtFixedRate(timerTask, ACTIVE_TIME+INACTIVE_TIME, ACTIVE_TIME+INACTIVE_TIME);
        }

        void stop(){
            Log.d(TAG, "stop: AdvertiseManager");
            timerTask.cancel();
            timer.cancel();
        }
    }

    private class ScanManager {
        private static final int ACTIVE_TIME = 5000;
        private static final int INACTIVE_TIME = 10000;
        private Timer timer;
        private TimerTask timerTask;

        ScanManager(){
        }

        void start(){
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "run: ScanManager - Timer triggered");
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            startLeScan();
                            try {
                                Thread.sleep(ACTIVE_TIME);
                            } catch (InterruptedException e) {
                                Log.e(TAG, "run: " + e.toString());
                                stop();
                            } finally {
                            }
                            stopLeScan();
                        }
                    });
                    thread.start();

                }
            };
            timer.scheduleAtFixedRate(timerTask, ACTIVE_TIME+INACTIVE_TIME, ACTIVE_TIME+INACTIVE_TIME);
        }

        void stop(){
            Log.d(TAG, "stop: ScanManager");
            timerTask.cancel();
            timer.cancel();
        }
    }

}
