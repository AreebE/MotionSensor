package com.example.motionsensorproject;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ApplicationList {
    private static final String TAG = "ApplicationList";
    private static ApplicationList list;

    private HashMap<String, InfoItem> apps;
    private ArrayList<InfoItem> items;

    private ApplicationList(PackageManager pm)
    {
        HashSet<String> names = new HashSet<>();
        apps = new HashMap<>();
        items = new ArrayList<>();
//        pm.get
        List<PackageInfo> info = pm.getInstalledPackages(0);
        for (PackageInfo pi: info)
        {
//            InfoItem infoItem = new
//            pi.applicationInfo.logo;
//            Log.d(TAG, "load item; " +  pi.applicationInfo.loadLabel(pm).toString() + "=" +pi.packageName);
            String appName = pi.applicationInfo.loadLabel(pm).toString();
//                             pi.applicationInfo.loadLabel(pm).toString()
            InfoItem infoItem = new InfoItem(appName, pi.packageName, pi.applicationInfo.loadIcon(pm));
            apps.put(appName, infoItem);
//            logos.put(appName, pi.applicationInfo.loadIcon(pm));
//            Log.d(TAG, pi.applicationInfo.loadIcon(pm) + ", " + pi.applicationInfo.loadIcon(pm) + ", ");
            if (appName.contains("com."))
            {
                continue;
            }
            if (!names.contains(appName)) {
//                Log.d(TAG, "adde/**/d");
                insertItem(infoItem);
            }
            names.add(appName);
//            Log.d(TAG, names.toString());

//                    insertName(appName);
//            Log.d(TAG, "All app items: " + items.toString());

        }
//        Collections.sort(items);
//        Log.d(TAG, "All app items: " + items.toString());
    }

    public String getPackageName(String appName)
    {
        return apps.get(appName).getPackageName();
    }

    public List<InfoItem> getItems()
    {
        return items;
    }

    private void insertItem(InfoItem item)
    {
//        Log.d(TAG, newName);
        int i;
        if (items.size() == 0)
        {
            items.add(0, item);
        }
        else if (item.compareTo(items.get(0)) < 0)
        {
            items.add(0, item);
            return;
        }
        for (i = 1; i < items.size(); i++)
        {
            int result = item.compareTo(items.get(i));
            if (result < 0)
            {
//                Log.d(TAG, i + " ");
                items.add(i, item);
                return;
            }
            else if (result == 0)
            {
                return;
            }
        }
        items.add(i, item);
    }

    public InfoItem getItem(String name)
    {
        return apps.get(name);
    }

    public static ApplicationList getInstance(PackageManager pm)
    {
        if (list == null)
        {
            list = new ApplicationList(pm);
        }
        return list;
    }
}
