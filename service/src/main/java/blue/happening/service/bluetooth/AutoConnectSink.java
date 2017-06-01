package blue.happening.service.bluetooth;

import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class AutoConnectSink extends Thread {

    private boolean d = true;
    private String TAG = getClass().getSimpleName();

    private LinkedBlockingQueue<Device> sink = null;

    AutoConnectSink() {
        sink = new LinkedBlockingQueue<>();
    }

    LinkedBlockingQueue<Device> getSink() {
        return sink;
    }

    void addDevice(Device device) {
        if (d) Log.d(TAG, "Connector - addDevice to Sink (" + device + ")");
        Log.d(TAG, "scheduled: " + device.isScheduled());
        Log.d(TAG, "trials: " + device.getTrials());
        if (device.getTrials() > 0) {
            scheduleDevice(device);
        } else {
            sink.offer(device);
        }
    }

    private void scheduleDevice(final Device device) {
        if (!device.isScheduled()) {
            device.setSchedule(true);
            int delay = device.getDelay();

            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    sink.offer(device);
                }
            }, delay, TimeUnit.SECONDS);
        }
    }

    @Override
    public void run() {

        Device device = null;

        while (!isInterrupted()) {
            try {
                if (device != null && device.getState() == Device.STATE.CONNECTING) {
                    Thread.sleep(1000);
                } else {
                    device = sink.take();
                    device.addTrial();
                    device.connectDevice();
                    device.setSchedule(false);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interrupt() {
        sink.clear();
        super.interrupt();
    }
}
