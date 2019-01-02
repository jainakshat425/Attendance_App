package com.example.android.attendance.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.attendance.R;
import com.example.android.attendance.TakeAttendanceActivity;
import com.example.android.attendance.pojos.AttendanceRecord;
import com.example.android.attendance.utilities.ExtraUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.MainViewHolder> {

    private Context mContext;
    private List<AttendanceRecord> mRecords;

    public MainListAdapter(Context context, List<AttendanceRecord> records) {

        mContext = context;
        mRecords = records;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.main_list_item, parent, false);

        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {

        AttendanceRecord record = mRecords.get(position);

        String college = record.getCollName();
        String semester = String.valueOf(record.getSem());
        String branch = record.getBName();
        String section = record.getSection();
        String subject = record.getSubName();
        String dateString = record.getDate();
        String day = record.getDay();
        String lectureNo = String.valueOf(record.getLectNo());
        String stdPresent = String.valueOf(record.getStudentsPresent());
        String totalStudents = String.valueOf(record.getTotalStudents());

        Date date = null;
        try {
            date = ExtraUtils.dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String displayDate = dateString;
        if (date != null)  displayDate = ExtraUtils.dateDisplayFormat.format(date);

        holder.collegeTv.setText(college);
        holder.semesterTv.setText(ExtraUtils.getSemester(semester));
        holder.branchTv.setText(branch);
        holder.sectionTv.setText(section);
        holder.subjectTv.setText(subject);
        holder.dateTv.setText(displayDate);
        holder.dayTv.setText(day.substring(0, 3) + ",");
        holder.lectureTv.setText(ExtraUtils.getLecture(lectureNo));
        holder.studentsPresentTv.setText(stdPresent);
        holder.totalStudentsTv.setText(totalStudents);

        String attendRecId = String.valueOf(record.getId());
        String facUserId = record.getFacUserId();
        String classId = String.valueOf(record.getClassId());
        String collegeId = String.valueOf(record.getCollegeId());


        Bundle intentBundle = new Bundle();
        intentBundle.putString(ExtraUtils.EXTRA_ATTEND_REC_ID, attendRecId);
        intentBundle.putString(ExtraUtils.EXTRA_FAC_USER_ID, facUserId);
        intentBundle.putString(ExtraUtils.EXTRA_DATE, dateString);
        intentBundle.putString(ExtraUtils.EXTRA_DISPLAY_DATE, displayDate);
        intentBundle.putString(ExtraUtils.EXTRA_SEMESTER, semester);
        intentBundle.putString(ExtraUtils.EXTRA_BRANCH, branch);
        intentBundle.putString(ExtraUtils.EXTRA_SECTION, section);
        intentBundle.putString(ExtraUtils.EXTRA_SUBJECT, subject);
        intentBundle.putString(ExtraUtils.EXTRA_COLLEGE, college);
        intentBundle.putString(ExtraUtils.EXTRA_LECTURE_NO, lectureNo);
        intentBundle.putString(ExtraUtils.EXTRA_DAY, day);
        intentBundle.putString(ExtraUtils.EXTRA_CLASS_ID, String.valueOf(classId));
        intentBundle.putString(ExtraUtils.EXTRA_COLLEGE_ID, String.valueOf(collegeId));

        holder.itemView.setTag(intentBundle);
    }

    @Override
    public int getItemCount() {
        if (mRecords == null)
            return 0;
        else
            return mRecords.size();
    }

    public void swapList(List<AttendanceRecord> records) {
        mRecords = records;
        this.notifyDataSetChanged();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView collegeTv;
        private TextView semesterTv;
        private TextView branchTv;
        private TextView sectionTv;
        private TextView subjectTv;
        private TextView dateTv;
        private TextView dayTv;
        private TextView lectureTv;
        private TextView studentsPresentTv;
        private TextView totalStudentsTv;

        public MainViewHolder(View view) {
            super(view);

            view.setOnClickListener(this);
            collegeTv = view.findViewById(R.id.main_college_tv);
            semesterTv = view.findViewById(R.id.main_semester_tv);
            branchTv = view.findViewById(R.id.main_branch_tv);
            sectionTv = view.findViewById(R.id.main_section_tv);
            subjectTv = view.findViewById(R.id.main_subject_tv);
            dateTv = view.findViewById(R.id.main_date_tv);
            dayTv = view.findViewById(R.id.main_day_tv);
            lectureTv = view.findViewById(R.id.main_lecture_tv);
            studentsPresentTv = view.findViewById(R.id.main_students_present_tv);
            totalStudentsTv = view.findViewById(R.id.main_total_students_tv);
        }

        @Override
        public void onClick(View v) {
            Intent takeAttendanceIntent = new Intent();
            takeAttendanceIntent.setClass(mContext, TakeAttendanceActivity.class);
            takeAttendanceIntent.putExtras((Bundle) v.getTag());
            mContext.startActivity(takeAttendanceIntent);
        }
    }
}
