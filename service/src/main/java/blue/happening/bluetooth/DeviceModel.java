package blue.happening.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanResult;

public class DeviceModel {

    private String type;
    private int currentState = BluetoothProfile.STATE_DISCONNECTED;
    private int targetState = BluetoothProfile.STATE_CONNECTED;
    private BluetoothGatt bluetoothGatt;
    private BluetoothDevice bluetoothDevice;

    public DeviceModel(ScanResult scanResult) {
        this.bluetoothDevice = scanResult.getDevice();
        this.type = "server";
    }

    public DeviceModel(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.type = "client";
    }

    public String getName() {
        return getType();
    }

    public String getAddress() {
        return bluetoothDevice.getAddress();
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public boolean isConnected() {
        return (getCurrentState() == BluetoothProfile.STATE_CONNECTED);
    }

    public boolean isDisconnected() {
        return (getCurrentState() == BluetoothProfile.STATE_DISCONNECTED);
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public int getCurrentState() {
        return this.currentState;
    }

    public void setTargetState(int targetState) {
        this.targetState = targetState;
    }

    public int getTargetState() {
        return this.targetState;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof DeviceModel)
            return getBluetoothDevice().equals(((DeviceModel) object).getBluetoothDevice());
        return false;
    }
}