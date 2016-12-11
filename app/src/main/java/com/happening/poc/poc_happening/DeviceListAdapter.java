package com.happening.poc.poc_happening;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


public class DeviceListAdapter extends BaseAdapter implements View.OnClickListener {

    public HashMap<String, ScanResult> deviceList = null;
    private LayoutInflater inflater = null;
    private Context context = null;
    private BluetoothGattCallback mBluetoothGattCallback = null;
    private ViewHolder vh = null;

    private static final class ViewHolder {
        TextView deviceName = null;
        TextView deviceAddress = null;
        TextView devicePayload = null;
        TextView deviceDbm = null;
    }

    public DeviceListAdapter(Context context, HashMap<String, ScanResult> deviceList) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.deviceList = deviceList;

        this.mBluetoothGattCallback = new BluetoothGattCallback() {

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                Log.d("GATT", "connection state changed " + newState);
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d("GATT", "services discovered");
                } else {
                    Log.e("GATT", "service discovery failed " + status);
                }

                for (BluetoothGattService service : gatt.getServices()) {
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                            Log.d("DESC", descriptor.toString());
                        }
                    }
                }
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                String value = new String(characteristic.getValue());
                Log.d("WHOOP", value);
            }
        };
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

        String serviceData = new String();

        for (Map.Entry<ParcelUuid, byte[]> entry : result.getScanRecord().getServiceData().entrySet()) {
            serviceData += new String(entry.getValue());
        }

        result.getDevice().connectGatt(context, true, mBluetoothGattCallback);

        vh.devicePayload.setText(serviceData);
        return v;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag(R.layout.device_list_item);
        final ScanResult result = (ScanResult) deviceList.values().toArray()[position];
        final BluetoothDevice bluetoothDevice = result.getDevice();
        bluetoothDevice.connectGatt(context, false, mBluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);

        Log.d("CLICK", result.toString());
    }
}
