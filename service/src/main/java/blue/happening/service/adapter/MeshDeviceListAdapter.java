package blue.happening.service.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import blue.happening.service.R;

public class MeshDeviceListAdapter extends ArrayAdapter<MeshDevice> {

    public MeshDeviceListAdapter(Context context, ArrayList<MeshDevice> deviceList) {
        super(context, 0, deviceList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MeshDevice device = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.mesh_device, parent, false);
        }

        TextView address = (TextView) convertView.findViewById(R.id.mesh_device_address);
        TextView name = (TextView) convertView.findViewById(R.id.mesh_device_name);

        address.setText(device.getDeviceid());
        name.setText(device.getDevicename());

        return convertView;
    }
}
