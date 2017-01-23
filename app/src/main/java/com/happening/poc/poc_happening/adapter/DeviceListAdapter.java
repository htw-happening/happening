package com.happening.poc.poc_happening.adapter;

import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.happening.poc.poc_happening.R;
import com.happening.poc.poc_happening.bluetooth.DeviceModel;

import java.util.ArrayList;

public class DeviceListAdapter extends ArrayAdapter<DeviceModel> {

    public DeviceListAdapter(Context context, ArrayList<DeviceModel> deviceList) {
        super(context, 0, deviceList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final DeviceModel device = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.discovered_device, parent, false);
        }

        TextView address = (TextView) convertView.findViewById(R.id.device_address);
        TextView name = (TextView) convertView.findViewById(R.id.device_name);

        address.setText(device.getAddress());
        name.setText(device.getName());

        TypedValue color = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorAccent, color, true);
        int colorAccent = color.data;
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, color, true);
        int colorPrimary = color.data;
        getContext().getTheme().resolveAttribute(R.attr.colorPrimaryDark, color, true);
        int colorIdle = color.data;
        switch (device.getCurrentState()) {
            case BluetoothProfile.STATE_CONNECTED:
                address.setTextColor(colorPrimary);
            case BluetoothProfile.STATE_DISCONNECTED:
                address.setTextColor(colorAccent);
            default:
                address.setTextColor(colorIdle);
        }

        return convertView;
    }
}
