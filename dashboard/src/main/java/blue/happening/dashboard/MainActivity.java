package blue.happening.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import blue.happening.dashboard.fragment.DashboardFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Fragment Tags
    private static final String TAG_FRAGMENT_DASHBOARD = "dashboard";

    private String TAG = getClass().getSimpleName();
    private FragmentManager fm = getSupportFragmentManager();

    // Fragment
    private Fragment currentFragment = null;
    private String currentFragmentTag = null;

    private Fragment dashboardFragment;

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set views
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                Log.d("Drawer", "OnDrawerClosed");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                Log.d("Drawer", "OnDrawerOpened");

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                Log.d("Drawer", "OnDrawerSlide " + slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                Log.d("Drawer", "OnDrawerStateChanged " + newState);
                if (newState == DrawerLayout.STATE_SETTLING) {
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

            }
        };

        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        // set image in drawer header
//        View drawerHeader = navigationView.getHeaderView(0);
//        Drawable headerImage = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_mobile);
//
//        if (deviceName.contains("white")) {
//            color = ContextCompat.getColor(this, R.color.mobile_white);
//        } else if (deviceName.contains("black")) {
//            color = ContextCompat.getColor(this, R.color.mobile_black);
//        } else if (deviceName.contains("red")) {
//            color = ContextCompat.getColor(this, R.color.mobile_red);
//        } else if (deviceName.contains("blue")) {
//            color = ContextCompat.getColor(this, R.color.mobile_blue);
//        } else if (deviceName.contains("yellow")) {
//            color = ContextCompat.getColor(this, R.color.mobile_yellow);
//        }
//
//        headerImage.setColorFilter(color, PorterDuff.Mode.SRC_IN);
//        ((ImageView) drawerHeader.findViewById(R.id.drawer_header_image)).setImageDrawable(headerImage);
//
//        // set device stats in drawer header
//        ((TextView) drawerHeader.findViewById(R.id.drawer_header_main_text)).setText(BluetoothAdapter.getDefaultAdapter().getName());
//        ((TextView) drawerHeader.findViewById(R.id.drawer_header_sub_text)).setText(Build.SERIAL);

        // initialise start fragment
        this.currentFragment = DashboardFragment.getInstance();
        this.currentFragmentTag = TAG_FRAGMENT_DASHBOARD;

        fm.beginTransaction()
                .replace(R.id.main_fragment_holder, currentFragment, currentFragmentTag)
                .commit();

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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.chat) {
            if (this.dashboardFragment == null) {
                this.dashboardFragment = getSupportFragmentManager().findFragmentByTag(this.TAG_FRAGMENT_DASHBOARD);
                if (this.dashboardFragment == null) {
                    this.dashboardFragment = DashboardFragment.getInstance();
                }
            }

            loadFragment(currentFragment, dashboardFragment, TAG_FRAGMENT_DASHBOARD);
            this.currentFragment = dashboardFragment;
            this.currentFragmentTag = TAG_FRAGMENT_DASHBOARD;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment current, Fragment fragment, String tag) {
        if (fm == null) {
            fm = getSupportFragmentManager();
        }

        fm.beginTransaction()
                .replace(current.getId(), fragment, tag)
                .addToBackStack(null)
                .commit();
    }

}
