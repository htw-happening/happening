package com.happening.poc_happening.bluetooth;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

public class BluetoothGattServerCallback extends android.bluetooth.BluetoothGattServerCallback {

    private String TAG = getClass().getSimpleName();
    private boolean d = true;

    @Override
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        if (d) Log.d(TAG, "BluetoothGattServerCallback - onCharacteristicReadRequest (read: " + new String(characteristic.getValue()) +")");
        if (Layer.getInstance().getBluetoothGattServer() != null)
            Layer.getInstance().getBluetoothGattServer().sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getStringValue(0).getBytes());
        super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
    }

    @Override
    public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {

        if (newState == BluetoothProfile.STATE_CONNECTED) {
            if (d) Log.d(TAG, "BluetoothGattServerCallback - onConnectionStateChange (STATE_CONNECTED)");
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            if (d) Log.d(TAG, "BluetoothGattServerCallback - onConnectionStateChange (STATE_DISCONNECTED)");

        } else {
            if (d) Log.d(TAG, "BluetoothGattServerCallback - onConnectionStateChange (status: " + status + "; newStatus: " + newState + ")");
        }
        super.onConnectionStateChange(device, status, newState);

    }

    @Override
    public void onServiceAdded(int status, BluetoothGattService service) {
        if (d) Log.d(TAG, "BluetoothGattServerCallback - onServiceAdded (status " + status + ")");
        super.onServiceAdded(status, service);
    }

    @Override
    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        String message = new String(value);
        if (d) Log.d(TAG, "BluetoothGattServerCallback - onCharacteristicWriteRequest (preparedWrite " + preparedWrite + "; message " + message + ")");
        super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);

    }

    @Override
    public void onNotificationSent(BluetoothDevice device, int status) {
        if (d) Log.d(TAG, "BluetoothGattServerCallback - onNotificationSent (status " + status + ")");
        super.onNotificationSent(device, status);
    }

    @Override
    public void onMtuChanged(BluetoothDevice device, int mtu) {
        if (d) Log.d(TAG, "BluetoothGattServerCallback - onMtuChanged (mtu " + mtu + ")");
        super.onMtuChanged(device,mtu);
    }

    @Override
    public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
        if (d) Log.d(TAG, "BluetoothGattServerCallback - onDescriptorReadRequest");
        super.onDescriptorReadRequest(device, requestId, offset, descriptor);
    }

    @Override
    public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        if (d) Log.d(TAG, "BluetoothGattServerCallback - onDescriptorWriteRequest");
        super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
    }

    @Override
    public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
        if (d) Log.d(TAG, "BluetoothGattServerCallback - onExecuteWrite");
        super.onExecuteWrite(device, requestId, execute);
    }
}