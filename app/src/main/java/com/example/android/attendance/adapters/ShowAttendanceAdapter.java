package com.example.android.attendance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.example.android.attendance.R;
import com.example.android.attendance.StudentReport;
import com.example.android.attendance.contracts.StudentContract.StudentEntry;

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

        holder.serialNoTv.setText(String.valueOf(++position));

        String name = currentStdReport.getmName();
        holder.nameTv.setText(name);

        String rollNo = currentStdReport.getmRollNo();
        holder.rollNoTv.setText(rollNo);

        int totalPresent = currentStdReport.getmTotalPresent();
        holder.totalPresentTv.setText(String.valueOf(totalPresent));

        holder.totalClassesTv.setText(String.valueOf(mTotalClasses));

        float attendancePercent = ((float)totalPresent/(float)mTotalClasses) * 100;
        holder.percentTv.setText(String.format("%.1f%%", attendancePercent));

        for (int i = 0; i < currentStdReport.getmSubAttendance().size(); i++) {
            LinearLayout childContainer = new LinearLayout(mContext);

            childContainer.setOrientation(LinearLayout.HORIZONTAL);
            childContainer.setLayoutParams(
                    new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            childContainer.setPadding(8,4,8,4);

            TextView subNameTv = new TextView(mContext);
            subNameTv.setText(mSubNameList.get(i));
            subNameTv.setGravity(Gravity.START);

            TextView subAttendTv = new TextView(mContext);
            subAttendTv.setText(String.valueOf(currentStdReport.getmSubAttendance().get(i)));
            subAttendTv.setGravity(Gravity.END);

            childContainer.addView(subNameTv);
            childContainer.addView(subAttendTv);

            holder.container.addView(childContainer);
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
        final LinearLayout container;

        public AttendanceViewHolder(View view) {
            super(view);

            serialNoTv = view.findViewById(R.id.tv_serial_no);
            nameTv = view.findViewById(R.id.tv_name);
            rollNoTv = view.findViewById(R.id.tv_roll_no);
            totalPresentTv = view.findViewById(R.id.tv_total_present);
            totalClassesTv = view.findViewById(R.id.tv_total_classes);
            percentTv = view.findViewById(R.id.tv_attendance_percent);
            container = view.findViewById(R.id.sub_wise_attend_container);

        }
    }


}
