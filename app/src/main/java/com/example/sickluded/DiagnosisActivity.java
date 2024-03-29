package com.example.sickluded;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class DiagnosisActivity extends MainActivity {
    private NumberPicker np;
    private String[] npValues = new String[]{"I have been diagnosed", "I am healthy"};
    private int valuePicker;
    private String valuePicked;
    private Button btnConfirm, btnRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_diagnosis, contentFrameLayout);
        np = findViewById(R.id.numberPicker);
        InitialisePicker();
        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertBoxBuilder();

            }
        });

        btnRequest = findViewById(R.id.Request);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestDiagnosis();
            }
        });

    }


    public void InitialisePicker() {
        np.setMinValue(0);
        np.setMaxValue(1);
        np.setDisplayedValues(npValues);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                valuePicker = np.getValue();
                valuePicked = npValues[valuePicker];
                if (valuePicked.equals("I am healthy")) {
                    btnConfirm.setVisibility(View.INVISIBLE);
                } else {
                    btnConfirm.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public void alertBoxBuilder() {
        new AlertDialog.Builder(DiagnosisActivity.this)
                .setTitle("Confirm Diagnosis")
                .setMessage("Please ensure that you have been tested for such disease.")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        datePicker();
                    }
                }).setNegativeButton("Cancel", null).show();
    }


    public void datePicker() {
        final Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog StartTime = new DatePickerDialog(DiagnosisActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                RecordDiagnosis(newDate.getTime());
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        StartTime.getDatePicker().setMaxDate(System.currentTimeMillis());

        StartTime.show();
    }

    public void RecordDiagnosis(Date d) {
        String jwt = SharedPreferenceClass.getData(getApplicationContext(), "jwt");
        ContentValues params = new ContentValues();
        params.put("function", "Diagnose");
        params.put("jwtPost", jwt);
        params.put("time", String.valueOf(d));
        System.out.println(jwt);
        System.out.println(String.valueOf(d));
        String URL = "https://lamp.ms.wits.ac.za/home/s2090704/indexDiagnosis.php";
        new PhpHandler().makeHttpRequest(DiagnosisActivity.this, URL, params, new RequestHandler() {
            @Override
            public void processRequest(String response) throws JSONException {
            Toast.makeText(DiagnosisActivity.this, response.trim(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void RequestDiagnosis() {
        String jwt = SharedPreferenceClass.getData(getApplicationContext(), "jwt");
        String URL = "https://lamp.ms.wits.ac.za/home/s2090704/indexDiagnosis.php";
        ContentValues params = new ContentValues();
        params.put("function", "Request");
        params.put("jwtPost", jwt);
        new PhpHandler().makeHttpRequest(DiagnosisActivity.this, URL, params, new RequestHandler() {
            @Override
            public void processRequest(String response) throws JSONException {
                System.out.println(response);
                JSONObject j = new JSONObject(response);
                String message = new PhpHandler().getMessage(j);
                new AlertDialog.Builder(DiagnosisActivity.this)
                        .setTitle("Diagnosis")
                        .setMessage(message)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Cancel", null).show();
            }
        });
    }

}
