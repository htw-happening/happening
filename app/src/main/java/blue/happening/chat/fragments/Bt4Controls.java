package blue.happening.chat.fragment;

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
import android.widget.Toast;

import blue.happening.chat.R;
import blue.happening.chat.adapter.DeviceListAdapter;
import blue.happening.chat.bluetooth.DeviceModel;
import blue.happening.chat.bluetooth.Layer;

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
        bluetoothLayer.addHandler(guiHandler);

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

        //region event listener
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
        Toast currentToast;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Layer.DEVICE_POOL_UPDATED:
                    deviceListAdapter.notifyDataSetChanged();
                    break;
                case Layer.MESSAGE_RECEIVED:
                    String message = msg.getData().getString("content");
                    Log.i("HANDLER", "Message was " + message);
                    String preview = message.substring(0, Math.min(message.length(), 32));
                    preview = preview.length() == 32 ? preview + " ..." : preview;
                    if (currentToast != null)
                        currentToast.cancel();
                    currentToast = Toast.makeText(getContext(), preview, Toast.LENGTH_SHORT);
                    currentToast.show();
                    break;
                default:
                    Log.i("HANDLER", "Unresolved Message Code");
                    break;
            }
        }
    };
}
