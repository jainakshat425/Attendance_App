package com.example.android.attendance.adapters;

import android.content.Context;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.android.attendance.R;
import com.example.android.attendance.pojos.Report;
import com.example.android.attendance.utilities.ExtraUtils;


import java.util.List;
import java.util.Locale;

public class ReportAdapter extends
        RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private Context mContext;
    private List<Report> mReports;
    private int mAttendTaken;


    public ReportAdapter(Context context, List<Report> mReports, int mAttendTaken) {
        this.mContext = context;
        this.mReports = mReports;
        this.mAttendTaken = mAttendTaken;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.report_list_item, parent, false);

        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = mReports.get(position);

        String name = report.getStdName();
        holder.nameTv.setText(name);

        String rollNo = report.getStdRollNo();
        holder.rollNoTv.setText(rollNo);

        String totalPresent = report.getTotalPresent();
        holder.totalPresentTv.setText(totalPresent);

        holder.totalClassesTv.setText(String.valueOf(mAttendTaken));

        float attendancePercent = 0;
        if (mAttendTaken > 0) {
            attendancePercent = (Float.valueOf(totalPresent) / (float) mAttendTaken) * 100;
        }
        holder.percentTv.setText(String.format(Locale.US,"%.1f%%", attendancePercent));

        holder.serialNoTv.setText(String.valueOf(++position));

        GradientDrawable attendanceLevelCircle = (GradientDrawable) holder.serialNoTv.getBackground();

        if (attendancePercent < 75) {
            attendanceLevelCircle.setColor(ContextCompat.getColor(mContext, R.color.darkRedColor));
        } else {
            attendanceLevelCircle.setColor(ContextCompat.getColor(mContext, R.color.serialNoBgColor));
        }
        holder.serialNoTv.setBackground(attendanceLevelCircle);

        // dara rows

        String[] colText = report.getSubWiseAttend().toArray(new String[0]);
        for (String text : colText) {
            TextView tv = ExtraUtils.getTextView(mContext, 16);
            tv.setText(text);
            holder.row.addView(tv);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mReports) return 0;
        return mReports.size();
    }

    public void swapList(List<Report> reports, int attendTaken) {
        this.mReports = reports;
        this.mAttendTaken = attendTaken;
        this.notifyDataSetChanged();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {

        final TextView serialNoTv;
        final TextView nameTv;
        final TextView rollNoTv;
        final TextView totalPresentTv;
        final TextView totalClassesTv;
        final TextView percentTv;
        final TableLayout subWiseAttendTable;
        final TableRow row;

        public ReportViewHolder(View view) {
            super(view);

            serialNoTv = view.findViewById(R.id.tv_serial_no_rep);
            nameTv = view.findViewById(R.id.tv_name_rep);
            rollNoTv = view.findViewById(R.id.tv_roll_no_rep);
            totalPresentTv = view.findViewById(R.id.tv_total_present_rep);
            totalClassesTv = view.findViewById(R.id.tv_total_classes_rep);
            percentTv = view.findViewById(R.id.tv_attendance_percent_rep);
            subWiseAttendTable = view.findViewById(R.id.sub_wise_attend_table);

            row = new TableRow(mContext);
            row.setBackgroundColor(Color.parseColor("#c0c0c0"));
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            subWiseAttendTable.addView(row);

        }
    }
}
