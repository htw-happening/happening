package com.happening.poc_happening.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happening.poc_happening.R;
import com.happening.poc_happening.bluetooth.BandwidthTester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

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

        // bandwidth tester
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

        // log
        String logName = Environment.getExternalStorageDirectory() + "/" + "happen.log";
        String content = "";

        try {
            File log = new File(logName);
            FileInputStream fi = new FileInputStream(log);
            BufferedReader br = new BufferedReader(new InputStreamReader(fi));
            String readLine;
            while ((readLine = br.readLine()) != null) {
                content += readLine;
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView log = (TextView) rootView.findViewById(R.id.bandwidth_test_log);
        log.setText(content);


        return rootView;
    }
}
