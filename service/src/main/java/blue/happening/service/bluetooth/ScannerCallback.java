package blue.happening.service.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;
import java.util.UUID;

public class ScannerCallback extends BroadcastReceiver {

    boolean debug = true;
    String TAG = this.getClass().getSimpleName();

    List<Device> devicesToFetch;

    public ScannerCallback() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (debug) Log.d(TAG, "onReceive");
        String action = intent.getAction();
        switch (action) {

            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                if (debug) Log.d(TAG, "ACTION_DISCOVERY_STARTED");
                break;


            case BluetoothDevice.ACTION_FOUND:
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (debug)
                    Log.d(TAG, "ACTION_FOUND " + device.getName() + " " + device.getAddress());
                if (device != null) {
                    Layer.getInstance().addNewScan(device);
                }
                break;


            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                if (debug) Log.d(TAG, "ACTION_DISCOVERY_FINISHED");
                devicesToFetch = (List<Device>) Layer.getInstance().getScannedDevices().clone();
                if (!devicesToFetch.isEmpty()){
                    Device deviceToFetch = devicesToFetch.remove(0);
                    deviceToFetch.fetchSdpList();
                }
                break;


            case BluetoothDevice.ACTION_UUID:
                if (debug) Log.d(TAG, "ACTION_UUID");

                BluetoothDevice deviceWithSDP = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                if (debug) Log.d(TAG, "UUIDs Found for " + deviceWithSDP.getName());

                Device scannedDevice = Layer.getInstance().getDeviceByMac(deviceWithSDP);

                if (uuidExtra == null) {
                    if (debug) Log.d(TAG, "UUIDs are NULL for " + scannedDevice);
                    Layer.getInstance().fetchedUUIDsFailedFor(scannedDevice);
                } else {
                    if (debug) Log.d(TAG, "UUIDs  are available for " + scannedDevice);
                    for (Parcelable parcelable : uuidExtra) {
                        ParcelUuid parcelUuid = (ParcelUuid) parcelable;
                        UUID uuid = parcelUuid.getUuid();
                        scannedDevice.addFetchedUuid(uuid);
                        if (debug) Log.d(TAG, "uuid: " + uuid);
                    }
                    Layer.getInstance().fetchedUUIDsFor(scannedDevice);
                }

                if (devicesToFetch != null && !devicesToFetch.isEmpty()){
                    Device deviceToFetch = devicesToFetch.remove(0);
                    if (debug) Log.d(TAG, "Removing device from devicesToFetch and start fetching "+deviceToFetch);
                    if (deviceToFetch.getState() != Device.STATE.FETCHING || deviceToFetch.getState() != Device.STATE.CONNECTED |
                            deviceToFetch.getState() != Device.STATE.FETCHED || deviceToFetch.getState() != Device.STATE.CONNECTING) {
                        deviceToFetch.fetchSdpList();
                    }
                }
                break;
        }
    }
}