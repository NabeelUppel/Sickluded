package com.example.sickluded;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

public class TutorialActivity extends Dialog implements  android.view.View.OnClickListener{
    public Activity c;
    public Dialog d;
    public Button btnNext, btnBack;

    public TutorialActivity( Activity c) {
        super(c);
        this.c = c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.frame_layout); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.map_tutorial, contentFrameLayout);




    }

    @Override
    public void onClick(View view) {

    }
}