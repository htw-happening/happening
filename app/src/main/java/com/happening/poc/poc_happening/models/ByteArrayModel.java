package com.happening.poc.poc_happening.models;

/**
 * Created by daired on 22/01/17.
 */

public abstract class ByteArrayModel {

    private boolean isValide;

    public  ByteArrayModel(){
        this.isValide = true;
    }
    public  ByteArrayModel(byte[] bytes){
        this.isValide = validateBytes(bytes);
    }

    protected boolean validateBytes(byte[] bytes){
        //TODO validate byte array
        return true;
    }

    public boolean isValide(){
        return isValide;
    }

    public abstract byte[] toBytes();


}
