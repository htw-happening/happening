package com.happening.poc.poc_happening.fragment;

import android.bluetooth.BluetoothAdapter;
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
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.happening.poc.poc_happening.R;
import com.happening.poc.poc_happening.adapter.DeviceListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by kaischulz on 11.12.16.
 */

public class Bt4Controls extends Fragment implements View.OnClickListener {

    private static Bt4Controls instance = null;
    private View rootView = null;

    private static final String HAPPENING_SERVICE_UUID = "11111111-1337-1337-1337-000000000000";
    public static final ParcelUuid parcelUuid = ParcelUuid.fromString(HAPPENING_SERVICE_UUID);

    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothLeScanner mBluetoothLeScanner = null;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;

    private ScanCallback mScanCallback = null;
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

        // set event Listener
        Button startDiscoverButton = (Button) rootView.findViewById(R.id.discover_start_button);
        startDiscoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscover();
            }
        });

        // set on click Listener
        Button stopDiscoverButton = (Button) rootView.findViewById(R.id.discover_stop_button);
        stopDiscoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDiscover();
            }
        });

        Button startAdvertiseButton = (Button) rootView.findViewById(R.id.advertise_start_button);
        startAdvertiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAdvertise();
            }
        });

        Button stopAdvertiseButton = (Button) rootView.findViewById(R.id.advertise_stop_button);
        stopAdvertiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAdvertise();
            }
        });

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

        return rootView;
    }

    private void startAdvertise() {

        Snackbar.make(rootView, "start advertise", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        AdvertiseSettings.Builder advertiseSettingsBuilder = new AdvertiseSettings.Builder();
        advertiseSettingsBuilder
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setTimeout(10000)
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

    private void stopAdvertise() {
        Snackbar.make(rootView, "stop advertise", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

    private void startDiscover() {
        Snackbar.make(rootView, "start discover", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        scanSettingsBuilder
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
        ScanSettings scanSettings = scanSettingsBuilder.build();

        List<ScanFilter> scanFilters = new ArrayList<>();

        mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
        mBluetoothLeScanner.stopScan(mScanCallback);
        deviceListAdapter.deviceList.clear();
        deviceListAdapter.notifyDataSetChanged();
        mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);
    }

    private void stopDiscover() {
        Snackbar.make(rootView, "stop discover", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
        mBluetoothLeScanner.stopScan(mScanCallback);
    }

    @Override
    public void onClick(View v) {

    }
}
