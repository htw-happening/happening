package com.happening.poc.poc_happening;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
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
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int TAG_CODE_PERMISSION_LOCATION = 2;
    private static final String HAPPENING_SERVICE_UUID = "11111111-1337-1337-1337-000000000000";
    public static final ParcelUuid parcelUuid = ParcelUuid.fromString(HAPPENING_SERVICE_UUID);

    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothLeScanner mBluetoothLeScanner = null;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser = null;
    private BluetoothGattServer mBluetoothGattServer = null;

    private ScanCallback mScanCallback = null;
    private AdvertiseCallback mAdvertiseCallback = null;
    private BluetoothGattServerCallback mGattServerCallback = null;

    private HashMap<String, ScanResult> mScanResults = new LinkedHashMap<>();
    private DeviceListAdapter deviceListAdapter = null;

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

        // set event Listener
        Switch discoverButton = (Switch) findViewById(R.id.discover_button);
        discoverButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startDiscover();
                } else {
                    stopDiscover();
                }
            }
        });

        Switch advertiseButton = (Switch) findViewById(R.id.advertise_button);
        advertiseButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startAdvertise();
                } else {
                    stopAdvertise();
                }
            }
        });

        Switch gattServerButton = (Switch) findViewById(R.id.gatt_server_button);
        gattServerButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    createGattServer();
                } else {
                    stopGattServer();
                }
            }
        });

        // initialize bluetooth adapter
        this.mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = mBluetoothManager.getAdapter();

        // ensure bluetooth is available and enabled
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (!mBluetoothAdapter.isMultipleAdvertisementSupported() ||
                !mBluetoothAdapter.isOffloadedFilteringSupported() ||
                !mBluetoothAdapter.isOffloadedScanBatchingSupported()) {
            advertiseButton.setChecked(false);
            advertiseButton.setEnabled(false);
            gattServerButton.setChecked(false);
            gattServerButton.setEnabled(false);
        }

        // request location permission
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                TAG_CODE_PERMISSION_LOCATION);

        // initialize list view
        ListView deviceList = (ListView) findViewById(R.id.discovered_devices_list);
        deviceListAdapter = new DeviceListAdapter(this, mScanResults);
        deviceList.setAdapter(deviceListAdapter);

        this.mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        this.mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();

        // set scanning callback
        this.mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                if (!mScanResults.containsKey(result.getDevice().getAddress())) {
                    mScanResults.put(result.getDevice().getAddress(), result);
                    deviceListAdapter.notifyDataSetChanged();
                }
            }
        };

        // set advertising callback
        this.mAdvertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Log.d("DEBUG", "advertising started");
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                Log.d("DEBUG", "advertising error " + errorCode);
            }
        };

        this.mGattServerCallback = new BluetoothGattServerCallback() {
            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
            }

            @Override
            public void onServiceAdded(int status, BluetoothGattService service) {
                super.onServiceAdded(status, service);
            }

            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
            }

            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
            }
        };

        Log.d("SELF", mBluetoothAdapter.getName() + " " + mBluetoothAdapter.getAddress());

        Context context = getApplicationContext();
        String macAddress = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
        TextView bleAddress = (TextView) findViewById(R.id.ble_mac_address);
        bleAddress.setText(macAddress);
    }

    private void startAdvertise() {
        View view = getCurrentFocus();

        if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {

            Snackbar.make(view, "Start Advertising", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            AdvertiseSettings.Builder advertiseSettingsBuilder = new AdvertiseSettings.Builder();
            advertiseSettingsBuilder
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                    .setConnectable(true);
            AdvertiseSettings advertiseSettings = advertiseSettingsBuilder.build();

            String[] loads = {"happen", "foobar", "lekker", "service", "matetee"};
            int index = new Random().nextInt(loads.length);
            byte[] payload = loads[index].getBytes();
            AdvertiseData.Builder advertiseDataBuilder = new AdvertiseData.Builder();
            advertiseDataBuilder
                    .addServiceData(parcelUuid, payload)
                    .setIncludeDeviceName(true)
                    .setIncludeTxPowerLevel(true);
            AdvertiseData advertiseData = advertiseDataBuilder.build();

            mBluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, mAdvertiseCallback);

        } else {
            Snackbar.make(view, "BLE Advertising is not supported!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private void stopAdvertise() {
        View view = getCurrentFocus();
        Snackbar.make(view, "Stop Advertising", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

    private void startDiscover() {
        View view = getCurrentFocus();
        Snackbar.make(view, "Start Discovering", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scanSettingsBuilder
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
        } else {
            scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        }
        ScanSettings scanSettings = scanSettingsBuilder.build();

        List<ScanFilter> scanFilters = new ArrayList<>();

        stopDiscover();
        mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);
    }

    private void stopDiscover() {
        View view = getCurrentFocus();
        Snackbar.make(view, "Stop Discovering", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        mBluetoothLeScanner.flushPendingScanResults(mScanCallback);
        mBluetoothLeScanner.stopScan(mScanCallback);
        deviceListAdapter.deviceList.clear();
        deviceListAdapter.notifyDataSetChanged();
    }

    private void createGattServer() {
        View view = getCurrentFocus();
        Snackbar.make(view, "Start Gatt-Server", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        this.mBluetoothGattServer = mBluetoothManager.openGattServer(MainActivity.this, mGattServerCallback);

        byte[] payload = "services".getBytes();
        UUID uuid = UUID.fromString(HAPPENING_SERVICE_UUID);
        BluetoothGattService gattService = new BluetoothGattService(uuid, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        BluetoothGattCharacteristic gattCharacteristic = new BluetoothGattCharacteristic(uuid, BluetoothGattCharacteristic.PROPERTY_BROADCAST, BluetoothGattCharacteristic.PERMISSION_READ);
        gattCharacteristic.setValue(payload);

        gattService.addCharacteristic(gattCharacteristic);
        mBluetoothGattServer.addService(gattService);
    }

    private void stopGattServer() {
        View view = getCurrentFocus();
        Snackbar.make(view, "Stop Gatt-Server", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        mBluetoothGattServer.close();
    }

    private void makeDeviceDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

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
