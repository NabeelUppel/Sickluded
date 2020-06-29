package com.example.sickluded;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class SymptomsActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_symptoms, contentFrameLayout);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isRunning) {
            navigationView.getMenu().findItem(R.id.Tracker).setChecked(true);
            navigationView.getMenu().findItem(R.id.Symptoms).setChecked(true);
        } else {
            navigationView.getMenu().findItem(R.id.Symptoms).setChecked(true);
        }

    }




}
