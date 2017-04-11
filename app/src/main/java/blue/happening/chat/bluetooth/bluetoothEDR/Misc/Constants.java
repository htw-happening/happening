package blue.happening.chat.bluetooth.bluetoothEDR.Misc;

/**
 * This class contains all kinds of different static final constant values.
 * I.e. Handler, Bundle ....
 */
public abstract class Constants {

    public static final int CACHESIZE = 4048;

    // For Gui Handler
    public static final int HANDLER_MESSAGE_RECEIVED = 1;
    public static final int HANDLER_MAKE_A_TOAST = 2;

    public static final int HANDLER_CONNECTION_CHANGED = 4;
    public static final int HANDLER_MESSAGE_BOX = 40;

    // Keyvalues of a Bundle
    public static final String BUNDLE_TOAST = "toast";

    public static final String BUNDLE_PACKAGE_AUTHOR = "package_author";
    public static final String BUNDLE_PACKAGE_TIMESTAMP = "package_timestamp";

    public static final String BUNDLE_PACKAGE_CONTENT = "package_content";

    public static final String BUNDLE_MESSAGEBOX_WHO = "who";
    public static final String BUNDLE_MESSAGEBOX_WHAT = "what";

    // Bundles InformationProvider
    public static final int ROLE_NONE = 1;
    public static final int ROLE_CLIENT = 2;
    public static final int ROLE_MASTER = 3;
}
