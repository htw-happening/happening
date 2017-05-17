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
        try {
            this.reader = new Reader(bluetoothSocket.getInputStream());
            this.writer = new Writer(bluetoothSocket.getOutputStream());
            this.reader.start();
            this.writer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(Package aPackage){
        writer.write(aPackage);
    }

    public void shutdown() {
        // TODO: 17.05.17
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

                    } catch (IOException e) {
                        Log.e(TAG, "Reader disconnected" + device, e);
                        Layer.getInstance().connectionLost(device);
                        break;
                    }
                }
                if(d) Log.i(TAG, "Reader stopped: " + device);

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
                Package aPackage;
                aPackage = packageQueue.poll();
                if (d) Log.d(TAG, "Polled a Package " + aPackage + " " + device);
                if (aPackage != null){
                    try {
                        outputStream.write(aPackage.getData());
                        if (d) Log.d(TAG, "Wrote data to outputstreamm " + device);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Layer.getInstance().connectionLost(device); // TODO: 17.05.17 really?
                    }
                }
            }
        }

        public void write(Package aPackage){
            packageQueue.offer(aPackage);
        }
    }

}
