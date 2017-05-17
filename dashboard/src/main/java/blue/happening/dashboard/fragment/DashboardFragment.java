package blue.happening.dashboard.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import blue.happening.HappeningClient;
import blue.happening.dashboard.R;
import blue.happening.dashboard.adapter.DashboardAdapter;
import blue.happening.dashboard.model.DashboardModel;
import blue.happening.sdk.Happening;
import blue.happening.sdk.HappeningCallback;


public class DashboardFragment extends Fragment implements View.OnClickListener {

    private List<DashboardModel> dashboardModels = new ArrayList<>();
    private DashboardAdapter dashboardAdapter;
    private Happening happening = new Happening();
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
    };

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

        Context context = getActivity().getApplicationContext();
        happening.register(context, happeningCallback);

        // Retrieve an initial set of clients from the service
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (HappeningClient client : happening.getClients()) {
                    dashboardModels.add(new DashboardModel(client.getClientId(), client.getClientName()));
                }
                dashboardAdapter.notifyDataSetChanged();
            }
        }, 500);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        Log.v(this.getClass().getSimpleName(), "onDestroyView");
        super.onDestroyView();
        happening.deregister();
    }

    @Override
    public void onClick(View v) {
        Log.v(this.getClass().getSimpleName(), "onClick");
        String message = "dashboard@" + android.os.Process.myPid();
        dashboardModels.add(new DashboardModel("hello", message));
        String reply = happening.hello(message);
        dashboardModels.add(new DashboardModel("reply", reply));
        dashboardAdapter.notifyDataSetChanged();
    }
}
