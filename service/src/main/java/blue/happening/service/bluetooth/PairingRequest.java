package blue.happening.service.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by fabi on 23.05.17.
 */

public class PairingRequest extends BroadcastReceiver {
    public PairingRequest() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
            Log.d("PairingRequest", "Triggered Intent");

            try {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("PairingRequest", "Device was "+device.getName() + " " + device.getAddress());

                int type  = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);
                Log.d("PairingRequest", "Tyoe was "+type);

                if (type == BluetoothDevice.PAIRING_VARIANT_PIN){
                    int pin=intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, 0);
                    device.setPin((""+pin).getBytes("UTF-8"));
                    Log.d("PairingRequest", "Pin was " + pin);
                    abortBroadcast();

                }
                //device.setPairingConfirmation(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
