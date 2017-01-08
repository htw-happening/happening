package com.happening.poc.poc_happening.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.happening.poc.poc_happening.MainActivity;
import com.happening.poc.poc_happening.fragment.Bt4Controls;

import java.util.UUID;

public class GattCallback extends android.bluetooth.BluetoothGattCallback {

    public static final int DEFAULT_MTU_BYTES = 128;

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        switch (newState) {
            case BluetoothProfile.STATE_CONNECTED:
                Log.d("CONN_CHANGE", "state connected");
                boolean mtuSuccess = gatt.requestMtu(DEFAULT_MTU_BYTES);
                Log.d("CONN_CHANGE", "mtu request success " + mtuSuccess);
                // gatt.discoverServices();
                break;
            case BluetoothProfile.STATE_DISCONNECTED:
                Log.d("CONN_CHANGE", "state disconnected");
                gatt.close();
                break;
            default:
                Log.d("CONN_CHANGE", "connection state changed " + newState);
                break;
        }
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        Log.d("MTU_CHANGE", "mtu changed to " + mtu);
        boolean discovering = gatt.discoverServices();
        Log.d("MTU_CHANGE", "discover start success " + discovering);
    }


    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        switch (status) {
            case BluetoothGatt.GATT_SUCCESS:
                Log.d("SERVICE_DISCO", "services discovered");
                UUID serviceUuid = UUID.fromString(Bt4Controls.SERVICE_UUID);
                UUID characteristicUuid = UUID.fromString(Bt4Controls.CHARACTERISTIC_UUID);
                UUID descriptorUuid = UUID.fromString(Bt4Controls.DESCRIPTOR_UUID);

                BluetoothGattService service = gatt.getService(serviceUuid);
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(characteristicUuid);
                Log.d("SERVICE_DISCO", "triggered");

                gatt.setCharacteristicNotification(characteristic, true);
                gatt.readCharacteristic(characteristic);
                // BluetoothGattDescriptor desc = characteristic.getDescriptor(descriptorUuid);

                //boolean desSuccess = gatt.writeDescriptor(desc);
                //Log.d("SERVICE_DISCO", "wrote descriptor with success " + desSuccess);
                break;
            case BluetoothGatt.GATT_FAILURE:
                Log.e("SERVICE_DISCO", "service discovery failed");
                break;
            default:
                Log.e("SERVICE_DISCO", "no service discovered " + status);
                break;
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.d("CHAR_CHANGE", "str-val " + characteristic.getStringValue(0));
        Log.d("CHAR_CHANGE", "get-val " + characteristic.getValue());
        Log.d("CHAR_CHANGE", "get-pro " + characteristic.getProperties());
        Log.d("CHAR_CHANGE", "get-per " + characteristic.getPermissions());
        Log.d("CHAR_CHANGE", "get-wri " + characteristic.getWriteType());

        Message msg = Bt4Controls.getHandler().obtainMessage(42);
        Bundle bundle = new Bundle();
        bundle.putString("content", characteristic.getStringValue(0));
        msg.setData(bundle);
        Bt4Controls.getHandler().sendMessage(msg);
        Log.d("CHAR_CHANGE", "Send data to gui handler");
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.d("CHAR_READ", "str-val " + characteristic.getStringValue(0));
        Log.d("CHAR_READ", "get-val " + characteristic.getValue());
        Log.d("CHAR_READ", "get-pro " + characteristic.getProperties());
        Log.d("CHAR_READ", "get-per " + characteristic.getPermissions());
        Log.d("CHAR_READ", "get-wri " + characteristic.getWriteType());
        Log.d("CHAR_READ", "status be like " + status);
        UUID descriptorUuid = UUID.fromString(Bt4Controls.DESCRIPTOR_UUID);
        //BluetoothGattDescriptor descriptor = characteristic.getDescriptor(descriptorUuid);
        //gatt.readDescriptor(descriptor);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.d("CHAR_WRITE", "str-val " + characteristic.getStringValue(0));
        Log.d("CHAR_WRITE", "get-val " + characteristic.getValue());
        Log.d("CHAR_WRITE", "get-pro " + characteristic.getProperties());
        Log.d("CHAR_WRITE", "get-per " + characteristic.getPermissions());
        Log.d("CHAR_WRITE", "get-wri " + characteristic.getWriteType());
        Log.d("CHAR_WRITE", "status be like " + status);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.d("DESC_READ", "str " + descriptor.toString());
        Log.d("DESC_READ", "val " + new String(descriptor == null ? descriptor.getValue(): "".getBytes()));
        Log.d("DESC_READ", "status be like " + status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.d("DESC_WRITE", "str " + descriptor.toString());
        Log.d("DESC_WRITE", "val " + new String(descriptor == null ? descriptor.getValue(): "".getBytes()));
        Log.d("DESC_WRITE", "status be like " + status);
    }
}
