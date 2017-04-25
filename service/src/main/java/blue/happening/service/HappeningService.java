package blue.happening.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@SuppressWarnings("unused")
public class HappeningService extends Service {

    private static final int START_MODE = START_STICKY;
    private static final boolean ALLOW_REBIND = true;

    private final IRemoteService.Stub binder = new IRemoteService.Stub() {

        final List<String> devices = Collections.synchronizedList(new ArrayList<String>());

        public void addDevice(String name) throws RemoteException {
            devices.add(name);
            Log.d(this.getClass().getSimpleName(), "added device " + name);
        }
    };

    /**
     * Called when the service is being created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(this.getClass().getSimpleName(), "onCreate");
    }

    /**
     * The service is starting, due to a call to startService()
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(this.getClass().getSimpleName(), "onStartCommand");
        Intent intents = new Intent(getBaseContext(), MainActivity.class);
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intents);
        Toast.makeText(this, "Happening started", Toast.LENGTH_SHORT).show();
        return START_MODE;
    }


    /**
     * Called when the service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        Log.v(this.getClass().getSimpleName(), "onDestroy");
        Toast.makeText(this, "Happening stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(this.getClass().getSimpleName(), "onBind");
        return binder;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.v(this.getClass().getSimpleName(), "onRebind");
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(this.getClass().getSimpleName(), "onUnbind");
        return ALLOW_REBIND;
    }
}
