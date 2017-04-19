package com.happening.poc_happening;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.happening.poc_happening.bluetooth.Layer;
import com.happening.poc_happening.fragment.Bt4Controls;

import net.sqlcipher.database.SQLiteDatabase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int TAG_CODE_PERMISSION_LOCATION = 2;
    // Fragment Tags
    private static final String TAG_FRAGMENT_CHAT = "chat";
    private static final String TAG_FRAGMENT_BT4CONTROLS = "bt4";
    private static final String TAG_FRAGMENT_BT2CONTROLS = "bt2";
    private static final String TAG_FRAGMENT_BTSTATUS = "btstatus";
    private static final String TAG_FRAGMENT_DB_TEST = "db_test";
    private static final String TAG_FRAGMENT_TEST_SUITE = "test_suite";

    private static final int TAG_PERMISSION_REQUESTS = 100;

    private FragmentManager fm = getSupportFragmentManager();
    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    // Fragment
    private Fragment currentFragment = null;
    private String currentFragmentTag = null;

    private Fragment bt4ControlsFragment;

    private String TAG = getClass().getSimpleName();

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load sqlcipher libs
        SQLiteDatabase.loadLibs(this);

        // set views
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle                (this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle (this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
//                InputMethodManager inputMethodManager = (InputMethodManager)
//                        getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                Log.d("Drawer", "OnDrawerClosed");

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
//                InputMethodManager inputMethodManager = (InputMethodManager)
//                        getSystemService(Context.INPUT_METHOD_SERVICE);
//                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                Log.d("Drawer", "OnDrawerOpened");

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                Log.d("Drawer", "OnDrawerSlide "+slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                Log.d("Drawer", "OnDrawerStateChanged "+newState);
                if (newState == DrawerLayout.STATE_SETTLING){
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

            }
        };


        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set image in drawer header
        View drawerHeader = navigationView.getHeaderView(0);
        Drawable headerImage = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_mobile);

        String deviceName = BluetoothAdapter.getDefaultAdapter().getName().toLowerCase();
        int color = 0x000000;

        if (deviceName.contains("white")) {
            color = ContextCompat.getColor(this, R.color.mobile_white);
        } else if (deviceName.contains("black")) {
            color = ContextCompat.getColor(this, R.color.mobile_black);
        } else if (deviceName.contains("red")) {
            color = ContextCompat.getColor(this, R.color.mobile_red);
        } else if (deviceName.contains("blue")) {
            color = ContextCompat.getColor(this, R.color.mobile_blue);
        } else if (deviceName.contains("yellow")) {
            color = ContextCompat.getColor(this, R.color.mobile_yellow);
        }

        headerImage.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        ((ImageView) drawerHeader.findViewById(R.id.drawer_header_image)).setImageDrawable(headerImage);

        // set device stats in drawer header
        ((TextView) drawerHeader.findViewById(R.id.drawer_header_main_text)).setText(BluetoothAdapter.getDefaultAdapter().getName());
        ((TextView) drawerHeader.findViewById(R.id.drawer_header_sub_text)).setText(Build.SERIAL);

        // initialise start fragment
        this.currentFragment = Bt4Controls.getInstance();
        this.currentFragmentTag = TAG_FRAGMENT_BT4CONTROLS;

        fm.beginTransaction()
                .replace(R.id.main_fragment_holder, currentFragment, currentFragmentTag)
                .commit();

        // initialize bluetooth adapter
        this.mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = mBluetoothManager.getAdapter();

        // ensure bluetooth is available and enabled
        Log.d(TAG, "mBluetoothAdapter.isEnabled() " + mBluetoothAdapter.isEnabled());
        Log.d(TAG, String.valueOf("mBluetoothAdapter == null " + mBluetoothAdapter == null));
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //Permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    TAG_PERMISSION_REQUESTS);
        }

        // request location permission
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                TAG_CODE_PERMISSION_LOCATION);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // don't show settings in toolbar
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.chat) {


        } else if (id == R.id.bt4controls) {
            if (this.bt4ControlsFragment == null) {
                this.bt4ControlsFragment = getSupportFragmentManager().findFragmentByTag(this.TAG_FRAGMENT_BT4CONTROLS);
                if (this.bt4ControlsFragment == null) {
                    this.bt4ControlsFragment = Bt4Controls.getInstance();
                }
            }

            loadFragment(currentFragment, bt4ControlsFragment, TAG_FRAGMENT_BT4CONTROLS);
            this.currentFragment = bt4ControlsFragment;
            this.currentFragmentTag = TAG_FRAGMENT_BT4CONTROLS;

        } else if (id == R.id.bt2controls) {


        } else if (id == R.id.bt_status) {


        } else if (id == R.id.db_test) {


        } else if (id == R.id.test_suite) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment current, Fragment fragment, String tag) {
        if (fm == null) {
            fm = getSupportFragmentManager();
        }

        fm.beginTransaction()
                .replace(current.getId(), fragment, tag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case TAG_PERMISSION_REQUESTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!


                } else {
                    // permission denied, boo!
                }
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        Layer layer = Layer.getInstance();
        //layer.stopScan();
        layer.stopAdvertising();
        layer.stopGattServer();
        super.onDestroy();
    }
}