package blue.happening.chat.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import blue.happening.bluetooth.Layer;
import blue.happening.chat.R;
import blue.happening.chat.adapter.ChatEntriesAdapter;
import blue.happening.chat.models.ChatEntryModel;
import blue.happening.chat.service.ServiceHandler;

// import blue.happening.poc_happening.datastore.DBHelper;

public class ChatFragment extends Fragment {

    private static ChatFragment instance = null;
    public ArrayList<ChatEntryModel> chatEntryModelArrayList;
    private ServiceHandler service = null;
    private View rootView = null;
    // private DBHelper dbHelper;
    private ListView listView;
    private ChatEntriesAdapter chatEntriesAdapter;
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

    public ChatFragment() {
        service = ServiceHandler.getInstance();
//        service.addHandler(guiHandler);
    }

    public static ChatFragment getInstance() {
        if (instance == null) {
            instance = new ChatFragment();
        }
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // dbHelper = DBHelper.getInstance();

        rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        // init chatEntryModel from DB+
        // chatEntryModelArrayList = dbHelper.getAllGlobalMessagesRaw();
        chatEntryModelArrayList = new ArrayList<>();

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

                    service.broadcastMessage(message);
                }
            }
        });

        return rootView;
    }

    private void addChatEntry(String author, String content) {

        // Use ByteArrayModelFactory.createChatEntryModel(bytes); in the Future
        ChatEntryModel chatEntryModel = new ChatEntryModel(author, "test", "test", content);

        chatEntryModelArrayList.add(chatEntryModel);
        chatEntriesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
//        service.addHandler(guiHandler);
        super.onResume();
    }

    @Override
    public void onPause() {
//        service.removeHandler(guiHandler);
        super.onPause();
    }
}

