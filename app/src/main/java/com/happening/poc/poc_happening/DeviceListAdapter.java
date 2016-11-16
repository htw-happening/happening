package com.happening.poc.poc_happening;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by kaischulz on 16.11.16.
 */

public class DeviceListAdapter extends BaseAdapter implements View.OnClickListener {
    private LayoutInflater inflater = null;
    private ArrayList<BluetoothDevice> deviceList = null;
    private ViewHolder vh = null;

    private static final class ViewHolder {
        TextView deviceName = null;
    }

    public DeviceListAdapter(Context context, ArrayList<BluetoothDevice> deviceList) {
        this.inflater = LayoutInflater.from(context);
        this.deviceList = deviceList;
    }

    @Override
    public int getCount() {
        return this.deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null) {
            v = inflater.inflate(R.layout.device_list_item, parent, false);

            vh = new ViewHolder();
            v.setTag(vh);

            vh.deviceName = (TextView) v.findViewById(R.id.device_name);

        } else  {
            vh = (ViewHolder) v.getTag();
        }

        vh.deviceName.setOnClickListener(this);
        vh.deviceName.setTag(R.layout.device_list_item, position);
        if(deviceList.get(position).getName() != null) {
            vh.deviceName.setText(deviceList.get(position).getName());
        } else if (deviceList.get(position).getAddress() != null) {
            vh.deviceName.setText(deviceList.get(position).getAddress().toString());
        } else {
            vh.deviceName.setText("no Name");
        }

        return v;
    }

    @Override
    public void onClick(View v) {
        if(v == null){
            Log.d("device list", "error");
        }

        int pos = (int) v.getTag(R.layout.device_list_item);
        Log.d("clicked on", "" + pos);

    }

}
