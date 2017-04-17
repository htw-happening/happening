package com.happening.bluetooth;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Fabian on 23.01.2017.
 */

public class BandwidthTester{

    public static final long DELAY = 1000; // in ms
    Layer layer;
    private boolean isRunning = false;
    private Timer timer = null;
    public int counter = 2;

    public BandwidthTester() {
        layer = Layer.getInstance();
    }

    public void start(){
        isRunning = true;
        layer.broadcastMessage("Ein Schäfchen springt über den Zaun.");
        counter = 2;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                String message =  "" + counter + " Schäfchen springen über den Zaun.";
                counter++;
                layer.broadcastMessage(message);
            }
        }, DELAY, DELAY);
    }


    public void stop(){
        isRunning = false;
        timer.cancel();
    }

    public boolean isRunning() {
        return isRunning;
    }
}
