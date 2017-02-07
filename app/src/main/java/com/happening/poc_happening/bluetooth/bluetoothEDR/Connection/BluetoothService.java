package com.happening.poc_happening.bluetooth.bluetoothEDR.Connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.happening.poc_happening.bluetooth.bluetoothEDR.BtPackage.BtPackage;
import com.happening.poc_happening.bluetooth.bluetoothEDR.BtPackage.BtPackageBuffer;
import com.happening.poc_happening.bluetooth.bluetoothEDR.BtPackage.BtPackageFactory;
import com.happening.poc_happening.bluetooth.bluetoothEDR.BtPackage.BtPackageHandler;
import com.happening.poc_happening.bluetooth.bluetoothEDR.BtPackage.BtPackageParser;
import com.happening.poc_happening.bluetooth.bluetoothEDR.Misc.Constants;
import com.happening.poc_happening.bluetooth.bluetoothEDR.Misc.Settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * This class contains all the logic for managing a member of a network.
 * All the services are located here. Each one has start and stop methods.
 * The BluetoothService is connected to the GUI (MainActivity) through the Handler (guiHandler).
 * This class includes inner classes like Acceptor, Connector, Connection and PriorityOutputStream,
 * cause of the tight relations to each other.
 */
public class BluetoothService {

    private String TAG = this.getClass().getSimpleName();
    private boolean d = true;

    private static final String NAME = "BlueToothNetwork";
    Context context;
    public final BluetoothAdapter bluetoothAdapter;
    public final Handler guihandler;
    private Acceptor acceptor;
    private Connector connector;
    public HashMap<BluetoothSocket,Connection> connectionHashMap;
    private Set<String> addressDevices;
    public BtPackageHandler packageHandler;
    public Forwarder forwarder;
    public AutoConnector autoConnector;
    private int state;
    private int attemps;
    private int package_id = 0;
    public UUID uuid;
    public int role;

