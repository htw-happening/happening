package com.happening;

import com.happening.lib.BluetoothDevice;

interface IRemoteHappening {
    void addDevice(String name);
    BluetoothDevice getDevice(String name);
    List<BluetoothDevice> getDevices();

    // for development only
    void enableAdapter();
    void disableAdapter();
    boolean isBtAdapterEnabled();

    void startScan();
    void stopScan();

    void startAdvertising();
    void stopAdvertising();
    boolean isAdvertisingSupported();

    void createGattServer();
    void stopGattServer();

    void broadcastMessage(String message);

    // void addHandler(Handler handler);
    // void removeHandler(Handler handler);
    // void connectDevice(DeviceModel deviceModel);
    // void disconnectDevice(DeviceModel deviceModel);
    // DevicePool getDevicePool();
}
