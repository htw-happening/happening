package com.happening.poc.poc_happening.adapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.Map;

public class DeviceModel {

    private int rssi;
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

        this.bluetoothGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        Log.d("GATT", "state connected");
                        gatt.discoverServices();
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        Log.d("GATT", "state disconnected");
                        // gatt.close();
                        break;
                    default:
                        Log.d("GATT", "connection state changed " + newState);
                        break;
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                switch (status) {
                    case BluetoothGatt.GATT_SUCCESS:
                        Log.d("GATT", "services discovered");
                        readCharacteristics(gatt);
                        break;
                    case BluetoothGatt.GATT_FAILURE:
                        Log.e("GATT", "service discovery failed");
                        break;
                    default:
                        Log.e("GATT", "no service discovered " + status);
                        break;
                }
            }

            private void readCharacteristics(BluetoothGatt gatt) {
                for (BluetoothGattService service : gatt.getServices()) {
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                            gatt.readDescriptor(descriptor);
                        }
                    }
                }
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                Log.d("GATT", "onDescriptorRead status changed " + status);
                Log.d("GATT", new String(descriptor.getValue()));
            }
        };
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

    public void connectDevice() {
        /* TODO: From BluetoothGatt docs
        * The autoConnect parameter determines whether to actively connect to
        * the remote device, or rather passively scan and finalize the connection
        * when the remote device is in range/available. Generally, the first ever
        * connection to a device should be direct (autoConnect set to false) and
        * subsequent connections to known devices should be invoked with the
        * autoConnect parameter set to true.
        */

        if (bluetoothGatt == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bluetoothGatt = bluetoothDevice.connectGatt(context, true, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
            } else {
                bluetoothGatt = bluetoothDevice.connectGatt(context, true, bluetoothGattCallback);
            }
        } else {
            Log.d("GATT", "Already connected");
        }
    }

    public void disconnectDevice() {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
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
