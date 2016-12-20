package com.happening.poc.poc_happening.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import com.happening.poc.poc_happening.fragment.Bt4Controls;

import java.util.UUID;

public class HappeningGattCallback extends android.bluetooth.BluetoothGattCallback {

    private final UUID happeningUUID = UUID.fromString(Bt4Controls.HAPPENING_SERVICE_UUID);

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        switch (newState) {
            case BluetoothProfile.STATE_CONNECTED:
                Log.d("GATT", "state connected");
                gatt.discoverServices();
                break;
            case BluetoothProfile.STATE_DISCONNECTED:
                Log.d("GATT", "state disconnected");
                gatt.close();
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

        BluetoothGattService service = gatt.getService(happeningUUID);
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(happeningUUID);
        Log.d("READCHARACTERISTIC", "" + characteristic.getStringValue(0));
        gatt.readCharacteristic(characteristic);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic
            characteristic, int status) {
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(happeningUUID);
        Log.d("READDESCRIPTOR", descriptor.toString() + " " + new String(descriptor.getValue()));
        gatt.readDescriptor(descriptor);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.d("DESCRIPTORVALUE", new String(descriptor.getValue()));
    }
}
