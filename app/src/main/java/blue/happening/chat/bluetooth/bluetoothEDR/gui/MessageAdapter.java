package blue.happening.chat.bluetooth.bluetoothEDR.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import blue.happening.chat.R;

/**
 * Created by Fabian on 08.12.2016.
 */

public class MessageAdapter extends ArrayAdapter<MessageModel> {

    public MessageAdapter(Context context, ArrayList<MessageModel> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MessageModel messageModel = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_entry, parent, false);
        }
        // accessing the gui elements
        TextView from = (TextView) convertView.findViewById(R.id.textView_chat_entry_author);
        TextView content = (TextView) convertView.findViewById(R.id.textView_chat_entry_content);

        //Setting the gui elements to the properties of the messageModel
        from.setText(messageModel.getFrom());
        content.setText(messageModel.getContent());

        return convertView;
    }
}
