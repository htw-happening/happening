package com.happening.poc_happening.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by fabi on 18.04.17.
 */

public class Device {

    public static final int STATE_NEW_SCANNED_DEVICE = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_DISCOVERING = 3;
    public static final int STATE_CONNECTED = 4;
    public static final int STATE_DISCONNECTED = 5;
    public static final int STATE_RECONNECTING = 6;
    public static final int STATE_OFFLINE = 7;

    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic bluetoothGattCharacteristic;

    private int id;
    private int state;

    public Device (BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.state = STATE_NEW_SCANNED_DEVICE;
    }
}
