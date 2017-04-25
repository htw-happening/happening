package blue.happening.service;

import android.app.Application;
import android.util.Log;

/**
 * Basic {@link Application application} container for the happening service to be shippable.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(this.getClass().getSimpleName(), "onCreate");
    }

    @Override
    public void onTerminate() {
        Log.v(this.getClass().getSimpleName(), "onTerminate");
        super.onTerminate();
    }
}