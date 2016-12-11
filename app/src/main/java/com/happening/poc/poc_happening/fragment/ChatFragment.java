package com.happening.poc.poc_happening.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.happening.poc.poc_happening.R;
import com.happening.poc.poc_happening.adapter.ChatEntriesAdapter;
import com.happening.poc.poc_happening.adapter.ChatEntryModel;

import java.util.ArrayList;

/**
 * Created by kaischulz on 10.12.16.
 */

public class ChatFragment extends Fragment {

    private static ChatFragment instance = null;
    private View rootView = null;

    private ListView listView;
    public ArrayList<ChatEntryModel> chatEntryModelArrayList;
    private ChatEntriesAdapter chatEntriesAdapter;

    public static ChatFragment getInstance() {
        instance = new ChatFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        chatEntryModelArrayList = new ArrayList<>();
        chatEntriesAdapter = new ChatEntriesAdapter(getContext(), chatEntryModelArrayList);
        listView = (ListView) rootView.findViewById(R.id.listView_chat_entries);
        listView.setAdapter(chatEntriesAdapter);

        return rootView;
    }

    private void addChatEntry(String author, String content){
        ChatEntryModel chatEntryModel = new ChatEntryModel(author, content);
        chatEntryModelArrayList.add(chatEntryModel);
        chatEntriesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO - remove
        addChatEntry("Peter","Hi");
        addChatEntry("Hans","Selber Hai!");
        addChatEntry("Torben","Wer is Kai?");
    }
}
