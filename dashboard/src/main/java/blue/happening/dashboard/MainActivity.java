package blue.happening.dashboard;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import blue.happening.dashboard.layout.MyDrawerLayout;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(this.getClass().getSimpleName(), "onCreate");

        setContentView(R.layout.activity_main);
        new MyDrawerLayout(this);
    }

    @Override
    protected void onDestroy() {
        Log.v(this.getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }
}
