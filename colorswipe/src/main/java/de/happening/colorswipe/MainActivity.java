package de.happening.colorswipe;

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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, GestureDetector.OnGestureListener {

    private static MainActivity instance;
    TextView textView;
    private String TAG = getClass().getSimpleName();
    private GestureDetector gDetector;
    private int idCounter = 0;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        Log.d(TAG, "onCreate: ");

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        textView = (TextView) findViewById(R.id.textView);
        textView.setBackgroundColor(Swiper.getInstance().getMyColor());

        gDetector = new GestureDetector(this);
    }

    public void updateColor() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setBackgroundColor(Swiper.getInstance().getMyColor());

            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        Swiper.getInstance().setMyIndex(pos + 1);
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
    public boolean onFling(MotionEvent start, MotionEvent finish, float velocityX, float velocityY) {

        Swiper swiper = Swiper.getInstance();

        float xDiff = Math.abs(Math.abs(start.getRawX()) - Math.abs(finish.getRawX()));
        float yDiff = Math.abs(Math.abs(start.getRawY()) - Math.abs(finish.getRawY()));

        Log.d(TAG, "onFling: xDiff " + xDiff + " | yDiff " + yDiff);

        if (xDiff > yDiff) {
            //horizonatal
            Log.d(TAG, "onFling: horizontal");
            if (start.getRawX() < finish.getRawX()) {
                //right
                swiper.broadCastColor(Swiper.Direction.RIGHT, swiper.getMyColor());
                Log.d(TAG, "onFling: right");

                startAnimation(Swiper.Direction.RIGHT, Swiper.getInstance().getMyColor(), Swiper.Packet.SWIPE_OBJECT);
//                startAnimation(Swiper.Direction.RIGHT, Swiper.getInstance().getMyColor(), Swiper.Packet.OGM_OBJECT);
            } else {
                //left
                swiper.broadCastColor(Swiper.Direction.LEFT, swiper.getMyColor());
                Log.d(TAG, "onFling: left");

                startAnimation(Swiper.Direction.LEFT, Swiper.getInstance().getMyColor(), Swiper.Packet.SWIPE_OBJECT);
//                startAnimation(Swiper.Direction.LEFT, Swiper.getInstance().getMyColor(), Swiper.Packet.OGM_OBJECT);
            }
        } else {
            //vertical
            Log.d(TAG, "onFling: vertial");
            if (start.getRawY() < finish.getRawY()) {
                //down
                Log.d(TAG, "onFling: down");
            } else {
                //up
                Log.d(TAG, "onFling: up");
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
//                TextView view = (TextView) findViewById(objectId);
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
//        Log.d("REMOVE", "COUNTER " + idCounter);

        for (int i = 0; i < layout.getChildCount(); i++) {
            View current = layout.getChildAt(i);
            if (current.getId() < idCounter - 25) {
//                Log.d("REMOVE", "VIEW " + current.getId() + " child count " + layout.getChildCount());
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