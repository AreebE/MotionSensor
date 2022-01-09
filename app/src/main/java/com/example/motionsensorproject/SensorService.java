package com.example.motionsensorproject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class SensorService extends Service {

    private SensorEventListener proximityListener = new SensorEventListener()
    {
        private long startTime;
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
        }

        private void farDistanceTriggered()
        {
            long endTime = System.currentTimeMillis();
            long timeInterval = endTime - startTime;
            Log.d(TAG, timeInterval + "");
            if (timeInterval > ONE_SEC && timeInterval < TWO_SEC)
            {
                Intent i = getPackageManager().getLaunchIntentForPackage(list.getPackageName("Brawl Stars"));
//                i.setType("plain/text");
//                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Toast t = Toast.makeText(getBaseContext(), "Trigger event!", Toast.LENGTH_SHORT);
                t.show();
                getBaseContext().startActivity(i);
                Log.d(TAG, "made it to end" + getBaseContext().toString());
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i)
        {

        }
    };

    private static final long ONE_SEC = 1000;
    private static final long TWO_SEC = 2000;

    private static final String TAG = "SensorService";

    private static final int ID = 320507;
    private static final String CHANNEL_ID = "Foreground Sensor";

    private ApplicationList list;
    public SensorService() {
        super();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        list = ApplicationList.getInstance(getPackageManager());
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(proximityListener);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY, true);
        sensorManager.registerListener(proximityListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
//        new Thread
//                (
//                        new Runnable()
//                        {
//                            @Override
//                            public void run()
//                            {
//
//                            }
//                        }
//                )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Proximity Sensor", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notifBuilder = new Notification.Builder(this, CHANNEL_ID)
                    .setContentText("The proximity sensor is running")
                    .setContentTitle("Testing Service")
                    .setSmallIcon(R.drawable.ic_launcher_foreground);
//            startForeground(ID, notifBuilder.build());
        }


        return super.onStartCommand(intent, flags, startId);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


//



}