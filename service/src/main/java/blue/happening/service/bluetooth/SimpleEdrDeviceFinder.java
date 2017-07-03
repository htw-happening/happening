package blue.happening.service.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import blue.happening.MyApplication;

class SimpleEdrDeviceFinder implements IDeviceFinder {

    private static final int SCANINTERVALL = 30000;
    private static final int SCANDELAY = 1000;

    private String TAG = getClass().getSimpleName();

    private ArrayList<BluetoothDevice> tempDevices = new ArrayList<>();
    private Layer layer;
    private Context context;
    private Timer timer;
    private TimerTask timerTask;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action){
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    tempDevices = new ArrayList<>();
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED :
                    for (BluetoothDevice tempDevice : tempDevices) {
                        tempDevice.fetchUuidsWithSdp();
                    }
                    break;

                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice bd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    tempDevices.add(bd);
                    Log.d(getClass().getSimpleName(), "Devicefinder Found " + bd.getName());
                    break;

                case BluetoothDevice.ACTION_UUID:
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                    Log.d(TAG, "onReceive: ACTION_UUID " + device.getName());
                    if (uuidExtra == null){
                        Log.d(TAG, "onReceive: Parcelable[] was null");
                        return;
                    }
                    for (Parcelable parcelable : uuidExtra) {
                        Log.d(TAG, "onReceive: Parcable " + parcelable + " " + device.getName());
                        if (parcelable instanceof ParcelUuid){
                            ParcelUuid uuid = (ParcelUuid) parcelable;
                            Log.d(TAG, "onReceive: ParcelUuid " + uuid + " " + device.getName());
                            if (uuid.getUuid().equals(UUID.fromString("cf71e905-0000-0000-0000-000011111111")) || uuid.getUuid().equals(UUID.fromString("11111111-0000-0000-0000-000005e971cf"))){
                                Log.d(TAG, "onReceive: Same as SERVICE_UUID "  + device.getName());
                                layer.addNewScan(device.getAddress());
                            }
                        }
                    }

                    break;
            }
        }
    };

    SimpleEdrDeviceFinder() {
        context = MyApplication.getAppContext();
    }

    @Override
    public void registerCallback(Layer layer) {
        this.layer = layer;
    }

    @Override
    public void start() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        context.registerReceiver(mReceiver, intentFilter);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                startScan();
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, SCANDELAY, SCANINTERVALL);
        Log.d(getClass().getSimpleName(), "Devicefinder created");
    }

    @Override
    public void stop() {
        context.unregisterReceiver(mReceiver);
        timerTask.cancel();
        timer.cancel();
        timer.purge();
    }

    private void startScan() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();
        }
        Log.d(getClass().getSimpleName(), "Devicefinder startScan");

    }
}