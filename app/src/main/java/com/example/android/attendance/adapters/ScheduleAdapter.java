package com.example.android.attendance.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.attendance.R;
import com.example.android.attendance.pojos.Schedule;
import com.example.android.attendance.utilities.ExtraUtils;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleHolder> {

    private Context mContext;
    private List<Schedule> mSchedules;

    public ScheduleAdapter(Context context, List<Schedule> mSchedules) {
        this.mContext = context;
        this.mSchedules = mSchedules;
    }
    @NonNull
    @Override
    public ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.schedule_list_item,
                parent, false);
        return new ScheduleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleHolder holder, int position) {

        Schedule sch = mSchedules.get(position);

        String college = sch.getCollName();
        String semester = String.valueOf(sch.getSem());
        String branch = sch.getBName();
        String section = sch.getSection();
        String lectNo = String.valueOf(sch.getLectNo());
        String subName = sch.getSubName();
        String lectStartTime = String.valueOf(sch.getLectStartTime());
        String lectEndTime = String.valueOf(sch.getLectEndTime());

        holder.collegeNameTv.setText(college);
        holder.semesterTv.setText(ExtraUtils.getSemester(semester));
        holder.branchTv.setText(branch);
        holder.sectionTv.setText(section);
        holder.lectureNoTv.setText(ExtraUtils.getLecture(lectNo));
        holder.subjectNameTv.setText(subName);
        holder.lectStartTimeTv.setText(lectStartTime);
        holder.lectEndTimeTv.setText(lectEndTime);

    }


    @Override
    public int getItemCount() {
        if (mSchedules != null) return mSchedules.size();
        else return 0;
    }

    public class ScheduleHolder extends RecyclerView.ViewHolder {

        final TextView collegeNameTv;
        final TextView semesterTv;
        final TextView branchTv;
        final TextView sectionTv;
        final TextView lectureNoTv;
        final TextView subjectNameTv;
        final TextView lectStartTimeTv;
        final TextView lectEndTimeTv;

        public ScheduleHolder(View view) {
            super(view);
            collegeNameTv =  view.findViewById(R.id.sch_college_tv);
            semesterTv = view.findViewById(R.id.sch_semester_tv);
            branchTv = view.findViewById(R.id.sch_branch_tv);
            sectionTv = view.findViewById(R.id.sch_section_tv);
            lectureNoTv = view.findViewById(R.id.sch_lecture_tv);
            subjectNameTv = view.findViewById(R.id.sch_subject_tv);
            lectStartTimeTv = view.findViewById(R.id.sch_lect_start_tv);
            lectEndTimeTv = view.findViewById(R.id.sch_lect_end_tv);
        }
    }

    public void swapList(List<Schedule> schedules) {
        mSchedules = schedules;
        this.notifyDataSetChanged();
    }
}
