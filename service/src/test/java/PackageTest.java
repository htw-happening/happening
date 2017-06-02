import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import blue.happening.service.bluetooth.Package;
import blue.happening.service.bluetooth.PackageHandler;
import dalvik.system.PathClassLoader;

public class PackageTest {

    @Test
    public void checkSplittingAndMerging() {
        Assert.assertEquals(1,1);

        Package aPackage = new Package(new byte[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20});
        Package other = null;

        PackageHandler handler = new PackageHandler();
        PipedOutputStream outputStream = new PipedOutputStream();
        PipedInputStream inputStream = new PipedInputStream();
        try {
            inputStream.connect(outputStream);

            Package[] data = PackageHandler.splitPackages(aPackage);
            for (Package aData : data) {
                outputStream.write(aData.getData());
            }

            while (true){
                byte[] buffer = new byte[PackageHandler.CHUNK_SIZE + PackageHandler.CHUNK_NUM];
                inputStream.read(buffer);
                handler.createNewFromMeta(buffer);
                for (int i = 0; i < handler.getPayloadNum(); i++) {
                    buffer = new byte[PackageHandler.PAYLOAD_SIZE];
                    inputStream.read(buffer);
                    handler.addContent(buffer);
                }
                other = handler.getPackage();
                handler.clear();

                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(aPackage, other);

    }
}