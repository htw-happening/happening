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

    public enum STATE {
        NEW_SCANNED_DEVICE(1),
        CONNECTING(2),
        DISCOVERING(3),
        CONNECTED(4),
        DISCONNECTED(5),
        RECONNECTING(6),
        OFFLINE(7), // TODO cleanUpMethod
        UNKNOWN(0);

        private final int state;

        public int getValue() {
            return state;
        }

        STATE (final int value){
            this.state = value;
        }
    }

    public static final int DEFAULT_MTU_BYTES = 128;

    private BluetoothDevice bluetoothDevice = null;
    private BluetoothGatt bluetoothGatt = null;
    private BluetoothGattCharacteristic bluetoothGattCharacteristic = null;

    private String userID;
    private STATE state;

    public Device (BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.state = STATE.NEW_SCANNED_DEVICE;
    }

    public boolean hasSameMacAddress(Device other){
        return this.bluetoothDevice.getAddress().equals(other.bluetoothDevice.getAddress());
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public String getUserID() {
        return userID;
    }

    private void changeState (STATE state) {
        if (d) Log.d(TAG, "Change State from "+this.state+" to "+state);
        this.state = state;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            bluetoothGatt = gatt;
            BluetoothDevice bluetoothDevice = gatt.getDevice();
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    if (d) Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange (STATE_CONNECTED)");
                    changeState(STATE.DISCOVERING);
                    boolean mtuSuccess = gatt.requestMtu(DEFAULT_MTU_BYTES); //TODO CHECK if true und so
                    if (d) {
                        Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange - connected and requesting mtu");
                    }
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    if (d) Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange (STATE_DISCONNECTED)");
                    bluetoothGatt.close();
                    changeState(STATE.DISCONNECTED);
                    //stopReader();TODO

                    if (status == 133) {
                        // do not retry connecting - seems to be an old mac address
                        Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange (GATT_FAILURE) --> Do not reconnect!!");
                        changeState(STATE.OFFLINE);

                    }else{
                        delayedConnectDevice(1500, STATE.RECONNECTING);
                    }
                    break;
                default:
                    if (d) Log.e(TAG, "BluetoothGattCallback - onConnectionStateChange (other state " + status + ")");
                    changeState(STATE.UNKNOWN);
                    break;
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            if (d) Log.d(TAG, "BluetoothGattCallback - onMtuChanged (mtu " + mtu + ")");
            boolean discovering = gatt.discoverServices();
            changeState(STATE.DISCOVERING);
            if (d) Log.d(TAG, "BluetoothGattCallback - onMtuChanged - start discovering services (" + discovering + ")");

        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (d) Log.d(TAG, "BluetoothGattCallback - onReadRemoteRssi (rssi " + rssi + ")");
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            switch (status) {
                case BluetoothGatt.GATT_SUCCESS:
                    if (d) Log.d(TAG, "BluetoothGattCallback - onServicesDiscovered (GATT_SUCCESS)");

                    UUID serviceUuid = UUID.fromString(Layer.SERVICE_UUID);
                    UUID characteristicUuid = UUID.fromString(Layer.CHARACTERISTIC_UUID);
                    UUID userinfoUuid = UUID.fromString(Layer.USERINFO_UUID);

                    BluetoothGattService service = gatt.getService(serviceUuid);

                    bluetoothGattCharacteristic = service.getCharacteristic(characteristicUuid);
                    gatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
                    //gatt.readCharacteristic(bluetoothGattCharacteristic);

                    BluetoothGattCharacteristic userinfo = service.getCharacteristic(userinfoUuid);
                    gatt.setCharacteristicNotification(userinfo, true);
                    gatt.readCharacteristic(userinfo);

                    bluetoothGatt = gatt;

                    //startReader();TODO
                    break;
                case BluetoothGatt.GATT_FAILURE:
                    if (d) Log.d(TAG, "BluetoothGattCallback - onServicesDiscovered (GATT_FAILURE)");
                    if (state == STATE.DISCOVERING){
                        delayedConnectDevice(1500, STATE.RECONNECTING);
                    }else {
                        changeState(STATE.OFFLINE);
                    }
                    break;
                case 129:
                    //TODO
                default:
                    if (d) Log.d(TAG, "BluetoothGattCallback - onServicesDiscovered (status " + status + ")");
                    changeState(STATE.UNKNOWN);
                    break;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (d) Log.d(TAG, "BluetoothGattCallback - onCharacteristicChanged (characteristic " + characteristic.getStringValue(0) + ")");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (d) Log.d(TAG, "BluetoothGattCallback - onCharacteristicRead (characteristic " + characteristic.getStringValue(0) + ", status " + status + ")");
            if (state == STATE.DISCOVERING){
                userID = characteristic.getStringValue(0);
                checkConnection();
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (d) Log.d(TAG, "BluetoothGattCallback - onCharacteristicWrite (characteristic " + characteristic.getStringValue(0) + ", status " + status + ")");
        }

    };

    private void checkConnection() {
        Layer layer = Layer.getInstance();
        for (Device device: layer.getConnectedDevices()) {

            if (device.getUserID().equals(this.userID) || hasSameMacAddress(device)) {
                //mergen - mac change
                //- den alten nochmal versuchen zu disconnected
                device.disconnect();
                // raus
                layer.getConnectedDevices().remove(device);
            }
        }
        layer.getConnectedDevices().add(this);

        changeState(STATE.CONNECTED);
    }

    private void disconnect() {
        //TODO
        bluetoothGatt.close();
    }

    public void connectDevice() {
        if (d) Log.d(TAG, "Connecting to Device (" + bluetoothDevice.getAddress() + ")");
        changeState(STATE.CONNECTING);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothGatt = bluetoothDevice.connectGatt(MyApp.getAppContext(), false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            bluetoothGatt = bluetoothDevice.connectGatt(MyApp.getAppContext(), false, mGattCallback);
        }
    }

    public void delayedConnectDevice(int delay, STATE state) {
        changeState(state);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                connectDevice();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, delay);
    }
}
