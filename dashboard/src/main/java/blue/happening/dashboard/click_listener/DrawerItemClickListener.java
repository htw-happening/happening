package blue.happening.dashboard.click_listener;

import android.app.Activity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import blue.happening.dashboard.R;
import blue.happening.dashboard.fragment.MenuItems;
import blue.happening.dashboard.fragment.MyFragmentManager;

public class DrawerItemClickListener
        implements ListView.OnItemClickListener {

    private Activity activity;
    private String[] menuItemList;
    MyFragmentManager myFragmentManager;

    public DrawerItemClickListener(Activity activity) {
        this.activity = activity;
        menuItemList = MenuItems.toArray();
        myFragmentManager = MyFragmentManager.getInstance(activity);
        myFragmentManager.swapFragment(MenuItems.DASHBOARD_FRAGMENT);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    /**
     * Swaps fragments in content holder view
     *
     * @param position
     */
    private void selectItem(int position) {
        myFragmentManager.swapFragment(MenuItems.getById(menuItemList[position]));

        // update the title, and close the drawer
        setTitle(menuItemList[position]);
        ((DrawerLayout) activity.findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
    }

    /**
     * Set ActionBar title as menu item title
     *
     * @param title
     */
    public void setTitle(CharSequence title) {
        ((Toolbar) activity.findViewById(R.id.toolbar)).setTitle(title);
    }

}
