package blue.happening.chat.bluetooth.bluetoothEDR.BtPackage;

import java.util.Random;

/**
 * This is the factory for instantiating the different kinds of packages.
 * Use that one instead of the BtPackage Constructor.
 */
public abstract class BtPackageFactory {

    public static BtPackage buildMessagePackage(int id, String author, String content){
        byte[] contentBytes = content.getBytes();
        return new BtPackage(BtPackageType.MESSAGE, id, System.currentTimeMillis(), author, contentBytes.length, contentBytes);
    }

    public static byte[] getTestData(int payload){
        byte[] bytes = new byte[payload];
        Random random = new Random();
        for(int i = 0; i < bytes.length; i++){
            bytes[i] = (byte) random.nextInt();
        }
        return bytes;
    }
}
