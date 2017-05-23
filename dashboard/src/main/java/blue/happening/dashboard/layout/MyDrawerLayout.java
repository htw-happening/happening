package blue.happening.dashboard.layout;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import blue.happening.dashboard.R;
import blue.happening.dashboard.click_listener.DrawerItemClickListener;

public class MyDrawerLayout {

    private String[] menuItemList;
    private DrawerLayout mainContent;
    private ListView leftDrawerList;

    public MyDrawerLayout(Activity activity) {
        menuItemList = activity.getResources().getStringArray(R.array.menu_item_list);
        mainContent = (DrawerLayout) activity.findViewById(R.id.main_content);
        leftDrawerList = (ListView) activity.findViewById(R.id.left_drawer);

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
