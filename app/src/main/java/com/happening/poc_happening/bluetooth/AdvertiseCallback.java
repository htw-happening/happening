package com.happening.poc_happening.bluetooth;

import android.bluetooth.le.AdvertiseSettings;
import android.util.Log;

public class AdvertiseCallback extends android.bluetooth.le.AdvertiseCallback {

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
