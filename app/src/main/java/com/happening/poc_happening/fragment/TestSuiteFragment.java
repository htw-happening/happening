package com.happening.poc_happening.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happening.poc_happening.R;
import com.happening.poc_happening.bluetooth.BandwidthTester;

public class TestSuiteFragment extends Fragment {

    private static TestSuiteFragment instance = null;
    private View rootView = null;

    private BandwidthTester bwt = null;

    public static TestSuiteFragment getInstance() {
        instance = new TestSuiteFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_test_suite, container, false);

        if (bwt == null) {
            bwt = new BandwidthTester();
        }
        rootView.findViewById(R.id.button_bandwidth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bwt.isRunning()) {
                    bwt.stop();
                } else {
                    bwt.start();
                }
            }
        });

        TextView log = (TextView) rootView.findViewById(R.id.bandwidth_test_log);
        log.setText(log.getText() + "\n" + "log stuff");

        return rootView;
    }
}
