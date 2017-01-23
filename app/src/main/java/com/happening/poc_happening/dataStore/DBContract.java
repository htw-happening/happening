package com.happening.poc_happening.dataStore;

import android.provider.BaseColumns;

/**
 * Created by daired on 03/01/17.
 */

public final class DBContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DBContract() {
    }

    /* Inner class that defines the table contents */
    public static class DBEntry implements BaseColumns {


        public static final String PROFILE_TABLE_NAME = "profile";
        public static final String PROFILE_COLUMN_USERNAME = "username";
        public static final String PROFILE_COLUMN_FIRSTNAME = "firstname";
        public static final String PROFILE_COLUMN_LASTNAME = "lastname";

        public static final String DEVICES_TABLE_NAME = "devices";
        public static final String DEVICES_COLUMN_NAME = "name";
        public static final String DEVICES_COLUMN_ADDRESS = "address";
        public static final String DEVICES_COLUMN_LAST_SEEN = "lastSeen";

        public static final String PRIVATE_MESSAGES_TABLE_NAME = "privateMessages";
        public static final String PRIVATE_MESSAGES_COLUMN_FROM_DEVICE_ID = "fromDeviceId";
        public static final String PRIVATE_MESSAGES_COLUMN_CREATION_TIME = "creationTime";
        public static final String PRIVATE_MESSAGES_COLUMN_TYPE = "type";
        public static final String PRIVATE_MESSAGES_COLUMN_CONTENT = "content";

        public static final String GLOBAL_MESSAGES_TABLE_NAME = "globalMessages";
        public static final String GLOBAL_MESSAGES_COLUMN_FROM_DEVICE_ID = "fromDeviceId";
        public static final String GLOBAL_MESSAGES_COLUMN_CREATION_TIME = "creationTime";
        public static final String GLOBAL_MESSAGES_COLUMN_TYPE = "type";
        public static final String GLOBAL_MESSAGES_COLUMN_CONTENT = "content";

    }
}