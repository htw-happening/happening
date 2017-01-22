package com.happening.lib;

import com.happening.lib.BluetoothDevice;

interface IRemoteDeviceService {
    void addDevice(String name);
    BluetoothDevice getDevice(String name);
    List<BluetoothDevice> getDevices();
}
