package com.happening.poc_happening.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
            }
        });


        Switch adapterButton = (Switch) rootView.findViewById(R.id.switch_bluetooth_adapter);
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

        Switch scanButton = (Switch) rootView.findViewById(R.id.swtich_scanner);
        scanButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startScan();
                } else {
                    stopScan();
                }
            }
        });

        Switch serverButton = (Switch) rootView.findViewById(R.id.switch_server);
        serverButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startServer();
                } else {
                    stopServer();
                }
            }
        });


        TextView userInfo = (TextView) rootView.findViewById(R.id.textView_info_user_id);
        userInfo.setText("    "+String.valueOf(bluetoothLayer.getUserID()));

        Intent makeMeVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        makeMeVisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0); //infinity
        startActivity(makeMeVisible);

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
                device.connectDevice();
                return true;
            case R.id.disconnect:
                Log.i("LONGCLICK", "Clicked on device " + device.toString() + " for Disonnect!");
                device.disconnect();
                return true;
            case R.id.read:
                Log.i("LONGCLICK", "Clicked on device " + device.toString() + " for Read!");
                //TODO device.readCharacteristic();
                return true;
            case R.id.fetch_sdp_list:
                Log.i("LONGCLICK", "Clicked on device " + device.toString() + " for Fetching SDP List");
                device.fetchSdpList();
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

    private void startScan() {
        Snackbar.make(rootView, "Start Discovering", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        bluetoothLayer.startScan();
    }

    private void stopScan() {
        Snackbar.make(rootView, "Stop Discovering", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        bluetoothLayer.stopScan();
    }

    private void startServer() {
        Snackbar.make(rootView, "Start Server", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        bluetoothLayer.createAcceptor();
    }

    private void stopServer() {
        Snackbar.make(rootView, "Stop Server", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        bluetoothLayer.stopAcceptor();
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
