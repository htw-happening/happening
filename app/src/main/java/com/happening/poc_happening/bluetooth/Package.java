package com.happening.poc_happening.bluetooth;

import java.util.Arrays;

/**
 * Created by fabi on 17.05.17.
 */

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
