package com.happening.poc.poc_happening.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.Map;

public class DeviceModel {

    private int rssi;
    private ScanRecord scanRecord;
    private BluetoothGatt bluetoothGatt;
    private BluetoothDevice bluetoothDevice;

    private int state = BluetoothProfile.STATE_DISCONNECTED;

    public DeviceModel(ScanResult scanResult) {
        this.bluetoothDevice = scanResult.getDevice();
        this.scanRecord = scanResult.getScanRecord();
        this.rssi = scanResult.getRssi();
    }

    public String getName() {
        if (bluetoothDevice.getName() != null) {
            return bluetoothDevice.getName();
        }
        return "n/a";
    }

    public String getAddress() {
        return bluetoothDevice.getAddress();
    }

    public String getPathloss() {
        if (scanRecord.getTxPowerLevel() != Integer.MIN_VALUE) {
            return (scanRecord.getTxPowerLevel() - this.rssi) + "dBm";
        }
        return "n/a";
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public boolean isConnected() {
        return (bluetoothGatt != null && getState() == BluetoothProfile.STATE_CONNECTED);
    }

    public boolean isDisconnected() {
        return (bluetoothGatt == null || getState() == BluetoothProfile.STATE_DISCONNECTED);
    }

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof DeviceModel) {
            return getBluetoothDevice().equals(((DeviceModel) object).getBluetoothDevice());
        }
        return false;
    }

}
