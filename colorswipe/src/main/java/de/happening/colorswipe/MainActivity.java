package de.happening.colorswipe;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

        float xDiff = Math.abs(Math.abs(start.getRawX()) - Math.abs(finish.getRawX()));
        float yDiff = Math.abs(Math.abs(start.getRawY()) - Math.abs(finish.getRawY()));

        Log.d(TAG, "onFling: xDiff " + xDiff  + " | yDiff " +yDiff);

        if (xDiff>yDiff){
            //horizonatal
            Log.d(TAG, "onFling: horizontal");
            if (start.getRawX() < finish.getRawX()) {
                //right
                Log.d(TAG, "onFling: right");
            } else {
                //left
                Log.d(TAG, "onFling: left");
            }
            Swiper.getInstance().broadCastMyColor();
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
}
