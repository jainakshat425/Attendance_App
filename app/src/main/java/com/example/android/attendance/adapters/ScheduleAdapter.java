package com.example.android.attendance.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.attendance.R;
import com.example.android.attendance.TakeAttendanceActivity;
import com.example.android.attendance.pojos.Schedule;
import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.volley.VolleyCallback;
import com.example.android.attendance.volley.VolleyTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleHolder> {

    private Context mContext;
    private List<Schedule> mSchedules;
    private String mDay;

    public ScheduleAdapter(Context context, List<Schedule> mSchedules, String mDay) {
        this.mContext = context;
        this.mSchedules = mSchedules;
        this.mDay = mDay;
    }
    @NonNull
    @Override
    public ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.schedule_list_item, parent, false);
        return new ScheduleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleHolder holder, int position) {

        Schedule sch = mSchedules.get(position);

        String semester = String.valueOf(sch.getSem());
        String branch = sch.getBName();
        String section = sch.getSection();
        String lectNo = String.valueOf(sch.getLectNo());
        String subName = sch.getSubName();
        String lectStartTime = String.valueOf(sch.getLectStartTime());
        String lectEndTime = String.valueOf(sch.getLectEndTime());

        try {
            Date lectStartTimeDisplay = ExtraUtils.timeFormat.parse(lectStartTime);
            lectStartTime = ExtraUtils.timeDisplayFormat.format(lectStartTimeDisplay);
            Date lectEndTimeDisplay = ExtraUtils.timeFormat.parse(lectEndTime);
            lectEndTime = ExtraUtils.timeDisplayFormat.format(lectEndTimeDisplay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.lectNoTv.setText(lectNo);
        holder.semesterTv.setText(ExtraUtils.getSemester(semester));
        holder.branchTv.setText(branch);
        holder.sectionTv.setText(section);
        holder.subjectNameTv.setText(subName);
        holder.lectStartTimeTv.setText(lectStartTime);
        holder.lectEndTimeTv.setText(lectEndTime);

        holder.itemView.setTag(position);
    }

    public class ScheduleHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView lectNoTv;
        final TextView semesterTv;
        final TextView branchTv;
        final TextView sectionTv;
        final TextView subjectNameTv;
        final TextView lectStartTimeTv;
        final TextView lectEndTimeTv;

        public ScheduleHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            lectNoTv =  view.findViewById(R.id.sch_lect_no_tv);
            semesterTv = view.findViewById(R.id.sch_semester_tv);
            branchTv = view.findViewById(R.id.sch_branch_tv);
            sectionTv = view.findViewById(R.id.sch_section_tv);
            subjectNameTv = view.findViewById(R.id.sch_subject_tv);
            lectStartTimeTv = view.findViewById(R.id.sch_lect_start_tv);
            lectEndTimeTv = view.findViewById(R.id.sch_lect_end_tv);
        }

        @Override
        public void onClick(View v) {


            if (mDay != null && mDay.equals(ExtraUtils.getCurrentDay())) {

                Schedule sch = mSchedules.get((int) v.getTag());

                int collegeId = sch.getCollId();
                int classId = sch.getClassId();
                int lectId = sch.getLectId();
                String semester = String.valueOf(sch.getSem());
                String branch = sch.getBName();
                String section = sch.getSection();
                String lectNo = String.valueOf(sch.getLectNo());
                String date = ExtraUtils.getCurrentDate();
                String dateDisplay = ExtraUtils.getCurrentDateDisplay();


                VolleyTask.takeNewAttendance(mContext, date, mDay, semester, branch,
                        section, lectNo, collegeId, lectId,
                        jObj -> {
                            Intent intent = new Intent();
                            intent.setClass(mContext, TakeAttendanceActivity.class);

                            intent.putExtra(ExtraUtils.EXTRA_CLASS_ID, String.valueOf(classId));
                            intent.putExtra(ExtraUtils.EXTRA_DATE, date);
                            intent.putExtra(ExtraUtils.EXTRA_DISPLAY_DATE, dateDisplay);
                            intent.putExtra(ExtraUtils.EXTRA_DAY, mDay);
                            intent.putExtra(ExtraUtils.EXTRA_LECTURE_NO, lectNo);

                            mContext.startActivity(intent);
                        });
            } else {
                Toast.makeText(mContext, "Attendance can only be taken for current day!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void swapList(List<Schedule> schedules, String day) {
        this.mSchedules = schedules;
        this.mDay = day;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mSchedules != null) return mSchedules.size();
        else return 0;
    }

}
