import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;
import java.util.Random;

import blue.happening.service.bluetooth.AppPackage;
import blue.happening.service.bluetooth.Package;
import blue.happening.service.bluetooth.Packetizer;

public class PackageTest {

    @Test
    public void checkSplittingAndMerging() {
        Assert.assertEquals(1, 1);


        Random random = new Random(0);
        byte[] origData = new byte[300];
        for (int i = 0; i < origData.length; i++) {
            origData[i] = (byte) random.nextInt(255);
        }
        Package aPackage = new Package(origData);
        Package other = null;

        final Packetizer handler = new Packetizer();
        PipedOutputStream outputStream = new PipedOutputStream();
        final PipedInputStream inputStream = new PipedInputStream();

        try {
            inputStream.connect(outputStream);

            System.out.println("Start Splitting");
            Package[] data = Packetizer.splitPackages(aPackage);
            System.out.println("splittin Done");
            System.out.println("splited: " + Arrays.toString(data));
            System.out.println("splited length: " + data.length);
            for (Package aData : data) {
                System.out.println("Write to Stream " + aData);
                outputStream.write(aData.getData(), 0, aData.getData().length);
                System.out.println("Wrote to Stream " + aData);
            }


            System.out.println("Read");
            byte[] buffer = new byte[Packetizer.CHUNK_SIZE];
            try {
                inputStream.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Read meta " + Arrays.toString(buffer));
            handler.createNewFromMeta(buffer);
            System.out.println("created meta package " + handler);
            System.out.println("read payload ");
            buffer = new byte[handler.getPayloadSize()];
            try {
                inputStream.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("read " + Arrays.toString(buffer));
            handler.addContent(buffer);
            System.out.println("Added to packetizer " + handler);
            System.out.println("Done");
            other = handler.getPackage();
            System.out.println(handler + " " + other);
            handler.clear();

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

        byte[] appPackageBytes = AppPackage.createAppPackage(appID, content);

        Assert.assertEquals("Length", content.length + 4, appPackageBytes.length);
        Assert.assertEquals("AppID", appID, AppPackage.getAppID(appPackageBytes));
        Assert.assertArrayEquals("Content", content, AppPackage.getContent(appPackageBytes));
    }
}