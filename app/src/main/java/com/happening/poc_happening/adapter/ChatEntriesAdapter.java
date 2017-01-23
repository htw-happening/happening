package com.happening.poc_happening.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.happening.poc_happening.R;
import com.happening.poc_happening.models.ChatEntryModel;

import java.util.ArrayList;

public class ChatEntriesAdapter extends ArrayAdapter<ChatEntryModel> {

    public ChatEntriesAdapter(Context context, ArrayList<ChatEntryModel> entries) {
        super(context, 0, entries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChatEntryModel chatEntryModel = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_entry, parent, false);
        }

        TextView author = (TextView) convertView.findViewById(R.id.textView_chat_entry_author);
        TextView content = (TextView) convertView.findViewById(R.id.textView_chat_entry_content);

        author.setText(chatEntryModel.getAuthor());
        content.setText(chatEntryModel.getContent());

        return convertView;
    }
}
