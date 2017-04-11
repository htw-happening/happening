package blue.happening.chat.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;

public class DeviceModel {

    private int currentState = BluetoothProfile.STATE_DISCONNECTED;
    private String type = "";
    private int rssi = 0;

    public String getName() {
        return getType();
    }

    public String getAddress() {
        return "";
    }

    public BluetoothDevice getBluetoothDevice() {
        return null;
    }

    public boolean isConnected() {
        return (getCurrentState() == BluetoothProfile.STATE_CONNECTED);
    }

    public boolean isDisconnected() {
        return (getCurrentState() == BluetoothProfile.STATE_DISCONNECTED);
    }

    public int getCurrentState() {
        return this.currentState;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof blue.happening.bluetooth.DeviceModel)
            return getBluetoothDevice().equals(((blue.happening.bluetooth.DeviceModel) object).getBluetoothDevice());
        return false;
    }

    public int getRssi() {
        return rssi;
    }
}