package com.example.sickluded;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends MainActivity {
    TextView tvCases, tvRecovered, tvCritical, tvActive, tvTotalDeaths;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_home, contentFrameLayout);


        if (!SharedPreferenceClass.getData(this, "jwt").isEmpty()) {
            getStats();
        } else {
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isRunning) {
            navigationView.getMenu().findItem(R.id.Tracker).setChecked(true);
            navigationView.getMenu().findItem(R.id.Home).setChecked(true);
        } else {
            navigationView.getMenu().findItem(R.id.Home).setChecked(true);
        }
    }

    private void getStats() {
        String URL = "https://disease.sh/v2/countries/South%20Africa?strict=true&allowNull=false";
        new PhpHandler().StatsRequest(this, URL, new RequestHandler() {
            @Override
            public void processRequest(String response) throws JSONException {
                tvCases = findViewById(R.id.tvCases);
                tvRecovered = findViewById(R.id.tvRecovered);
                tvCritical = findViewById(R.id.tvCritical);
                tvActive = findViewById(R.id.tvActive);
                tvTotalDeaths = findViewById(R.id.tvTotalDeaths);
                JSONObject jsonObject = new JSONObject(response);
                tvCases.setText(
                        jsonObject.getString("cases"));
                tvRecovered.setText(
                        jsonObject.getString("recovered"));
                tvCritical.setText(
                        jsonObject.getString("critical"));
                tvActive.setText(
                        jsonObject.getString("active"));
                tvTotalDeaths.setText(
                        jsonObject.getString("deaths"));
            }
        });
    }




}