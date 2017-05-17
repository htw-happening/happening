package blue.happening.service.bluetooth;

import java.util.Arrays;

public class Package {

    private byte[] data;

    public Package (byte[] data){
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }
}
