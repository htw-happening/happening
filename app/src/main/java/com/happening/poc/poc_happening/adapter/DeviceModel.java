package com.happening.poc.poc_happening.adapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import com.happening.poc.poc_happening.bluetooth.HappeningGattCallback;

import java.util.Map;

public class DeviceModel {

    private int rssi;
    private boolean firstConnect;
    private Context context;
    private ScanRecord scanRecord;
    private BluetoothGatt bluetoothGatt;
    private BluetoothDevice bluetoothDevice;
    private BluetoothGattCallback bluetoothGattCallback;

    public DeviceModel(Context context, ScanResult scanResult) {
        this.bluetoothDevice = scanResult.getDevice();
        this.scanRecord = scanResult.getScanRecord();
        this.rssi = scanResult.getRssi();
        this.context = context;
        this.firstConnect = false;

        this.bluetoothGattCallback = new HappeningGattCallback();
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

    public void connectDevice() {
        /* TODO: From BluetoothGatt docs
        * The autoConnect parameter determines whether to actively connect to
        * the remote device, or rather passively scan and finalize the connection
        * when the remote device is in range/available. Generally, the first ever
        * connection to a device should be direct (autoConnect set to false) and
        * subsequent connections to known devices should be invoked with the
        * autoConnect parameter set to true.
        */

        if (isConnected()) {
            Log.d("GATT", "Already connected");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bluetoothGatt = bluetoothDevice.connectGatt(context, !firstConnect, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
            } else {
                bluetoothGatt = bluetoothDevice.connectGatt(context, !firstConnect, bluetoothGattCallback);
            }
            firstConnect = true;
            Log.d("GATT", "Connecting");
        }
    }

    public void disconnectDevice() {
        if (isConnected()) {
            bluetoothGatt.disconnect();
            Log.d("GATT", "Disconnecting");
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
