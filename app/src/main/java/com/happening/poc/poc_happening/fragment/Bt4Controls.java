package com.happening.poc.poc_happening.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
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
import android.content.pm.PackageManager;
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
import com.happening.poc.poc_happening.adapter.DeviceModel;
import com.happening.poc.poc_happening.bluetooth.GattServerCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


public class Bt4Controls extends Fragment {

    private static Bt4Controls instance = null;
    private View rootView = null;

    public static final String ADVERTISE_UUID = "11111111-0000-0000-0000-000ad7e9415e";
    public static final String SERVICE_UUID = "11111111-0000-0000-0000-000005e971ce";
    public static final String CHARACTERISTIC_UUID = "11111111-0000-0000-00c8-a9ac4e91541c";
    public static final String DESCRIPTOR_UUID = "11111111-0000-0000-0000-00de5c919409";

    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    public BluetoothGattServer mBluetoothGattServer = null;

    private BluetoothLeScanner mBluetoothLeScanner = null;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;

    private ScanCallback mScanCallback = null;
    private BluetoothGattServerCallback mGattServerCallback = null;
    private AdvertiseCallback mAdvertiseCallback = null;

    private ArrayList<DeviceModel> mDeviceList = new ArrayList<>();
    private DeviceListAdapter deviceListAdapter = null;

    public static Bt4Controls getInstance() {
        instance = new Bt4Controls();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bt4controls, container, false);

        if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Snackbar.make(rootView, "BLE features are not supported!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        // initialize list view
        ListView deviceListView = (ListView) rootView.findViewById(R.id.discovered_devices_list);
        deviceListAdapter = new DeviceListAdapter(rootView.getContext(), mDeviceList);
        deviceListView.setAdapter(deviceListAdapter);

        // initialize bluetooth adapter
        this.mBluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = mBluetoothManager.getAdapter();

        this.mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        this.mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();

        this.mGattServerCallback = new GattServerCallback();

        // set scanning callback
        this.mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                DeviceModel deviceModel = new DeviceModel(rootView.getContext(), result);
                if (!mDeviceList.contains(deviceModel)) {
                    mDeviceList.add(deviceModel);
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

        Context context = rootView.getContext();
        String macAddress = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
        TextView bleAddress = (TextView) rootView.findViewById(R.id.ble_mac_address);
        bleAddress.setText(macAddress);
        Log.i("SELF", mBluetoothAdapter.getName() + " " + macAddress);

        // set event Listener
        Switch adapterButton = (Switch) rootView.findViewById(R.id.adapter_button);
        adapterButton.setChecked(mBluetoothAdapter.isEnabled());
        adapterButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enableAdapter();
                } else {
                    disableAdapter();
                }
            }
        });

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

    private void enableAdapter() {
        Snackbar.make(rootView, "Enable Adapter", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        mBluetoothAdapter.enable();
    }

    private void disableAdapter() {
        Snackbar.make(rootView, "Disable Adapter", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        mBluetoothAdapter.disable();
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
            ParcelUuid advertiseUuid = ParcelUuid.fromString(ADVERTISE_UUID);
            advertiseDataBuilder
                    .addServiceData(advertiseUuid, payload)
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

        int[] c = {BluetoothProfile.STATE_CONNECTED, BluetoothProfile.STATE_CONNECTING};
        List<BluetoothDevice> connectedDevices = mBluetoothManager.getDevicesMatchingConnectionStates(
                BluetoothProfile.GATT, c);
        Log.i("MATCH", "Conntected gatt devices: " + connectedDevices.size());

        int[] d = {BluetoothProfile.STATE_DISCONNECTED, BluetoothProfile.STATE_DISCONNECTING};
        List<BluetoothDevice> disconnectedDevices = mBluetoothManager.getDevicesMatchingConnectionStates(
                BluetoothProfile.GATT, d);
        Log.i("MATCH", "Disconntected gatt devices: " + disconnectedDevices.size());

        int[] e = {BluetoothProfile.STATE_CONNECTED, BluetoothProfile.STATE_CONNECTING};
        List<BluetoothDevice> connectedServer = mBluetoothManager.getDevicesMatchingConnectionStates(
                BluetoothProfile.GATT_SERVER, e);
        Log.i("MATCH", "Conntected gatt server: " + connectedServer.size());

        int[] f = {BluetoothProfile.STATE_DISCONNECTED, BluetoothProfile.STATE_DISCONNECTING};
        List<BluetoothDevice> disconnectedServer = mBluetoothManager.getDevicesMatchingConnectionStates(
                BluetoothProfile.GATT_SERVER, f);
        Log.i("MATCH", "Disconntected gatt server: " + disconnectedServer.size());

        mBluetoothLeScanner.stopScan(mScanCallback);
        mDeviceList.clear();
        deviceListAdapter.notifyDataSetChanged();
        mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);
    }

    private void stopDiscover() {
        Snackbar.make(rootView, "Stop Discovering", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
        mBluetoothLeScanner.stopScan(mScanCallback);
        mDeviceList.clear();
        deviceListAdapter.notifyDataSetChanged();
    }

    private void createGattServer() {
        Snackbar.make(rootView, "Start Gatt-Server", Snackbar.LENGTH_LONG).setAction("Action", null).show();


        UUID serviceUuid = UUID.fromString(SERVICE_UUID);
        UUID characteristicUuid = UUID.fromString(CHARACTERISTIC_UUID);
        UUID descriptorUuid = UUID.fromString(DESCRIPTOR_UUID);

        BluetoothGattService gattService = new BluetoothGattService(
                serviceUuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic gattCharacteristic = new BluetoothGattCharacteristic(
                characteristicUuid, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
        gattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);

//        BluetoothGattDescriptor gattDescriptor = new BluetoothGattDescriptor(
//                descriptorUuid, BluetoothGattDescriptor.PERMISSION_WRITE);

//        gattDescriptor.setValue("awesome descriptor".getBytes());
//        gattCharacteristic.addDescriptor(gattDescriptor);

        gattCharacteristic.setValue("moep".getBytes());
        gattService.addCharacteristic(gattCharacteristic);

        getBluetoothGattServer().addService(gattService);
    }

    private void stopGattServer() {
        if (mBluetoothGattServer != null) {
            Snackbar.make(rootView, "Stop Gatt-Server", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            getBluetoothGattServer().close();
        } else {
            Snackbar.make(rootView, "Nuttin to close", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }

    public BluetoothGattServer getBluetoothGattServer() {
        if (mBluetoothGattServer == null) {
            mBluetoothGattServer = mBluetoothManager.openGattServer(rootView.getContext(), mGattServerCallback);
        }
        return mBluetoothGattServer;
    }
}
