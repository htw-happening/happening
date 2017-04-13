package blue.happening;

import blue.happening.lib.BluetoothDevice;
import blue.happening.lib.IHandler;

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

}
