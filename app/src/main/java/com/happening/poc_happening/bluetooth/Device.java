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
    private BluetoothGatt bluetoothGatt = null;
    private BluetoothGattCharacteristic bluetoothGattCharacteristic = null;
    private String userID;
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

    public Device (BluetoothDevice bluetoothDevice, String userId) {
        this.bluetoothDevice = bluetoothDevice;
        this.state = STATE.NEW_SCANNED_DEVICE;
        this.userID = userId;
    }

    public String getAddress() {
        return this.bluetoothDevice.getAddress();
    }

    public String getName() {
        if(userID == null){
            return "N/A";
        }else{
            return this.userID;
        }
    }

    public String getState() {
        return state.toString();
    }


    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
    }

    public boolean hasSameMacAddress(Device other){
        return this.bluetoothDevice.getAddress().equals(other.bluetoothDevice.getAddress());
    }

    public boolean hasSameUserId(Device other){
        return this.getUserID().equals(other.getUserID());
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public String getUserID() {
        return userID;
    }

    public void changeState (STATE state) {
        if (d) Log.d(TAG, "Change State from "+this.state+" to "+state + " of "+toString());
        if (state == STATE.CONNECTED){
            startReader();
        }
        if (state != STATE.CONNECTED){
            stopReader();
        }
        this.state = state;
        Layer.getInstance().notifyHandlers(1);
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (d) Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange status: "+status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    if (d) Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange (STATE_CONNECTED) of " + toString());
                    changeState(STATE.DISCOVERING);
                    boolean mtuSuccess = gatt.requestMtu(DEFAULT_MTU_BYTES);
                    if (d) Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange - connected and requesting mtu");
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    if (d) Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange (STATE_DISCONNECTED) of" + toString());
                    if (state == STATE.SHADOW){
                        return;
                    }
                    changeState(STATE.DISCONNECTED);
                    if (status == 133 || status == 129) {
                        // do not retry connecting - seems to be an old mac address
                        Log.d(TAG, "BluetoothGattCallback - onConnectionStateChange (GATT_FAILURE) --> Do not reconnect!! " + toString());
                        changeState(STATE.OFFLINE);

                    }else{
                        delayedConnectDevice(1500, STATE.RECONNECTING);
                    }
                    break;
                default:
                    if (d) Log.e(TAG, "BluetoothGattCallback - onConnectionStateChange (other state " + status + ") "+toString());
                    changeState(STATE.UNKNOWN);
                    break;
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            if (d) Log.d(TAG, "BluetoothGattCallback - onMtuChanged (mtu " + mtu + ") "+toString());
            boolean discovering = gatt.discoverServices();
            changeState(STATE.DISCOVERING);
            if (d) Log.d(TAG, "BluetoothGattCallback - onMtuChanged - start discovering services (" + discovering + ") " +toString());

        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if (d) Log.d(TAG, "BluetoothGattCallback - onReadRemoteRssi (rssi " + rssi + ")");
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (d) Log.d(TAG, "BluetoothGattCallback - onServicesDiscovered status: "+status);
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

//                    BluetoothGattCharacteristic userinfo = service.getCharacteristic(userinfoUuid);
//                    gatt.setCharacteristicNotification(userinfo, true);
//                    gatt.readCharacteristic(userinfo);

                    addConnection();
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
                    break;
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
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (d) Log.d(TAG, "BluetoothGattCallback - onCharacteristicWrite (characteristic " + characteristic.getStringValue(0) + ", status " + status + ")");
        }

    };

    private void addConnection() {
//        Layer layer = Layer.getInstance();
//        layer.getConnectedDevices().add(this);
        changeState(STATE.CONNECTED);

    }

    public void disconnect() {
        //TODO
        if (d) Log.d(TAG, "Disconnect to Device " + toString());
        bluetoothGatt.disconnect();
        //bluetoothGatt.close();
    }

    public void connectDevice(STATE state) {
        if (this.state == STATE.SHADOW) return;
        if (d) Log.d(TAG, "Connecting to Device " + toString());
        changeState(state);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothGatt = bluetoothDevice.connectGatt(MyApp.getAppContext(), false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            bluetoothGatt = bluetoothDevice.connectGatt(MyApp.getAppContext(), false, mGattCallback);
        }
    }

    public void delayedConnectDevice(int delay, final STATE state) {
//        changeState(state);
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

    private void startReader(){
        if (readerTimer != null) return;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //if (d) Log.d(TAG, "Reader Trigger");
                readCharacteristic();

            }
        };
        readerTimer = new Timer();
        readerTimer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    public void readCharacteristic(){
        if (bluetoothGatt == null) return;
        if (bluetoothGattCharacteristic == null) return;
        bluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);
    }

    private void stopReader(){
        if (readerTimer == null){
            return;
        }
        readerTimer.cancel();
        readerTimer = null;
    }
}
