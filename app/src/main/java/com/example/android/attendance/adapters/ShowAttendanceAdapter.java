package com.example.android.attendance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.attendance.R;

public class ShowAttendanceAdapter extends RecyclerView.Adapter<ShowAttendanceAdapter.AttendanceViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public ShowAttendanceAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @NonNull
    @Override
    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.show_attendance_list_item, parent, false);

        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder {


        public AttendanceViewHolder(View itemView) {
            super(itemView);
        }
    }
}
