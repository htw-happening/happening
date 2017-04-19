package com.happening.poc_happening.bluetooth;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
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

    }

    @Override
    public void onServiceAdded(int status, BluetoothGattService service) {
        if (d) Log.d(TAG, "BluetoothGattServerCallback - onServiceAdded (status " + status + ")");
    }

    @Override
    public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        String message = new String(value);
        if (d) Log.d(TAG, "BluetoothGattServerCallback - onCharacteristicWriteRequest (preparedWrite " + preparedWrite + "; message " + message + ")");

    }

    @Override
    public void onNotificationSent(BluetoothDevice device, int status) {
        if (d) Log.d(TAG, "BluetoothGattServerCallback - onNotificationSent (status " + status + ")");
    }

    @Override
    public void onMtuChanged(BluetoothDevice device, int mtu) {
        if (d) Log.d(TAG, "BluetoothGattServerCallback - onMtuChanged (mtu " + mtu + ")");

    }
}