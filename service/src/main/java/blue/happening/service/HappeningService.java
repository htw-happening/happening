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

import blue.happening.IHappeningCallback;
import blue.happening.IHappeningService;


/**
 * Main happening {@link Service service} class containing lifecycle management and
 * an interface binding with the actual service methods.
 */
public class HappeningService extends Service {

    private static final String HAPPENING_APP_ID = "HAPPENING_APP_ID";
    private static final int START_MODE = START_STICKY;
    private static final boolean ALLOW_REBIND = true;

    private static List<IHappeningCallback> callbacks = new ArrayList<>();

    private final IHappeningService.Stub binder = new IHappeningService.Stub() {

        final List<String> messages = Collections.synchronizedList(new ArrayList<String>());

        @Override
        public void registerHappeningCallback(IHappeningCallback happeningCallback) throws RemoteException {
            Log.d(this.getClass().getSimpleName(), "callback added " + happeningCallback);
            callbacks.add(happeningCallback);
        }

        public String hello(String message) throws RemoteException {
            messages.add(message);
            Log.d(this.getClass().getSimpleName(), "hello " + message);
            String reply = "service@" + android.os.Process.myPid();
            Log.d(this.getClass().getSimpleName(), "reply " + reply);
            for (IHappeningCallback callback : callbacks) {
                if (callback != null)
                    callback.onClientAdded("async call from service hello");
            }
            return reply;
        }

        @Override
        public String getClient(String clientId) throws RemoteException {
            return null;
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
        String appId = intent.getStringExtra(HAPPENING_APP_ID);
        Toast.makeText(this, (appId == null ? "Something" : appId) + " bound", Toast.LENGTH_LONG).show();
        return binder;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        String appId = intent.getStringExtra(HAPPENING_APP_ID);
        Toast.makeText(this, (appId == null ? "Something" : appId) + " rebound", Toast.LENGTH_LONG).show();
        Log.v(this.getClass().getSimpleName(), "onRebind");
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(this.getClass().getSimpleName(), "onUnbind");
        String appId = intent.getStringExtra(HAPPENING_APP_ID);
        Toast.makeText(this, (appId == null ? "Something" : appId) + " unbound", Toast.LENGTH_LONG).show();
        return ALLOW_REBIND;
    }
}
