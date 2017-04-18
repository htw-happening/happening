package blue.happening;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class MyApp extends Application {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int TAG_PERMISSION_LOCATION = 2;
    private static final int TAG_PERMISSION_REQUESTS = 100;

    @Override
    public void onCreate() {
        super.onCreate();
        /*
        // request permissions
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    TAG_PERMISSION_REQUESTS);
        }
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                TAG_PERMISSION_LOCATION);
        */
        // initialize bluetooth adapter
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = mBluetoothManager.getAdapter();

        // ensure bluetooth is available and enabled
        Log.d("MainActivity", "mBluetoothAdapter.isEnabled() " + mBluetoothAdapter.isEnabled());
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case TAG_PERMISSION_REQUESTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("PermissionRequest", "Permission was granted, yay!");
                } else {
                    Log.i("PermissionRequest", "Permission denied, boo!");
                }
            }
        }
    }

    private static Context context = null;

    public MyApp() {
        this.context = this.getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