    public boolean isForwarding = true;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    /**
     * Constructor. Setting up all the services.
     * @param context activity context
     * @param handler a gui handler
     */
    public BluetoothService(Context context, Handler handler) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        role = Constants.ROLE_NONE;
        state = STATE_NONE;
        this.guihandler = handler;
        connectionHashMap = new HashMap<BluetoothSocket,Connection>();
        addressDevices = new HashSet<String>();
        uuid = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
        packageHandler = new BtPackageHandler(this, handler);
        packageHandler.start();
        forwarder = new Forwarder(this);
        autoConnector = new AutoConnector(this);
    }

    private synchronized void setState(int state) {
        setState(state, -1);
    }

    private synchronized void setState(int state, int arg2) {
        if(d) Log.d(TAG, "setState() " + this.state + " -> " + state);
        this.state = state;
    }

    private synchronized void setState(int state, String reason) {
        setState(state);
    }

    public synchronized int getState() {
        return state;
    }

    /**
     * Starting the BluetoothService
     * Reseting the Threads for Connections & Connectors.
     * Establishing a BluetoothServerSocket to be accessible
     */
    public synchronized void start() {
        if(d) Log.d(TAG, "starting BluetoothService");

        if (connector != null) {
            if(d) Log.d(TAG, "connect thread " + connector.getName() + " is about to stop");
            connector.cancel();
            connector = null;
        }
        if (connectionHashMap.size() > 0) {
            Iterator<Map.Entry<BluetoothSocket, Connection>> it = connectionHashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<BluetoothSocket, Connection> entrie = it.next();
                Connection connection = entrie.getValue();
                connection.cancel();
                connection = null;
            }
            connectionHashMap.clear();
            addressDevices.clear();
        }

        if (acceptor == null) {
            acceptor = new Acceptor();
            acceptor.start();
        }
        setState(STATE_LISTEN, "start");
    }

    /**
     * Establish a single Connection to a Device
     * @param device the BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        showInMessageBox("Log","Connecting to "+device.getName());

        if(d) Log.d(TAG, "connect to: " + device);
        try {
            connector = new Connector(device, uuid);
            connector.setName("connect to: " + device.getName() + " attempts:" + (attemps + 1));
            connector.start();
            setState(STATE_CONNECTING,"trying connect to: " + device.getName());
        } catch (Exception e) { }
    }

    /**
     * A Connection of two devices was successful. Creating Connection Object and put it to the Map.
     * Also changing the Role of the Member
     * @param socket the socket to the remote device
     * @param device the remote device
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if(d) Log.d(TAG, "connected!");
        Connection mConnection = new Connection(socket, device);
        mConnection.start();

        if (d) Log.d(TAG, "connectionHashMap.size() = " + connectionHashMap.size());
        if (connectionHashMap.size()==0){
            role = Constants.ROLE_CLIENT;
        }

        connectionHashMap.put(socket, mConnection);

        makeToastNotification("Connected to " + device.getName());
        showInMessageBox("Log", "Connected to "+device.getName());
        guihandler.obtainMessage(Constants.HANDLER_CONNECTION_CHANGED).sendToTarget();
        setState(STATE_CONNECTED, "connected");
    }

    /**
     * Disconnecting the member form the network, through canceling all connections.
     */
    public synchronized void disconnectAllConnections(){
        Iterator<Map.Entry<BluetoothSocket, Connection>> it = connectionHashMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BluetoothSocket, Connection> entrie = it.next();
            Connection connection = entrie.getValue();
            if(d) Log.d(TAG, "about to stopStreamingSpeech: " + connection.device.getName());
            connection.cancel();
            connection = null;
        }
        connectionHashMap.clear();
        addressDevices.clear();


        guihandler.obtainMessage(Constants.HANDLER_CONNECTION_CHANGED).sendToTarget();
    }

    /**
     * Restarting the Acceptor. Used, if a Connection was accepted or if settings changed.
     */
    public synchronized void restartAcceptor(){
        if (acceptor != null) {
            acceptor.cancel();
            acceptor = new Acceptor();
            acceptor.start();
        }else{
            acceptor = new Acceptor();
            acceptor.start();
        }
    }

    /**
     * Stopping the BluetoothService. Canceling all Subservices and Threads.
     */
    public synchronized void stop() {
        if(d) Log.d(TAG, "stop BLuetoothChatService");
        if (connector != null) {
            connector.cancel(); connector = null;}
        if (connectionHashMap.size() > 0) {
            Iterator<Map.Entry<BluetoothSocket, Connection>> it = connectionHashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<BluetoothSocket, Connection> entrie = it.next();
                Connection connection = entrie.getValue();
                if(d) Log.d(TAG, "about to stopStreamingSpeech: " + connection.device.getName());
                connection.cancel();
                connection = null;
            }
            connectionHashMap.clear();
            addressDevices.clear();
        }
        if (acceptor != null) {
            acceptor.cancel();
            acceptor = null;
        }
        setState(STATE_NONE, "stop");
        guihandler.obtainMessage(Constants.HANDLER_CONNECTION_CHANGED).sendToTarget();
    }

    /**
     * The attempt to make a connection with a remote device failed.
     * @param name
     */
    private void connectionFailed(String name) {
        if (connectionHashMap.size() == 0)
            setState(STATE_LISTEN, "failed");
        else
            setState(STATE_CONNECTED, "failed");

        makeToastNotification("Unable to connect device: " + name);
        showInMessageBox("Log","Unable to Connect" + name);
    }

    /**
     * The Connection to a remote was lost, due the closing of the connection from the remote device.
     * Canceling Connection to this device and checking status of role.
     * @param socket the socket of the lost device
     */
    private synchronized void connectionLost(BluetoothSocket socket) {
        if (d) Log.d(TAG, "connectionHashMap.size:" + connectionHashMap.size() + ", addressDevices.size:" + addressDevices.size());
        Connection threadLost = connectionHashMap.get(socket);
        if (threadLost != null) {
            threadLost.cancel();
            connectionHashMap.remove(socket);
            addressDevices.remove(socket.getRemoteDevice().getAddress());
        }

        if(d) Log.d(TAG, "connectionHashMap.size:" + connectionHashMap.size() + ", addressDevices.size:" + addressDevices.size());
        makeToastNotification("Device connection with " + socket.getRemoteDevice().getName() + " was lost");
        guihandler.obtainMessage(Constants.HANDLER_CONNECTION_CHANGED).sendToTarget();
        showInMessageBox("log","Lost Connection to "+socket.getRemoteDevice().getName());

        if (connectionHashMap.size() == 0)
            setState(STATE_LISTEN, "lost");
        else
            setState(STATE_CONNECTED, "lost");

    }

    /**
     * Making a Toast Notification at the GUI through the own GUI Handler.
     * @param s the String to show
     */
    public void makeToastNotification(String s){
        Message msg = guihandler.obtainMessage(Constants.HANDLER_MAKE_A_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_TOAST, s);
        msg.setData(bundle);
        guihandler.sendMessage(msg);
    }

    /**
     * Adding a Text in the Messagebox. Used for Network Logs.
     * @param who who (i.e. device)
     * @param what what
     */
    public void showInMessageBox(String who, String what){
        Message msg = guihandler.obtainMessage(Constants.HANDLER_MESSAGE_BOX);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_MESSAGEBOX_WHO, who);
        bundle.putString(Constants.BUNDLE_MESSAGEBOX_WHAT, what);
        msg.setData(bundle);
        guihandler.sendMessage(msg);
    }

    /**
     * Sending a Message to Network.
     * @param s message
     */
    public void broadCastAMessage(String s) {
        BtPackage btPackage = BtPackageFactory.buildMessagePackage(getPackageID(), this.bluetoothAdapter.getName(), s);
        forwarder.sendToAllDevices(btPackage);
    }

    /**
     * Helper method for generating an ID. The ID increments itself after calling.
     * @return the unique ID
     */
    public int getPackageID(){
        int toreturn = this.package_id;
        package_id++;
        return toreturn;
    }

    //region Acceptor
    /**
     * This class opens a BluetoothServerSocket to make this device connectable for remote devices.
     * The listeninf for incoming request is blocking. Therefore it runs in separate thread.
     */
    private class Acceptor extends Thread {
        BluetoothServerSocket serverSocket = null;

        public Acceptor() {
        }

        /**
         * the runtime method.
         * opens the serversocket and waiting for incoming request (interruptable).
         */
        public void run() {
            if(d) Log.d(TAG, "Acceptor is running");
            setName("BNA Acceptor");
            BluetoothSocket socket = null;
            try {
                while(true){
                    if (Settings.secureModeOn) {
                        serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, uuid);
                    }
                    else{
                        serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, uuid);
                    }
                    if(d) Log.d(TAG,"About to wait, accepting for a client");
                    socket = serverSocket.accept();
                    if (socket != null) {
                        Boolean added = addressDevices.add(socket.getRemoteDevice().getAddress());
                        if (!added) {
                            if(d) Log.d(TAG, "AcceptThred Denied the Connection becazse Service has already a connection to "+socket.getRemoteDevice().getName());
                        }else {
                            connected(socket, socket.getRemoteDevice());
                        }
                    }
                    serverSocket.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "accept() has been interrupted, cause: " + e.getMessage());
            }
            if(d) Log.i(TAG, "Acceptor stopped");
        }

        /**
         * Stopping the Acceptor, closing the Serversocket.
         */
        public void cancel() {
            if(d) Log.d(TAG, "stop " + this);
            if (serverSocket!=null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "close() of server failed", e);
                }
            }
        }
    }
    //endregion

    //region Connector
    /**
     * This class creates a Connection to a given remote device.
     * Cause of blocking methods, it is running in a thread.
     */
    private class Connector extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;
        private final UUID tempUuid;

        /**
         * Constructor
         * @param device the remote device to connect
         * @param uuid the uuid of the service (should be listed in the sdp)
         */
        public Connector(BluetoothDevice device, UUID uuid) {
            if(d) Log.d(TAG, "Connector created: "+device.getName());
            this.device = device;
            BluetoothSocket tmp = null;
            tempUuid = uuid;
            try {

                if (Settings.secureModeOn){
                    tmp = device.createRfcommSocketToServiceRecord(tempUuid);
                }
                else{
                    tmp = device.createInsecureRfcommSocketToServiceRecord(tempUuid);
                }

            } catch (IOException e) {
                Log.e(TAG, "createRfcommSocketToServiceRecord() failed: "+device.getName(), e);
            }
            socket = tmp;
        }

        /**
         * runtime method
         * creating the connection with connect() -> blocking.
         * 2 attempts for connecting. Else it will fail.
         */
        public synchronized void run() {
            setName("BNA Connector");
            if(d) Log.d(TAG, "Connector is running: " + device.getName());
            try {
                Boolean added = addressDevices.add(device.getAddress());
                if (!added) {
                    Message msg = guihandler.obtainMessage(Constants.HANDLER_MAKE_A_TOAST);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BUNDLE_TOAST, " You're already connecting to this device ("+ device.getName()+")");
                    msg.setData(bundle);
                    guihandler.sendMessage(msg);
                    return;
                }

                try {
                    if(d) Log.i(TAG, "About to wait to connect to " + device.getName());
                    socket.connect(); //blocking
                } catch (IOException e) {
                    synchronized (BluetoothService.this) {
                        attemps++;
                        if(added) {
                            addressDevices.remove(device.getAddress());
                        }
                        if(d) Log.d(TAG, "connection fail, attemps:" + attemps);
                        try {
                            socket.close();
                        } catch (IOException e2) {
                            Log.e(TAG, "unable to close() socket during connection failure", e2);
                        }
                        if (attemps >= 2) {
                            connectionFailed(device.getName());
                            attemps = 0;
                        } else {
                            connect(device);
                        }
                        if(d) Log.i(TAG, "END connector " + device.getName() + " FAIL");
                        return;
                    }
                }
                if(d) Log.i(TAG, "connection done, device:" + device.getName());
                synchronized (BluetoothService.this) {
                    connector = null;
                    attemps = 0;
                }
                connected(socket, device);

            }finally {
                if(d) Log.d(TAG, "Connector stopped" + device.getName());
            }
        }

        /**
         * Stopping the Connector.
         */
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "unable to close() socket", e);
            }
        }
    }
    //endregion

    //region Connection
    /**
     * This class represents a connection to a remote device. A kind of a rich Socket.
     * Here the Streams can be accessed. During Runtime, the Thread will read continuously form the
     * inputstream. The class also contains another thread called PriorityOutputStream. This one is for
     * continuously writing to the outputstream.
     */
    public class Connection extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        /**
         * Checking if the Connection can be used for forwarding.
         * @return true, if connection is active
         */
        public boolean isActive() {
            return isActive;
        }

        public void setIsActive(boolean isActive) {
            this.isActive = isActive;
        }

        private boolean isActive = true;
        private BtPackageBuffer btPackageBuffer;
        private PriorityOutputStream priorityOutputStream;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        /**
         * Constructor
         * @param socket the socket for communication with the remote device
         * @param device the remote device
         */
        public Connection(BluetoothSocket socket, BluetoothDevice device) {
            if(d) Log.i(TAG, "Connection created: " + socket.getRemoteDevice().getName());
            this.socket = socket;
            InputStream inStream = null;
            OutputStream outStream = null;
            this.device = device;
            btPackageBuffer = new BtPackageBuffer(packageHandler);

            try {
                inStream = socket.getInputStream();
                outStream = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            inputStream = inStream;
            outputStream = outStream;
            priorityOutputStream = new PriorityOutputStream(outputStream);
        }

        public BluetoothDevice getDevice() {
            return device;
        }

        /**
         * the runtime method.
         * reading from inputstream all the time.
         */
        public void run() {
            setName("BNA Connection");
            if(d) Log.i(TAG, "Connection is running: " + socket.getRemoteDevice().getName());
            priorityOutputStream.start();

            byte[] buffer = new byte[1];
            while (true) {
                try {
                    inputStream.read(buffer);
                    btPackageBuffer.byteReceived(buffer[0], this);

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost(socket);
                    break;
                }
            }
            if(d) Log.i(TAG, "Connection stopped: " + device.getName());
        }

        /**
         * "writing" a package. More like adding the package to the queue to the priorityoutputstream.
         * @param btPackage the package to write
         */
        public void write(BtPackage btPackage) {
            priorityOutputStream.addToQueue(btPackage);
        }

        /**
         * Stopping the Connection. Closing Sockets and interrupt threads.
         */
        public void cancel() {
            priorityOutputStream.interrupt();
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }

        /**
         * This inner inner class represens a Writer thread.
         * It continuously writes to the outputstream during runtime. It also provides Queuing
         * and prioritizing of packages.
         */
        private class PriorityOutputStream extends Thread {
            private OutputStream outputStream;
            private PriorityBlockingQueue<BtPackage> datapackages;

            /**
             * Constructor
             * @param outputStream the native outputstream of the connection
             */
            public PriorityOutputStream(OutputStream outputStream) {
                this.outputStream = outputStream;
                datapackages = new PriorityBlockingQueue<BtPackage>();
            }

            /**
             * runtime method
             * taking packages from internal buffer and send them.
             */
            @Override
            public void run() {
                setName("BNA PriorityOutputStream");

                //grap from queues and send to output
                while(!Thread.currentThread().isInterrupted()) {
                    BtPackage packageToSend = null;

                    packageToSend = datapackages.poll();

                    if (packageToSend != null) {
//                        Log.d(TAG,"packageToSend: " + packageToSend.toString());
                        byte[] packageAsBytes = BtPackageParser.generateByteArrayFrom(packageToSend);
                        try {
                            outputStream.write(packageAsBytes);
                        } catch (IOException e) {
                            Log.e(TAG,e.toString());
                        }
                    }
                }
            }

            /**
             * inserting a package to internal buffer.
             * @param btPackage package to add
             */
            public void addToQueue(BtPackage btPackage) {
                datapackages.put(btPackage);
            }

            public int getOutgoinfQueueSize(){
                return datapackages.size();
            }
        }
    }
    //endregion
}
