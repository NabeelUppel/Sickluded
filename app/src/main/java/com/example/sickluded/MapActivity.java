package com.example.sickluded;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


public class MapActivity extends MainActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static String TAG = "Places";
    private static final String RVTAG = "RecyclerView";

    //vars
    private ArrayList<String> mTitles = new ArrayList<>();
    private ArrayList<String> mLat = new ArrayList<>();
    private ArrayList<String> mLng = new ArrayList<>();
    private ArrayList<String> mID = new ArrayList<>();
    private ArrayList<String> mCount = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_map, contentFrameLayout);
        navigationView.setCheckedItem(R.id.Map);
        InitLocations();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Places.initialize(getApplicationContext(), getResources().getString(R.string.Places_Key));

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setCountries("ZA", "NA");

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(final Marker marker) {

                        new AlertDialog.Builder(MapActivity.this)
                                .setTitle("Add Location?")
                                .setMessage("Please confirm the location and select the date and time")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        datePicker(marker);

                                    }
                                }).setNegativeButton("Cancel", null).show();
                        return true;
                    }
                });

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName())).setTag(place.getId());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isRunning) {
            navigationView.getMenu().findItem(R.id.Tracker).setChecked(true);
            navigationView.getMenu().findItem(R.id.Map).setChecked(true);
        } else {
            navigationView.getMenu().findItem(R.id.Map).setChecked(true);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void datePicker(final Marker marker) {
        final Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog StartTime = new DatePickerDialog(MapActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                timePicker(marker, year, monthOfYear, dayOfMonth);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        StartTime.getDatePicker().setMaxDate(System.currentTimeMillis());
        StartTime.show();
    }

    public void timePicker(final Marker marker, final int year, final int monthOfYear, final int dayOfMonth) {
        final Calendar nCalender = Calendar.getInstance();
        int hour = nCalender.get(Calendar.HOUR_OF_DAY);
        int minute = nCalender.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(MapActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);


                addLocation(marker, newDate.getTime());

            }
        }, hour, minute, true);
        timePickerDialog.show();
    }

    public void addLocation(Marker marker, final Date t) {
        String jwt = SharedPreferenceClass.getData(getApplicationContext(), "jwt");
        double lat = marker.getPosition().latitude;
        double lng = marker.getPosition().longitude;
        String MarkerTitle = marker.getTitle();
        final String place_ID = (String) marker.getTag();
        String URL = "https://lamp.ms.wits.ac.za/home/s2090704/indexLocation.php";
        ContentValues params = new ContentValues();
        params.put("function", "AddLocation");
        params.put("jwtPost", jwt);
        params.put("Latitude", lat);
        params.put("Longitude", lng);
        params.put("place_title", MarkerTitle);
        params.put("placeID", place_ID);
        new PhpHandler().makeHttpRequest(this, URL, params, new RequestHandler() {
            @Override
            public void processRequest(String response) throws JSONException {
                System.out.println(response);
                if (response.equals("added")) {
                    System.out.println("here");
                    RecordLocation(place_ID, t);
                }

            }
        });
    }


    public void RecordLocation(String place_id, Date t) {
        String jwt = SharedPreferenceClass.getData(getApplicationContext(), "jwt");
        ContentValues params = new ContentValues();
        params.put("function", "RecordLocation");
        params.put("jwtPost", jwt);
        params.put("time", String.valueOf(t));
        params.put("placeID", place_id);
        String URL = "https://lamp.ms.wits.ac.za/home/s2090704/indexLocation.php";
        new PhpHandler().makeHttpRequest(this, URL, params, new RequestHandler() {
            @Override
            public void processRequest(String response) {
                Toast.makeText(MapActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void InitLocations() {
        String jwt = SharedPreferenceClass.getData(getApplicationContext(), "jwt");
        Log.d(RVTAG, "InitTitles: preparing Locations ");
        String URL = "https://lamp.ms.wits.ac.za/home/s2090704/indexLocation.php";
        ContentValues params = new ContentValues();
        params.put("function", "ReturnUserLocations");
        params.put("jwtPost", jwt);

        new PhpHandler().makeHttpRequest(this, URL, params, new RequestHandler() {
            @Override
            public void processRequest(String response) throws JSONException {


                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String Location_ID = object.getString("Location_ID");
                    String Location_Title = object.getString("Location_Title");
                    String Latitude = object.getString("Latitude");
                    String Longitude = object.getString("Longitude");
                    String InfectedCount = object.getString("InfectedCount");
                    mID.add(i, Location_ID);
                    mTitles.add(i, Location_Title);
                    mLat.add(i, Latitude);
                    mLng.add(i, Longitude);
                    mCount.add(i, InfectedCount);
                    InitRecyclerView();
                }


            }
        });

    }

    private void InitRecyclerView() {
        Log.d(RVTAG, "InitRecyclerView: Initialise RecyclerView");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mTitles, mLat, mLng, mID, mCount, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


}
