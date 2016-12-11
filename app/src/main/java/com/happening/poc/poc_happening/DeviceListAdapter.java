package com.happening.poc.poc_happening;

import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;


public class DeviceListAdapter extends BaseAdapter implements View.OnClickListener {

    public HashMap<String, ScanResult> deviceList = null;
    private LayoutInflater inflater = null;
    private ViewHolder vh = null;

    private static final class ViewHolder {
        TextView deviceName = null;
        TextView deviceAddress = null;
        TextView devicePayload = null;
        TextView deviceDbm = null;
    }

    public DeviceListAdapter(Context context, HashMap<String, ScanResult> deviceList) {
        this.inflater = LayoutInflater.from(context);
        this.deviceList = deviceList;
    }

    @Override
    public int getCount() {
        return this.deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.deviceList.values().toArray()[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = inflater.inflate(R.layout.device_list_item, parent, false);
            vh = new ViewHolder();
            v.setTag(vh);
            vh.deviceName = (TextView) v.findViewById(R.id.device_name);
            vh.deviceAddress = (TextView) v.findViewById(R.id.device_address);
            vh.devicePayload = (TextView) v.findViewById(R.id.device_payload);
            vh.deviceDbm = (TextView) v.findViewById(R.id.device_dbm);
        } else {
            vh = (ViewHolder) v.getTag();
        }

        vh.deviceName.setOnClickListener(this);
        vh.deviceName.setTag(R.layout.device_list_item, position);
        ScanResult result = (ScanResult) deviceList.values().toArray()[position];

        if (result.getDevice().getAddress() != null) {
            vh.deviceAddress.setText(result.getDevice().getAddress());
        }

        if (result.getDevice().getName() != null) {
            vh.deviceName.setText(result.getDevice().getName());
        }

        if (result.getRssi() != 0) {
            vh.deviceDbm.setText(result.getRssi() + "dBm");
        }

        if (result.getScanRecord().getServiceData().containsKey(MainActivity.parcelUuid)) {
            Log.d("DEBUG", result.getScanRecord().getServiceData().toString());
            if (result.getScanRecord().getServiceData().get(MainActivity.parcelUuid) != null) {
                vh.devicePayload.setText(new String(
                        result.getScanRecord().getServiceData().get(MainActivity.parcelUuid)));
            } else {
                vh.devicePayload.setText("bytes N/A");
            }
        }

        return v;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag(R.layout.device_list_item);
        final ScanResult result = (ScanResult) deviceList.values().toArray()[position];
        Log.d("CLICK", result.toString());
    }
}
