package com.happening.sdk;

import android.content.Context;

public class HappeningClient {

    private static HappeningClient hc = null;
    private static ServiceHandler sh = null;
    private static Context context = null;

    private HappeningClient() {
        HappeningClient.sh = ServiceHandler.getInstance();
    }

    public static HappeningClient getHappeningClient() {
        if (HappeningClient.hc == null) {
            HappeningClient.hc = new HappeningClient();
        }
        return HappeningClient.hc;
    }

    public Context getAppContext() {
        return HappeningClient.context;
    }

    public void registerAppContext(Context context) {
        HappeningClient.context = context;
    }


}
