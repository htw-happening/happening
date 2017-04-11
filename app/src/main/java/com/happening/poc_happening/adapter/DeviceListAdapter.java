package com.happening.poc_happening.adapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.happening.poc_happening.R;

import java.util.ArrayList;

public class DeviceListAdapter extends ArrayAdapter<ScanResult> {

    public DeviceListAdapter(Context context, ArrayList<ScanResult> deviceList) {
        super(context, 0, deviceList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final BluetoothDevice device = getItem(position).getDevice();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.discovered_device, parent, false);
        }

        TextView address = (TextView) convertView.findViewById(R.id.device_address);
        TextView name = (TextView) convertView.findViewById(R.id.device_name);

        address.setText(device.getAddress());
        name.setText(device.getName());

        return convertView;
    }
}
