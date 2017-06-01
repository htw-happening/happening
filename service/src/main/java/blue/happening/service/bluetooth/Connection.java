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

    public Connection (Device device, BluetoothSocket bluetoothSocket){
        this.device = device;
        this.socket = bluetoothSocket;
        start();
    }

    public void write(Package aPackage){
        writer.write(aPackage);
    }

    public void start(){
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

    public void shutdown() {
        try {
            this.reader.inputStream.close();
            this.writer.packageQueue.clear();
            this.writer.outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Layer.getInstance().connectionLost(device);
    }


    class Reader extends Thread {

        private InputStream inputStream;

        public Reader (InputStream inputStream){
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            setName("Reader for " + device);
            while (!isInterrupted()){

                if(d) Log.i(TAG, "Reader is running: " + device);

                byte[] buffer = new byte[128];
                while (true) {
                    try {
                        inputStream.read(buffer);
                        Layer.getInstance().receivedData(buffer, device);
                        if (Layer.getInstance().getLayerCallback() != null) {
                            Layer.getInstance().getLayerCallback().onReceivedMessage(buffer, device);
                        }

                    } catch (IOException e) {
                        Log.e(TAG, "Reader disconnected " + device, e);
                        shutdown();
                        return;
                    }
                }
            }
        }
    }

    class Writer extends Thread {

        private OutputStream outputStream;
        private LinkedBlockingQueue<Package> packageQueue = new LinkedBlockingQueue<>();

        public Writer (OutputStream outputStream){
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            while (!isInterrupted()){
                Package aPackage = null;
                try {
                    aPackage = packageQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    shutdown();
                }
                if (aPackage != null){
                    try {
                        outputStream.write(aPackage.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                        shutdown();
                        return;
                    }
                }
            }
        }

        public void write(Package aPackage){
            packageQueue.offer(aPackage);
        }
    }

}
