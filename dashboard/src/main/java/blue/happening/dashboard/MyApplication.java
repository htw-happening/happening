package blue.happening.dashboard;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
        formUri = "http://collector.tracepot.com/6ed46252",
        logcatArguments = { "-t", "100", "-v", "long", "ActivityManager:I", "MyApp:D", "*:S" }
)
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }

}
