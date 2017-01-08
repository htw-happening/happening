package com.happening.poc.poc_happening.fragment;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.happening.poc.poc_happening.MainActivity;
import com.happening.poc.poc_happening.R;
import com.happening.poc.poc_happening.service.Bluetooth4Service;

public class BtStatus extends Fragment {

    private static BtStatus instance = null;
    private View rootView = null;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        ((Switch) rootView.findViewById(R.id.switch_bluetooth_enabled)).setChecked(false);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        ((Switch) rootView.findViewById(R.id.switch_bluetooth_enabled)).setChecked(true);
                        break;
                }
            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                final int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                switch (state) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        ((Switch) rootView.findViewById(R.id.switch_wifi_enabled)).setChecked(true);
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        ((Switch) rootView.findViewById(R.id.switch_wifi_enabled)).setChecked(false);
                        break;
                }
            }
        }
    };
    private BluetoothManager bluetoothManager;
    private WifiP2pManager wifiP2pManager;
    private String availableTxt = "Läuft";
    private String unAvailableTxt = "Läuft Nicht!";

    private Intent bt4BackgroundService = null;
    private Bluetooth4Service mService = null;
    private boolean mBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(this.getClass().getSimpleName(), "onServiceConnected");
            // Because we have bound to an explicit
            // service that is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            Bluetooth4Service.LocalBinder binder = (Bluetooth4Service.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            Log.d(this.getClass().getSimpleName(), "service loaded " + mService.toString());
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.d(this.getClass().getSimpleName(), "onServiceDisconnected");
            mBound = false;
        }
    };

    public BtStatus() {
        super();
    }

    public static BtStatus getInstance() {
        instance = new BtStatus();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bt_status, container, false);

        bluetoothManager = (BluetoothManager) rootView.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        String bluetoothAddress = Settings.Secure.getString(rootView.getContext().getApplicationContext().getContentResolver(), "bluetooth_address");
        wifiP2pManager = (WifiP2pManager) rootView.getContext().getSystemService(Context.WIFI_P2P_SERVICE);
        WifiManager wifiManager = (WifiManager) rootView.getContext().getSystemService(Context.WIFI_SERVICE);
        boolean hasBLE = rootView.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);

        if (bluetoothAdapter == null) {
            ((TextView) rootView.findViewById(R.id.bluetooth_value)).setText(unAvailableTxt);
        } else {
            ((TextView) rootView.findViewById(R.id.bluetooth_value)).setText(availableTxt);
            ((TextView) rootView.findViewById(R.id.bluetooth_value)).setText(availableTxt);
            ((TextView) rootView.findViewById(R.id.bluetooth_address_value)).setText(bluetoothAddress);
            ((Switch) rootView.findViewById(R.id.switch_bluetooth_enabled)).setChecked(bluetoothAdapter.isEnabled());
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
            ((Switch) rootView.findViewById(R.id.switch_wifi_enabled)).setChecked(wifiManager.isWifiEnabled());
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        rootView.getContext().registerReceiver(receiver, filter);

        // Bluetooth Background Service Switch
        Switch bt4ServiceSwitch = (Switch) rootView.findViewById(R.id.switch_background_service);
        bt4ServiceSwitch.setChecked(isMyServiceRunning(Bluetooth4Service.class));
        if (isMyServiceRunning(Bluetooth4Service.class)) {
            ((TextView) rootView.findViewById(R.id.background_service_value)).setText(availableTxt);
        } else {
            ((TextView) rootView.findViewById(R.id.background_service_value)).setText(unAvailableTxt);
        }

        bt4ServiceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bt4BackgroundService == null) {
                    startBt4Service();
                    ((Switch) rootView.findViewById(R.id.switch_background_service)).setChecked(true);
                    ((TextView) rootView.findViewById(R.id.background_service_value)).setText(availableTxt);
                } else if (bt4BackgroundService != null) {
                    stopBt4Service();
                    ((Switch) rootView.findViewById(R.id.switch_background_service)).setChecked(false);
                    ((TextView) rootView.findViewById(R.id.background_service_value)).setText(unAvailableTxt);
                    bt4BackgroundService = null;
                }
                rootView.findViewById(R.id.button_notify).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doNotification();
                    }
                });


            }
        });

        return rootView;
    }

    private void doNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getContext())
                        .setSmallIcon(R.drawable.side_nav_bar)
                        .setContentTitle("Happening")
                        .setContentText("Hi, ich bin in deiner Notification Bar zu sehen. Muahaaahaaaaa")
                        .setAutoCancel(true)
                        .setPriority(2)
                        .setVibrate(new long[]{1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2});
        Intent resultIntent = new Intent(this.getContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(rootView.getContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) rootView.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(47474747, mBuilder.build());
    }

    private void startBt4Service() {
        Log.d(this.getClass().getSimpleName(), "start service in activity");
        bt4BackgroundService = new Intent(this.getContext(), Bluetooth4Service.class);
//        rootView.getContext().bindService(bt4BackgroundService, mConnection, Context.BIND_AUTO_CREATE);
        rootView.getContext().startService(bt4BackgroundService);
    }

    private void stopBt4Service() {
        Log.d(this.getClass().getSimpleName(), "stop service in activity");
        rootView.getContext().stopService(bt4BackgroundService);
        bt4BackgroundService = null;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (receiver != null) {
            rootView.getContext().unregisterReceiver(receiver);
        }

        if (mBound) {
            rootView.getContext().unbindService(mConnection);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) rootView.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
