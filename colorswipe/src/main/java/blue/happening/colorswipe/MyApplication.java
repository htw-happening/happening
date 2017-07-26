package blue.happening.colorswipe;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context context;
//    private Swiper swiper = null;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
//        swiper = Swiper.getInstance();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
