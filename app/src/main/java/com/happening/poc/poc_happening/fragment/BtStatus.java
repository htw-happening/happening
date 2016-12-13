package com.happening.poc.poc_happening.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.happening.poc.poc_happening.R;
import com.happening.poc.poc_happening.TutorialService;

public class BtStatus extends Fragment {

    private static BtStatus instance = null;
    private View rootView = null;
    private BluetoothManager bluetoothManager;
    private WifiP2pManager wifiP2pManager;
    private String availableTxt = "Läuft";
    private String unAvailableTxt = "Läuft Nicht!";

    public BtStatus() {
        super();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        ((Switch) rootView.findViewById(R.id.bluetooth_enabled)).setChecked(false);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        ((Switch) rootView.findViewById(R.id.bluetooth_enabled)).setChecked(true);
                        break;
                }
            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                final int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                switch (state) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        ((Switch) rootView.findViewById(R.id.wifi_enabled)).setChecked(true);
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        ((Switch) rootView.findViewById(R.id.wifi_enabled)).setChecked(false);
                        break;
                }
            }
        }
    };

    public static BtStatus getInstance() {
        instance = new BtStatus();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bt_status, container, false);

        bluetoothManager = (BluetoothManager) rootView.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        String bluetoothAddress = android.provider.Settings.Secure.getString(rootView.getContext().getApplicationContext().getContentResolver(), "bluetooth_address");
        wifiP2pManager = (WifiP2pManager) rootView.getContext().getSystemService(Context.WIFI_P2P_SERVICE);
        WifiManager wifiManager = (WifiManager) rootView.getContext().getSystemService(Context.WIFI_SERVICE);
        boolean hasBLE = rootView.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);

        if (bluetoothAdapter == null) {
            ((TextView) rootView.findViewById(R.id.bluetooth_value)).setText(unAvailableTxt);
        } else {
            ((TextView) rootView.findViewById(R.id.bluetooth_value)).setText(availableTxt);
            ((TextView) rootView.findViewById(R.id.bluetooth_value)).setText(availableTxt);
            ((TextView) rootView.findViewById(R.id.bluetooth_address_value)).setText(bluetoothAddress);
            ((Switch) rootView.findViewById(R.id.bluetooth_enabled)).setChecked(bluetoothAdapter.isEnabled());
            if (bluetoothAdapter.isMultipleAdvertisementSupported()) {
                ((TextView) rootView.findViewById(R.id.bluetooth_le_adv_value)).setText(availableTxt);
            } else {
                ((TextView) rootView.findViewById(R.id.bluetooth_le_adv_value)).setText(unAvailableTxt);
            }
        }

        if (!hasBLE) {
            ((TextView) rootView.findViewById(R.id.bluetooth_le_value)).setText(unAvailableTxt);
        } else {
            ((TextView) rootView.findViewById(R.id.bluetooth_le_value)).setText(availableTxt);
        }

        if (wifiP2pManager == null) {
            ((TextView) rootView.findViewById(R.id.wifi_value)).setText(unAvailableTxt);
        } else {
            ((TextView) rootView.findViewById(R.id.wifi_value)).setText(availableTxt);
            ((Switch) rootView.findViewById(R.id.wifi_enabled)).setChecked(wifiManager.isWifiEnabled());
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        rootView.getContext().registerReceiver(receiver, filter);

//        Intent alarm = new Intent(rootView.getContext(), AlarmReceiver.class);
//        boolean alarmRunning = (PendingIntent.getBroadcast(rootView.getContext(), 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
//        if(alarmRunning == false) {
//            Log.d("start service", "start the service in activity");
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(rootView.getContext(), 0, alarm, 0);
//            AlarmManager alarmManager = (AlarmManager) rootView.getContext().getSystemService(Context.ALARM_SERVICE);
//            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 100, pendingIntent);
//        }
//
//        // Construct an intent that will execute the AlarmReceiver
//        Intent intent = new Intent(rootView.getContext().getApplicationContext(), AlarmReceiver.class);
//        // Create a PendingIntent to be triggered when the alarm goes off
//        final PendingIntent pIntent = PendingIntent.getBroadcast(rootView.getContext(), AlarmReceiver.REQUEST_CODE,
//                intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        // Setup periodic alarm every 5 seconds
//        long firstMillis = System.currentTimeMillis(); // alarm is set right away
//        AlarmManager alarm = (AlarmManager) rootView.getContext().getSystemService(Context.ALARM_SERVICE);
//        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
//        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
//        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
//                AlarmManager.INTERVAL_HALF_HOUR, pIntent);

        rootView.findViewById(R.id.button_start_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startServiceIntent = new Intent(rootView.getContext(), TutorialService.class);
                    rootView.getContext().startService(startServiceIntent);
            }
        });

        rootView.findViewById(R.id.button_stop_service).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.getContext().stopService(new Intent(rootView.getContext(), TutorialService.class));
            }
        });

        return rootView;
    }

//
//    public class AlarmReceiver extends BroadcastReceiver {
//        public static final int REQUEST_CODE = 12345;
//        public static final String ACTION = "com.codepath.example.servicesdemo.alarm";
//
//        public AlarmReceiver() {
//            Log.d(this.getClass().getSimpleName(), "constructor");
//        }
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Intent background = new Intent(context, TutorialService.class);
//            context.startService(background);
//        }
//
//    }

}
