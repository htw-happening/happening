package com.happening.poc.poc_happening.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.happening.poc.poc_happening.R;

import java.util.ArrayList;

public class DeviceListAdapter extends ArrayAdapter<DeviceModel> implements View.OnClickListener {

    public DeviceListAdapter(Context context, ArrayList<DeviceModel> deviceList) {
        super(context, 0, deviceList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DeviceModel device = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_list_item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.device_name);
        TextView address = (TextView) convertView.findViewById(R.id.device_address);
        TextView payload = (TextView) convertView.findViewById(R.id.device_payload);
        TextView deviceDbm = (TextView) convertView.findViewById(R.id.device_dbm);

        name.setText(device.getName());
        address.setText(device.getAddress());
        payload.setText(device.getPayload());
        deviceDbm.setText(device.getSignalStrength() + " | " + device.getPathloss());

        return convertView;
    }


    @Override
    public void onClick(View v) {}/*
        int position = (int) v.getTag(R.layout.device_list_item);
        final ScanResult result = (ScanResult) deviceList.values().toArray()[position];
        final BluetoothDevice bluetoothDevice = result.getDevice();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothDevice.connectGatt(context, false, mBluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            bluetoothDevice.connectGatt(context, false, mBluetoothGattCallback);
        }

        Log.d("CLICK", result.toString());
    }*/
}
