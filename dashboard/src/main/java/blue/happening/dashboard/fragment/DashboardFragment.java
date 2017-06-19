package blue.happening.dashboard.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import blue.happening.HappeningClient;
import blue.happening.dashboard.MyApplication;
import blue.happening.dashboard.R;
import blue.happening.dashboard.adapter.DashboardAdapter;
import blue.happening.dashboard.model.DashboardModel;
import blue.happening.sdk.Happening;
import blue.happening.sdk.HappeningCallback;

public class DashboardFragment extends Fragment {

    private static DashboardFragment instance = null;
    private final String TAG = this.getClass().getSimpleName();
    private List<DashboardModel> dashboardModels = new ArrayList<>();
    private DashboardAdapter dashboardAdapter;
    ListView listView;

    private Happening happening = new Happening();

    public Happening getHappening() {
        return happening;
    }

    private HappeningCallback happeningCallback = new HappeningCallback() {
        // TODO: These callback methods don't do anything useful yet.
        @Override
        public void onClientAdded(String client) {
            Log.v(this.getClass().getSimpleName(), "onClientAdded " + client);
            dashboardModels.add(new DashboardModel("add", client));
            dashboardAdapter.notifyDataSetChanged();
        }

        @Override
        public void onClientUpdated(String client) {
            Log.v(this.getClass().getSimpleName(), "onClientUpdated " + client);
            dashboardModels.add(new DashboardModel("update", client));
            dashboardAdapter.notifyDataSetChanged();
        }

        @Override
        public void onClientRemoved(String client) {
            Log.v(this.getClass().getSimpleName(), "onClientRemoved " + client);
            dashboardModels.add(new DashboardModel("remove", client));
            dashboardAdapter.notifyDataSetChanged();
        }

        @Override
        public void onParcelQueued(long parcelId) {
            Log.v(this.getClass().getSimpleName(), "onParcelQueued");
            dashboardModels.add(new DashboardModel("queue", "" + parcelId));
            dashboardAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMessageReceived(byte[] message, int deviceId) {
            Log.d(TAG, "onMessageReceived: " + new String(message));
            Toast.makeText(MyApplication.getAppContext(), String.valueOf(message), Toast.LENGTH_SHORT).show();
        }
    };

    public DashboardFragment() {

    }

    public static DashboardFragment getInstance() {
        if (instance == null)
            instance = new DashboardFragment();

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.v(this.getClass().getSimpleName(), "onCreateView");
        final View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Button button = (Button) rootView.findViewById(R.id.dashboard_button_get_devices);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(this.getClass().getSimpleName(), "onClick");
                List<HappeningClient> devices = happening.getDevices();

                dashboardModels.clear();
                for (HappeningClient device : devices) {
                    dashboardModels.add(new DashboardModel(device.getClientName(), device.getClientId()));
                    Log.d(TAG, "onClick: " + device.getClientName());
                }
                dashboardAdapter.notifyDataSetChanged();
            }
        });

        dashboardAdapter = new DashboardAdapter(container.getContext(), dashboardModels);
        listView = (ListView) rootView.findViewById(R.id.dashboard_model_list);
        listView.setAdapter(dashboardAdapter);

        Context context = getActivity().getApplicationContext();
        happening.register(context, happeningCallback);


        return rootView;
    }

    @Override
    public void onDestroyView() {
        Log.v(this.getClass().getSimpleName(), "onDestroyView");
        super.onDestroyView();
        happening.deregister();
    }

}
