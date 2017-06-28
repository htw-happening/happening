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
import blue.happening.dashboard.logic.BlueCallback;
import blue.happening.dashboard.logic.BlueDashboard;
import blue.happening.dashboard.R;
import blue.happening.dashboard.adapter.DeviceAdapter;

public class DeviceFragment extends Fragment implements BlueCallback {

    private static DeviceFragment instance = null;
    private final String TAG = this.getClass().getSimpleName();
    private List<HappeningClient> dashboardClients = new ArrayList<>();
    private DeviceAdapter dashboardAdapter;
    private ListView listView;

    private BlueDashboard blueDashboard = null;
//    private Happening happening = new Happening();

    public DeviceFragment() {
        blueDashboard = BlueDashboard.getInstance();
        blueDashboard.register(this);
    }

    public static DeviceFragment getInstance() {
        if (instance == null)
            instance = new DeviceFragment();
        return instance;
    }

//    public Happening getHappening() {
//        return happening;
//    }

//    private HappeningCallback happeningCallback = new HappeningCallback() {
//        // TODO: These callback methods don't do anything useful yet and are ugly af
//
//        private void notifyDataSetChanged() {
//            getActivity().runOnUiThread(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            dashboardAdapter.notifyDataSetChanged();
//                        }
//                    }
//            );
//        }
//
//        private void toast(final String message) {
//            getActivity().runOnUiThread(
//                    new Runnable() {
//                        @Override
//                        public void run() {
//                            Context context = getActivity().getApplicationContext();
//                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//                        }
//                    }
//            );
//        }
//
//        @Override
//        public void onClientAdded(HappeningClient client) {
//            dashboardClients.add(client);
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public void onClientUpdated(HappeningClient client) {
//            HappeningClient removeDevice = null;
//            for (HappeningClient candidate : dashboardClients) {
//                if (candidate.getUuid().equals(client.getUuid())) {
//                    removeDevice = candidate;
//                }
//            }
//            try {
//                dashboardClients.remove(removeDevice);
//            } catch (Exception ignored) {
//                return;
//            }
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public void onClientRemoved(HappeningClient client) {
//            HappeningClient removeDevice = null;
//            for (HappeningClient candidate : dashboardClients) {
//                if (candidate.getUuid().equals(client.getUuid())) {
//                    removeDevice = candidate;
//                }
//            }
//            try {
//                dashboardClients.remove(removeDevice);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public void onMessageReceived(byte[] message, HappeningClient source) {
//            Log.v(TAG, "onMessageReceived: " + new String(message) + " from " + source.getUuid());
//            toast(new String(message));
//        }
//    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.v(this.getClass().getSimpleName(), "onCreateView");
        final View rootView = inflater.inflate(R.layout.fragment_device, container, false);

//        Button button = (Button) rootView.findViewById(R.id.dashboard_button_get_devices);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.v(this.getClass().getSimpleName(), "onClick");
//                List<HappeningClient> clients = happening.getClients();
//                dashboardClients.clear();
//                for (HappeningClient client : clients) {
//                    dashboardClients.add(client);
//                    Log.d(TAG, "onClick: " + client.getName());
//                }
//
//                getActivity().runOnUiThread(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                dashboardAdapter.notifyDataSetChanged();
//                            }
//                        }
//                );
//            }
//        });

        dashboardAdapter = new DeviceAdapter(container.getContext(), dashboardClients);
        listView = (ListView) rootView.findViewById(R.id.dashboard_model_list);
        listView.setAdapter(dashboardAdapter);

//        Context context = getActivity().getApplicationContext();
//        happening.register(context, happeningCallback);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        Log.v(this.getClass().getSimpleName(), "onDestroyView");
        super.onDestroyView();
        blueDashboard.deregister(this);
    }

    @Override
    public void onClientAdded() {
        List<HappeningClient> clients = blueDashboard.getHappening().getClients();
        dashboardClients.clear();
        for (HappeningClient client : clients) {
            dashboardClients.add(client);
            Log.d(TAG, "onClick: " + client.getName());
        }

        getActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        dashboardAdapter.notifyDataSetChanged();
                    }
                }
        );
    }

    @Override
    public void onClientUpdate() {
        List<HappeningClient> clients = blueDashboard.getHappening().getClients();
        dashboardClients.clear();
        for (HappeningClient client : clients) {
            dashboardClients.add(client);
            Log.d(TAG, "onClick: " + client.getName());
        }

        getActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        dashboardAdapter.notifyDataSetChanged();
                    }
                }
        );
    }
}
