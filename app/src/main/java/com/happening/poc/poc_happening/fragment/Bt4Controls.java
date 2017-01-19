package com.happening.poc.poc_happening.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.happening.poc.poc_happening.R;
import com.happening.poc.poc_happening.adapter.DeviceListAdapter;
import com.happening.poc.poc_happening.bluetooth.DeviceModel;
import com.happening.poc.poc_happening.bluetooth.Layer;


public class Bt4Controls extends Fragment {

    private static Bt4Controls instance = null;
    private Layer bluetoothLayer = null;
    private View rootView = null;
    private DeviceListAdapter deviceListAdapter = null;

    public static Bt4Controls getInstance() {
        if (instance == null)
            instance = new Bt4Controls();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bt4controls, container, false);

        if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Snackbar.make(rootView, "BLE features are not supported!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        bluetoothLayer = Layer.getInstance();

        ListView deviceListView = (ListView) rootView.findViewById(R.id.discovered_devices_list);
        deviceListAdapter = new DeviceListAdapter(rootView.getContext(), bluetoothLayer.getDevicePool());
        deviceListView.setAdapter(deviceListAdapter);

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeviceModel device = (DeviceModel) parent.getItemAtPosition(position);
                Log.i("CLICK", "Clicked on device " + device.getName());
                if (device.isConnected()) {
                    bluetoothLayer.disconnectDevice(device);
                } else if (device.isDisconnected()) {
                    bluetoothLayer.connectDevice(device);
                } else {
                    Log.i("GATT", "Enhance your calm");
                }
            }
        });

        // set event Listener
        Switch adapterButton = (Switch) rootView.findViewById(R.id.adapter_button);
        adapterButton.setChecked(bluetoothLayer.isEnabled());
        adapterButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("Bt4Controls", "adapterButton - onCheckedChanged " + isChecked);
                if (isChecked) {
                    enableAdapter();
                } else {
                    disableAdapter();
                }
            }
        });

        Switch scanButton = (Switch) rootView.findViewById(R.id.scan_button);
        scanButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("Bt4Controls", "scanButton - onCheckedChanged " + isChecked);
                if (isChecked) {
                    startScan();
                } else {
                    stopScan();
                }
            }
        });

        Switch advertiseButton = (Switch) rootView.findViewById(R.id.advertise_button);
        advertiseButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("Bt4Controls", "advertiseButton - onCheckedChanged " + isChecked);
                if (isChecked) {
                    startAdvertising();
                } else {
                    stopAdvertising();
                }
            }
        });

        Switch gattServerButton = (Switch) rootView.findViewById(R.id.gatt_server_button);
        gattServerButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("Bt4Controls", "gattServerButton - onCheckedChanged " + isChecked);
                if (isChecked) {
                    createGattServer();
                } else {
                    stopGattServer();
                }
            }
        });

        if (!bluetoothLayer.isAdvertisingSupported()) {
            advertiseButton.setEnabled(false);
            gattServerButton.setEnabled(false);
        }

        return rootView;
    }

    @Override
    public void onPause() {
        Log.i("Bt4Controls", "onPause");
        bluetoothLayer.removeHandler(guiHandler);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("Bt4Controls", "onResume");
        super.onResume();
        bluetoothLayer.addHandler(guiHandler);
    }

    private void enableAdapter() {
        Snackbar.make(rootView, "Enable Adapter", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        bluetoothLayer.enableAdapter();
    }

    private void disableAdapter() {
        Snackbar.make(rootView, "Disable Adapter", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        bluetoothLayer.disableAdapter();
    }

    private void startAdvertising() {
        Snackbar.make(rootView, "Start Advertising", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        bluetoothLayer.startAdvertising();
    }

    private void stopAdvertising() {
        Snackbar.make(rootView, "Stop Advertising", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        bluetoothLayer.stopAdvertising();
    }

    private void startScan() {
        Snackbar.make(rootView, "Start Discovering", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        bluetoothLayer.startScan();
    }

    private void stopScan() {
        Snackbar.make(rootView, "Stop Discovering", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        bluetoothLayer.stopScan();
    }

    private void createGattServer() {
        Snackbar.make(rootView, "Start Gatt-Server", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        bluetoothLayer.createGattServer();
    }

    private void stopGattServer() {
        Snackbar.make(rootView, "Stop Gatt-Server", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        bluetoothLayer.stopGattServer();
    }

    private Handler guiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Log.i("HANDLER", "Message received from layer with Code " + msg.what);
            switch (msg.what) {
                case Layer.DEVICE_POOL_UPDATED:
                    deviceListAdapter.notifyDataSetChanged();
                    TextView textViewCount = (TextView) getActivity().findViewById(R.id.ble_connect_count);
                    if (textViewCount != null)
                        textViewCount.setText("Num: " + bluetoothLayer.getNumOfConnectedDevices());
                    break;
                case Layer.MESSAGE_RECEIVED:

                    String message = msg.getData().getString("content");
                    Log.i("HANDLER", "Content was " + message);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                        }
                    });
                    builder.setMessage(message);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
                default:
                    Log.i("HANDLER", "Unresolved Message Code");
                    break;
            }
        }
    };
}
