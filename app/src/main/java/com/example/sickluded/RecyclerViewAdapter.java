package com.example.sickluded;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> mTitles;
    private ArrayList<String> mLat;
    private ArrayList<String> mLng;
    private ArrayList<String> mID;
    private ArrayList<String> mCount;
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> mTitles, ArrayList<String> mLat, ArrayList<String> mLng, ArrayList<String> mID, ArrayList<String> mCount, Context mContext) {
        this.mTitles = mTitles;
        this.mLat = mLat;
        this.mLng = mLng;
        this.mID = mID;
        this.mContext = mContext;
        this.mCount = mCount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "called");
        holder.txtTitle.setText(mTitles.get(position));
        holder.txtCount.setText("Cases: " + mCount.get(position));


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick:");
                datePicker(mID.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return mID.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtCount;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.Title);
            txtCount = itemView.findViewById(R.id.Count);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }


    public void datePicker(final String id) {
        final Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog StartTime = new DatePickerDialog(mContext,R.style.CustomDatePickerTheme, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                timePicker(id, year, monthOfYear, dayOfMonth);
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        StartTime.getDatePicker().setMaxDate(System.currentTimeMillis());
        StartTime.show();
    }

    public void timePicker(final String ID, final int year, final int monthOfYear, final int dayOfMonth) {
        final Calendar nCalender = Calendar.getInstance();
        int hour = nCalender.get(Calendar.HOUR_OF_DAY);
        int minute = nCalender.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth, hourOfDay, minute);


                RecordLocation(mContext, ID, newDate.getTime());

            }
        }, hour, minute, true);
        timePickerDialog.show();
    }


    public void RecordLocation(final Context c, String place_id, Date t) {
        String jwt = SharedPreferenceClass.getData(c, "jwt");
        ContentValues params = new ContentValues();
        params.put("function", "RecordLocation");
        params.put("jwtPost", jwt);
        params.put("time", String.valueOf(t));
        params.put("placeID", place_id);
        String URL = "https://lamp.ms.wits.ac.za/home/s2090704/indexLocation.php";
        new PhpHandler().makeHttpRequest((Activity) c, URL, params, new RequestHandler() {
            @Override
            public void processRequest(String response) {
                Toast.makeText(c, response, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
