package com.happening.poc_happening.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;

import java.util.UUID;

public class ScannerCallback extends BroadcastReceiver {

    boolean debug = true;
    String TAG = this.getClass().getSimpleName();

    public ScannerCallback(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (debug) Log.d(TAG, "onReceive");
        String action = intent.getAction();
        switch (action) {
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                if (debug) Log.d(TAG, "ACTION_DISCOVERY_STARTED");
                break;
            case BluetoothDevice.ACTION_FOUND:
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (debug) Log.d(TAG, "ACTION_FOUND " + device.getName() + " " + device.getAddress());
                if (device != null) {
                    Layer.getInstance().addNewScan(device);
                }
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                if (debug) Log.d(TAG, "ACTION_DISCOVERY_STARTED");
                break;
            case BluetoothDevice.ACTION_UUID:
                Log.d(TAG, "ACTION_UUID");

                BluetoothDevice deviceWithSDP = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                if (debug) Log.d(TAG, "UUIDs Found for " + deviceWithSDP.getName());

                if (uuidExtra == null) {
                    if (debug) Log.d(TAG, "UUIDs  have been NULL");
                } else {
                    if (debug) Log.d(TAG, "UUIDs  are available");
                    for (Parcelable parcelable : uuidExtra) {
                        ParcelUuid parcelUuid = (ParcelUuid) parcelable;
                        UUID uuid = parcelUuid.getUuid();
                        if (debug) Log.d("ParcelUuidTest", "uuid: " + uuid);
                    }
                }
        }
    }
}