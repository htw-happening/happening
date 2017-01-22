package com.happening.poc.poc_happening.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanResult;

public class DeviceModel {

    private boolean stayConnected = true;
    private int state = BluetoothProfile.STATE_DISCONNECTED;
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
        if (clientDevice == null || serverDevice == null) s.append("neither");
        return s.toString();
    }

    public String getAddress() {
        StringBuilder s = new StringBuilder();
        if (clientDevice != null) s.append("CLIENT:" + clientDevice.getAddress());
        if (clientDevice != null && serverDevice != null) s.append(" ");
        if (serverDevice != null) s.append("SERVER:" + serverDevice.getAddress());
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
        return (getState() == BluetoothProfile.STATE_CONNECTED);
    }

    public boolean isDisconnected() {
        return (getState() == BluetoothProfile.STATE_DISCONNECTED);
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof DeviceModel)
            return getClientDevice().equals(((DeviceModel) object).getClientDevice());
        return false;
    }

    public boolean getStayConnected() {
        return stayConnected;
    }

    public void setStayConnected(boolean stayConnected) {
        this.stayConnected = stayConnected;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(getName());
        switch (getState()) {
            case BluetoothProfile.STATE_CONNECTED:
                s.append("connected");
            case BluetoothProfile.STATE_DISCONNECTED:
                s.append("disconnected");
            case BluetoothProfile.STATE_CONNECTING:
                s.append("connecting");
            case BluetoothProfile.STATE_DISCONNECTING:
                s.append("disconnecting");
        }
        s.append("stayConnected ").append(stayConnected);
        return s.toString();
    }
}
