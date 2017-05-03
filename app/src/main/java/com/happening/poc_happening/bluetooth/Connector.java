package com.happening.poc_happening.bluetooth;

import android.util.Log;

import java.util.LinkedList;

/**
 * Created by fabi on 19.04.17.
 */

public class Connector extends Thread{

    private boolean d = false;
    private String TAG = getClass().getSimpleName();

    private LinkedList<Device> sink = null;

    public Connector (){
        this.sink = new LinkedList<>();
    }

    public void addDevice (Device device){
        if (d) Log.d(TAG, "Connector - addDevice to Sink ("+device.getBluetoothDevice().getAddress()+")");
        this.sink.add(device);
    }

    @Override
    public void run() {
        while (!isInterrupted()){
            if (d) Log.d(TAG, "Connector Thread Trigger - Lets Poll");
            Device device = this.sink.poll();
            if (device!=null){
                if (d) Log.d(TAG, "Connector Thread Trigger - Polling works - Device " + device.getBluetoothDevice().getAddress() +" - Sinksize: "+sink.size());
                device.delayedConnectDevice(500, Device.STATE.CONNECTING);
            }
            try {
                Thread.currentThread().sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void interrupt() {
        this.sink.clear();
        super.interrupt();
    }
}
