package blue.happening.colorswipe;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, GestureDetector.OnGestureListener {

    private static MainActivity instance;
    private ImageView imageView;
    private TextView textView;
    private String TAG = getClass().getSimpleName();
    private GestureDetector gDetector;
    private int idCounter = 0;
    private static final long DOUBLE_TAP_TIME_DIFF = 400;
    private long lastTap = System.currentTimeMillis();

    public static final String KEY_PREFS_SPINNER_ID = "spinner_id";
    private static final String APP_SHARED_PREFS = MainActivity.class.getSimpleName();
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        this.sharedPrefs = getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this.prefsEditor = sharedPrefs.edit();

        int id = sharedPrefs.getInt(KEY_PREFS_SPINNER_ID, 1);
        spinner.setSelection(id-1);
        Swiper.getInstance().setMyIndex(id);
        Swiper.getInstance().setStaticColor();
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        textView.setBackgroundColor(Swiper.getInstance().getMyColor());
        gDetector = new GestureDetector(this);

        Context context = getApplicationContext();
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Log.d(TAG, "start: NOT SCAN_MODE_CONNECTABLE_DISCOVERABLE --> Switch on Discoverable!");
            Intent makeMeVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            makeMeVisible.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            makeMeVisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0); //infinity
            context.startActivity(makeMeVisible);
        }

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Swiper.getInstance().setMyIndex(pos + 1);

        Swiper.getInstance().setStaticColor();
        textView = (TextView) findViewById(R.id.textView);
        textView.setBackgroundColor(Swiper.getInstance().getMyColor());

        prefsEditor.putInt(KEY_PREFS_SPINNER_ID, pos+1);
        prefsEditor.commit();
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp");
        long diff = System.currentTimeMillis() - lastTap;
        Log.d(TAG, "onSingleTapUp: diff " + diff);
        if (diff < DOUBLE_TAP_TIME_DIFF){
            Log.d(TAG, "onSingleTapUp: Double Tap Triggered");
            Swiper.getInstance().setStaticColor();
            textView.setBackgroundColor(Swiper.getInstance().getMyColor());

        }
        lastTap = System.currentTimeMillis();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(final MotionEvent start, final MotionEvent finish, float velocityX, float velocityY) {

        Swiper swiper = Swiper.getInstance();

        final float startX = start.getRawX();
        final float startY = start.getRawY();
        final float finishX = finish.getRawX();
        final float finishY = finish.getRawY();

        final float xDiff = Math.abs(Math.abs(startX) - Math.abs(finishX));
        final float yDiff = Math.abs(Math.abs(startY) - Math.abs(finishY));

        Log.d(TAG, "onFling: xDiff " + xDiff + " | yDiff " + yDiff);

        if (xDiff > yDiff) {
            //horizonatal
            Log.d(TAG, "onFling: horizontal");
            final int colorToBroadcast = swiper.getMyColor();
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (startX < finishX) {
                        //right
                        Swiper.getInstance().broadCastColor(Swiper.Direction.RIGHT, colorToBroadcast);
                    } else {
                        //left
                        Swiper.getInstance().broadCastColor(Swiper.Direction.LEFT, colorToBroadcast);
                    }

                }
            };
            timer.schedule(timerTask, 900);


            if (startX < finishX) {
                //right
                startAnimation(Swiper.Direction.RIGHT, Swiper.getInstance().getMyColor(), Swiper.Packet.SWIPE_OBJECT);
            } else {
                //left
                startAnimation(Swiper.Direction.LEFT, Swiper.getInstance().getMyColor(), Swiper.Packet.SWIPE_OBJECT);
            }

        } else {
            //vertical
//            Log.d(TAG, "onFling: vertial");
            if (start.getRawY() < finish.getRawY()) {
                //down
//                Log.d(TAG, "onFling: down");
            } else {
                //up
//                Log.d(TAG, "onFling: up");
            }
            Swiper.getInstance().setNewRandomColor();
            textView.setBackgroundColor(Swiper.getInstance().getMyColor());
        }
        return true;

    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return gDetector.onTouchEvent(me);
    }

    void startAnimation(final Swiper.Direction direction, final int color, final Swiper.Packet packetType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView obj = createAnimationObject(packetType);
                obj.setBackgroundColor(color);

                Animation animate = null;
                switch (direction) {
                    case LEFT:
                        animate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_left);
                        obj.startAnimation(animate);
                        break;
                    case RIGHT:
                        animate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_right);
                        obj.startAnimation(animate);
                        break;
                    default:
                        break;
                }

            }
        });

    }

    private TextView createAnimationObject(Swiper.Packet packetType) {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        TextView obj = new TextView(MyApplication.getAppContext());

        idCounter = idCounter + 1;
        obj.setId(idCounter);

        for (int i = 0; i < layout.getChildCount(); i++) {
            View current = layout.getChildAt(i);
            if (current.getId() < idCounter - 25) {
                layout.removeViewAt(i);
            }
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(70, 70);
        switch (packetType) {
            case OGM_OBJECT:
                params.height = getResources().getDimensionPixelSize(R.dimen.animation_ogm_obj_size);
                params.width = getResources().getDimensionPixelSize(R.dimen.animation_ogm_obj_size);
                Random rndm = new Random();
                params.setMargins(0, getResources().getDimensionPixelSize(R.dimen.animation_ogm_obj_margin) + rndm.nextInt(100), 0, 0);
                params.setMarginStart(getResources().getDimensionPixelSize(R.dimen.animation_ogm_obj_margin_start));
                break;
            case SWIPE_OBJECT:
                params.height = getResources().getDimensionPixelSize(R.dimen.animation_swipe_obj_size);
                params.width = getResources().getDimensionPixelSize(R.dimen.animation_swipe_obj_size);
                params.setMargins(0, getResources().getDimensionPixelSize(R.dimen.animation_swipe_obj_margin), 0, 0);
                params.setMarginStart(getResources().getDimensionPixelSize(R.dimen.animation_swipe_margin_start));
                break;
        }

        layout.addView(obj, params);

        return obj;
    }

}
