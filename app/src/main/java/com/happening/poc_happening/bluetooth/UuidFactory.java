package com.happening.poc_happening.bluetooth;

import java.util.UUID;

public class UuidFactory {

    private static final long SERVICE_VALUE = 123456789;

    public static UUID getServiceUuid(long userID) {
        return new UUID(SERVICE_VALUE, userID);
    }

    public static long getServiceValue(UUID uuid) {
        return uuid.getMostSignificantBits();
    }

    public static long getUserID(UUID uuid) {
        return uuid.getLeastSignificantBits();
    }

    public static boolean sameService(UUID uuid) {
        return uuid.getMostSignificantBits() == SERVICE_VALUE;
    }

    public static boolean sameUserID(UUID uuid, long userID) {
        return uuid.getLeastSignificantBits() == userID;
    }

}
