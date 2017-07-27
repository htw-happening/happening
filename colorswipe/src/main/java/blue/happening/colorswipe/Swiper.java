package blue.happening.colorswipe;


import android.util.Log;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import blue.happening.HappeningClient;
import blue.happening.sdk.Happening;
import blue.happening.sdk.HappeningCallback;

public class Swiper {

    public static final int MIN_INDEX = 1;
    public static final int MAX_INDEX = 4;
    private static final int[] STATIC_COLOR_TABLE = {
            0xFFFAD93A, 0xFF51975D, 0xFF182D63, 0xFFFA522F
    };
    private static Swiper instance;
    private Happening happening;
    private int myIndex = 0;
    private int myColor = 0;
    private String TAG = getClass().getSimpleName();

    private Swiper() {

        myColor = generateColor();

        happening = new Happening();
        happening.register(MyApplication.getAppContext(), new HappeningCallback() {
            @Override
            public void onClientAdded(HappeningClient happeningClient) {
//                Log.d(getClass().getSimpleName(), "HappeningCallback - onClientAdded");
//                checkClients();
            }

            @Override
            public void onClientUpdated(HappeningClient happeningClient) {
//                Log.d(getClass().getSimpleName(), "HappeningCallback - onClientUpdated");
//                checkClients();
            }

            @Override
            public void onClientRemoved(HappeningClient happeningClient) {
//                Log.d(getClass().getSimpleName(), "HappeningCallback - onClientRemoved");
//                checkClients();
            }

            @Override
            public void onMessageLogged(int packageType, int action) {
//                Log.d(TAG, "onMessageLogged: " + action);
//                Log.d(TAG, "onMessageLogged: PACKAGETYPE: "+ packageType);
                switch (packageType){

                    case MESSAGE_TYPE_OGM:
                        if (action == MESSAGE_ACTION_ARRIVED) {
                            MainActivity.getInstance().startAnimation(Direction.RIGHT, generateColor(), Packet.OGM_OBJECT);
                            break;
                        }
                        break;
                    case MESSAGE_TYPE_UCM:
                        MainActivity.getInstance().startAnimation(Direction.RIGHT, 0xFF444444, Packet.OGM_OBJECT);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onMessageReceived(byte[] bytes, HappeningClient happeningClient) {
//                Log.d(getClass().getSimpleName(), "HappeningCallback - onMessageReceived");
                final ColorPackage colorPackage = ColorPackage.fromBytes(bytes);
                if (colorPackage.getTo() == getMyIndex()) {
//                    Log.d(TAG, "onMessageReceived: CHANGE MY COLOR!! " + colorPackage.toString());
                    final int currentReceivedColor = colorPackage.getColor();
                    MainActivity.getInstance().startAnimation(colorPackage.getDirection(), currentReceivedColor, Packet.SWIPE_OBJECT);

                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
//                            Log.d(TAG, "run: REBROADCAST COLOR");
                            broadCastColor(colorPackage.getDirection(), currentReceivedColor);
                        }
                    };
                    timer.schedule(timerTask, 900);
                }
            }
        });
    }

    public static Swiper getInstance() {
        if (instance == null) instance = new Swiper();
        return instance;
    }

    public static int generateColor() {
        Random random = new Random();
        return ((0xFF << 24) | ((random.nextInt(256)) << 16) | ((random.nextInt(256)) << 8) | random.nextInt(256));
    }

    void checkClients() {
//        List<HappeningClient> happeningClients
    }

    public int getMyIndex() {
        return myIndex;
    }

    public void setMyIndex(int myIndex) {
        Log.d(getClass().getSimpleName(), "setMyIndex: " + myIndex);
        this.myIndex = myIndex;
    }

    public int getMyColor() {
        return myColor;
    }

    public void setStaticColor() {
        this.myColor = STATIC_COLOR_TABLE[getMyIndex() - 1];
    }

    public void broadCastColor(final Direction direction, final int color) {
//        Log.d(TAG, "broadCastColor()");
        ColorPackage colorPackage = null;
        if (direction == Direction.LEFT) {
            colorPackage = new ColorPackage(getMyIndex(), getMyIndex() - 1, direction, color);
        }
        if (direction == Direction.RIGHT) {
            colorPackage = new ColorPackage(getMyIndex(), getMyIndex() + 1, direction, color);
        }

        if (colorPackage.getTo() > MAX_INDEX || colorPackage.getTo() < MIN_INDEX) {
//            Log.d(TAG, "broadCastColor: END OF LINE! Do not rebroadcast");
//            Log.d(TAG, "broadCastColor: getTo: " + colorPackage.getTo());
            return;
        }
//        Log.d(TAG, "broadCastColor: "+colorPackage.toString());
        happening.sendMessage(colorPackage.toBytes());
    }

    public void setNewRandomColor() {
        this.myColor = generateColor();
    }

    public Happening getHappening() {
        return happening;
    }


    public enum Direction {
        LEFT, RIGHT
    }

    public enum Packet {
        OGM_OBJECT, SWIPE_OBJECT
    }
}
