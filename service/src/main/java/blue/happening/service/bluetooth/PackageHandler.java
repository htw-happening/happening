package blue.happening.service.bluetooth;

import java.nio.ByteBuffer;

public class PackageHandler {

    public static final int PAYLOAD_SIZE = 8;
    public static final int CHUNK_SIZE = 4;
    public static final int CHUNK_NUM = 4;

    private byte[] buffer = new byte[0];
    private int payloadNum = 0;
    private int payloadSize = 0;

    public PackageHandler (){

    }

    public PackageHandler createNewFromMeta(byte[] metadata) {
        byte[] payloadNumBytes = new byte[CHUNK_NUM];
        byte[] payloadSizeBytes = new byte[CHUNK_SIZE];
        System.arraycopy(metadata, 0, payloadNumBytes, 0, CHUNK_NUM);
        System.arraycopy(metadata, CHUNK_NUM, payloadSizeBytes, 0, CHUNK_NUM);
        final ByteBuffer bb = ByteBuffer.wrap(payloadNumBytes);
        this.payloadNum =  bb.getInt();
        final ByteBuffer bb2 = ByteBuffer.wrap(payloadSizeBytes);
        this.payloadSize =  bb2.getInt();
        return this;
    }

    public void addContent(byte[] data){
        this.buffer = merge(buffer, data);
    }

    public byte[] getContent() {
        return buffer;
    }

    public Package getPackage(){
        byte[] payloadBytes = new byte[payloadSize];
        System.arraycopy(buffer, 0, payloadBytes, 0, payloadSize);
        return new Package(payloadBytes);
    }

    public void clear(){
        this.buffer = new byte[0];
        this.payloadNum = 0;
        this.payloadSize = 0;
    }

    public int getPayloadNum() {
        return payloadNum;
    }

    public int getPayloadSize() {
        return payloadSize;
    }

    public static Package[] splitPackages(Package aPackage){

        int sizeOfInputPackageContent = aPackage.getData().length;

        int numOfPayloadPackages;
        if (sizeOfInputPackageContent%PAYLOAD_SIZE == 0){
            numOfPayloadPackages = sizeOfInputPackageContent/PAYLOAD_SIZE;

        }else{
            numOfPayloadPackages = sizeOfInputPackageContent/PAYLOAD_SIZE + 1;
        }

        byte[] data = ByteBuffer.allocate(CHUNK_SIZE).putInt(sizeOfInputPackageContent).array();
        byte[] data2 = ByteBuffer.allocate(CHUNK_NUM).putInt(numOfPayloadPackages).array();

        Package[] packagesToSend = new Package[2 + numOfPayloadPackages];
        packagesToSend[0] = new Package(data2);

        for (int i = 0; i < sizeOfInputPackageContent; i++) {
            if (((i) % PAYLOAD_SIZE) == 0){
                packagesToSend[i / PAYLOAD_SIZE + 1] = new Package(data);

                data = new byte[PAYLOAD_SIZE];
                data[i % PAYLOAD_SIZE] = aPackage.getData()[i];

            }else{
                data[i % PAYLOAD_SIZE] = aPackage.getData()[i];
            }
        }
        packagesToSend[numOfPayloadPackages + 1] = new Package(data);

        return packagesToSend;
    }

    private static byte[] merge(byte[] first, byte[] second){
        byte[] combined = new byte[first.length + second.length];
        System.arraycopy(first, 0, combined, 0 , first.length);
        System.arraycopy(second, 0, combined, first.length, second.length);
        return combined;
    }

    @Override
    public String toString() {
        return "payloadNum: " + payloadNum + " | payloadSize: "+payloadSize;
    }
}
