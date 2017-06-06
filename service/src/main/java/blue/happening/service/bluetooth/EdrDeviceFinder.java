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

/**
 * BL EDR Scanner
 * is using Reflection! See ACTION_SDP_RECORD
 */
class EdrDeviceFinder {

    interface Callback{
        void onDeviceFound(BluetoothDevice bd);
        void onFinishedCallback();
        void onStartCallback();
    }

    public boolean isActive = false;
    private ArrayList<BluetoothDevice> tempDevices = new ArrayList<>();
    private Callback mCallback;
    private Context mContext;
    private String ACTION_SDP_RECORD;
    private String EXTRA_SDP_SEARCH_RESULT;

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
                mCallback.onStartCallback();
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
                mCallback.onFinishedCallback();
            }else if( ACTION_SDP_RECORD.equals(action)){
                Log.d(getClass().getSimpleName(), "Devicefinder ACTION_SDP_RECORD");
                // check if the device has the specified uuid
                BluetoothDevice bd = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (intent.getIntExtra(EXTRA_SDP_SEARCH_RESULT, 1) == 0){
                    mCallback.onDeviceFound(bd);
                }
            }
        }
    };


    public EdrDeviceFinder(Context context, Callback mCallback){
        this.mCallback = mCallback;
        this.mContext = context;

        try {
            Field f = BluetoothDevice.class.getDeclaredField("ACTION_SDP_RECORD");
            ACTION_SDP_RECORD = ((String)f.get(null));
            f = BluetoothDevice.class.getDeclaredField("EXTRA_SDP_SEARCH_STATUS");
            EXTRA_SDP_SEARCH_RESULT = ((String)f.get(null));
        } catch (Exception e) {
            e.printStackTrace();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(ACTION_SDP_RECORD);
        context.registerReceiver(mReceiver, intentFilter);
        Log.d(getClass().getSimpleName(), "Devicefinder created");
    }

    public void startScan(){

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.startDiscovery();
        }
        Log.d(getClass().getSimpleName(), "Devicefinder startScan");

    }

    public void unregisterReciever(){
        mContext.unregisterReceiver(mReceiver);
    }
}