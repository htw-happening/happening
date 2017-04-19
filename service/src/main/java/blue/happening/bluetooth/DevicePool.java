package blue.happening.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DevicePool extends ArrayList<Device> {

    public DevicePool() {
        final Handler handler = new Handler();
        final DevicePool that = this;

        final Runnable r = new Runnable() {
            public void run() {
                synchronized (that) {
                    Iterator<Device> it = that.iterator();
                    while (it.hasNext()) {
                        Device device = it.next();
                        if (device.isCold()) {
                            it.remove();
                            Log.i("BADEMEISTER", "The water is too cold for " + device.getAddress());
                        } else if (device.isConnected()) {
                            if (device.getTargetState() == BluetoothProfile.STATE_CONNECTED) {
                                device.readRssi();
                                Log.i("BADEMEISTER", "Read rssi " + device.getAddress());
                            } else if (device.getTargetState() == BluetoothProfile.STATE_DISCONNECTED) {
                                device.coolDown();
                                Log.i("BADEMEISTER", device.getAddress() + " needs to get out of the water in " + device.getHotness());
                                device.setCurrentState(BluetoothProfile.STATE_DISCONNECTING);
                                Layer.getInstance().disconnectDevice(device);
                            }
                        } else if (device.isDisconnected()) {
                            if (device.getTargetState() == BluetoothProfile.STATE_CONNECTED) {
                                device.coolDown();
                                Log.i("BADEMEISTER", device.getAddress() + " wants to take a dive in " + device.getHotness());
                                device.setCurrentState(BluetoothProfile.STATE_CONNECTING);
                                Layer.getInstance().connectDevice(device);
                            } else if (device.getTargetState() == BluetoothProfile.STATE_DISCONNECTED) {
                                Log.i("BADEMEISTER", device.getAddress() + " likes to chill by the pool");
                            }
                        }
                        Layer.getInstance().notifyHandlers(Layer.DEVICE_POOL_UPDATED);
                    }
                }
                int delay = Math.max(1000, that.size() * 250);
                handler.postDelayed(this, delay);
            }
        };

        handler.postDelayed(r, 1000);
    }

    public void changeState(BluetoothDevice device, int newState) {
        Device model = getModelByDevice(device);
        changeState(model, newState);
    }

    public void changeState(Device model, int newState) {
        if (model != null) {
            model.setCurrentState(newState);
            Log.i("DEVICE_POOL", "Changed State to " + newState);
        } else {
            Log.i("DEVICE_POOL", "Device not found, can't update state!");
        }
    }

    public List<Device> getConnectedDevices() {
        return getDevicesMatchingConnectionState(BluetoothProfile.STATE_CONNECTED);
    }

    public List<Device> getDevicesMatchingConnectionState(int state) {
        List<Device> matchingDevices = new ArrayList<>();
        for (Device model : this) {
            if (state == model.getCurrentState()) {
                matchingDevices.add(model);
            }
        }
        return matchingDevices;
    }

    public Device getModelByDevice(BluetoothDevice device) {
        for (Device model : this) {
            if (model.getBluetoothDevice().equals(device)) {
                return model;
            }
        }
        return null;
    }
}