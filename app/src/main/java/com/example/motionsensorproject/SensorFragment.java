package com.example.motionsensorproject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SensorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class SensorFragment extends Fragment {

    private static final ColorChoices[] COLORS = new ColorChoices[]
            {
                    ColorChoices.BROWN,
                    ColorChoices.RED,
                    ColorChoices.ORANGE,
                    ColorChoices.YELLOW,
                    ColorChoices.GREEN,
                    ColorChoices.TEAL,
                    ColorChoices.PURPLE,
                    ColorChoices.BLACK
            };

    public interface SavePackageItemListener
    {
        public void saveItems(String[] items);
    }

    public static enum ColorChoices
    {
        BROWN("Brown", R.color.brown),
        RED("Red", R.color.red),
        ORANGE("Orange", R.color.oran),
        YELLOW("Yellow", R.color.yell),
        GREEN("Green", R.color.gree),
        TEAL("Teal", R.color.teal),
        PURPLE("Purple", R.color.purp),
        BLACK("Black", R.color.black);


        private String name;
        private int id;

        ColorChoices(String name, int id)
        {
            this.name = name;
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public int getId() {
            return id;
        }
    }

    public static final long MAX_TIME = 3500;
    public static final long MIN_TIME = 1000;
    public static final int NUM_INTERVALS = COLORS.length - 2;
    public static final int INTERVAL = (int) (MAX_TIME - MIN_TIME) / NUM_INTERVALS;

    private static final String ITEMS_KEYS = "Items KEYS";
    private static final String TAG = "SensorFragment";
    private TextView display;
    private ApplicationList list;
    private ListView listView;
    private InfoItem[] chosenItems;
    private ProximitySensorListener proximityListener;



    public SensorFragment() {
        // Required empty public constructor
    }

    public static SensorFragment newInstance(String[] packageNames) {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putStringArray(ITEMS_KEYS, packageNames);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "created fragment");
        list = ApplicationList.getInstance(getContext().getPackageManager());
        chosenItems = new InfoItem[NUM_INTERVALS];
        setHasOptionsMenu(true);
        Bundle saveState = (savedInstanceState == null)? getArguments(): savedInstanceState;
        if (saveState != null)
        {
            String[] items = saveState.getStringArray(ITEMS_KEYS);
            for (int i = 0; i < items.length; i++)
            {
                if (items[i] != null)
                {
                    chosenItems[i] = list.getItem(items[i]);
                }
                else
                {
                    chosenItems[i] = null;
                }
            }
        }
        else {
            for (int i = 0; i < NUM_INTERVALS; i++)
            {
                chosenItems[i] = null;
            }
        }


//        if (!isForegroundServiceRunning())
//        {
//            Intent i = new Intent(getActivity(), SensorService.class);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            i.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            getActivity().startService(i);
//            getActivity().startForegroundService(i);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sensor, container, false);
        listView = (ListView) v.findViewById(android.R.id.list);
        listView.setAdapter(new PackageAdapter(getActivity(), chosenItems));
        TimerBar timerBar = v.findViewById(R.id.time_bar);
        proximityListener = new ProximitySensorListener(display, chosenItems, getContext(), timerBar);
//        display = (TextView) v.findViewById(R.id.sensorFrag_proximityType);
        return v;
    }

    private class PackageAdapter extends ArrayAdapter<InfoItem>
    {
        public PackageAdapter(@NonNull Context context, @NonNull InfoItem[] objects)
        {
            super(context, R.layout.list_set_item, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(R.layout.list_set_item, null);
            }

            ((TextView) convertView.findViewById(R.id.time_interval))
                    .setText(getColor(position + 1).getName());

            InfoItem current = getItem(position);
            if (current == null)
            {
                ((TextView) convertView.findViewById(R.id.app_name)).setText("No app selected");
                return convertView;
            }

            ((TextView) convertView.findViewById(R.id.app_name)).setText(current.getName());
            ((ImageView) convertView.findViewById(R.id.image)).setImageDrawable(current.getIcon());
            return convertView;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult " + requestCode);
        if (resultCode == Activity.RESULT_OK)
        {
            switch (requestCode)
            {
                case ChooseApplicationFragment.CODE:
                    String packageName = data.getStringExtra(ChooseApplicationFragment.PACKAGE_KEY_NAME);
                    int position = data.getIntExtra(ChooseApplicationFragment.POSITION_KEY, -1);
                    if (position != -1)
                    {
                        chosenItems[position] = list.getItem(packageName);
                        Log.d(TAG, packageName);
                        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                    }
                    break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.sensor_frag_options_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.search:
                DialogFragment fragment = ChooseApplicationFragment.newInstance(0);
                fragment.setTargetFragment(this, ChooseApplicationFragment.CODE);
                fragment.show(getActivity().getSupportFragmentManager(), TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "resumed");
        SensorManager sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
//        sensorManager.unregisterListener(proximityListener);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY, true);
        sensorManager.registerListener(proximityListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
//        Intent i = new Intent(getActivity(), SensorService.class);
//        PendingIntent pi = PendingIntent.getService(getActivity(), 0, i, 0);
//        pi.send();
//        getActivity().startService();
    }



    @Override
    public void onPause() {
        super.onPause();
        ((SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE)).unregisterListener(proximityListener);
        proximityListener.reset();
    }

    private boolean isForegroundServiceRunning()
    {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (SensorService.class.getName().equals(service.service.getClassName()))
            {
//                service.service.notify();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(ITEMS_KEYS, getNames());

    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((SavePackageItemListener) getActivity()).saveItems(getNames());
    }

    private String[] getNames()
    {
        String[] names = new String[NUM_INTERVALS];
        for (int i = 0; i < NUM_INTERVALS; i++)
        {
            if (chosenItems[i] != null)
            {
                names[i] = chosenItems[i].getName();
            }
            else
            {
                names[i] = null;
            }
        }
        return names;
    }

    public static ColorChoices getColor(int index)
    {
        if (index < COLORS.length && index >= 0)
        {
            return COLORS[index];
        }
        return ColorChoices.BLACK;
    }
}