package com.happening.poc_happening.fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.happening.poc_happening.R;
import com.happening.poc_happening.bluetooth.bluetoothEDR.Connection.BluetoothService;
import com.happening.poc_happening.bluetooth.bluetoothEDR.Misc.Constants;
import com.happening.poc_happening.bluetooth.bluetoothEDR.gui.MessageAdapter;
import com.happening.poc_happening.bluetooth.bluetoothEDR.gui.MessageModel;

import java.util.ArrayList;

public class Bt2Controls extends Fragment {

    private static Bt2Controls instance = null;
    private View rootView = null;

    private String TAG = this.getClass().getSimpleName();
    private boolean d = true;

    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothService bluetoothService = null;

    private ListView listView_Messages;
    public ArrayList<MessageModel> messageModelArrayList;
    private MessageAdapter messageAdapter;

    public static Bt2Controls getInstance() {
        instance = new Bt2Controls();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_b2controls, container, false);

        if (d) Log.d(TAG, "start OnCreate");

        setHasOptionsMenu(true);

        messageModelArrayList = new ArrayList<MessageModel>();
        messageAdapter = new MessageAdapter(getContext(), messageModelArrayList);
        listView_Messages = (ListView) rootView.findViewById(R.id.listView_messages);
        listView_Messages.setAdapter(messageAdapter);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView_send_message);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (d) Log.d(TAG, "on button send clicked");
                sendMessage();
            }
        });

        //TODO
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(getContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }

        if (!bluetoothAdapter.isEnabled()) {
            if (d) Log.d(TAG, "Bluetooth wasnt enabbled --> enable");
            //TODO
            bluetoothAdapter.enable();
        }

        if (d) Log.d(TAG, "end OnCreate");

        rootView.findViewById(R.id.button_chat_edr_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothAdapter.startDiscovery();
            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        makeDeviceVisible();

        if (bluetoothService == null) {
            bluetoothService = new BluetoothService(getContext(), guiHandler);
            if (d) Log.d(TAG, "new BluetoothService");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (d) Log.d(TAG, "onResume");
        if (bluetoothService == null){
            bluetoothService = new BluetoothService(getContext(), guiHandler);
            if (d) Log.d(TAG, "new BluetoothService");
        }

        if (d) Log.d(TAG, "BluetoothService was not null");
        if (bluetoothService.getState() == BluetoothService.STATE_NONE) {
            bluetoothService.start();
            if (d) Log.d(TAG, "Starting BlueothService");
        }

        if (d) Log.d(TAG, "Registration if Filters for Autoconnector");
        bluetoothAdapter.cancelDiscovery();
        registrateBroadcastReceiver();
        if (d) Log.d(TAG, "----------------------------------onResume end");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void makeDeviceVisible() {
        Intent makeMeVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        makeMeVisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0); //infinity
        startActivity(makeMeVisible);
    }

    private void registrateBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        getContext().registerReceiver(bluetoothService.autoConnector, filter);
    }

    private void sendMessage() {
        String message = ((TextView)rootView.findViewById(R.id.editText_message_input)).getText().toString();
        if (d) Log.d(TAG, "From Textview "+message);
        if (message.length() == 0){
            //was empty
            if (d) Log.d(TAG, "message to send was empty");
            Toast.makeText(getContext(),"Type Something",Toast.LENGTH_SHORT).show();
        }else{
            appendMessageToMessageBox("You", message, false);
            ((TextView)rootView.findViewById(R.id.editText_message_input)).setText("");
            bluetoothService.broadCastAMessage(message);
        }
    }

    private void appendMessageToMessageBox(String author, String message, boolean isLog){
        MessageModel messageModel = new MessageModel(author,message, isLog);
        messageModelArrayList.add(messageModel);
        messageAdapter.notifyDataSetChanged();
    }

    private final Handler guiHandler = new Handler() {
        /**
         * Callback method
         * @param msg see Constants
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.HANDLER_MESSAGE_RECEIVED:
                    //for logging
                    appendMessageToMessageBox(msg.getData().getString(Constants.BUNDLE_PACKAGE_AUTHOR),
                            msg.getData().getString(Constants.BUNDLE_PACKAGE_CONTENT), false);
                    break;

                case Constants.HANDLER_MESSAGE_BOX:
                    appendMessageToMessageBox(msg.getData().getString(Constants.BUNDLE_MESSAGEBOX_WHO),
                            msg.getData().getString(Constants.BUNDLE_MESSAGEBOX_WHAT), true);
                    break;
            }
        }
    };
}
