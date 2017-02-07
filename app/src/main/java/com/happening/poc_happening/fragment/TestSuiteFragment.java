package com.happening.poc_happening.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ScrollView;

import com.happening.poc_happening.R;
import com.happening.poc_happening.bluetooth.BandwidthTester;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestSuiteFragment extends Fragment {

    private static TestSuiteFragment instance = null;
    private View rootView = null;

    private BandwidthTester bwt = null;
    private FileObserver fileObserver = null;
    private String logName;
    private WebView logContent;
    private String fileContent = "";

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

        // logContent
        logName = Environment.getExternalStorageDirectory() + "/" + "happen.log";
        logContent = (WebView) rootView.findViewById(R.id.bandwidth_test_log);

        fileObserver = new FileObserver(logName) {
            @Override
            public void onEvent(int event, String path) {
                if (event == MODIFY) {
                    Log.d("MODIFY", "" + event + " " + path);
                    readLogFile();
                }
            }
        };

        readLogFile();
        fileObserver.startWatching();

        return rootView;
    }

    private void readLogFile() {
        try {
            File log = new File(logName);
            FileInputStream fi = new FileInputStream(log);
            BufferedReader br = new BufferedReader(new InputStreamReader(fi));
            String readLine;
            while ((readLine = br.readLine()) != null) {
                fileContent += readLine;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        logContent.post(new Runnable() {
            public void run() {
                logContent.loadData("<body>" + fileContent + "</body", "text/html", null);
            }
        });

//        scrollDown((ScrollView) rootView.findViewById(R.id.log_scroll));
    }

    private void scrollDown(final ScrollView scrollView) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onResume() {
        fileObserver.startWatching();
        super.onResume();
    }

    @Override
    public void onStop() {
        fileObserver.stopWatching();
        super.onStop();
    }
}
