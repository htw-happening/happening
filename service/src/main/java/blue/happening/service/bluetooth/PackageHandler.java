package blue.happening.service.bluetooth;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

public abstract class PackageHandler {

    public static final int PAYLOAD_SIZE = 6;
    public static final int CHUNK_SIZE = 4;

    private static String TAG = "PackageHandler";


    public static Package[] splitPackages(Package aPackage){

        int sizeOfInputPackageContent = aPackage.getData().length;
        System.out.println("splitPackages: sizeOfInputPackageContent: " +sizeOfInputPackageContent);

        int numOfPayloadPackages;
        if (sizeOfInputPackageContent%PAYLOAD_SIZE == 0){
            numOfPayloadPackages = sizeOfInputPackageContent/PAYLOAD_SIZE;

        }else{
            numOfPayloadPackages = sizeOfInputPackageContent/PAYLOAD_SIZE + 1;
        }
        System.out.println("splitPackages: numOfPayloadPackages: "+numOfPayloadPackages);

        byte[] data = ByteBuffer.allocate(CHUNK_SIZE).putInt(numOfPayloadPackages).array();

        Package[] packagesToSend = new Package[1 + numOfPayloadPackages];

        for (int i = 0; i < sizeOfInputPackageContent; i++) {
            System.out.println("splitPackages: i: "+i + " | "+(i) % PAYLOAD_SIZE);
            if (((i) % PAYLOAD_SIZE) == 0){
                System.out.println("splitPackages: create Package");
                packagesToSend[i / PAYLOAD_SIZE] = new Package(data);
                System.out.println("splitPackages: add ol to index "+ (i / PAYLOAD_SIZE));

                data = new byte[PAYLOAD_SIZE];
                data[i % PAYLOAD_SIZE] = aPackage.getData()[i];

            }else{
                System.out.println("splitPackages: fill Package");
                data[i % PAYLOAD_SIZE] = aPackage.getData()[i];
            }
        }
        packagesToSend[numOfPayloadPackages] = new Package(data);

        for (int i = 0; i < packagesToSend.length; i++) {
            System.out.println(packagesToSend[i]);
        }
        return packagesToSend;
    }

}
