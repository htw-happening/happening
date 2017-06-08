package blue.happening.service.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import blue.happening.service.MainActivity;

/**
 * BL EDR Scanner
 * is using Reflection! See ACTION_SDP_RECORD
 */
class EdrDeviceFinder implements IDeviceFinder {

    private static final int SCANINTERVALL = 30000;
    private static final int SCANDELAY = 1000;

    public boolean isActive = false;
    private ArrayList<BluetoothDevice> tempDevices = new ArrayList<>();
    private String ACTION_SDP_RECORD;
    private String EXTRA_SDP_SEARCH_RESULT;
    private Layer layer;
    private Context context;
    private Timer timer;
    private TimerTask timerTask;


    public EdrDeviceFinder(){
        context = MainActivity.getContext();
        try {
            Field f = BluetoothDevice.class.getDeclaredField("ACTION_SDP_RECORD");
            ACTION_SDP_RECORD = ((String)f.get(null));
            f = BluetoothDevice.class.getDeclaredField("EXTRA_SDP_SEARCH_STATUS");
            EXTRA_SDP_SEARCH_RESULT = ((String)f.get(null));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        intentFilter.addAction(ACTION_SDP_RECORD);
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

    public void startScan(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();
        }
        Log.d(getClass().getSimpleName(), "Devicefinder startScan");

    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                // Aggregating found devices
                BluetoothDevice bd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                tempDevices.add(bd);
                Log.d(getClass().getSimpleName(), "Devicefinder Found");
            }else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                Log.d(getClass().getSimpleName(), "Devicefinder Discovery Started");
                isActive = true;

                // Prepare for new search
                tempDevices = new ArrayList<>();
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Log.d(getClass().getSimpleName(), "Devicefinder Discovery Finished");

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        EdrDeviceFinder.this.isActive = false;
                    }
                }, 5000);

                // Do a sdpSearch for all found devices
                for (BluetoothDevice bd : tempDevices){
                    try {
                        Method m = bd.getClass().getDeclaredMethod("sdpSearch", ParcelUuid.class);
                        m.invoke(bd, new ParcelUuid(UUID.fromString(Layer.SERVICE_UUID)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else if( ACTION_SDP_RECORD.equals(action)){
                Log.d(getClass().getSimpleName(), "Devicefinder ACTION_SDP_RECORD");
                // check if the device has the specified uuid
                BluetoothDevice bd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (intent.getIntExtra(EXTRA_SDP_SEARCH_RESULT, 1) == 0){
                    layer.addNewScan(bd.getAddress());
                }
            }
        }
    };
}