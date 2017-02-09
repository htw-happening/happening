package com.happening.poc_happening.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.happening.poc_happening.MainActivity;
import com.happening.poc_happening.MyApp;
import com.happening.poc_happening.R;
import com.happening.poc_happening.adapter.ChatEntriesAdapter;
import com.happening.poc_happening.datastore.DBHelper;
import com.happening.poc_happening.bluetooth.Layer;
import com.happening.poc_happening.models.ChatEntryModel;

import java.util.ArrayList;


public class ChatFragment extends Fragment {

    private static ChatFragment instance = null;
    private Layer bluetoothLayer = null;
    public ArrayList<ChatEntryModel> chatEntryModelArrayList;

    private View rootView = null;
    private DBHelper dbHelper;
    private ListView listView;
    private ChatEntriesAdapter chatEntriesAdapter;

    public static ChatFragment getInstance() {
        if (instance == null) {
            instance = new ChatFragment();
        }
        return instance;
    }

    public ChatFragment() {
        bluetoothLayer = Layer.getInstance();
        if (!bluetoothLayer.isAdvertisingSupported()) {
            //ay caramba - this isnt good
            Toast.makeText(MyApp.getAppContext(), "Advertising not supported!", Toast.LENGTH_LONG).show();
        } else {
            bluetoothLayer.setAutoConnect(true);
            bluetoothLayer.createGattServer();
            bluetoothLayer.startAdvertising();
            bluetoothLayer.startScan();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dbHelper = new DBHelper(this.getContext());

        rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        // init chatEntryModel from DB
        chatEntryModelArrayList = dbHelper.getAllGlobalMessagesRaw();

        chatEntriesAdapter = new ChatEntriesAdapter(getContext(), chatEntryModelArrayList);
        listView = (ListView) rootView.findViewById(R.id.chat_entries_list);
        listView.setAdapter(chatEntriesAdapter);

        rootView.findViewById(R.id.imageView_send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Handle Message for Sending
                String message = ((EditText) rootView.findViewById(R.id.editText_message_input)).getText().toString();
                if (message.length() == 0) {
                    //was empty
                    Toast.makeText(rootView.getContext(), "Type Something", Toast.LENGTH_SHORT).show();
                } else {
                    addChatEntry("You", message);
                    ((EditText) rootView.findViewById(R.id.editText_message_input)).setText("");

                    bluetoothLayer.broadcastMessage(message);
                }
            }
        });

        return rootView;
    }

    private void addChatEntry(String author, String content) {

        // Use ByteArrayModelFactory.createChatEntryModel(bytes); in the Future
        ChatEntryModel chatEntryModel = new ChatEntryModel(author, "test", "test", content);

        dbHelper.insertGlobalMessage(author, "test", "test", content);
        chatEntryModelArrayList.add(chatEntryModel);
        chatEntriesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        bluetoothLayer.addHandler(guiHandler);
        super.onResume();
    }

    @Override
    public void onPause() {
        bluetoothLayer.removeHandler(guiHandler);
        super.onPause();
    }

    private Handler guiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Layer.DEVICE_POOL_UPDATED:
                    break;
                case Layer.MESSAGE_RECEIVED:
                    String content = msg.getData().getString("content");
                    String author = msg.getData().getString("author");
                    Log.i("HANDLER", "" + author + " says " + content);
                    addChatEntry(author, content);
                    break;
                default:
                    Log.i("HANDLER", "Unresolved Message Code");
                    break;
            }
        }
    };
}

