package com.happening.poc.poc_happening.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.Random;

public class Bluetooth4Service extends Service {

    private static final String HAPPENING_SERVICE_UUID = "11111111-1337-1337-1337-000000000000";
    public static final ParcelUuid parcelUuid = ParcelUuid.fromString(HAPPENING_SERVICE_UUID);
    /**
     * indicates how to behave if the service is killed
     */
    int mStartMode;
    /**
     * interface for clients that bind
     */
    IBinder mBinder;
    /**
     * indicates whether onRebind should be used
     */
    boolean mAllowRebind;

    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;
    private AdvertiseCallback mAdvertiseCallback = null;

    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this.getClass().getSimpleName(), "onCreate");

        this.mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = mBluetoothManager.getAdapter();

        this.mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();

        this.mAdvertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Log.d(this.getClass().getSimpleName(), "advertising started");
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                Log.d(this.getClass().getSimpleName(), "advertising error " + errorCode);
            }

        };
    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(this.getClass().getSimpleName(), "onStartCommand");

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

        //TODO start once only
        mBluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, mAdvertiseCallback);

        return mStartMode;
//        return START_STICKY;
    }

    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {

    }

    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

}
