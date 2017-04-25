package blue.happening.dashboard;

import android.app.Application;
import android.util.Log;

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