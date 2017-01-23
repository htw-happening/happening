package com.happening.poc_happening.datastore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.happening.poc_happening.R;

import java.util.ArrayList;

public class DBTestAdapter extends ArrayAdapter<DBTestEntryModel> {

    public DBTestAdapter(Context context, ArrayList<DBTestEntryModel> entries) {
        super(context, 0, entries);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DBTestEntryModel dbTestEntryModel = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.db_entry, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.textView_db_entry_name);
        TextView address = (TextView) convertView.findViewById(R.id.textView_db_entry_address);
        TextView lastSeen = (TextView) convertView.findViewById(R.id.textView_db_entry_lastSeen);

        name.setText(dbTestEntryModel.getName());
        address.setText(dbTestEntryModel.getAddress());
        lastSeen.setText(dbTestEntryModel.getLastSeen());

        return convertView;
    }
}
