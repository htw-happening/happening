package blue.happening.service;

import android.app.Application;
import android.util.Log;


/**
 * Basic {@link Application application} container for the happening service to be shippable.
 */
@SuppressWarnings("unused")
public class MyApp extends Application {

    @SuppressWarnings("unused")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(this.getClass().getSimpleName(), "onCreate");
    }

    @SuppressWarnings("unused")
    @Override
    public void onTerminate() {
        Log.v(this.getClass().getSimpleName(), "onTerminate");
        super.onTerminate();
    }
}