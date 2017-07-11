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
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, GestureDetector.OnGestureListener {

    private String TAG = getClass().getSimpleName();
    private GestureDetector gDetector;
    TextView textView;
    private static MainActivity instance;

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

    public static MainActivity getInstance() {
        return instance;
    }

    public void updateColor(){
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

        Log.d(TAG, "onFling: xDiff " + xDiff  + " | yDiff " +yDiff);

        if (xDiff>yDiff){
            //horizonatal
            Log.d(TAG, "onFling: horizontal");
            if (start.getRawX() < finish.getRawX()) {
                //right
                swiper.broadCastColor(Swiper.Direction.RIGHT, swiper.getMyColor());
                Log.d(TAG, "onFling: right");
                startAnimation(R.id.animateObject, Swiper.Direction.RIGHT, Swiper.getInstance().getMyColor());


            } else {
                //left
                swiper.broadCastColor(Swiper.Direction.LEFT, swiper.getMyColor());
                Log.d(TAG, "onFling: left");

                startAnimation(R.id.animateObject, Swiper.Direction.LEFT, Swiper.getInstance().getMyColor());

            }
        }
        else{
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

    void startAnimation(final int objectId, final Swiper.Direction direction, final int color) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView view = (TextView) findViewById(objectId);
                view.setBackgroundColor(color);

                Animation animate = null;
                switch (direction) {
                    case LEFT:
                        animate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_left);
                        view.startAnimation(animate);
                        break;
                    case RIGHT:
                        animate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_right);
                        view.startAnimation(animate);
                        break;
                    default:
                        break;
                }

            }
        });

    }

}
