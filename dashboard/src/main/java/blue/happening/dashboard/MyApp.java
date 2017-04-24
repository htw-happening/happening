package blue.happening.dashboard;

import android.app.Application;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import blue.happening.sdk.IRemoteService;
import blue.happening.sdk.ServiceHandler;

public class MyApp extends Application {

    private ServiceHandler serviceHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this.getClass().getSimpleName(), "onCreate");
        Context context = getApplicationContext();
        serviceHandler = new ServiceHandler();
        IRemoteService remoteService = serviceHandler.register(context);
        Log.i("ServiceHandler", "Adding");
        try {
            remoteService.addDevice("new");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(this.getClass().getSimpleName(), "onCreate");
        serviceHandler.deregister();
    }
}
