package com.happening.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DevicePool extends ArrayList<DeviceModel> {

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