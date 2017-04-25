package blue.happening.dashboard;

import android.app.Application;
import android.util.Log;

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