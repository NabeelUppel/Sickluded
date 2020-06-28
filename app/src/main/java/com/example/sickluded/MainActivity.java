package com.example.sickluded;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    View headerView;
    private static final int PERMISSIONS_REQUEST = 100;
    private Context mContext = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.navigation_view);
        String username = SharedPreferenceClass.getData(mContext, "username");
        String email = SharedPreferenceClass.getData(mContext, "email");


        headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.SavedUsername);
        navUsername.setText(username);
        TextView navEmail = headerView.findViewById(R.id.SavedEmailAddress);
        navEmail.setText(email);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_closed);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                int permission = ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
                switch (item.getItemId()) {

                    case R.id.Map:
                        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            new AlertDialog.Builder(mContext)
                                    .setTitle("Enable GPS")
                                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                        }
                                    }).setNegativeButton("Cancel", null).show();
                        }
                        if (permission == PackageManager.PERMISSION_GRANTED) {
                            Intent mapIntent = new Intent(getApplicationContext(), MapActivity.class);
                            startActivity(mapIntent);
                            drawerLayout.closeDrawers();
                        } else {
                            //If the app doesn’t currently have access to the user’s location, then request access
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSIONS_REQUEST);
                        }
                        break;

                    case R.id.Home:
                        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(homeIntent);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.Tracker:
                        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            new AlertDialog.Builder(mContext)
                                    .setTitle("Enable GPS")
                                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                        }
                                    }).setNegativeButton("Cancel", null).show();
                        }


                        if (permission == PackageManager.PERMISSION_GRANTED) {
                            startNearbyService();
                            drawerLayout.closeDrawers();
                        } else {
                            //If the app doesn’t currently have access to the user’s location, then request access
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    PERMISSIONS_REQUEST);
                        }
                        break;

                    case R.id.Settings:
                        Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(settingsIntent);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.Diagnosis:
                        Intent diagnosisIntent = new Intent(getApplicationContext(), DiagnosisActivity.class);
                        startActivity(diagnosisIntent);
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.Logout:
                        Logout();
                        drawerLayout.closeDrawers();
                        break;

                    case R.id.Symptoms:
                        Intent symptomsIntent = new Intent(getApplicationContext(), SymptomsActivity.class);
                        startActivity(symptomsIntent);
                        drawerLayout.closeDrawers();
                        break;

                }
                return false;
            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        actionBarDrawerToggle.syncState();
    }


    public void Logout() {
        RemoveDeviceToken();
        SharedPreferenceClass.deleteAllData(this);
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void startNearbyService() {
        startService(new Intent(this, NearbyMessageService.class));
    }

    public void RemoveDeviceToken() {
        final String jwt = SharedPreferenceClass.getData(mContext, "jwt");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                ContentValues params = new ContentValues();
                params.put("jwtPost", jwt);
                params.put("DeviceToken", token);
                params.put("function", "Logout");
                String URL = "https://lamp.ms.wits.ac.za/home/s2090704/index.php";

                new PhpHandler().makeHttpRequest(MainActivity.this, URL, params, new RequestHandler() {
                    @Override
                    public void processRequest(String response) throws JSONException {
                        Toast.makeText(mContext, response, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {

    }
}