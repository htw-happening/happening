package com.happening.poc.poc_happening.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServerCallback;
import android.util.Log;

public class HappeningGattServerCallback extends BluetoothGattServerCallback {
    @Override
    public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
        Log.d("SERVERCALLBACK", "Descriptor Read " + new String(descriptor.getValue()));
    }

    @Override
    public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        Log.d("SERVERCALLBACK", "Character Read " + new String(characteristic.getValue()));
    }
}
