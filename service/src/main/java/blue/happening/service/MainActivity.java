package blue.happening.service;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


/**
 * Translucent {@link Activity activity} that finishes shortly after creation
 * without rendering anything.
 */
public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(this.getClass().getSimpleName(), "onCreate");
        // TODO: Verify compatibility and prompt for permissions.
        finish();
    }

    @Override
    protected void onDestroy() {
        Log.v(this.getClass().getSimpleName(), "onDestroy");
        super.onDestroy();
    }
}