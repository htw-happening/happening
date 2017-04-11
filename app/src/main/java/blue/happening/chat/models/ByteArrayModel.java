package blue.happening.chat.models;

import java.util.Arrays;

public abstract class ByteArrayModel {

    private boolean isBytesValide;

    protected ByteArrayModel() {
        this.isBytesValide = true;
    }

    public ByteArrayModel(byte[] bytes) {
        this.isBytesValide = validateBytes(bytes);
    }

    static byte[] trimZeros(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }

    protected boolean validateBytes(byte[] bytes) {
        // TODO validate byte array more
        return bytes.length == 128;
    }

    public boolean isBytesValide() {
        return isBytesValide;
    }

    public abstract byte[] toBytes();
}
