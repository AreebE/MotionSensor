package com.example.motionsensorproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;


public class ChooseApplicationFragment extends DialogFragment {

    private static final String TAG = "ChooseAppFragment";

    public static final String PACKAGE_KEY_NAME = "PackageKeyName";
    public static final String POSITION_KEY = "PositionKey";
    public static final int CODE = 240629;

    private RadioGroup group;
    private int position;
    private String chosenPackage;

    private class PackageAdapter extends ArrayAdapter<InfoItem>
    {
        public PackageAdapter(@NonNull Context context, @NonNull List<InfoItem> objects) {
            super(context, R.layout.list_package_item, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            InfoItem info = getItem(position);
            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(R.layout.list_package_item, null);
            }
//            Log.d(TAG, info.getIcon() + "");
            ((ImageView) convertView.findViewById(R.id.logo)).setImageDrawable(info.getIcon());
            ((TextView) convertView.findViewById(R.id.name)).setText(info.getName());
            return convertView;
        }
    }

    public ChooseApplicationFragment() {
        // Required empty public constructor
    }

    public static ChooseApplicationFragment newInstance(Integer position) {
        ChooseApplicationFragment fragment = new ChooseApplicationFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION_KEY, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    public View createView(LayoutInflater inflater)
    {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_choose_application, null);
        ListView list = v.findViewById(android.R.id.list);
//        list.setSelected(true);
        ApplicationList appList = ApplicationList.getInstance(getContext().getPackageManager());
        list.setAdapter(new PackageAdapter(getActivity(), appList.getItems()));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chosenPackage = ((TextView) view.findViewById(R.id.name)).getText().toString();
//                adapterView.setActivated(true);
                Log.d(TAG, chosenPackage);
            }
        });

        group = (RadioGroup) v.findViewById(R.id.chooseApplicationFragment_timeSelection);
        for (int i = 0; i < SensorFragment.NUM_INTERVALS; i++)
        {

            RadioButton button = (RadioButton) inflater.inflate(R.layout.radio_button, null);
            Log.d(TAG, i + " = position");
            int position = i + 1;
//            button.margin
            button.setText(SensorFragment.getColor(position).getName());
            button.setId(i);
            group.addView(button, i);
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d(TAG, (i == radioGroup.getCheckedRadioButtonId()) + ", " + i);
            }
        });
        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null)
        {
            position = savedInstanceState.getInt(POSITION_KEY);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(createView(getLayoutInflater()))
                .setPositiveButton("End", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Log.d(TAG, "done");
                        Intent data = new Intent();
                        data.putExtra(PACKAGE_KEY_NAME, chosenPackage);
                        position = group.getCheckedRadioButtonId();
                        Log.d(TAG, "done" + position);
                        data.putExtra(POSITION_KEY, position);
                        dismiss();
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
                    }
                });
        return builder.create();
    }
}