package com.technohacker.apps.phonemouse;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.technohacker.apps.phonemouse.net.NetworkThread;

public class MainActivity extends ActionBarActivity implements SensorEventListener {

    private long lastUpdate = 0;
    private static final float TILT_THRESHOLD = 1.1f;

    private static final int NONE = 0;
    private static final int SWIPE = 1;
    private int mode = NONE;
    private float startY;
    private float stopY;
    // We will only detect a swipe if the difference is at least 100 pixels
    // Change this value to your needs
    private static final int SWIPE_THRESHOLD = 100;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button btnLeft = (Button) findViewById(R.id.btnLeft);
        final Button btnRight = (Button) findViewById(R.id.btnRight);

        btnLeft.setOnTouchListener((v, event) -> {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    new NetworkThread().execute("LFTPRS");
                    return true; // if you want to handle the touch event
                case MotionEvent.ACTION_UP:
                    new NetworkThread().execute("LFTRLS");
                    return true; // if you want to handle the touch event
            }
            return false;
        });

        btnRight.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    new NetworkThread().execute("RGTPRS");
                    return true; // if you want to handle the touch event
                case MotionEvent.ACTION_UP:
                    new NetworkThread().execute("RGTRLS");
                    return true; // if you want to handle the touch event
            }
            return false;
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        NetworkThread net = new NetworkThread();
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_POINTER_DOWN:
                // This happens when you touch the screen with two fingers
                mode = SWIPE;
                // You can also use event.getY(1) or the average of the two
                startY = event.getY(0);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // This happens when you release the second finger
                mode = NONE;
                if(Math.abs(startY - stopY) > SWIPE_THRESHOLD){
                    if(startY > stopY){
                        //Swipe up
                        net.execute("SCRLDN");
                    }
                    else{
                        //Swipe down
                        net.execute("SCRLUP");
                    }
                }
                this.mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if(mode == SWIPE){
                    stopY = event.getY(0);
                }
                break;
        }
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(sensorEvent);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            //float z = event.values[2];
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > (1000/60)) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                if(Math.abs(x) > TILT_THRESHOLD || Math.abs(y) > TILT_THRESHOLD) {
                    int dx = (int)(x  * diffTime);
                    int dy = (int)(y  * diffTime);

                    new NetworkThread().execute("MOVE " + dx + "," + dy);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Ignorable
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
    }
}
