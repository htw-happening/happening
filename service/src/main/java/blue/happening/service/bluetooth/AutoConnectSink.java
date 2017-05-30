package blue.happening.service.bluetooth;

import android.util.Log;

import java.util.LinkedList;

public class AutoConnectSink extends Thread{

    private boolean d = false;
    private String TAG = getClass().getSimpleName();

    private LinkedList<Device> sink = null;

    public AutoConnectSink (){
        this.sink = new LinkedList<>();
    }

    public void addDevice (Device device){
        if (d) Log.d(TAG, "Connector - addDevice to Sink ("+device+")");
        this.sink.add(device);
    }

    @Override
    public void run() {
        while (!isInterrupted()){
            if (d) Log.d(TAG, "Connector Thread Trigger - Lets Poll");
            Device device = this.sink.poll();
            if (device!=null){
                if (d) Log.d(TAG, "Connector Thread Trigger - Polling works - Device " + device +" - Sinksize: "+sink.size());
                device.delayedConnectDevice();
            }
            try {
                Thread.sleep(3000);
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
