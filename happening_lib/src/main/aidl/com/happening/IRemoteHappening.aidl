package com.happening;

import com.happening.lib.BluetoothDevice;

interface IRemoteHappening {
    void addDevice(String name);
    BluetoothDevice getDevice(String name);
    List<BluetoothDevice> getDevices();

    // for development only
    void enableAdapter();
    void disableAdapter();
    void isBtAdapterEnabled();

    void startScan();
    void stopScan();

    void startAdvertising();
    void stopAdvertising();
    void isAdvertisingSupported();

    void createGattServer();
    void stopGattServer();

//    void addHandler(Handler handler);
//    void removeHandler(Handler handler);

}
