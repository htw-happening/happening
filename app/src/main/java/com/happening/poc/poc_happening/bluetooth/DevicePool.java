package com.happening.poc.poc_happening.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DevicePool extends ArrayList<DeviceModel> {

    public void changeState(BluetoothDevice device, int newState) {

        DeviceModel model = getModelByDevice(device);
        if (model != null) {
            model.setState(newState);
            Log.d("DEVICE_POOL", "Changed State to "+newState);

        } else {
            Log.d("DEVICE_POOL", "Device not found, can't update state!");
        }
    }

    public List<DeviceModel> getConnectedDevices() {
//        int[] states = {BluetoothProfile.STATE_CONNECTED};
//        return this.getDevicesMatchingConnectionStates(states);
        return this.getDevicesMatchingConnectionState(BluetoothProfile.STATE_CONNECTED);
    }

    public List<DeviceModel> getDevicesMatchingConnectionState(int state) {
        List<DeviceModel> matchingDevices = new ArrayList<>();
        for (DeviceModel model : this) {
            if (state == model.getState()) {
                matchingDevices.add(model);
            }
        }
        return matchingDevices;
    }

    public List<DeviceModel> getDevicesMatchingConnectionStates(int[] states) {
        List<DeviceModel> matchingDevices = new ArrayList<>();
        for (DeviceModel model : this) {
            if (Arrays.asList(states).contains(model.getState())) {
                matchingDevices.add(model);
            }
        }
        return matchingDevices;
    }

    public DeviceModel getModelByDevice(BluetoothDevice device) {
        Log.d("DEVICE-POOL-MINE", device.getAddress());
        for (DeviceModel model : this) {
                if(model.getBluetoothDevice().equals(device)){
                    Log.d("DEVICE-POOL-SEARCH", "Found "+model.getBluetoothDevice().getAddress());
                    return model;
            }
        }
        return null;
    }
}
