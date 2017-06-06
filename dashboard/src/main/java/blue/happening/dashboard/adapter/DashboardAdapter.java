package blue.happening.dashboard.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import blue.happening.dashboard.R;
import blue.happening.dashboard.model.DashboardModel;


public class DashboardAdapter extends ArrayAdapter<DashboardModel> {

    public DashboardAdapter(Context context, List<DashboardModel> models) {
        super(context, 0, models);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        DashboardModel dashboardModel = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dashboard_model, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.dashboard_model_title);
        TextView message = (TextView) convertView.findViewById(R.id.dashboard_model_message);

        if (dashboardModel != null) {
            title.setText(dashboardModel.getTitle());
            message.setText(dashboardModel.getMessage());
        }

        return convertView;
    }
}
