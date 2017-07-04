package blue.happening.dashboard.layout;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import blue.happening.dashboard.R;
import blue.happening.dashboard.fragment.MenuItems;
import blue.happening.dashboard.listener.DrawerItemClickListener;

public class MyDrawerLayout {

    private String[] menuItemList;
    private ListView leftDrawerList;

    public MyDrawerLayout(Activity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
//                Log.d("Drawer", "OnDrawerClosed");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
//                Log.d("Drawer", "OnDrawerOpened");

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
//                Log.d("Drawer", "OnDrawerSlide " + slideOffset);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
//                Log.d("Drawer", "OnDrawerStateChanged " + newState);
//                if (newState == DrawerLayout.STATE_SETTLING) {
//                    InputMethodManager inputMethodManager = (InputMethodManager)
//                            getSystemService(Context.INPUT_METHOD_SERVICE);
//                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//                }
            }
        };

        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // set menu items
        menuItemList = MenuItems.toArray();
        leftDrawerList = (ListView) activity.findViewById(R.id.list_view_inside_nav);

        // Set the adapter for the list view
        leftDrawerList.setAdapter(new ArrayAdapter<>(
                activity,
                R.layout.drawer_list_item,
                R.id.menu_item1,
                menuItemList));

        // Set the list's click listener
        leftDrawerList.setOnItemClickListener(new DrawerItemClickListener(activity));
    }
}
