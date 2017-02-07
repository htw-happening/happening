package com.happening.poc_happening.bluetooth.bluetoothEDR.BtPackage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.happening.poc_happening.bluetooth.bluetoothEDR.Connection.BluetoothService;
import com.happening.poc_happening.bluetooth.bluetoothEDR.Misc.Constants;

import java.util.Arrays;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * This class is the main receiver for all the different types of packages.
 * Handles easy logic for non-sophisticated packages. For further processing,
 * it sends the package zo the belonging service. Is using an own internal
 * buffer that stores the incoming packages. During runtime, it will process
 * the packages form the buffer.
 */
public class BtPackageHandler extends Thread {

    private String TAG = this.getClass().getSimpleName();
    private boolean d = true;
    private boolean seeRawData = false;

    private BtPackageCache cache;
    private Handler guiHandler;
    private PriorityBlockingQueue<BtPackageWrapper> btPackages;
    private BluetoothService bluetoothService;

    /**
     * Constructor
     * @param bluetoothService context
     * @param handler gui handler
     */
    public BtPackageHandler(BluetoothService bluetoothService, Handler handler) {
        this.cache = new BtPackageCache();
        this.guiHandler = handler;
        this.bluetoothService = bluetoothService;
        this.btPackages = new PriorityBlockingQueue<BtPackageWrapper>();
    }

    /**
     * runtime method
     * taking packages from internal buffer and processing them belonging
     * to its type.
     */
    @Override
    public void run() {
        setName("BNA BtPackageHandler");
        //get Packages from Queue and process them
        while (!Thread.currentThread().isInterrupted()){
            BtPackageWrapper wrapper = null;
            try {
                wrapper = btPackages.take();
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
                return;
            }
            if (wrapper != null){
                BtPackage btPackage = wrapper.btPackage;
                BluetoothService.Connection fromConnection = wrapper.connection;

                if (seeRawData) {
                    Log.d(TAG, Arrays.toString(BtPackageParser.generateByteArrayFrom(btPackage)));
                }
                //insert BtPackage into Cache
                synchronized (cache) {
                    if (d) Log.d(TAG, "Processing received package");

                    if (cache.insertInCache(btPackage)) { //True if we can add | false if its already contained in the cache
                        //First Seen Package - Hit

                        if (bluetoothService.isForwarding && btPackage.isForwardable()) {
                            bluetoothService.forwarder.sendToDevices(btPackage, fromConnection);
                        }

                        switch (btPackage.getType()) {
                            case BtPackageType.MESSAGE:
                                if (d) Log.d(TAG, "Is an Message Packet -> send to gui");
                                processMessagePackage(btPackage);
                                break;

                            default:
                                Log.e(TAG, "Unresolved PackageType, Type was: " + btPackage.getType());
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Add a package to the internal buffer of the BtPackageHanlder. This will be later processed.
     * @param btPackage package to add
     * @param fromConnection socket, wehere the package is from
     */
    public void addToQueue(BtPackage btPackage, BluetoothService.Connection fromConnection){
        BtPackageWrapper wrapper = new BtPackageWrapper(btPackage, fromConnection);
        btPackages.put(wrapper);
    }

    /**
     * Helper class that wraps a BtPackage together with its Connection object.
     */
    private class BtPackageWrapper implements Comparable<BtPackageWrapper> {
        public BtPackage btPackage;
        public BluetoothService.Connection connection;

        public BtPackageWrapper(BtPackage btPackage, BluetoothService.Connection connection) {
            this.btPackage = btPackage;
            this.connection = connection;
        }

        @Override
        public int compareTo(BtPackageWrapper another) {
            return this.btPackage.compareTo(another.btPackage);
        }
    }

    /**
     * Inserting an own created Package to cache (Prevents, that this package will be rebroadcasted
     * to the sender).
     * @param btPackagee the package to add
     */
    public void insertOwnCreatedPackage(BtPackage btPackagee){
        synchronized(cache) {
            cache.insertInCache(btPackagee);
        }
    }

    /**
     * Helper method for processing incoming Message Packages
     * @param btPackage the package to process
     */
    private void processMessagePackage(BtPackage btPackage){
        Message msg = guiHandler.obtainMessage(Constants.HANDLER_MESSAGE_RECEIVED);
        Bundle b = new Bundle();
        b.putString(Constants.BUNDLE_PACKAGE_AUTHOR, btPackage.getAuthor());
        b.putString(Constants.BUNDLE_PACKAGE_CONTENT, btPackage.getContentAsUTF8String());
        msg.setData(b);
        if (d) Log.d(TAG, "Received from "+btPackage.getAuthor()+" "+btPackage.getContentAsUTF8String());
        if (d) Log.d(TAG, "Package: "+btPackage.toString());
        guiHandler.sendMessage(msg);
    }
}
