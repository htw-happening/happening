package blue.happening.dashboard;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import blue.happening.dashboard.fragment.DashboardFragment;
import blue.happening.sdk.IRemoteService;
import blue.happening.sdk.ServiceHandler;

@SuppressWarnings("unused")
public class MainActivity extends Activity {

    private ServiceHandler serviceHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(this.getClass().getSimpleName(), "onCreate");
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getFragmentManager();
        Fragment dashboardFragment = new DashboardFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_holder, dashboardFragment, "dashboard")
                .commit();

        Context context = getApplicationContext();
        serviceHandler = new ServiceHandler();
        serviceHandler.register(context);
        IRemoteService remoteService = serviceHandler.getService();
        Log.i(this.getClass().getSimpleName(), "got handler " + remoteService);
    }

    @Override
    protected void onDestroy() {
        Log.v(this.getClass().getSimpleName(), "onDestroy");
        serviceHandler.deregister();
        super.onDestroy();
    }
}
