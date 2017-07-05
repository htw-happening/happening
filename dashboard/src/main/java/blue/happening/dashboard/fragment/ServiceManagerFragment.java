package blue.happening.dashboard.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import blue.happening.dashboard.R;
import blue.happening.dashboard.logic.BlueDashboard;

public class ServiceManagerFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    private static ServiceManagerFragment instance = null;

    public ServiceManagerFragment() {
    }

    public static ServiceManagerFragment getInstance() {
        if (instance == null)
            instance = new ServiceManagerFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.v(this.getClass().getSimpleName(), "onCreateView");
        final View rootView = inflater.inflate(R.layout.fragment_service_manager, container, false);

        Button startButton = (Button) rootView.findViewById(R.id.start_service);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlueDashboard.getInstance().getHappening().startHappeningService();

                // TODO TEST Boradcast Message
                BlueDashboard.getInstance().getHappening().sendMessage("jojo".getBytes());

            }
        });

        Button restartButton = (Button) rootView.findViewById(R.id.restart_service);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlueDashboard.getInstance().getHappening().restartHappeningService();
            }
        });

        Button stopButton = (Button) rootView.findViewById(R.id.stop_service);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BlueDashboard.getInstance().getHappening().stopHappeningService();
            }
        });

        return rootView;
    }

}
