package blue.happening.service.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import blue.happening.service.R;
import blue.happening.service.adapter.DeviceListAdapter;
import blue.happening.service.bt4.Bt4Layer;

public class Bt4Controls extends Fragment {

    private static Bt4Controls instance = null;
    private Bt4Layer bluetooth4Layer = null;
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

        this.bluetooth4Layer = Bt4Layer.getInstance();

//        if (!getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Snackbar.make(rootView, "BLE features are not supported!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//        }

        bluetooth4Layer.addHandler(guiHandler);

        ListView deviceListView = (ListView) rootView.findViewById(R.id.discovered_devices_list);
        deviceListAdapter = new DeviceListAdapter(rootView.getContext(), bluetooth4Layer.getScannedDevices());
        deviceListView.setAdapter(deviceListAdapter);

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                DeviceModel device = (DeviceModel) parent.getItemAtPosition(position);
//                Log.i("CLICK", "Clicked on device " + device.getName());
//                if (device.isConnected()) {
//                    bluetoothLayer.disconnectDevice(device);
//                } else if (device.isDisconnected()) {
//                    bluetoothLayer.connectDevice(device);
//                } else {
//                    Log.i("GATT", "Enhance your calm");
//                }
            }
        });

        //region event listener
        Switch adapterButton = (Switch) rootView.findViewById(R.id.adapter_button);
        adapterButton.setChecked(bluetooth4Layer.isEnabled());
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

//        if (!bluetooth4Layer.isAdvertisingSupported()) {
//            advertiseButton.setEnabled(false);
//            gattServerButton.setEnabled(false);
//        }
        return rootView;
    }

    @Override
    public void onPause() {
        Log.i("Bt4Controls", "onPause");
//        bluetooth4Layer.removeHandler(guiHandler);
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("Bt4Controls", "onResume");
        super.onResume();
//        bluetooth4Layer.addHandler(guiHandler);
    }

    private void enableAdapter() {
        Log.d(this.getClass().getSimpleName(), "Enable BT4Adapter");
        bluetooth4Layer.enableAdapter();
    }

    private void disableAdapter() {
        Log.d(this.getClass().getSimpleName(), "Disable BT4Adapter");
        bluetooth4Layer.disableAdapter();
    }

    private void startScan() {
        Log.d(this.getClass().getSimpleName(), "Start Discovering BT4Adapter");
        bluetooth4Layer.startScan();
    }

    private void stopScan() {
        Log.d(this.getClass().getSimpleName(), "Stop Discovering BT4Adapter");
        bluetooth4Layer.stopScan();
    }

    private void createGattServer() {
//        Snackbar.make(rootView, "Start Gatt-Server", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//        bluetooth4Layer.createGattServer();
    }

    private void stopGattServer() {
//        Snackbar.make(rootView, "Stop Gatt-Server", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//        bluetoothLayer.stopGattServer();
    }

    private Handler guiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            deviceListAdapter.notifyDataSetChanged();
        }
    };
}

