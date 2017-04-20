package com.happening.poc_happening.fragment;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.happening.poc_happening.R;
import com.happening.poc_happening.service.ServiceHandler;

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
            }
        }
    };
    private ServiceHandler sh = null;
    private BluetoothManager bluetoothManager = null;
    private String availableTxt = "yes";
    private String unAvailableTxt = "no";

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

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        rootView.getContext().registerReceiver(receiver, filter);

        // Bluetooth Background Service Switch
        sh = ServiceHandler.getInstance();

        Switch bt4ServiceSwitch = (Switch) rootView.findViewById(R.id.switch_background_service);
        bt4ServiceSwitch.setChecked(sh.isRunning());

        if (sh.isRunning()) {
            ((TextView) rootView.findViewById(R.id.background_service_value)).setText(availableTxt);
        } else {
            ((TextView) rootView.findViewById(R.id.background_service_value)).setText(unAvailableTxt);
        }

        bt4ServiceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sh.isRunning()) {
                    sh.startService();
                    ((Switch) rootView.findViewById(R.id.switch_background_service)).setChecked(true);
                    ((TextView) rootView.findViewById(R.id.background_service_value)).setText(availableTxt);
                } else if (sh.isRunning()) {
                    sh.stopService();
                    ((Switch) rootView.findViewById(R.id.switch_background_service)).setChecked(false);
                    ((TextView) rootView.findViewById(R.id.background_service_value)).setText(unAvailableTxt);
                }

            }
        });

        (rootView.findViewById(R.id.button_get_data_from_service)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sh.getService().addDevice("jojo " + System.currentTimeMillis());
                    sh.getService().addDevice("jojo");

                    Log.d("device jojo in main", "" + sh.getService().getDevice("jojo"));
                    Log.d("devices in main", "" + sh.getService().getDevices());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (receiver != null) {
            try {
                rootView.getContext().unregisterReceiver(receiver);
            } catch (IllegalArgumentException illegalArgumentException) {
                illegalArgumentException.printStackTrace();
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) rootView.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.d(this.getClass().getSimpleName(), "Background Service " + service);
                return true;
            }
        }
        return false;
    }
}
