package com.example.android.attendance.adapters;

import android.content.Context;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.android.attendance.R;
import com.example.android.attendance.StudentReport;


import java.util.ArrayList;
import java.util.List;

public class ShowAttendanceAdapter extends
        RecyclerView.Adapter<ShowAttendanceAdapter.AttendanceViewHolder> {

    private Context mContext;
    private List<StudentReport> mStdReportList;
    private ArrayList<String> mSubNameList;
    private int mTotalClasses;


    public ShowAttendanceAdapter(Context context, List<StudentReport> stdReportList,
                                 ArrayList<String> subNameList, int totalClasses) {
        this.mContext = context;
        this.mStdReportList = stdReportList;
        this.mSubNameList = subNameList;
        this.mTotalClasses = totalClasses;
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
        StudentReport currentStdReport = mStdReportList.get(position);

        String name = currentStdReport.getmName();
        holder.nameTv.setText(name);

        String rollNo = currentStdReport.getmRollNo();
        holder.rollNoTv.setText(rollNo);

        int totalPresent = currentStdReport.getmTotalPresent();
        holder.totalPresentTv.setText(String.valueOf(totalPresent));

        holder.totalClassesTv.setText(String.valueOf(mTotalClasses));

        float attendancePercent = ((float) totalPresent / (float) mTotalClasses) * 100;
        holder.percentTv.setText(String.format("%.1f%%", attendancePercent));

        holder.serialNoTv.setText(String.valueOf(++position));

        GradientDrawable attendanceLevelCircle = (GradientDrawable) holder.serialNoTv.getBackground();

        if (attendancePercent < 75) {
            attendanceLevelCircle.setColor(ContextCompat.getColor(mContext, R.color.darkRedColor));
        } else {
            attendanceLevelCircle.setColor(ContextCompat.getColor(mContext, R.color.serialNoBgColor));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            holder.serialNoTv.setBackground(attendanceLevelCircle);
        }

        // dara rows

        Integer[] colText = currentStdReport.getmSubAttendance().toArray(new Integer[0]);
        for (int text : colText) {
            TextView tv = new TextView(mContext);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(16);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextColor(mContext.getColor(android.R.color.black));
            }
            tv.setPadding(4, 4, 4, 4);
            tv.setText(String.valueOf(text));
            holder.row.addView(tv);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mStdReportList) return 0;
        return mStdReportList.size();
    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder {

        final TextView serialNoTv;
        final TextView nameTv;
        final TextView rollNoTv;
        final TextView totalPresentTv;
        final TextView totalClassesTv;
        final TextView percentTv;
        final TableLayout subWiseAttendTable;
        final TableRow rowHeader;
        final TableRow row;

        public AttendanceViewHolder(View view) {
            super(view);

            serialNoTv = view.findViewById(R.id.tv_serial_no);
            nameTv = view.findViewById(R.id.tv_name);
            rollNoTv = view.findViewById(R.id.tv_roll_no);
            totalPresentTv = view.findViewById(R.id.tv_total_present);
            totalClassesTv = view.findViewById(R.id.tv_total_classes);
            percentTv = view.findViewById(R.id.tv_attendance_percent);
            subWiseAttendTable = view.findViewById(R.id.sub_wise_attend_table);

            rowHeader = new TableRow(mContext);
            rowHeader.setBackgroundColor(Color.parseColor("#c0c0c0"));
            rowHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            String[] headerText = mSubNameList.toArray(new String[0]);
            for (String c : headerText) {
                TextView tv = new TextView(mContext);
                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT));
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(14);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv.setTextColor(mContext.getColor(android.R.color.black));
                }
                tv.setPadding(4, 4, 4, 4);
                tv.setText(c);
                rowHeader.addView(tv);
            }
            subWiseAttendTable.addView(rowHeader);

            row = new TableRow(mContext);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            subWiseAttendTable.addView(row);

        }
    }


}
