package com.example.sickluded;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.os.Bundle;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocationStatsActivity extends MainActivity {
    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mCount =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_location_stats, contentFrameLayout);
        InitLocations();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (isRunning) {
            navigationView.getMenu().findItem(R.id.Tracker).setChecked(true);
            navigationView.getMenu().findItem(R.id.LocationStats).setChecked(true);
        } else {
            navigationView.getMenu().findItem(R.id.LocationStats).setChecked(true);
        }
    }
    private void InitLocations() {
        String jwt = SharedPreferenceClass.getData(getApplicationContext(), "jwt");
        String URL = "https://lamp.ms.wits.ac.za/home/s2090704/indexLocation.php";
        ContentValues params = new ContentValues();
        params.put("function", "ReturnAllLocations");
        params.put("jwtPost", jwt);

        new PhpHandler().makeHttpRequest(this, URL, params, new RequestHandler() {
            @Override
            public void processRequest(String response) throws JSONException {


                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String Location_Title = object.getString("Location_Title");
                    String InfectedCount = object.getString("InfectedCount");
                    mTitles.add(i, Location_Title);
                    mCount.add(i, InfectedCount);
                    InitRecyclerView();
                }


            }
        });

    }

    private void InitRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        HomeRecyclerViewAdapter adapter = new HomeRecyclerViewAdapter(mTitles, mCount, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
