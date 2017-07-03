package blue.happening.service;

import android.util.Log;

import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import blue.happening.mesh.MeshHandler;
import blue.happening.service.bluetooth.Layer;

/**
 *
 *
 *
 *
 *
 * FOR DEBUGGING! Not for Dashboard and co. Deactivate via "d" boolean (or remove in HappeningService class)
 *
 *
 *
 *
 *
 */

public class StatsLogTimer {

    private String TAG = "__________" + getClass().getSimpleName();
    private boolean d = true;

    private MeshHandler meshHandler;
    private Layer layer;

    private static final int RATE = 5000;
    private Timer timer;
    private TimerTask timerTask;

    StatsLogTimer(MeshHandler meshHandler, Layer layer){
        if (d) Log.d(TAG, "StatsLogTimer: ");
        this.meshHandler = meshHandler;
        this.layer = layer;
    }

    void start(){
        if (d) Log.d(TAG, "start: ");
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (d) Log.d(TAG, "meshHandler.getDevices().size()   " + meshHandler.getDevices().size());
                if (d) Log.d(TAG, "layer.getNumOfConnectedDevices()  " + layer.getNumOfConnectedDevices());
                if (d) Log.d(TAG, "layer.getScannedDevices().size()  " + layer.getScannedDevices().size());
            }
        };
        timer.scheduleAtFixedRate(timerTask, RATE, RATE);
    }

    void stop(){
        if (d) Log.d(TAG, "stop: ");
        timerTask.cancel();
        timer.cancel();
    }

}
