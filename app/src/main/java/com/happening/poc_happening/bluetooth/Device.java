package com.happening.poc_happening.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.util.Log;

import com.happening.poc_happening.MyApp;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class Device {

    private String TAG = getClass().getSimpleName();
    private boolean d = true;

    public static final int DEFAULT_MTU_BYTES = 128;

    private BluetoothDevice bluetoothDevice = null;
    private int userID;
    private STATE state;
    private Timer readerTimer;

    public enum STATE {
        NEW_SCANNED_DEVICE(1),
        CONNECTING(2),
        DISCOVERING(3),
        CONNECTED(4),
        DISCONNECTED(5),
        RECONNECTING(6),
        OFFLINE(7), // TODO cleanUpMethod
        SHADOW(8),
        UNKNOWN(0);

        private final int state;
        public int getValue() {
            return state;
        }
        STATE (final int value){
            this.state = value;
        }

    }

    public Device (BluetoothDevice bluetoothDevice, int userId) {
        this.bluetoothDevice = bluetoothDevice;
        this.state = STATE.NEW_SCANNED_DEVICE;
        this.userID = userId;
    }

    public String getAddress() {
        return this.bluetoothDevice.getAddress();
    }

    public String getName() {
        if(userID == 0){
            return "N/A";
        }else{
            return String.valueOf(this.userID);
        }
    }

    public String getStateAsString() {
        return state.toString();
    }

    public STATE getState() {
        return state;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public boolean hasSameMacAddress(Device other){
        return this.bluetoothDevice.getAddress().equals(other.bluetoothDevice.getAddress());
    }

    public boolean hasSameUserId(Device other){
        return this.getUserID() == other.getUserID();
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public int getUserID() {
        return userID;
    }

    public void changeState (STATE state) {
        if (d) Log.d(TAG, "Change State from "+this.state+" to "+state + " of "+toString());
        if (state == STATE.CONNECTED){

        }
        if (state != STATE.CONNECTED){

        }
        this.state = state;
        Layer.getInstance().notifyHandlers(1);
    }

    public void connectDevice(STATE state) {
        if (this.state == STATE.SHADOW) return;
        if (d) Log.d(TAG, "Connecting to Device " + toString());
        changeState(state);
        // TODO
    }

    public void delayedConnectDevice(int delay, final STATE state) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                connectDevice(state);
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, delay);
    }

    @Override
    public String toString() {
        String s = "";
        s += getAddress() + " | ";
        s += getUserID() + "";
        return s;
    }
}
