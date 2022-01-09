package com.example.motionsensorproject;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProximitySensorListener implements SensorEventListener {
    private long startTime;

    private static final String TAG = "SensorFragment";
    private TextView display;
    private ArrayList<InfoItem> list;
    private Context c;
    private TimerBar timerBar;
    private Handler handler;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            timerBar.updateTime(System.currentTimeMillis() - startTime);
            handler.postDelayed(runnable, 1000 / 60);
        }
    };

    public ProximitySensorListener(TextView display, ArrayList<InfoItem> list, Context c, TimerBar timerBar)
    {
        this.display = display;
        this.list = list;
        this.c = c;
        this.timerBar = timerBar;
        this.handler = new Handler();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        float[] values = sensorEvent.values;
        float distance = values[0];
        Log.d(TAG, distance + ", ");
        if (new Float(distance).equals(0.0f))
        {
//                Log.d(TAG, "short distance triggered");
            nearDistanceTriggered();
        }
        else
        {
            farDistanceTriggered();
        }
//            display.setText(Float.toString(values[0]));
//            Toast.makeText(getContext(), Double.toString(values[0]), Toast.LENGTH_SHORT).show();
//            Intent i = new Intent(Intent.ACTION_ALL_APPS);
//            i.putExtra("Title", "testing" );

//            ?startActivity(i);
//            Log.d(TAG, "sensor changed");
    }

    private void nearDistanceTriggered()
    {
        startTime = System.currentTimeMillis();
        timerBar.changeState(TimerBar.State.TIMED);
        handler.postDelayed(runnable, 1000/60);
    }

    private void farDistanceTriggered()
    {
        long endTime = System.currentTimeMillis();
        long timeInterval = (endTime - startTime);
        handler.removeCallbacks(runnable);
        timerBar.changeState(TimerBar.State.NONE);
        Log.d(TAG, timeInterval + "");
        if (timeInterval < SensorFragment.MIN_TIME || timeInterval > SensorFragment.MAX_TIME)
        {
            return;
        }
        int item = (int) ((timeInterval - SensorFragment.MIN_TIME) / SensorFragment.INTERVAL);
        InfoItem packageItem = list.get(item);
        if (packageItem == null)
        {
            return;
        }
        Intent i = c.getPackageManager().getLaunchIntentForPackage(list.get(item).getPackageName());
        try {
            c.startActivity(i);
        }
        catch (NullPointerException e)
        {
            Toast.makeText(c, "Could not start activity", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    public void reset()
    {
        startTime = 0;
    }
}
