package blue.happening.chat.bluetooth.bluetoothEDR.Connection;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;

import blue.happening.chat.bluetooth.bluetoothEDR.BtPackage.BtPackage;

/**
 * This class handles the logic for the forwarding of BtPackages. Is using the BtPackageCache as a
 * history lookup.
 */
public class Forwarder {

    private BluetoothService bluetoothService;

    /**
     * Constructor
     * @param bluetoothService context
     */
    public Forwarder(BluetoothService bluetoothService) {
        this.bluetoothService = bluetoothService;
    }

    /**
     * Forwarding this package according to its properties. This method is sends a package to all
     * connected Devices. Checking Cache for preventing boradcaststorms.
     * @param btPackage the package to send
     */
    public void sendToAllDevices (BtPackage btPackage){
        bluetoothService.packageHandler.insertOwnCreatedPackage(btPackage);
        Iterator<Map.Entry<BluetoothSocket, BluetoothService.Connection>> it = bluetoothService.connectionHashMap.entrySet().iterator();
        while (it.hasNext()) {
            try {
                Map.Entry<BluetoothSocket, BluetoothService.Connection> entrie = it.next();
                BluetoothService.Connection r;
                synchronized (bluetoothService.connectionHashMap) {
                    r = entrie.getValue();
                }
                r.write(btPackage);
            }
            catch (Exception e) {
                Log.e(this.getClass().toString(), e.toString());
            }
        }
    }

    /**
     * Forwarding this package according to its properties. This method is sends a package to all
     * connected Devices except the Socket of the 2. parameter (This can be the origin).
     * Checking Cache for preventing boradcaststorms.
     * @param btPackage the package to send
     * @param exceptSocket the origin socket
     */
    public void sendToDevices (BtPackage btPackage, BluetoothService.Connection exceptSocket){
        bluetoothService.packageHandler.insertOwnCreatedPackage(btPackage);
        Iterator<Map.Entry<BluetoothSocket, BluetoothService.Connection>> it = bluetoothService.connectionHashMap.entrySet().iterator();
        while (it.hasNext()) {
            try {
                Map.Entry<BluetoothSocket, BluetoothService.Connection> entrie = it.next();
                BluetoothService.Connection r;
                synchronized (bluetoothService.connectionHashMap) {
                    r = entrie.getValue();
                }
                if(r != exceptSocket && (r.isActive())) {
                    r.write(btPackage);
                }
            }
            catch (Exception e) {
                Log.e(this.getClass().toString(), e.toString());
            }
        }
    }

    public void sendToPeer (BtPackage btPackage, BluetoothService.Connection socketOfPeer){
        bluetoothService.packageHandler.insertOwnCreatedPackage(btPackage);
        socketOfPeer.write(btPackage);
    }

    /**
     * For Debugging
     * @param bytes bytes to send
     */
    public void sendToAllDevices (byte[] bytes){
        Iterator<Map.Entry<BluetoothSocket, BluetoothService.Connection>> it = bluetoothService.connectionHashMap.entrySet().iterator();
        while (it.hasNext()) {
            try {
                Map.Entry<BluetoothSocket, BluetoothService.Connection> entrie = it.next();
                BluetoothService.Connection r;
                synchronized (bluetoothService.connectionHashMap) {
                    r = entrie.getValue();
                }
//                r.write(bytes); // TODO
            }
            catch (Exception e) {
                Log.e(this.getClass().toString(), e.toString());
            }
        }
    }
}
