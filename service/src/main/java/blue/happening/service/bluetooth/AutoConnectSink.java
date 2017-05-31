package blue.happening.service.bluetooth;

import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class AutoConnectSink extends Thread {

    private boolean d = false;
    private String TAG = getClass().getSimpleName();

    private LinkedBlockingQueue<Device> sink = null;

    public AutoConnectSink() {
        this.sink = new LinkedBlockingQueue<>();
    }

    public void addDevice(Device device) {
        if (d) Log.d(TAG, "Connector - addDevice to Sink (" + device + ")");
        this.sink.offer(device);
    }

    @Override
    public void run(){

        Device device = null;

        while (!isInterrupted()) {
            try {
                if (device != null && device.getState() == Device.STATE.CONNECTING) {
                    Thread.sleep(1000);
                } else{
                    device = sink.take();
                    device.connectDevice();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        this.sink.clear();
        super.interrupt();
    }
}
