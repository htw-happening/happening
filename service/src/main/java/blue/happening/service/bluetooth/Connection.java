package blue.happening.service.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

public class Connection {

    private boolean d = true;
    private String TAG = getClass().getSimpleName();

    private Reader reader;
    private Writer writer;

    private Device device;
    private BluetoothSocket socket;

    Connection(Device device, BluetoothSocket bluetoothSocket) {
        this.device = device;
        this.socket = bluetoothSocket;
        start();
    }

    public void write(Package aPackage) {
        writer.write(aPackage);
    }

    private void start() {
        try {
            this.reader = new Reader(this.socket.getInputStream());
            this.writer = new Writer(this.socket.getOutputStream());
            this.reader.start();
            this.writer.start();
        } catch (IOException e) {
            e.printStackTrace();
            shutdown();
        }

    }

    void shutdown() {
        try {
            this.reader.inputStream.close();
            this.writer.packageQueue.clear();
            this.writer.outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Layer.getInstance().connectionLost(device);
    }


    private class Reader extends Thread {

        private InputStream inputStream;
        private Packetizer packageHandler;

        Reader(InputStream inputStream) {
            this.inputStream = inputStream;
            this.packageHandler = new Packetizer();
        }

        @Override
        public void run() {
            setName("Reader of " + device);
            while (!isInterrupted()) {
                try {
                    byte[] buffer = new byte[Packetizer.CHUNK_SIZE];
                    inputStream.read(buffer);
                    packageHandler.createNewFromMeta(buffer);
                    buffer = new byte[packageHandler.getPayloadSize()];
                    inputStream.read(buffer);
                    packageHandler.addContent(buffer);
                    Package aPackage = packageHandler.getPackage();
                    System.out.println(TAG + " " + getName() + " package received " + aPackage.getData().length + " bytes");
                    packageHandler.clear();
                    if (Layer.getInstance().getLayerCallback() != null) {
                        Layer.getInstance().getLayerCallback().onMessageReceived(aPackage.getData());
                    }

                } catch (IOException e) {
                    Log.e(TAG, "Reader closed of " + device + " cause of IO Error");
                    shutdown();
                    return;
                } catch (Exception e) {
                    Log.e(TAG, "Reader closed of " + device + " cause of unreadable package");
                }
            }
        }
    }

    private class Writer extends Thread {

        private OutputStream outputStream;
        private LinkedBlockingQueue<Package> packageQueue = new LinkedBlockingQueue<>();

        Writer(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            setName("Writer of " + device);
            while (!isInterrupted()) {
                Package aPackage;
                if (Layer.getInstance().state == Layer.STATE.SCANNING) continue;
                try {
                    aPackage = packageQueue.take();
                    if (aPackage != null) {
                        Package[] packages = Packetizer.splitPackages(aPackage);
                        for (Package packageToSend : packages) {
                            outputStream.write(packageToSend.getData());
                            Log.d(TAG, "SEND via layer: " + packageToSend.getData().length + " bytes");
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Writer Closed of " + device + " cause of read -1");
                    shutdown();
                    return;
                }
            }
        }

        void write(Package aPackage) {
            packageQueue.offer(aPackage);
            Log.d(TAG, "SEND via layer (offerring): " + aPackage.getData().length + " bytes");
        }
    }

}
