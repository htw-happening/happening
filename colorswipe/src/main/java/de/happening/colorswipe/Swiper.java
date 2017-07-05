package de.happening.colorswipe;


import android.util.Log;

import java.util.Random;

import blue.happening.HappeningClient;
import blue.happening.sdk.Happening;
import blue.happening.sdk.HappeningCallback;

public class Swiper {

    private static Swiper instance;
    private Happening happening;
    private int myIndex = 0;
    private int myColor = 0;
    private String TAG = getClass().getSimpleName();

    public static Swiper getInstance() {
        if (instance == null) instance = new Swiper();
        return instance;
    }

    private Swiper(){

        myColor = generateColor();

        happening = new Happening();
        happening.register(MyApplication.getAppContext(), new HappeningCallback() {
            @Override
            public void onClientAdded(HappeningClient happeningClient) {
                Log.d(getClass().getSimpleName(), "HappeningCallback - onClientAdded");
            }

            @Override
            public void onClientUpdated(HappeningClient happeningClient) {
                Log.d(getClass().getSimpleName(), "HappeningCallback - onClientUpdated");
            }

            @Override
            public void onClientRemoved(HappeningClient happeningClient) {
                Log.d(getClass().getSimpleName(), "HappeningCallback - onClientRemoved");
            }

            @Override
            public void onMessageReceived(byte[] bytes, HappeningClient happeningClient) {
                Log.d(getClass().getSimpleName(), "HappeningCallback - onMessageReceived");
            }
        });

    }

    public void setMyIndex(int myIndex) {
        Log.d(getClass().getSimpleName(), "setMyIndex: " + myIndex);
        this.myIndex = myIndex;
    }

    public int getMyColor() {
        return myColor;
    }

    public void setMyColor(int myColor) {
        this.myColor = myColor;
        MainActivity.getInstance().updateColor();
    }

    public void broadCastMyColor() {
        Log.d(TAG, "broadCastMyColor()");
    }


    public void setNewRandomColor(){
        this.myColor = generateColor();
    }

    public static int generateColor() {
        Random random = new Random();
        return  ((0xFF << 24) | ((random.nextInt(256)) << 16) | ((random.nextInt(256)) << 8) | random.nextInt(256));
    }
}
