package com.example.sickluded;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
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
    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mCount =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_home, contentFrameLayout);
        InitLocations();
        if (!SharedPreferenceClass.getData(this, "jwt").isEmpty()) {
            getStats();
        } else {
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(i);
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