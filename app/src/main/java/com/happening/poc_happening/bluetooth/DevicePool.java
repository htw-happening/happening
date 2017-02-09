package com.happening.poc_happening.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DevicePool extends ArrayList<DeviceModel> {

    public DevicePool() {
        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                boolean updateOccurred = false;
                List<DeviceModel> connectedDevices = getConnectedDevices();
                for (DeviceModel device : connectedDevices) {
                    updateOccurred |= device.readRssi();
                }
                Layer.getInstance().notifyHandlers(Layer.DEVICE_POOL_UPDATED);
                int delay = updateOccurred ? connectedDevices.size() * 250 : 1000;
                handler.postDelayed(this, delay);
            }
        };
        handler.postDelayed(r, 100);
    }

    public void changeState(BluetoothDevice device, int newState) {
        DeviceModel model = getModelByDevice(device);
        changeState(model, newState);
    }

    public void changeState(DeviceModel model, int newState) {
        if (model != null) {
            model.setCurrentState(newState);
            Log.i("DEVICE_POOL", "Changed State to " + newState);
        } else {
            Log.i("DEVICE_POOL", "Device not found, can't update state!");
        }
    }

    public List<DeviceModel> getConnectedDevices() {
        return getDevicesMatchingConnectionState(BluetoothProfile.STATE_CONNECTED);
    }

    public List<DeviceModel> getDevicesMatchingConnectionState(int state) {
        List<DeviceModel> matchingDevices = new ArrayList<>();
        for (DeviceModel model : this) {
            if (state == model.getCurrentState()) {
                matchingDevices.add(model);
            }
        }
        return matchingDevices;
    }

    public DeviceModel getModelByDevice(BluetoothDevice device) {
        for (DeviceModel model : this) {
            if (model.getBluetoothDevice().equals(device)) {
                return model;
            }
        }
        return null;
    }
}