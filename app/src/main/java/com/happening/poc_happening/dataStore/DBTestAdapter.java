package com.happening.poc_happening.dataStore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.happening.poc_happening.R;

import java.util.ArrayList;

public class DBTestAdapter extends ArrayAdapter<DBEntryModel> {

    public DBTestAdapter(Context context, ArrayList<DBEntryModel> entries) {
        super(context, 0, entries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DBEntryModel dbEntryModel = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.db_entry, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.textView_db_entry_name);
        TextView address = (TextView) convertView.findViewById(R.id.textView_db_entry_address);
        TextView lastSeen = (TextView) convertView.findViewById(R.id.textView_db_entry_lastSeen);

        name.setText(dbEntryModel.getName());
        address.setText(dbEntryModel.getAddress());
        lastSeen.setText(dbEntryModel.getLastSeen());

        return convertView;
    }
}
