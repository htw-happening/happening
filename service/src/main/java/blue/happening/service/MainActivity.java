package blue.happening.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import blue.happening.service.fragment.Bt4Controls;

/**
 * Translucent {@link Activity activity} that finishes shortly after creation
 * without rendering anything.
 */
public class MainActivity extends FragmentActivity {

    private static final String TAG_FRAGMENT_BT4CONTROLS = "bt4";
    private static final int TAG_MULTI_PERMISSION_REQUESTS = 100;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECEIVE_BOOT_COMPLETED};
    private static Context context = null;
    private Fragment currentFragment;
    private String currentFragmentTag;
    private FragmentManager fm = getSupportFragmentManager();
    private Fragment bt4Controls = null;

    public static Context getContext() {
        return MainActivity.context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getApplicationContext();
        Log.d(this.getClass().getSimpleName(), " " + this);
        Log.v(this.getClass().getSimpleName(), "onCreate");

        setContentView(R.layout.main_fragment_holder);

        // initialise start fragment
        this.currentFragment = Bt4Controls.getInstance();
        this.currentFragmentTag = TAG_FRAGMENT_BT4CONTROLS;

        fm.beginTransaction()
                .add(R.id.main_fragment_holder, currentFragment, currentFragmentTag)
                .commit();

        List<String> requiredPermissions = new ArrayList<>();
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                requiredPermissions.add(permission);
            }
        }
        if (requiredPermissions.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    requiredPermissions.toArray(new String[0]),
                    TAG_MULTI_PERMISSION_REQUESTS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.v(this.getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }

}