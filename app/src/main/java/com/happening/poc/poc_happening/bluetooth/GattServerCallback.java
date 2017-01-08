package com.happening.poc.poc_happening.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.happening.poc.poc_happening.fragment.Bt4Controls;

public class GattServerCallback extends BluetoothGattServerCallback {

    @Override
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        Log.d("CHAR_READ", "character read " + new String(characteristic.getValue()));
        Bt4Controls controls = Bt4Controls.getInstance();
        BluetoothGattServer server = controls.getBluetoothGattServer();
        Log.d("CHAR_READ", "server " + server);
        if (server != null)
            server.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
    }

    @Override
    public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
        Log.d("DESC_READ", "descriptor read " + new String(descriptor.getValue()));
    }

    @Override
    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
        Log.d("CONN_CHANGE", "state changed to " + newState);
    }

    @Override
    public void onServiceAdded(int status, BluetoothGattService service) {
        Log.d("SERVICE_ADD", "service added " + status + " " + service.getUuid().toString());
    }

    @Override
    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        Log.d("CHAR_WRITE", "device: " + device.getAddress() + " preparedWrite: " + preparedWrite + " responseNeeded: " + responseNeeded);
    }

    @Override
    public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        Log.d("DESC_WRITE", "device: " + device.getAddress() + " preparedWrite: " + preparedWrite + " responseNeeded: " + responseNeeded);
    }

    @Override
    public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
        Log.d("EXECUTE", "device: " + device.getAddress() + " execute: " + execute);
    }

    @Override
    public void onNotificationSent(BluetoothDevice device, int status) {
        Log.d("NOTIFICATION", "device: " + device.getAddress() + " status: " + status);
    }

    @Override
    public void onMtuChanged(BluetoothDevice device, int mtu) {
        Log.d("MTU_CHANGE", "device: " + device.getAddress() + " mtu: " + mtu);
    }
}
