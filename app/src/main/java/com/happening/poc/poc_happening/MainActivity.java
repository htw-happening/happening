package com.happening.poc.poc_happening;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.happening.poc.poc_happening.adapter.DeviceListAdapter;
import com.happening.poc.poc_happening.fragment.Bt2Controls;
import com.happening.poc.poc_happening.fragment.Bt4Controls;
import com.happening.poc.poc_happening.fragment.BtStatus;
import com.happening.poc.poc_happening.fragment.ChatFragment;
import com.happening.poc.poc_happening.fragment.MainFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fm = getSupportFragmentManager();

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int TAG_CODE_PERMISSION_LOCATION = 2;
    private static final String HAPPENING_SERVICE_UUID = "11111111-1337-1337-1337-000000000000";
    public static final ParcelUuid parcelUuid = ParcelUuid.fromString(HAPPENING_SERVICE_UUID);

    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothLeScanner mBluetoothLeScanner = null;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;

    private ScanCallback mScanCallback = null;
    private AdvertiseCallback mAdvertiseCallback = null;

    private HashMap<String, ScanResult> mScanResults = new LinkedHashMap<>();
    private DeviceListAdapter deviceListAdapter = null;

    // Fragment
    private Fragment currentFragment = null;
    private String currentFragmentTag = null;

    private Fragment mainFragment;
    private Fragment chatFragment;
    private Fragment bt4ControlsFragment;
    private Fragment bt2ControlsFragment;
    private Fragment btStatusFragment;

    // Fragment Tags
    private static final String TAG_FRAGMENT_MAIN = "main";
    private static final String TAG_FRAGMENT_CHAT = "chat";
    private static final String TAG_FRAGMENT_BT4CONTROLS = "bt4";
    private static final String TAG_FRAGMENT_BT2CONTROLS = "bt2";
    private static final String TAG_FRAGMENT_BTSTATUS = "btstatus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.currentFragment = MainFragment.getInstance();
        this.currentFragmentTag = TAG_FRAGMENT_MAIN;
        fm.beginTransaction()
                .replace(R.id.main_fragment_holder, currentFragment, currentFragmentTag)
                .commit();

//        // set event Listener
//        Button startDiscoverButton = (Button) findViewById(R.id.discover_start_button);
//        startDiscoverButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startDiscover();
//            }
//        });
//
//        Button stopDiscoverButton = (Button) findViewById(R.id.discover_stop_button);
//        stopDiscoverButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopDiscover();
//            }
//        });
//
//        Button startAdvertiseButton = (Button) findViewById(R.id.advertise_start_button);
//        startAdvertiseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startAdvertise();
//            }
//        });
//
//        Button stopAdvertiseButton = (Button) findViewById(R.id.advertise_stop_button);
//        stopAdvertiseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopAdvertise();
//            }
//        });
//
//        // initialize bluetooth adapter
//        this.mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        this.mBluetoothAdapter = mBluetoothManager.getAdapter();
//
//        // ensure bluetooth is available and enabled
//        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//
//        // request location permission
//        ActivityCompat.requestPermissions(this, new String[]{
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION},
//                TAG_CODE_PERMISSION_LOCATION);
//
//        // initialize list view
//        ListView deviceList = (ListView) findViewById(R.id.discovered_devices_list);
//        deviceListAdapter = new DeviceListAdapter(this, mScanResults);
//        deviceList.setAdapter(deviceListAdapter);
//
//        this.mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
//        this.mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
//
//        // set scanning callback
//        this.mScanCallback = new ScanCallback() {
//            @Override
//            public void onScanResult(int callbackType, ScanResult result) {
//                super.onScanResult(callbackType, result);
//                if (!mScanResults.containsKey(result.getDevice().getAddress())) {
//                    mScanResults.put(result.getDevice().getAddress(), result);
//                    deviceListAdapter.notifyDataSetChanged();
//                }
//            }
//        };
//
//        // set advertising callback
//        this.mAdvertiseCallback = new AdvertiseCallback() {
//            @Override
//            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
//                super.onStartSuccess(settingsInEffect);
//                Log.d("DEBUG", "advertising started");
//            }
//
//            @Override
//            public void onStartFailure(int errorCode) {
//                super.onStartFailure(errorCode);
//                Log.d("DEBUG", "advertising error " + errorCode);
//            }
//        };
//
//        Log.d("SELF", mBluetoothAdapter.getName() + " " + mBluetoothAdapter.getAddress());
    }

