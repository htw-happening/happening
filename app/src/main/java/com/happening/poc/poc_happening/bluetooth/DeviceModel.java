package com.happening.poc.poc_happening.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.Map;

public class DeviceModel {

    private int rssi;
    private ScanRecord scanRecord;
    private BluetoothGatt bluetoothGatt;
    private BluetoothDevice bluetoothDevice;

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

    public String getSignalStrength() {
        return this.rssi + "dBm";
    }

    public String getPayload() {
        String payload = "";
        for (byte[] value : this.getServiceData().values()) {
            payload += new String(value);
        }
        return payload;
    }

    public String getPathloss() {
        if (scanRecord.getTxPowerLevel() != Integer.MIN_VALUE) {
            return (scanRecord.getTxPowerLevel() - this.rssi) + "dBm";
        }
        return "n/a";
    }

    public Map<ParcelUuid, byte[]> getServiceData() {
        return scanRecord.getServiceData();
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public boolean isBonded() {
        return bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED;
    }

    public boolean isConnected() {
        return bluetoothGatt != null;
    }

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    public void disconnectDevice() {
        if (isConnected()) {
            Log.d("GATT", "Disconnecting");
            bluetoothGatt.disconnect();
            bluetoothGatt = null;
        } else {
            Log.d("GATT", "Nothing to disconnect");
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof DeviceModel) {
            return getBluetoothDevice().equals(((DeviceModel) object).getBluetoothDevice());
        }
        return false;
    }
}
