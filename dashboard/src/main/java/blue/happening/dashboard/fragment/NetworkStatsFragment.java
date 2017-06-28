package blue.happening.dashboard.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import blue.happening.dashboard.R;

/**
 * Created by kaischulz on 28.06.17.
 */

public class NetworkStatsFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    private static NetworkStatsFragment instance = null;

    public NetworkStatsFragment() {
    }

    public static NetworkStatsFragment getInstance() {
        if (instance == null)
            instance = new NetworkStatsFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.v(this.getClass().getSimpleName(), "onCreateView");
        final View rootView = inflater.inflate(R.layout.fragment_network_stats, container, false);

        TextView stats = (TextView) rootView .findViewById(R.id.stats);
        stats.setText("123");

        return rootView;
    }

}
