package de.happening.colorswipe;


import android.util.Log;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import blue.happening.HappeningClient;
import blue.happening.sdk.Happening;
import blue.happening.sdk.HappeningCallback;

public class Swiper {

    private static Swiper instance;
    private Happening happening;
    private int myIndex = 0;
    private int myColor = 0;

    private String TAG = getClass().getSimpleName();

    public static final int MIN_INDEX = 1;
    public static final int MAX_INDEX = 4;

    public enum Direction{
        LEFT, RIGHT
    }

    public enum Packet{
        OGM_OBJECT, SWIPE_OBJECT
    }

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
                checkClients();
            }

            @Override
            public void onClientUpdated(HappeningClient happeningClient) {
                Log.d(getClass().getSimpleName(), "HappeningCallback - onClientUpdated");
                checkClients();
            }

            @Override
            public void onClientRemoved(HappeningClient happeningClient) {
                Log.d(getClass().getSimpleName(), "HappeningCallback - onClientRemoved");
                checkClients();
            }

            @Override
            public void logMessage(int packageType, int action) {
                Log.d(TAG, "logMessage: " + action);
                Log.d(TAG, "logMessage: PACKAGETYPE: "+ packageType);
                switch (packageType){

                    case 1: //OGM
                        /*
                        from Meshhandler
                        public static final int MESSAGE_ACTION_ARRIVED = 0;
                        public static final int MESSAGE_ACTION_RECEIVED = 1;
                        public static final int MESSAGE_ACTION_DROPPED = 2;
                        public static final int MESSAGE_ACTION_FORWARDED = 3;

                         */
                        if (action == 0) {
                            MainActivity.getInstance().startAnimation(Direction.RIGHT, generateColor(), Packet.OGM_OBJECT);
                            break;
                        }
                        break;
                    case 2: //UCM

                        MainActivity.getInstance().startAnimation(Direction.RIGHT, 0xFF000000, Packet.OGM_OBJECT);

                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onMessageReceived(byte[] bytes, HappeningClient happeningClient) {
                Log.d(getClass().getSimpleName(), "HappeningCallback - onMessageReceived");
                final ColorPackage colorPackage = ColorPackage.fromBytes(bytes);
                if (colorPackage.getTo() == getMyIndex()){
                    Log.d(TAG, "onMessageReceived: CHANGE MY COLOR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    final int currentReceivedColor = colorPackage.getColor();
                    MainActivity.getInstance().startAnimation(colorPackage.getDirection(), currentReceivedColor, Packet.SWIPE_OBJECT);

                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: REBROADCAST COLOR");
                            broadCastColor(colorPackage.getDirection(), currentReceivedColor);
                        }
                    };
                    timer.schedule(timerTask, 900);
                }
            }
        });
    }

    void checkClients(){
//        List<HappeningClient> happeningClients
    }



    public void setMyIndex(int myIndex) {
        Log.d(getClass().getSimpleName(), "setMyIndex: " + myIndex);
        this.myIndex = myIndex;
    }

    public int getMyIndex() {
        return myIndex;
    }

    public int getMyColor() {
        return myColor;
    }

    public void broadCastColor(Direction direction, int color) {
        Log.d(TAG, "broadCastColor()");
        ColorPackage colorPackage = null;
        if (direction == Direction.LEFT){
            colorPackage = new ColorPackage(getMyIndex(), getMyIndex() - 1, direction, color);
        }
        if (direction == Direction.RIGHT){
            colorPackage = new ColorPackage(getMyIndex(), getMyIndex() + 1, direction, color);
        }

        if (colorPackage.getTo() > MAX_INDEX || colorPackage.getTo() < MIN_INDEX){
            Log.d(TAG, "broadCastColor: END OF LINE! Do not rebroadcast");
            Log.d(TAG, "broadCastColor: getTo: " + colorPackage.getTo());
            return;
        }

        happening.sendMessage(colorPackage.toBytes());
    }


    public void setNewRandomColor(){
        this.myColor = generateColor();
    }

    public static int generateColor() {
        Random random = new Random();
        return  ((0xFF << 24) | ((random.nextInt(256)) << 16) | ((random.nextInt(256)) << 8) | random.nextInt(256));
    }
}
