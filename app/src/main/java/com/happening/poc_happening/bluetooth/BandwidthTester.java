package com.happening.poc_happening.bluetooth;

import java.util.Timer;
import java.util.TimerTask;

public class BandwidthTester{

    public static final long DELAY = 30000; // in ms
    Layer layer;
    private boolean isRunning = false;
    private Timer timer = null;
    public int counter = 2;

    public BandwidthTester() {
        layer = Layer.getInstance();
    }

    public void start(){
        isRunning = true;
        layer.broadcastMessage("Ein Schäfchen springt über den Zaun.", true);
        counter = 2;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                String message =  "" + counter + " Schäfchen springen über den Zaun.";
                counter++;
                layer.broadcastMessage(message, true);
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
