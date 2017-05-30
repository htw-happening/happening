package blue.happening.service.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Device implements IRemoteDevice{

    private String TAG = getClass().getSimpleName();
    private boolean d = true;

    private BluetoothDevice bluetoothDevice = null;
    private Connector connector;
    private STATE state;
    public Connection connection;

    public enum STATE {
        NEW_SCANNED_DEVICE(1),
        CONNECTING(5),
        CONNECTED(6),
        DISCONNECTED(7),
        OFFLINE(8),
        UNKNOWN(0);

        private final int state;

        public int getState() {
            return state;
        }

        STATE(final int value) {
            this.state = value;
        }

    }
    public Device(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.state = STATE.NEW_SCANNED_DEVICE;
    }

    public String getAddress() {
        return this.bluetoothDevice.getAddress();
    }

    public String getName() {
        return bluetoothDevice.getName();
    }

    public String getStateAsString() {
        return state.toString();
    }

    public STATE getState() {
        return state;
    }

    public boolean hasSameMacAddress(Device other) {
        return this.bluetoothDevice.getAddress().equals(other.bluetoothDevice.getAddress());
    }

    public void changeState(STATE state) {
        if (d) Log.d(TAG, "Change State from " + this.state + " to " + state + " of " + this);
        this.state = state;
        Layer.getInstance().notifyHandlers(1);
    }

    @Override
    public boolean send(byte[] bytes) {
        if (this.getState() == STATE.CONNECTED && connection != null){
            connection.write(new Package(bytes));
            return true;
        }else{
            return false;
        }
    }

    public void delayedConnectDevice(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                connectDevice();
            }
        }, 1000);
    }

    public void connectDevice() {
        if (d) Log.d(TAG, "Connecting to Device " + toString());
        changeState(STATE.CONNECTING);

        if (d) Log.d(TAG, "Start Connecting to: " + this);
        try {
            connector = new Connector();
            connector.setName("Connector for: " + this);
            connector.start();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }

    public void disconnect() {
        // TODO: 16.05.17 handle disconnecting process

    }

    @Override
    public String toString() {
        String s = "";
        s += getName() + " | ";
        s += getAddress();
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
            while (!isInterrupted()) {
                try {
                    attempts++;
                    if (d) Log.i(TAG, "About to wait to connect to " + Device.this);
                    socket.connect(); //blocking
                } catch (IOException e) {
                    if (d) Log.d(TAG, "connection failed");
                    try {
                        socket.close();
                        Device.this.changeState(STATE.UNKNOWN);
                    } catch (IOException e2) {
                        Log.e(TAG, "unable to close() socket during connection failure", e2);
                        Device.this.changeState(STATE.UNKNOWN);
                    }

                    if (d) Log.d(TAG, "connector to " + Device.this + " failed "+attempts+" times");
                    if (attempts > 4) {
                        Device.this.changeState(STATE.UNKNOWN);
                        return;
                    } else {
                        continue;
                    }

                }
                if (d) Log.i(TAG, "connection done, device:" + Device.this);
                connector = null;
                Layer.getInstance().connectedToServer(socket, Device.this);
                return;
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
            connector.interrupt();
        }
    }
}
