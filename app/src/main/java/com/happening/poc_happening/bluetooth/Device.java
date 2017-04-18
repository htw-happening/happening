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

    public static final int STATE_NEW_SCANNED_DEVICE = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_DISCOVERING = 3;
    public static final int STATE_CONNECTED = 4;
    public static final int STATE_DISCONNECTED = 5;
    public static final int STATE_RECONNECTING = 6;
    public static final int STATE_OFFLINE = 7; // TODO cleanUpMethod
    public static final int STATE_UNKNOWN = 0;

    public static final int DEFAULT_MTU_BYTES = 128;

    private BluetoothDevice bluetoothDevice = null;
    private BluetoothGatt bluetoothGatt = null;
    private BluetoothGattCharacteristic bluetoothGattCharacteristic = null;

    private int id;
    private int state;

    public Device (BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.state = STATE_NEW_SCANNED_DEVICE;
    }

    public boolean hasSameMacAddress(Device other){
        return this.bluetoothDevice.getAddress().equals(other.bluetoothDevice.getAddress());
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            bluetoothGatt = gatt;
            BluetoothDevice bluetoothDevice = gatt.getDevice();
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    if (d) Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange (STATE_CONNECTED)");
                    state = STATE_DISCOVERING;
                    boolean mtuSuccess = gatt.requestMtu(DEFAULT_MTU_BYTES); //TODO CHECK if true und so
                    if (d) {
                        Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange - connected and requesting mtu");
                    }
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    if (d) Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange (STATE_DISCONNECTED)");
                    bluetoothGatt.close();
                    state = STATE_DISCONNECTED;
                    //stopReader();TODO

                    if (status == 133) {
                        // do not retry connecting - seems to be an old mac address
                        Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange (GATT_FAILURE) --> Do not reconnect!!");
                        state = STATE_OFFLINE;

                    }else{
                        delayedConnectDevice(1500, STATE_RECONNECTING);
                    }
                    break;
                default:
                    if (d) Log.e(TAG, "BluetoothGattCallback - onConnectionStateChange (other state " + status + ")");
                        state = STATE_UNKNOWN;
                    break;
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            if (d) Log.d(TAG, "BluetoothGattCallback - onMtuChanged (mtu " + mtu + ")");
            boolean discovering = gatt.discoverServices();
            state = STATE_DISCOVERING;
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
                    if (state == STATE_DISCOVERING){
                        delayedConnectDevice(1500, STATE_RECONNECTING);
                    }else {
                        state = STATE_OFFLINE;
                    }
                    break;
                default:
                    if (d) Log.d(TAG, "BluetoothGattCallback - onServicesDiscovered (status " + status + ")");
                    state = STATE_UNKNOWN;
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
            if (state == STATE_DISCOVERING){
                checkConnection(characteristic.getStringValue(0));
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (d) Log.d(TAG, "BluetoothGattCallback - onCharacteristicWrite (characteristic " + characteristic.getStringValue(0) + ", status " + status + ")");
        }

    };

    private void checkConnection(String uniqueID) {

        state = STATE_CONNECTED;
    }

    public void connectDevice() {
        if (d) Log.d(TAG, "Connecting to Device (" + bluetoothDevice.getAddress() + ")");
        this.state = STATE_CONNECTING;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothGatt = bluetoothDevice.connectGatt(MyApp.getAppContext(), false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            bluetoothGatt = bluetoothDevice.connectGatt(MyApp.getAppContext(), false, mGattCallback);
        }
    }

    public void delayedConnectDevice(int delay, int state) {
        this.state = state;
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