//    private void startAdvertise() {
//
//        View view = getCurrentFocus();
//        Snackbar.make(view, "start advertise", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//
//        AdvertiseSettings.Builder advertiseSettingsBuilder = new AdvertiseSettings.Builder();
//        advertiseSettingsBuilder
//                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
//                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
//                .setTimeout(10000)
//                .setConnectable(true);
//        AdvertiseSettings advertiseSettings = advertiseSettingsBuilder.build();
//
//        String[] loads = {"happen", "foobar", "lekker", "service", "matetee"};
//        int index = new Random().nextInt(loads.length);
//        byte[] payload = loads[index].getBytes();
//        AdvertiseData.Builder advertiseDataBuilder = new AdvertiseData.Builder();
//        advertiseDataBuilder
//                .addServiceData(parcelUuid, payload)
//                .setIncludeDeviceName(true)
//                .setIncludeTxPowerLevel(true);
//        AdvertiseData advertiseData = advertiseDataBuilder.build();
//
//        mBluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, mAdvertiseCallback);
//    }
//
//    private void stopAdvertise() {
//        View view = getCurrentFocus();
//        Snackbar.make(view, "stop advertise", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//
//        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
//    }
//
//    private void startDiscover() {
//        View view = getCurrentFocus();
//        Snackbar.make(view, "start discover", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//
//        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
//        scanSettingsBuilder
//                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
//                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
//        ScanSettings scanSettings = scanSettingsBuilder.build();
//
//        List<ScanFilter> scanFilters = new ArrayList<>();
//
//        mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
//        mBluetoothLeScanner.stopScan(mScanCallback);
//        deviceListAdapter.deviceList.clear();
//        deviceListAdapter.notifyDataSetChanged();
//        mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);
//    }
//
//    private void stopDiscover() {
//        View view = getCurrentFocus();
//        Snackbar.make(view, "stop discover", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//
//        mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
//        mBluetoothLeScanner.stopScan(mScanCallback);
//    }

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.main) {
            if (this.mainFragment == null) {
                this.mainFragment = getSupportFragmentManager().findFragmentByTag(this.TAG_FRAGMENT_MAIN);
                if (this.mainFragment == null) {
                    this.mainFragment = ChatFragment.getInstance();
                }
            }

            loadFragment(currentFragment, mainFragment, TAG_FRAGMENT_MAIN);
            this.currentFragment = mainFragment;
            this.currentFragmentTag = TAG_FRAGMENT_MAIN;

        } else if (id == R.id.chat) {
            if (this.chatFragment == null) {
                this.chatFragment = getSupportFragmentManager().findFragmentByTag(this.TAG_FRAGMENT_CHAT);
                if (this.chatFragment == null) {
                    this.chatFragment = ChatFragment.getInstance();
                }
            }

            loadFragment(currentFragment, chatFragment, TAG_FRAGMENT_CHAT);
            this.currentFragment = chatFragment;
            this.currentFragmentTag = TAG_FRAGMENT_CHAT;

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
            if (this.bt2ControlsFragment == null) {
                this.bt2ControlsFragment = getSupportFragmentManager().findFragmentByTag(this.TAG_FRAGMENT_BT2CONTROLS);
                if (this.bt2ControlsFragment == null) {
                    this.bt2ControlsFragment = Bt2Controls.getInstance();
                }
            }

            loadFragment(currentFragment, bt2ControlsFragment, TAG_FRAGMENT_BT2CONTROLS);
            this.currentFragment = bt2ControlsFragment;
            this.currentFragmentTag = TAG_FRAGMENT_BT2CONTROLS;

        } else if (id == R.id.bt_status) {
            if (this.btStatusFragment == null) {
                this.btStatusFragment = getSupportFragmentManager().findFragmentByTag(this.TAG_FRAGMENT_BTSTATUS);
                if (this.btStatusFragment == null) {
                    this.btStatusFragment = BtStatus.getInstance();
                }
            }

            loadFragment(currentFragment, btStatusFragment, TAG_FRAGMENT_BTSTATUS);
            this.currentFragment = btStatusFragment;
            this.currentFragmentTag = TAG_FRAGMENT_BTSTATUS;

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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

}
