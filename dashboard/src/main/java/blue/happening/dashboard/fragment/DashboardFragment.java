package blue.happening.dashboard.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import blue.happening.dashboard.MainActivity;
import blue.happening.dashboard.R;
import blue.happening.dashboard.adapter.DashboardAdapter;
import blue.happening.dashboard.model.DashboardModel;
import blue.happening.sdk.Happening;

public class DashboardFragment extends Fragment implements View.OnClickListener  {

    private final List<DashboardModel> dashboardModels = new ArrayList<>();
    private DashboardAdapter dashboardAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.v(this.getClass().getSimpleName(), "onCreateView");
        final View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Button button = (Button) rootView.findViewById(R.id.dashboard_button_hello);
        button.setOnClickListener(this);

        dashboardAdapter = new DashboardAdapter(container.getContext(), dashboardModels);
        ListView listView = (ListView) rootView.findViewById(R.id.dashboard_model_list);
        listView.setAdapter(dashboardAdapter);



        return rootView;
    }

    @Override
    public void onDestroyView() {
        Log.v(this.getClass().getSimpleName(), "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        Log.v(this.getClass().getSimpleName(), "onClick");
        String message = "dashboard@" + android.os.Process.myPid();
        Happening happening = ((MainActivity) getActivity()).getHappening();
        dashboardModels.add(new DashboardModel("hello", message));
        dashboardAdapter.notifyDataSetChanged();
        String reply = happening.hello(message);
        dashboardModels.add(new DashboardModel("reply", reply));
        dashboardAdapter.notifyDataSetChanged();
    }
}
