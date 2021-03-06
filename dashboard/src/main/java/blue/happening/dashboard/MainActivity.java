package blue.happening.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import blue.happening.dashboard.layout.MyDrawerLayout;


public class MainActivity extends AppCompatActivity {

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set views
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Context context = MyApplication.getAppContext();
        String macAddress = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
        TextView subText = (TextView) findViewById(R.id.drawer_header_sub_text);
        subText.setText(macAddress);

        new MyDrawerLayout(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // don't show settings in toolbar
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
