package blue.happening.service.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import blue.happening.service.R;
import blue.happening.service.bluetooth.Device;

public class DeviceListAdapter extends ArrayAdapter<Device> {

    public DeviceListAdapter(Context context, ArrayList<Device> deviceList) {
        super(context, 0, deviceList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Device device = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.discovered_device, parent, false);
        }

        TextView address = (TextView) convertView.findViewById(R.id.device_address);
        TextView name = (TextView) convertView.findViewById(R.id.device_name);
        TextView state = (TextView) convertView.findViewById(R.id.device_state);

        address.setText(device.getAddress());
        name.setText(device.getName());
        state.setText(device.getStateAsString());

        if (device.getState() == Device.STATE.OFFLINE ||
                device.getState() == Device.STATE.UNKNOWN ||
                device.getState() == Device.STATE.DISCONNECTED) {
            address.setTextColor(Color.RED);
        }
        if (device.getState() == Device.STATE.CONNECTED) {
            address.setTextColor(Color.GREEN);
        }
        if (device.getState() == Device.STATE.SCHEDULED ||
                device.getState() == Device.STATE.CONNECTING) {
            address.setTextColor(Color.YELLOW);
        }

        return convertView;
    }
}
