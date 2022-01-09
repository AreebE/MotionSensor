package com.example.motionsensorproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import java.security.Permission;
import java.time.LocalDate;

public class MainActivity
        extends AppCompatActivity
        implements SensorFragment.SavePackageItemListener
{

    private static final String TAG = "MainActivity";
    private String[] items;
    private static final String PACKAGE_ITEM_KEYS = "key package item";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "created activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentById(R.id.fragment_container) == null)
        {
            Fragment f;
            if (savedInstanceState != null)
            {
                f = SensorFragment.newInstance(savedInstanceState.getStringArray(PACKAGE_ITEM_KEYS));
            }
            else
            {
                f = new SensorFragment();
            }
            manager.beginTransaction().add(R.id.fragment_container, f).commit();
            Log.d(TAG, "created frag " + f.getClass());

        }
//        Log.d(TAG, manager.findFragmentById(R.id.fragment_container).toString());
    }

    @Override
    protected void onResume() {
        FragmentManager manager = getSupportFragmentManager();

//        Log.d(TAG, manager.findFragmentById(R.id.fragment_container).toString());

        super.onResume();
    }

    @Override
    public void saveItems(String[] items) {
        this.items = items;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArray(PACKAGE_ITEM_KEYS, items);
    }
}