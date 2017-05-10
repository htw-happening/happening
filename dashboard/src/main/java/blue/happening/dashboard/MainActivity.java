package blue.happening.dashboard;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import blue.happening.dashboard.fragment.DashboardFragment;
import blue.happening.dashboard.fragment.MyHappeningCallback;
import blue.happening.sdk.Happening;


public class MainActivity extends Activity {

    private Happening happening;

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
        happening = new Happening();
        happening.register(context, new MyHappeningCallback());


    }

    public Happening getHappening() {
        return happening;
    }

    @Override
    protected void onDestroy() {
        Log.v(this.getClass().getSimpleName(), "onDestroy");
        happening.deregister();
        super.onDestroy();
    }
}
