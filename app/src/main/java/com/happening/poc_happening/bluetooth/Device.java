package com.happening.poc_happening.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Device {

    private String TAG = getClass().getSimpleName();
    private boolean d = true;

    public static final int DEFAULT_MTU_BYTES = 128;

    private BluetoothDevice bluetoothDevice = null;
    private Connector connector;
    private int userID;
    private STATE state;
    private Timer readerTimer;

    public void fetchSdpList() {
        bluetoothDevice.fetchUuidsWithSdp();
    }

    public enum STATE {
        NEW_SCANNED_DEVICE(1),
        CONNECTING(2),
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
            return bluetoothDevice.getName();
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

        if(d) Log.d(TAG, "Start Connecting to: " + this);
        try {
            connector = new Connector();
            connector.setName("Connector for: " + this);
            connector.start();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

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


    private class Connector extends Thread {
        private final BluetoothSocket socket;

        public Connector() {
            if (d) Log.d(TAG, "Connector created: " + Device.this);
            BluetoothSocket tmp = null;
            try {
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(Layer.SERVICE_UUID));

            } catch (IOException e) {
                Log.e(TAG, "createRfcommSocketToServiceRecord() failed: " + Device.this, e);
            }
            socket = tmp;
        }

        public synchronized void run() {
            setName("BNA Connector");
            int attempts = 0;
            if (d) Log.d(TAG, "Connector is running: " + Device.this);
            try {
                while(true) {
                    try {
                        attempts++;
                        if (d) Log.i(TAG, "About to wait to connect to " + Device.this);
                        socket.connect(); //blocking
                    } catch (IOException e) {

                        if (d) Log.d(TAG, "connection failed");
                        try {
                            socket.close();
                        } catch (IOException e2) {
                            Log.e(TAG, "unable to close() socket during connection failure", e2);
                        }

                        if (d) Log.d(TAG, "END connector " + Device.this + " FAIL");
                        if (attempts > 4) {return;} else {continue;}

                    }
                    if (d) Log.i(TAG, "connection done, device:" + Device.this);
                    connector = null;
                    Layer.getInstance().connected(socket, Device.this);
                    return;
                }

            } finally {
                if (d) Log.d(TAG, "Connector stopped" + Device.this);
            }
        }

        /**
         * Stopping the Connector.
         */
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "unable to close() socket", e);
            }
        }
    }
}
