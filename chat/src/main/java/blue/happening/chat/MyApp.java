package blue.happening.chat;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import blue.happening.sdk.ServiceHandler;

public class MyApp extends Application {

    private ServiceHandler serviceHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(this.getClass().getSimpleName(), "onCreate");
        Context context = getApplicationContext();
        serviceHandler = ServiceHandler.register(context);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(this.getClass().getSimpleName(), "onCreate");
        serviceHandler.deregister();
    }
}
