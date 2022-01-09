package com.example.motionsensorproject;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

public class InfoItem implements Comparable<InfoItem> {
    private Drawable icon;
    private String name;
    private String packageName;

    public InfoItem(
            String name,
            String packageName,
            Drawable icon)
    {
        this.name = name;
        this.packageName = packageName;
        this.icon = icon;
//        Log.d("Test", icon + "");
    }

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public int compareTo(InfoItem other) {
        return name.compareTo(other.getName());
    }
}
