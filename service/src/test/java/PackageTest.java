import org.junit.Assert;
import org.junit.Test;

import blue.happening.service.bluetooth.Package;
import blue.happening.service.bluetooth.PackageHandler;

public class PackageTest {

    @Test
    public void checkSplitting() {
        Assert.assertEquals(1,1);

        Package aPackage = new Package(new byte[]{0,1,2,3,4,5,6,8,9,10,11,12,13,14,15,16,17,18,19,20});

        Package[] data = PackageHandler.splitPackages(aPackage);

    }
}