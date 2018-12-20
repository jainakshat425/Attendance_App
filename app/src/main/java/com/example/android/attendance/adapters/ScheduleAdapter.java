package com.example.android.attendance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.attendance.R;
import com.example.android.attendance.contracts.BranchContract.BranchEntry;
import com.example.android.attendance.contracts.ClassContract.ClassEntry;
import com.example.android.attendance.contracts.CollegeContract.CollegeEntry;
import com.example.android.attendance.contracts.LectureContract.LectureEntry;
import com.example.android.attendance.contracts.SubjectContract.SubjectEntry;
import com.example.android.attendance.utilities.ExtraUtils;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleHolder> {

    private Context mContext;
    private Cursor mCursor;

    public ScheduleAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
        if (mCursor != null)
            cursor.moveToFirst();
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
        mCursor.moveToPosition(position);

        String college = mCursor.getString(mCursor.getColumnIndex(CollegeEntry.NAME));
        String semester = mCursor.getString(mCursor.getColumnIndex(ClassEntry.SEMESTER));
        String branch = mCursor.getString(mCursor.getColumnIndex(BranchEntry.BRANCH_NAME));
        String section = mCursor.getString(mCursor.getColumnIndex(ClassEntry.SECTION));
        String lectNo = mCursor.getString(mCursor.getColumnIndex(LectureEntry.LECTURE_NUMBER));
        String subName = mCursor.getString(mCursor.getColumnIndex(SubjectEntry.SUB_NAME_COL));
        String lectStartTime = mCursor.getString(mCursor.getColumnIndex(LectureEntry.LECTURE_START_TIME));
        String lectEndTime = mCursor.getString(mCursor.getColumnIndex(LectureEntry.LECTURE_END_TIME));

        holder.collegeNameTv.setText(college);
        holder.semesterTv.setText(ExtraUtils.getSemester(semester));
        holder.branchTv.setText(branch);
        holder.sectionTv.setText(section);
        holder.lectureNoTv.setText(ExtraUtils.getLecture(lectNo));
        holder.subjectNameTv.setText(subName);
        holder.lectStartTimeTv.setText(lectStartTime);
        holder.lectEndTimeTv.setText(lectEndTime);

    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (mCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) return mCursor.getCount();
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
}
