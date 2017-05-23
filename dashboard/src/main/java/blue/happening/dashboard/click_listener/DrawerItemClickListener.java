package blue.happening.dashboard.click_listener;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import blue.happening.dashboard.R;
import blue.happening.dashboard.fragment.DashboardFragment;

public class DrawerItemClickListener implements ListView.OnItemClickListener {

    private Activity activity;

    private String[] menuItemList;
    private DrawerLayout mainContent;
    private ListView leftDrawerList;

    public DrawerItemClickListener(Activity activity) {
        this.activity = activity;

        menuItemList = activity.getResources().getStringArray(R.array.menu_item_list);
        mainContent = (DrawerLayout) activity.findViewById(R.id.main_content);
        leftDrawerList = (ListView) activity.findViewById(R.id.left_drawer);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    /**
     * Swaps fragments in content holder view
     * @param position
     */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = new DashboardFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_holder, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        leftDrawerList.setItemChecked(position, true);
        setTitle(menuItemList[position]);
        mainContent.closeDrawer(leftDrawerList);
    }

    /**
     * Set ActionBar title as menu item title
     * @param title
     */
    public void setTitle(CharSequence title) {
        activity.getActionBar().setTitle(title);
    }

}
