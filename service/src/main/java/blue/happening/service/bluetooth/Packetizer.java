package blue.happening.service.bluetooth;

import java.nio.ByteBuffer;

public class Packetizer {

    public static final int CHUNK_SIZE = 4;

    private byte[] data = new byte[0];
    private int payloadSize = 0;

    public Packetizer() {

    }

    public static Package[] splitPackages(Package aPackage) {

        int sizeOfInputPackageContent = aPackage.getData().length;
        byte[] meta = ByteBuffer.allocate(CHUNK_SIZE).putInt(sizeOfInputPackageContent).array();

        Package[] packagesToSend = new Package[2];
        packagesToSend[0] = new Package(meta);
        packagesToSend[1] = new Package(aPackage.getData());

        return packagesToSend;
    }

    public Packetizer createNewFromMeta(byte[] metadata) {
        final ByteBuffer bb2 = ByteBuffer.wrap(metadata);
        this.payloadSize = bb2.getInt();
        return this;
    }

    public void addContent(byte[] data) {
        this.data = new byte[data.length];
        System.arraycopy(data, 0, this.data, 0, payloadSize);
    }

    public byte[] getContent() {
        return this.data;
    }

    public Package getPackage() {
        return new Package(this.data);
    }

    public void clear() {
        this.data = new byte[0];
        this.payloadSize = 0;
    }

    public int getPayloadSize() {
        return payloadSize;
    }

    @Override
    public String toString() {
        return "payloadSize: " + payloadSize;
    }
}
