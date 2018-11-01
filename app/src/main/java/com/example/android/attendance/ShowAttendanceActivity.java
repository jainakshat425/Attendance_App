package com.example.android.attendance;

import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.android.attendance.data.DbHelperMethods;
import com.example.android.attendance.utilities.ExtraUtils;

public class ShowAttendanceActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_show_attendance);

        Bundle classDetails = getIntent().getExtras();
        String collegeId = classDetails.getString(ExtraUtils.EXTRA_COLLEGE_ID);
        String semester = classDetails.getString(ExtraUtils.EXTRA_SEMESTER);
        String branch = classDetails.getString(ExtraUtils.EXTRA_BRANCH);
        String section = classDetails.getString(ExtraUtils.EXTRA_SECTION);

        int branchId = DbHelperMethods.getBranchId(this, branch);
        int classId = DbHelperMethods.getClassId(this, Integer.parseInt(collegeId),
                semester, String.valueOf(branchId),
                section);

        Cursor cursor = DbHelperMethods.getClassAttendanceCursor(this, classId);
    }
}
