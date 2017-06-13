import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import blue.happening.service.bluetooth.AppPackage;
import blue.happening.service.bluetooth.Package;
import blue.happening.service.bluetooth.Packetizer;

public class PackageTest {

    @Test
    public void checkSplittingAndMerging() {
        Assert.assertEquals(1, 1);

        Package aPackage = new Package(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20});
        Package other = null;

        Packetizer handler = new Packetizer();
        PipedOutputStream outputStream = new PipedOutputStream();
        PipedInputStream inputStream = new PipedInputStream();
        try {
            inputStream.connect(outputStream);

            Package[] data = Packetizer.splitPackages(aPackage);
            for (Package aData : data) {
                outputStream.write(aData.getData());
            }

            while (true) {
                byte[] buffer = new byte[Packetizer.CHUNK_SIZE + Packetizer.CHUNK_NUM];
                inputStream.read(buffer);
                handler.createNewFromMeta(buffer);
                for (int i = 0; i < handler.getPayloadNum(); i++) {
                    buffer = new byte[Packetizer.PAYLOAD_SIZE];
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

    @Test
    public void checkAppPackage() {
        Assert.assertEquals("Works!", 1, 1);

        int appID = 666;
        byte[] content = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20};

        AppPackage appPackage = new AppPackage();
        byte[] appPackageBytes = appPackage.createAppPackage(appID, content);

        Assert.assertEquals("Length", content.length + 4, appPackageBytes.length);
        Assert.assertEquals("AppID", appID, appPackage.getAppID(appPackageBytes));
        Assert.assertArrayEquals("Content", content, appPackage.getContent(appPackageBytes));
    }
}