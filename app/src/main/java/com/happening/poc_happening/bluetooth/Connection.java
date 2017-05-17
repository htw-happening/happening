package com.happening.poc_happening.bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by fabi on 17.05.17.
 */

public class Connection {

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


    class Reader extends Thread {

        private InputStream inputStream;

        public Reader (InputStream inputStream){
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            while (!isInterrupted()){

            }
        }
    }

    class Writer extends Thread {

        private OutputStream outputStream;

        public Writer (OutputStream outputStream){
            this.outputStream = outputStream;
        }

        @Override
        public void run() {
            while (!isInterrupted()){

            }
        }
    }

}
