package blue.happening.dashboard.click_listener;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import blue.happening.dashboard.R;
import blue.happening.dashboard.fragment.MyFragmentManager;

public class DrawerItemClickListener implements ListView.OnItemClickListener {

    private Activity activity;

    private String[] menuItemList;
    private DrawerLayout mainContent;
    private ListView leftDrawerList;

    public DrawerItemClickListener(Activity activity) {
        this.activity = activity;

        menuItemList = MyFragmentManager.MenuItems.toArray();
        mainContent = (DrawerLayout) activity.findViewById(R.id.main_content);
        leftDrawerList = (ListView) activity.findViewById(R.id.left_drawer);
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
        MyFragmentManager myFragmentManager = MyFragmentManager.getInstance(activity);
        myFragmentManager.swapFragment(MyFragmentManager.MenuItems.getById(menuItemList[position]));

        // update the title, and close the drawer
        setTitle(menuItemList[position]);
        mainContent.closeDrawer(leftDrawerList);
    }

    /**
     * Set ActionBar title as menu item title
     *
     * @param title
     */
    public void setTitle(CharSequence title) {
        activity.getActionBar().setTitle(title);
    }

}
