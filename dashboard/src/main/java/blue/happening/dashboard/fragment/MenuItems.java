package blue.happening.dashboard.fragment;

import java.util.LinkedList;

public enum MenuItems {
    /**
     * List of Enums
     * Add a new Enum to create a new Menu item
     */
    DEVICE_FRAGMENT("Devices"),
    NETWORK_STATS_FRAGMENT("Network Stats"),
    IMPRESSUM_FRAGMENT("Impressum");

    // fields
    private final String name;

    // constructor
    MenuItems(final String name) {
        this.name = name;
    }

    /**
     * get Enum Object by name
     *
     * @param name
     * @return
     */
    public static MenuItems getById(String name) {
        for (MenuItems e : MenuItems.values()) {
            if (e.name == name) {
                return e;
            }
        }
        return null;// not found
    }

    /**
     * return all enums names as string array
     *
     * @return
     */
    public static String[] toArray() {
        LinkedList<String> list = new LinkedList<>();
        for (MenuItems s : MenuItems.values()) {
            list.add(s.getName());
        }

        return list.toArray(new String[list.size()]);
    }

    public String getName() {
        return name;
    }

}