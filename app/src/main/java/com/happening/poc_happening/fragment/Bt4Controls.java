package com.happening.poc_happening.fragment;

import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.happening.poc_happening.R;
import com.happening.poc_happening.adapter.DeviceListAdapter;
import com.happening.poc_happening.bluetooth.Device;
import com.happening.poc_happening.bluetooth.Layer;

import java.util.ArrayList;

public class Bt4Controls extends Fragment {

    private TextView textView;

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

        textView = (TextView) rootView.findViewById(R.id.textView_info_bt);

        bluetoothLayer = Layer.getInstance();

        ListView deviceListView = (ListView) rootView.findViewById(R.id.discovered_devices_list);
        ArrayList<Device> scanResults = bluetoothLayer.getScannedDevices();
        deviceListAdapter = new DeviceListAdapter(rootView.getContext(), scanResults);
        deviceListView.setAdapter(deviceListAdapter);
        registerForContextMenu(deviceListView);

        bluetoothLayer.addHandler(guiHandler);

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Device device = (Device) parent.getItemAtPosition(position);
                Log.i("CLICK", "Clicked on device " + device.toString());
                //device.connectDevice();
            }
        });

        //region event listener

        Button button = (Button) rootView.findViewById(R.id.button_uptime);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Uptime Status")
                        .setMessage(""+bluetoothLayer.calcUpTime() + "%");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        Switch adapterButton = (Switch) rootView.findViewById(R.id.adapter_button);
        adapterButton.setChecked(bluetoothLayer.isEnabled());
        adapterButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
                if (isChecked) {
                    createGattServer();
                } else {
                    stopGattServer();
                }
            }
        });
        //endregion

        if (!bluetoothLayer.isAdvertisingSupported()) {
            advertiseButton.setEnabled(false);
            gattServerButton.setEnabled(false);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        Log.i("Bt4Controls", "onResume");
        super.onResume();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.discovered_devices_list) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.device_context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = info.position;
        Device device = (Device) deviceListAdapter.getItem(pos);
        switch(item.getItemId()) {
            case R.id.connect:
                Log.i("LONGCLICK", "Clicked on device " + device.toString() + " for Connect!");
                device.connectDevice(Device.STATE.CONNECTING);
                return true;
            case R.id.disconnect:
                Log.i("LONGCLICK", "Clicked on device " + device.toString() + " for Disonnect!");
                device.disconnect();
                return true;
            case R.id.read:
                Log.i("LONGCLICK", "Clicked on device " + device.toString() + " for Read!");
                device.readCharacteristic();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public void onPause() {
        Log.i("Bt4Controls", "onPause");
        super.onPause();
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
        Toast currentToast;

        @Override
        public void handleMessage(Message msg) {
            //make gui
            deviceListAdapter.notifyDataSetChanged();
            textView.setText("Num Connections: "+bluetoothLayer.getNumOfConnectedDevices());
        }
    };
}
