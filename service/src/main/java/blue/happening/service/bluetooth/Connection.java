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

    Connection(Device device, BluetoothSocket bluetoothSocket){
        this.device = device;
        this.socket = bluetoothSocket;
        start();
    }

    public void write(Package aPackage){
        writer.write(aPackage);
    }

    private void start(){
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
        private PackageHandler packageHandler;

        Reader(InputStream inputStream){
            this.inputStream = inputStream;
            this.packageHandler = new PackageHandler();
        }

        @Override
        public void run() {
            setName("Reader of " + device);
            while (!isInterrupted()){

                try {
                    byte[] buffer = new byte[PackageHandler.CHUNK_NUM + PackageHandler.CHUNK_SIZE];
                    inputStream.read(buffer);
                    packageHandler.createNewFromMeta(buffer);
                    for (int i = 0; i < packageHandler.getPayloadNum(); i++) {
                        buffer = new byte[PackageHandler.PAYLOAD_SIZE];
                        inputStream.read(buffer);
                        packageHandler.addContent(buffer);
                    }
                    Package aPackage = packageHandler.getPackage();
                    packageHandler.clear();
                    Layer.getInstance().receivedData(aPackage.getData(), device);
                    if (Layer.getInstance().getLayerCallback() != null) {
                        Layer.getInstance().getLayerCallback().onMessageReceived(aPackage.getData());
                    }

                } catch (IOException e) {
                    Log.e(TAG, "Reader disconnected of " + device, e);
                    shutdown();
                    return;
                }
            }
        }
    }

    private class Writer extends Thread {

        private OutputStream outputStream;
        private LinkedBlockingQueue<Package> packageQueue = new LinkedBlockingQueue<>();

        Writer(OutputStream outputStream){
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            setName("Writer of " + device);
            while (!isInterrupted()){
                Package aPackage;
                try {
                    aPackage = packageQueue.take();
                    if (aPackage != null) {
                        Package[] packages = PackageHandler.splitPackages(aPackage);
                        for (Package packageToSend : packages) {
                            outputStream.write(packageToSend.getData());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    shutdown();
                    return;
                }
            }
        }

        void write(Package aPackage){
            packageQueue.offer(aPackage);
        }
    }

}
