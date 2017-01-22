package com.happening.poc.poc_happening.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanResult;

public class DeviceModel {

    private int currentState = BluetoothProfile.STATE_DISCONNECTED;
    private int targetState = BluetoothProfile.STATE_CONNECTED;
    private BluetoothGatt clientGatt;
    private BluetoothGatt serverGatt;
    private BluetoothDevice clientDevice;
    private BluetoothDevice serverDevice;

    public DeviceModel(ScanResult scanResult) {
        this.clientDevice = scanResult.getDevice();
    }

    public DeviceModel(BluetoothDevice bluetoothDevice) {
        this.serverDevice = bluetoothDevice;
    }

    public String getName() {
        StringBuilder s = new StringBuilder();
        if (clientDevice != null) s.append("client");
        if (serverDevice != null) s.append("server");
        if (clientDevice == null && serverDevice == null) s.append("neither");
        if (clientDevice != null && serverDevice != null) s.append("both");
        return s.toString();
    }

    public String getAddress() {
        StringBuilder s = new StringBuilder();
        if (clientDevice != null) s.append("C:").append(clientDevice.getAddress());
        if (clientDevice != null && serverDevice != null) s.append(" ");
        if (serverDevice != null) s.append("S:").append(serverDevice.getAddress());
        return s.toString();
    }

    public String getPathloss() {
        return "n/a";
    }

    public BluetoothDevice getClientDevice() {
        return clientDevice;
    }

    public void setClientDevice(BluetoothDevice clientDevice) {
        this.clientDevice = clientDevice;
    }

    public void setClientGatt(BluetoothGatt clientGatt) {
        this.clientGatt = clientGatt;
    }

    public BluetoothGatt getClientGatt() {
        return clientGatt;
    }

    public BluetoothGatt getServerGatt() {
        return serverGatt;
    }

    public BluetoothDevice getServerDevice() {
        return serverDevice;
    }

    public void setServerGatt(BluetoothGatt serverGatt) {
        this.serverGatt = serverGatt;
    }

    public void setServerDevice(BluetoothDevice serverDevice) {
        this.serverDevice = serverDevice;
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

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof DeviceModel) {
            DeviceModel deviceModel = (DeviceModel) object;
            if (getClientDevice() != null &&
                    deviceModel.getClientDevice() != null &&
                    getClientDevice().equals(deviceModel.getClientDevice()))
                return true;
            if (getServerDevice() != null &&
                    deviceModel.getServerDevice() != null &&
                    getServerDevice().equals(deviceModel.getServerDevice()))
                return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getName());
        switch (getCurrentState()) {
            case BluetoothProfile.STATE_CONNECTED:
                s.append("connected");
            case BluetoothProfile.STATE_DISCONNECTED:
                s.append("disconnected");
            case BluetoothProfile.STATE_CONNECTING:
                s.append("connecting");
            case BluetoothProfile.STATE_DISCONNECTING:
                s.append("disconnecting");
        }
        s.append("currentState ").append(currentState);
        s.append("targetState ").append(targetState);
        return s.toString();
    }
}
