package blue.happening.dashboard.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import blue.happening.dashboard.R;
import blue.happening.dashboard.adapter.DashboardAdapter;
import blue.happening.dashboard.model.DashboardModel;


public class DashboardFragment extends Fragment {

    private List<DashboardModel> dashboardModels = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.v(this.getClass().getSimpleName(), "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        DashboardAdapter dashboardAdapter = new DashboardAdapter(getContext(), dashboardModels);
        ListView listView = (ListView) rootView.findViewById(R.id.dashboard_model_list);
        listView.setAdapter(dashboardAdapter);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        Log.v(this.getClass().getSimpleName(), "onDestroyView");
        super.onDestroyView();
    }
}
