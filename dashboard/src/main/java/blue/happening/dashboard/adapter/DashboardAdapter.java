package blue.happening.dashboard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import blue.happening.HappeningClient;
import blue.happening.dashboard.R;
import blue.happening.dashboard.fragment.DashboardFragment;


public class DashboardAdapter extends ArrayAdapter<HappeningClient> {

    public DashboardAdapter(Context context, List<HappeningClient> models) {
        super(context, 0, models);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        HappeningClient happeningClient = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dashboard_model, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.dashboard_model_title);
        TextView message = (TextView) convertView.findViewById(R.id.dashboard_model_message);

        if (happeningClient != null) {
            title.setText(happeningClient.getName());
            message.setText(happeningClient.getUuid());
        }

        convertView.findViewById(R.id.dashboard_button_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Send", Toast.LENGTH_SHORT).show();
                String message = "Hey there, i'm using happening!";
                HappeningClient client = getItem(position);
                if (client != null) {
                    Log.d(getClass().getSimpleName(), "Sending Message to " + client.getUuid());
                    DashboardFragment.getInstance().getHappening().sendMessage(message.getBytes(), client);
                }
            }
        });

        return convertView;
    }
}
