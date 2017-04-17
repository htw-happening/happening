package com.happening.happening.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.happening.happening.R;
import com.happening.sdk.HappeningClient;

public class BtStatus extends Fragment {

    private static BtStatus instance = null;
    private View rootView = null;
    private BluetoothManager bluetoothManager = null;
    private String availableTxt = "Läuft";
    private String unAvailableTxt = "Läuft Nicht!";
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        ((TextView) rootView.findViewById(R.id.bluetooth_value)).setText(unAvailableTxt);
                        ((TextView) rootView.findViewById(R.id.bluetooth_le_value)).setText(unAvailableTxt);
                        ((TextView) rootView.findViewById(R.id.bluetooth_le_adv_value)).setText(unAvailableTxt);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        ((TextView) rootView.findViewById(R.id.bluetooth_value)).setText(availableTxt);
                        ((TextView) rootView.findViewById(R.id.bluetooth_le_value)).setText(availableTxt);
                        ((TextView) rootView.findViewById(R.id.bluetooth_le_adv_value)).setText(availableTxt);
                        break;
                }
            }
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

        if (bluetoothAdapter == null) {
            ((TextView) rootView.findViewById(R.id.bluetooth_value)).setText(unAvailableTxt);
            ((TextView) rootView.findViewById(R.id.bluetooth_le_value)).setText(unAvailableTxt);
            ((TextView) rootView.findViewById(R.id.bluetooth_le_adv_value)).setText(unAvailableTxt);
        } else {
            ((TextView) rootView.findViewById(R.id.bluetooth_value)).setText(availableTxt);
            ((TextView) rootView.findViewById(R.id.bluetooth_value)).setText(availableTxt);

            String bluetoothAddress = Settings.Secure.getString(rootView.getContext().getApplicationContext().getContentResolver(), "bluetooth_address");
            ((TextView) rootView.findViewById(R.id.bluetooth_address_value)).setText(bluetoothAddress);

            if (bluetoothAdapter.isMultipleAdvertisementSupported()) {
                ((TextView) rootView.findViewById(R.id.bluetooth_le_adv_value)).setText(availableTxt);
            } else {
                ((TextView) rootView.findViewById(R.id.bluetooth_le_adv_value)).setText(unAvailableTxt);
            }
        }

        boolean hasBLE = rootView.getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        if (!hasBLE) {
            ((TextView) rootView.findViewById(R.id.bluetooth_le_value)).setText(unAvailableTxt);
        } else {
            ((TextView) rootView.findViewById(R.id.bluetooth_le_value)).setText(availableTxt);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        rootView.getContext().registerReceiver(receiver, filter);

        // Bluetooth Background Service Switch
        Switch bt4ServiceSwitch = (Switch) rootView.findViewById(R.id.switch_background_service);
        bt4ServiceSwitch.setChecked(HappeningClient.getHappeningClient().isRunning());

        if (HappeningClient.getHappeningClient().isRunning()) {
            ((TextView) rootView.findViewById(R.id.background_service_value)).setText(availableTxt);
        } else {
            ((TextView) rootView.findViewById(R.id.background_service_value)).setText(unAvailableTxt);
        }

        bt4ServiceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HappeningClient.getHappeningClient().isRunning()) {
                    HappeningClient.getHappeningClient().startService();
                    ((Switch) rootView.findViewById(R.id.switch_background_service)).setChecked(true);
                    ((TextView) rootView.findViewById(R.id.background_service_value)).setText(availableTxt);
                } else if (HappeningClient.getHappeningClient().isRunning()) {
                    HappeningClient.getHappeningClient().stopService();
                    ((Switch) rootView.findViewById(R.id.switch_background_service)).setChecked(false);
                    ((TextView) rootView.findViewById(R.id.background_service_value)).setText(unAvailableTxt);
                }

            }
        });

        (rootView.findViewById(R.id.button_get_data_from_service)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
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

}
