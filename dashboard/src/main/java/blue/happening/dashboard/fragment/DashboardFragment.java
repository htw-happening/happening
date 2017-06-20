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
import blue.happening.dashboard.R;
import blue.happening.dashboard.adapter.DashboardAdapter;
import blue.happening.sdk.Happening;
import blue.happening.sdk.HappeningCallback;

public class DashboardFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private static DashboardFragment instance = null;
    private List<HappeningClient> dashboardClients;
    private DashboardAdapter dashboardAdapter;
    private ListView listView;
    private Happening happening;

    public DashboardFragment() {
        happening = new Happening();
        dashboardClients = new ArrayList<>();
    }

    public static DashboardFragment getInstance() {
        if (instance == null)
            instance = new DashboardFragment();
        return instance;
    }

    public Happening getHappening() {
        return happening;
    }

    private HappeningCallback happeningCallback = new HappeningCallback() {
        // TODO: These callback methods don't do anything useful yet.
        @Override
        public void onClientAdded(HappeningClient client) {
            dashboardClients.add(client);
            dashboardAdapter.notifyDataSetChanged();
        }

        @Override
        public void onClientUpdated(HappeningClient client) {
            onClientRemoved(client);
            onClientAdded(client);
            dashboardAdapter.notifyDataSetChanged();
        }

        @Override
        public void onClientRemoved(HappeningClient client) {
            HappeningClient removeDevice = null;
            for (HappeningClient candidate : dashboardClients) {
                if (candidate.getUuid().equals(client.getUuid())) {
                    removeDevice = candidate;
                }
            }
            if (removeDevice != null) {
                dashboardClients.remove(removeDevice);
            }
            dashboardAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMessageReceived(byte[] message, HappeningClient source) {
            Log.v(TAG, "onMessageReceived: " + new String(message) + " from " + source.getUuid());
            Context context = getActivity().getApplicationContext();
            Toast.makeText(context, new String(message), Toast.LENGTH_LONG).show();
        }
    };

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
                List<HappeningClient> clients = happening.getClients();
                dashboardClients.clear();
                for (HappeningClient client : clients) {
                    dashboardClients.add(client);
                    Log.d(TAG, "onClick: " + client.getName());
                }
                dashboardAdapter.notifyDataSetChanged();
            }
        });

        dashboardAdapter = new DashboardAdapter(container.getContext(), dashboardClients);
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
