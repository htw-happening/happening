package blue.happening.dashboard.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.util.Log;

import blue.happening.dashboard.R;

public class MyFragmentManager {

    private static MyFragmentManager myFragmentManager = null;
    private Activity activity;
    private FragmentManager fragmentManager;

    private Fragment currentFragment;
    private Fragment newFragment;

    private Fragment dashboardFragment = null;
    private Fragment netWorkStatsFragment = null;
    private Fragment impressumFragment = null;

    private MyFragmentManager(Activity activity) {
        this.activity = activity;
        this.fragmentManager = activity.getFragmentManager();

        // inflate default fragment
        this.dashboardFragment = DeviceFragment.getInstance();
        this.currentFragment = this.dashboardFragment;
        this.newFragment = this.dashboardFragment;

        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment_holder, newFragment)
                .commit();

    }

    public static MyFragmentManager getInstance(Activity activity) {
        if (myFragmentManager == null)
            myFragmentManager = new MyFragmentManager(activity);

        return myFragmentManager;
    }

    /**
     * replace current Fragment
     *
     * @param menuItemId
     */
    public void swapFragment(MenuItems menuItemId) {

        switch (menuItemId) {
            case DEVICE_FRAGMENT: {
                Log.d(this.getClass().getSimpleName(), "dashboard");
                if (this.dashboardFragment == null) {
                    this.dashboardFragment = fragmentManager.findFragmentByTag(MenuItems.DEVICE_FRAGMENT.getName());
                    if (this.dashboardFragment == null) {
                        this.dashboardFragment = DeviceFragment.getInstance();
                    }
                }

                loadFragment(currentFragment, dashboardFragment, MenuItems.DEVICE_FRAGMENT.getName());
                this.currentFragment = dashboardFragment;
                break;
            }
            case NETWORK_STATS_FRAGMENT: {
                Log.d(this.getClass().getSimpleName(), "network stats");
                if (this.netWorkStatsFragment == null) {
                    this.netWorkStatsFragment = fragmentManager.findFragmentByTag(MenuItems.NETWORK_STATS_FRAGMENT.getName());
                    if (this.netWorkStatsFragment == null) {
                        this.netWorkStatsFragment = NetworkStatsFragment.getInstance();
                    }
                }

                loadFragment(currentFragment, netWorkStatsFragment, MenuItems.NETWORK_STATS_FRAGMENT.getName());
                this.currentFragment = netWorkStatsFragment;
                break;
            }
            case IMPRESSUM_FRAGMENT: {
                Log.d(this.getClass().getSimpleName(), "impressum");
                if (this.impressumFragment == null) {
                    this.impressumFragment = fragmentManager.findFragmentByTag(MenuItems.IMPRESSUM_FRAGMENT.getName());
                    if (this.impressumFragment == null) {
                        this.impressumFragment = ImpressumFragment.getInstance();
                    }
                }

                loadFragment(currentFragment, impressumFragment, MenuItems.IMPRESSUM_FRAGMENT.getName());
                this.currentFragment = impressumFragment;
                break;
            }
            default: {
                break;
            }
        }

    }

    private void loadFragment(Fragment current, Fragment fragment, String tag) {
        if (this.fragmentManager == null) {
            this.fragmentManager = this.activity.getFragmentManager();
        }

        if (current.getId() != 0) {
            this.fragmentManager.beginTransaction()
                    .replace(current.getId(), fragment, tag)
                    .addToBackStack(null)
                    .commit();
        }
    }

}
