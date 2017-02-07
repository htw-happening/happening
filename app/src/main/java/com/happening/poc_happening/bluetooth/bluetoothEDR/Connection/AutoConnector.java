package com.happening.poc_happening.bluetooth.bluetoothEDR.Connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This class represents the Procedure for Auto Connecting to all reachable devices.
 * Extending the BroadcastReceiver for getting the Callbacks of BluetoothAdapter & BluetoothDevice.
 */
public class AutoConnector extends BroadcastReceiver {

    boolean d = true;
    String TAG = this.getClass().getSimpleName();

    BluetoothService bluetoothService;

    public AutoConnector(BluetoothService bluetoothService) {
        this.bluetoothService = bluetoothService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (d) Log.d(TAG, "onReceive");

        String action = intent.getAction();

        switch (action) {

            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:

                if (d) Log.d(TAG, "Discovery Started --> Clear Sets");

                break;

            case BluetoothDevice.ACTION_FOUND:
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    //connect!
                    if (d) Log.d(TAG, "Discovered a Device: " + device.getName() + " --> Connect!");
                    bluetoothService.connect(device);
                    return;
                }
                break;

            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:

                if (d) Log.d(TAG, "Discovery finished");
                break;

            case BluetoothDevice.ACTION_UUID:
                break;
        }
    }
}
