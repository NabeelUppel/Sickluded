package com.example.sickluded;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.HomeViewHolder> {

    private static final String TAG = "HomeRecyclerViewAdapter";
    private ArrayList<String> mTitles;
    private ArrayList<String> mCount;
    private Context mContext;

    public HomeRecyclerViewAdapter(ArrayList<String> mTitles, ArrayList<String> mCount, Context mContext) {
        this.mTitles = mTitles;
        this.mCount = mCount;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_layout_list_item, parent, false);
        HomeRecyclerViewAdapter.HomeViewHolder viewHolder = new HomeRecyclerViewAdapter.HomeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Log.d(TAG, "called");
        holder.txtTitle.setText(mTitles.get(position));
        holder.txtCount.setText("Cases: " + mCount.get(position));
    }

    @Override
    public int getItemCount() {
        return mTitles.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtCount;
        RelativeLayout parentLayout;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.Title);
            txtCount = itemView.findViewById(R.id.Count);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
