package blue.happening.chat.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import blue.happening.chat.R;

public class TestSuiteFragment extends Fragment {

    private static TestSuiteFragment instance = null;
    private static BandwidthTester bwt;
    private static FileObserver fileObserver;
//    private static String logName;
    private static TextView logContent;
    private View rootView = null;

    public static TestSuiteFragment getInstance() {
        instance = new TestSuiteFragment();

        // logContent
        startLogger();

        return instance;
    }

    private static void startLogger() {

        final String logName = Environment.getExternalStorageDirectory() + "/" + "happen.log";
        fileObserver = new FileObserver(logName) {
            @Override
            public void onEvent(int event, final String path) {
                if (event == MODIFY) {
                    logContent.post(new Runnable() {
                        public void run() {
                            final String log = readLogFile(logName);
                            Log.d("logger", path + " read " + log);
                            logContent.setText(log);
                        }
                    });
                }

                if (event == DELETE_SELF || event == DELETE) {
                    Log.d("logger", "voll");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startLogger();
                }
            }
        };

        fileObserver.startWatching();
    }

    private static String readLogFile(String logName) {
        String fileContent = "";
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

        return fileContent;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_test_suite, container, false);
        logContent = (TextView) rootView.findViewById(R.id.bandwidth_test_log);

        // bandwidth tester
        if (bwt == null) {
            bwt = new BandwidthTester();
        }

        final Button startBandwidthTest = (Button) rootView.findViewById(R.id.button_bandwidth);

        if (bwt.isRunning()) {
            startBandwidthTest.setText("Bandwidth - running");
        } else {
            startBandwidthTest.setText("Bandwidth - stopped");
        }

        startBandwidthTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bwt.isRunning()) {
                    bwt.stop();
                    startBandwidthTest.setText("Bandwidth - stopped");
                } else {
                    bwt.start();
                    startBandwidthTest.setText("Bandwidth - running");
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        fileObserver.startWatching();
    }

    @Override
    public void onStop() {
        super.onStop();
        fileObserver.stopWatching();
    }
}
