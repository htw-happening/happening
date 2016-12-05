package com.happening.poc.poc_happening;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothClass.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String TAG = this.getClass().getSimpleName();

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int TAG_CODE_PERMISSION_LOCATION = 2 ;

    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothGattServer mBluetoothGattServer = null;
    private BluetoothAdapter.LeScanCallback mLeScanCallback = null;
    private ScanCallback mScanCallback = null;

    private ArrayList<BluetoothDevice> mDiscoveredDevices = new ArrayList<>();
    private DeviceListAdapter deviceListAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set event Listener
        Button discoverStartButtom = (Button) findViewById(R.id.discover_start_button);
        discoverStartButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscoverMode();
            }
        });

        Button discoverStopButtom = (Button) findViewById(R.id.discover_stop_button);
        discoverStopButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDiscoverMode();
            }
        });

        Button serverStartButton = (Button) findViewById(R.id.server_start_button);
        serverStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServer();
            }
        });

        // Initializes Bluetooth adapter.
        this.mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = mBluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // request location permission
        ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION },
                        TAG_CODE_PERMISSION_LOCATION);

        // init list view
        ListView deviceList = (ListView) findViewById(R.id.discovered_devices_list);
        deviceListAdapter = new DeviceListAdapter(this, mDiscoveredDevices);

        deviceList.setAdapter(deviceListAdapter);

        Log.d("I am ", mBluetoothAdapter.getName() + " "+mBluetoothAdapter.getAddress());
    }

    private void startServer() {
        UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
        mBluetoothGattServer = mBluetoothManager.openGattServer(this, mGattServerCallback);
        BluetoothGattService service = new BluetoothGattService(uuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(uuid, BluetoothGattCharacteristic.FORMAT_UINT8, BluetoothGattCharacteristic.PERMISSION_WRITE);

        service.addCharacteristic(characteristic);
        mBluetoothGattServer.addService(service);

        Log.d("MainActivity", "start Gatt server");
    }

    private final BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.d("MainActivity", "BluetoothGattServerCallback from "+device.getName());
        }

    };

    private void startDiscoverMode() {
        View view = getCurrentFocus();
        Snackbar.make(view, "start discover", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        this.mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                if(!mDiscoveredDevices.contains(result.getDevice())) {
                    mDiscoveredDevices.add(result.getDevice());
                }
                deviceListAdapter.notifyDataSetChanged();
//                Log.d("bt scan result", result.getDevice().getName().toString());
            }


        };

        this.mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
                Log.d("leScan", bluetoothDevice.toString());
                Log.d("alles", bluetoothDevice.getName());
                if(!mDiscoveredDevices.contains(bluetoothDevice)) {
                    mDiscoveredDevices.add(bluetoothDevice);
                }
                deviceListAdapter.notifyDataSetChanged();
            }
        };

        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                // start bluetooth discover
                //mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
        };
        handler.postDelayed(r, 250);

    }

    private void stopDiscoverMode() {
        View view = getCurrentFocus();
        Snackbar.make(view, "stop discover", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        // stop bluetooth discover
        mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
