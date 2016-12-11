package com.happening.poc.poc_happening.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
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
import android.os.ParcelUuid;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.happening.poc.poc_happening.R;
import com.happening.poc.poc_happening.adapter.DeviceListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class Bt4Controls extends Fragment implements View.OnClickListener {

    private static Bt4Controls instance = null;
    private View rootView = null;

    private static final String HAPPENING_SERVICE_UUID = "11111111-1337-1337-1337-000000000000";
    public static final ParcelUuid parcelUuid = ParcelUuid.fromString(HAPPENING_SERVICE_UUID);

    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothGattServer mBluetoothGattServer = null;

    private BluetoothLeScanner mBluetoothLeScanner = null;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;

    private ScanCallback mScanCallback = null;
    private BluetoothGattServerCallback mGattServerCallback = null;
    private AdvertiseCallback mAdvertiseCallback = null;

    private HashMap<String, ScanResult> mScanResults = new LinkedHashMap<>();
    private DeviceListAdapter deviceListAdapter = null;

    public static Bt4Controls getInstance() {
        instance = new Bt4Controls();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bt4controls, container, false);

        // initialize list view
        ListView deviceList = (ListView) rootView.findViewById(R.id.discovered_devices_list);
        deviceListAdapter = new DeviceListAdapter(rootView.getContext(), mScanResults);
        deviceList.setAdapter(deviceListAdapter);

        // initialize bluetooth adapter
        this.mBluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = mBluetoothManager.getAdapter();

        this.mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        this.mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();

        // set scanning callback
        this.mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                if (!mScanResults.containsKey(result.getDevice().getAddress())) {
                    mScanResults.put(result.getDevice().getAddress(), result);
                    deviceListAdapter.notifyDataSetChanged();
                }
            }
        };

        // set advertising callback
        this.mAdvertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Log.d("DEBUG", "advertising started");
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                Log.d("DEBUG", "advertising error " + errorCode);
            }
        };

        this.mGattServerCallback = new BluetoothGattServerCallback() {
            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
            }

            @Override
            public void onServiceAdded(int status, BluetoothGattService service) {
                super.onServiceAdded(status, service);
            }

            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            }

            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            }
        };

        Log.d("SELF", mBluetoothAdapter.getName() + " " + mBluetoothAdapter.getAddress());

        Context context = rootView.getContext();
        String macAddress = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
        TextView bleAddress = (TextView) rootView.findViewById(R.id.ble_mac_address);
        bleAddress.setText(macAddress);

        // set event Listener
        Switch discoverButton = (Switch) rootView.findViewById(R.id.discover_button);
        discoverButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startDiscover();
                } else {
                    stopDiscover();
                }
            }
        });

        Switch advertiseButton = (Switch) rootView.findViewById(R.id.advertise_button);
        advertiseButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startAdvertise();
                } else {
                    stopAdvertise();
                }
            }
        });

        Switch gattServerButton = (Switch) rootView.findViewById(R.id.gatt_server_button);
        gattServerButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    createGattServer();
                } else {
                    stopGattServer();
                }
            }
        });

        if (!mBluetoothAdapter.isMultipleAdvertisementSupported() ||
                !mBluetoothAdapter.isOffloadedFilteringSupported() ||
                !mBluetoothAdapter.isOffloadedScanBatchingSupported()) {
            advertiseButton.setChecked(false);
            advertiseButton.setEnabled(false);
            gattServerButton.setChecked(false);
            gattServerButton.setEnabled(false);
        }

        return rootView;
    }

    private void startAdvertise() {
        if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {

            Snackbar.make(rootView, "Start Advertising", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            AdvertiseSettings.Builder advertiseSettingsBuilder = new AdvertiseSettings.Builder();
            advertiseSettingsBuilder
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                    .setConnectable(true);
            AdvertiseSettings advertiseSettings = advertiseSettingsBuilder.build();

            String[] loads = {"happen", "foobar", "lekker", "service", "matetee"};
            int index = new Random().nextInt(loads.length);
            byte[] payload = loads[index].getBytes();
            AdvertiseData.Builder advertiseDataBuilder = new AdvertiseData.Builder();
            advertiseDataBuilder
                    .addServiceData(parcelUuid, payload)
                    .setIncludeDeviceName(true)
                    .setIncludeTxPowerLevel(true);
            AdvertiseData advertiseData = advertiseDataBuilder.build();

            mBluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, mAdvertiseCallback);

        }
    }

    private void stopAdvertise() {
        Snackbar.make(rootView, "Stop Advertising", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

    private void startDiscover() {
        Snackbar.make(rootView, "Start Discovering", Snackbar.LENGTH_LONG).setAction("Action", null).show();

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

        List<ScanFilter> scanFilters = new ArrayList<>();

        stopDiscover();
        mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);
    }

    private void stopDiscover() {
        Snackbar.make(rootView, "Stop Discovering", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
        mBluetoothLeScanner.stopScan(mScanCallback);
        deviceListAdapter.deviceList.clear();
        deviceListAdapter.notifyDataSetChanged();
    }

    private void createGattServer() {
        Snackbar.make(rootView, "Start Gatt-Server", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        this.mBluetoothGattServer = mBluetoothManager.openGattServer(rootView.getContext(), mGattServerCallback);

        byte[] payload = "awesome characteristic!".getBytes();
        UUID uuid = UUID.fromString(HAPPENING_SERVICE_UUID);
        BluetoothGattService gattService = new BluetoothGattService(uuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic gattCharacteristic = new BluetoothGattCharacteristic(uuid, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
        BluetoothGattDescriptor gattDescriptor = new BluetoothGattDescriptor(uuid, BluetoothGattDescriptor.PERMISSION_READ);
        gattCharacteristic.addDescriptor(gattDescriptor);
        gattCharacteristic.setValue(payload);

        gattService.addCharacteristic(gattCharacteristic);
        mBluetoothGattServer.addService(gattService);
    }

    private void stopGattServer() {
        Snackbar.make(rootView, "Stop Gatt-Server", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        mBluetoothGattServer.close();
    }

    @Override
    public void onClick(View v) {
    }
}
