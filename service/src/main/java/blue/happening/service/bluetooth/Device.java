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
    private ArrayList<UUID> fetchedUuids = new ArrayList<>();
    private Connector connector;
    private int userID;
    private STATE state;
    public Connection connection;

    public enum STATE {
        NEW_SCANNED_DEVICE(1),
        FETCHING(2),
        FETCHED(3), // -> This means ready to connect!
        IGNORE(4),
        CONNECTING(5),
        CONNECTED(6),
        DISCONNECTED(7),
        OFFLINE(8),
        FETCHING_FAILED(9),
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

    public void fetchSdpList() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                changeState(STATE.FETCHING);
                if (bluetoothDevice.fetchUuidsWithSdp()) {
                    if (d) Log.d(TAG, "Fetching UUIDS");
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 1000);

    }

    public void addFetchedUuid(UUID uuid) {
        fetchedUuids.add(uuid);
    }

    public ArrayList<UUID> getFetchedUuids() {
        return fetchedUuids;
    }

    public String getAddress() {
        return this.bluetoothDevice.getAddress();
    }

    public String getName() {
        if (userID == 0) {
            return bluetoothDevice.getName();
        } else {
            return String.valueOf(this.userID);
        }
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

    public boolean hasSameUserId(Device other) {
        return this.getUserID() == other.getUserID();
    }

    public int getUserID() {
        return userID;
    }

    public void changeState(STATE state) {
        if (d) Log.d(TAG, "Change State from " + this.state + " to " + state + " of " + this);
        this.state = state;
        Layer.getInstance().notifyHandlers(1);
    }

    @Override
    public boolean send(Package aPackage) {
        if (this.getState() == STATE.CONNECTED && connection != null){
            connection.write(aPackage);
            return true;
        }else{
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
        // TODO: 16.05.17 handle disconnecting process

    }

    @Override
    public String toString() {
        String s = "";
        s += getName() + " | ";
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
            connector.interrupt();
        }
    }
}
