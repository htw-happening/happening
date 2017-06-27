package blue.happening.service.bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import blue.happening.MyApplication;

public class BluetoothStateReceiver {

    private String TAG = getClass().getSimpleName();
    private boolean d = true;

    BluetoothStateReceiver(){

    }

    void start(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        MyApplication.getAppContext().registerReceiver(broadcastReceiver, intentFilter);
    }

    void stop(){
        MyApplication.getAppContext().unregisterReceiver(broadcastReceiver);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        if (d) Log.d(TAG, "onReceive: STATE_OFF");

                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        if (d) Log.d(TAG, "onReceive: STATE_TURNING_OFF");

                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (d) Log.d(TAG, "onReceive: STATE_ON");

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        if (d) Log.d(TAG, "onReceive: STATE_TURNING_ON");

                        break;
                    default:
                        if (d) Log.e(TAG, "onReceive: Unknown STATE " + state);
                        break;
                }

            }
        }
    };
}
