package blue.happening.service.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class Device implements IRemoteDevice {

    private String TAG = getClass().getSimpleName();
    private boolean d = true;

    private BluetoothDevice bluetoothDevice = null;
    private Connector connector;
    private STATE state;
    public Connection connection;
    private boolean scheduled;
    private int trials = 0;

    enum STATE {
        NEW_SCANNED_DEVICE(1),
        CONNECTING(2),
        CONNECTED(3),
        DISCONNECTED(4),
        SCHEDULED(5),
        OFFLINE(6),
        UNKNOWN(0);

        private final int state;

        public int getState() {
            return state;
        }

        STATE(final int value) {
            this.state = value;
        }
    }

    boolean isScheduled() {
        return scheduled;
    }

    void setSchedule(boolean schedule) {
        this.scheduled = schedule;
        if (schedule){
            changeState(STATE.SCHEDULED);
        }
    }

    int getTrials() {
        return trials;
    }

    void addTrial() {
        this.trials += 1;
    }

    void resetTrials() {
        this.trials = 0;
    }

    int getDelay() {
        return (int) Math.pow(this.trials, 2);
    }

    Device(BluetoothDevice bluetoothDevice) {
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
        if (this.getState() == STATE.CONNECTED && connection != null) {
            connection.write(new Package(bytes));
            return true;
        } else {
            return false;
        }
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
        connection.shutdown();
        this.changeState(STATE.DISCONNECTED);

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
            setName("Connector");
            if (d) Log.d(TAG, "Connector is running: " + Device.this);
            try {
                if (d) Log.i(TAG, "About to wait to connect to " + Device.this);
                socket.connect(); //blocking
            } catch (IOException e) {
                if (d) Log.d(TAG, "connection failed");
                try {
                    socket.close();
                    Device.this.changeState(STATE.OFFLINE);
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                    Device.this.changeState(STATE.UNKNOWN);
                }
                return;
            }
            if (d) Log.i(TAG, "connection done, device:" + Device.this);
            connector = null;
            Device.this.setSchedule(false);
            Layer.getInstance().connectedToServer(socket, Device.this);
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
