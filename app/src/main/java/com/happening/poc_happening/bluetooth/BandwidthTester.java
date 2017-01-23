package com.happening.poc_happening.bluetooth;

import android.util.Log;

/**
 * Created by Fabian on 23.01.2017.
 */

public class BandwidthTester extends Thread{

    public static final long DELAY = 1000; // in ms
    Layer layer;
    private boolean isRunning = false;

    public BandwidthTester() {
        layer = Layer.getInstance();
    }

    @Override
    public void run() {

        isRunning = true;

        layer.broadcastMessage("Ein Schäfchen springt über den Zaun.");

        int counter = 2;

        while (!Thread.currentThread().isInterrupted()){

            String message =  "" + counter + " Schäfchen springen über den Zaun.";
            counter++;

            layer.broadcastMessage(message);

            try {
                Thread.currentThread().wait(DELAY);
            } catch (InterruptedException e) {
                Log.e(this.getClass().getSimpleName(), e.toString());
                isRunning = false;
                return;
            }
        }
    }

    @Override
    public void interrupt() {
        isRunning = false;
        super.interrupt();
    }

    public boolean isRunning() {
        return isRunning;
    }
}
