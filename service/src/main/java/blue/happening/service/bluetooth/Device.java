package blue.happening.service.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import blue.happening.mesh.Message;
import blue.happening.mesh.RemoteDevice;

public class Device extends RemoteDevice {

    private String TAG = getClass().getSimpleName();
    private boolean d = true;

    private static final int[] DELAYS = {0, 1, 2, 3, 5, 10, 15, 30, 60, 90};
    private BluetoothDevice bluetoothDevice = null;
    private Connector connector;
    private STATE state;
    public Connection connection;

    private int trials = 0;

    public enum STATE {
        NEW_SCANNED_DEVICE,
        SCHEDULED,
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
        OFFLINE,
        UNKNOWN
    }

    Device(BluetoothDevice bluetoothDevice) {
        super(bluetoothDevice.getAddress());
        this.bluetoothDevice = bluetoothDevice;
        this.state = STATE.NEW_SCANNED_DEVICE;
    }

    int getTrials() {
        return trials;
    }

    void addTrial() {
        trials += 1;
    }

    void resetTrials() {
        trials = 0;
    }

    int getDelay() {
        try {
            return DELAYS[trials];
        }catch (IndexOutOfBoundsException e){
            return DELAYS[DELAYS.length-1];
        }
    }

    public String getAddress() {
        return this.bluetoothDevice.getAddress();
    }

    public String getName() {
        return bluetoothDevice.getName();
    }

    void changeState(STATE state) {
        if (d) Log.d(TAG, "Change State from " + this.state + " to " + state + " of " + this);
        this.state = state;
        Layer.getInstance().notifyHandlers(1);
    }

    public String getStateAsString() {
        return state.toString();
    }

    public STATE getState() {
        return state;
    }

    boolean hasSameMacAddress(Device other) {
        return this.bluetoothDevice.getAddress().equals(other.bluetoothDevice.getAddress());
    }

    @Override
    public boolean sendMessage(Message message) {
        byte[] bytes = message.toBytes();
        if (this.getState() == STATE.CONNECTED && connection != null) {
            connection.write(new Package(bytes));
            return true;
        } else {
            return false;
        }
    }

    public void connect() {
        if (d) Log.d(TAG, "Connecting to Device " + toString());
        if (getState() == STATE.CONNECTED) return;
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
        if (this.connector != null){
            this.connector.cancel();
        }
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

        Connector() {
            if (d) Log.d(TAG, "Connector created for " + Device.this);
            BluetoothSocket bluetoothSocket = null;
            try {
                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(Layer.SERVICE_UUID));

            } catch (IOException e) {
                Log.e(TAG, "createRfcommSocketToServiceRecord() failed: " + Device.this, e);
            }
            socket = bluetoothSocket;
        }

        public synchronized void run() {
            if (d) Log.d(TAG, "Connector is running for " + Device.this);
            try {
                if (d) Log.i(TAG, "Connecting to Device (Blocking Call) " + Device.this);
                socket.connect(); //blocking
            } catch (IOException e) {
                if (d) Log.d(TAG, "Connecting Failed for Device "+Device.this);
                try {
                    socket.close();
                    Device.this.changeState(STATE.OFFLINE);
                } catch (IOException ee) {
                    Log.e(TAG, "Something went wrong during Socket Close! " +Device.this, ee);
                    Device.this.changeState(STATE.UNKNOWN);
                }
                return;
            }
            if (d) Log.i(TAG, "Connecting successfully for Device: " + Device.this);
            connector = null;
            Layer.getInstance().connectedToServer(socket, Device.this);
        }

        void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Something went wrong during Socket Close! " + Device.this, e);
            }
            connector.interrupt();
        }
    }
}
