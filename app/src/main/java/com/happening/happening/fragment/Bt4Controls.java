package com.happening.happening.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.happening.happening.R;
import com.happening.happening.adapter.DeviceListAdapter;
import com.happening.happening.models.DeviceModel;
import com.happening.sdk.CallbackInterface;
import com.happening.sdk.HappeningClient;

public class Bt4Controls extends Fragment {

    private static Bt4Controls instance = null;
    private DeviceListAdapter deviceListAdapter = null;
    private View rootView = null;

    public Bt4Controls() {
    }

    public static Bt4Controls getInstance() {
        if (instance == null)
            instance = new Bt4Controls();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bt4controls, container, false);

        ListView deviceListView = (ListView) rootView.findViewById(R.id.discovered_devices_list);
        deviceListView.setAdapter(deviceListAdapter);

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DeviceModel device = (DeviceModel) parent.getItemAtPosition(position);
                Log.i("CLICK", "Clicked on device " + device.getName());
//                if (device.isConnected()) {
//                    service.disconnectDevice(device);
//                } else if (device.isDisconnected()) {
//                    service.connectDevice(device);
//                } else {
//                    Log.i("GATT", "Enhance your calm");
//                }
            }
        });

        rootView.findViewById(R.id.button_start_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start scan for devices
                HappeningClient.getHappeningClient().registerOnClientDiscoverCallback(new CallbackInterface() {
                    @Override
                    public void onClientDiscovered(String clientName) {
                        Log.d("callback in bt4", clientName);
                    }
                });
            }
        });

        rootView.findViewById(R.id.button_stop_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // stop scan for devices
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        Log.i("Bt4Controls", "onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        Log.i("Bt4Controls", "onResume");
        super.onResume();
    }
}
