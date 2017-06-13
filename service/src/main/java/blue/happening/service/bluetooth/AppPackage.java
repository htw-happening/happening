package blue.happening.service.bluetooth;

import java.nio.ByteBuffer;

public abstract class AppPackage {

    private static final int APP_ID_SIZE = 4;

    public AppPackage() {
    }

    public static byte[] createAppPackage(int appID, byte[] content) {
        byte[] appPackage = new byte[APP_ID_SIZE + content.length];
        byte[] appIdBytes = ByteBuffer.allocate(4).putInt(appID).array();
        System.arraycopy(appIdBytes, 0, appPackage, 0, APP_ID_SIZE);
        System.arraycopy(content, 0, appPackage, APP_ID_SIZE, content.length);
        return appPackage;
    }

    public static int getAppID(byte[] appPackage) {
        byte[] buffer = new byte[APP_ID_SIZE];
        System.arraycopy(appPackage, 0, buffer, 0, APP_ID_SIZE);
        return ByteBuffer.wrap(buffer).getInt();
    }

    public static byte[] getContent(byte[] appPackage) {
        byte[] buffer = new byte[appPackage.length - APP_ID_SIZE];
        System.arraycopy(appPackage, APP_ID_SIZE, buffer, 0, appPackage.length - APP_ID_SIZE);
        return buffer;
    }

}
