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

import blue.happening.HappeningClient;
import blue.happening.dashboard.R;
import blue.happening.dashboard.adapter.DeviceAdapter;
import blue.happening.dashboard.logic.BlueCallback;
import blue.happening.dashboard.logic.BlueDashboard;

public class DeviceFragment extends Fragment implements BlueCallback {

    private static DeviceFragment instance = null;
    private final String TAG = this.getClass().getSimpleName();
    private List<HappeningClient> dashboardClients = new ArrayList<>();
    private DeviceAdapter deviceAdapter;
    private ListView listView;

    private BlueDashboard blueDashboard = null;

    public DeviceFragment() {

    }

    public static DeviceFragment getInstance() {
        if (instance == null)
            instance = new DeviceFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.v(this.getClass().getSimpleName(), "onCreateView");
        final View rootView = inflater.inflate(R.layout.fragment_device, container, false);

        blueDashboard = BlueDashboard.getInstance();
        blueDashboard.register(this);

        deviceAdapter = new DeviceAdapter(container.getContext(), dashboardClients);
        listView = (ListView) rootView.findViewById(R.id.dashboard_model_list);
        listView.setAdapter(deviceAdapter);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        Log.v(this.getClass().getSimpleName(), "onDestroyView");
        super.onDestroyView();
        blueDashboard.deregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        blueDashboard.register(this);
    }

    @Override
    public void onClientAdded() {
        dashboardClients.clear();
        dashboardClients.addAll(BlueDashboard.getInstance().getDevices());
        Log.d(TAG, "dashboard client " + dashboardClients.size());
        notifyDataSetChanged();

    }

    @Override
    public void onClientUpdate() {
        dashboardClients.clear();
        dashboardClients.addAll(BlueDashboard.getInstance().getDevices());
        Log.d(TAG, "dashboard client " + dashboardClients.size());
        notifyDataSetChanged();

    }

    private void notifyDataSetChanged() {
        getActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        deviceAdapter.notifyDataSetChanged();
                    }
                }
        );
    }
}
