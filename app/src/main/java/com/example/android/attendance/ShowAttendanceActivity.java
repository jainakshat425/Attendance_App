package com.example.android.attendance;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.attendance.adapters.ShowAttendanceAdapter;
import com.example.android.attendance.contracts.StudentContract.StudentEntry;
import com.example.android.attendance.contracts.SubjectContract.SubjectEntry;
import com.example.android.attendance.data.DatabaseHelper;
import com.example.android.attendance.data.DbHelperMethods;
import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.utilities.PdfUtils;

import java.util.ArrayList;
import java.util.List;

public class ShowAttendanceActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FloatingActionButton savePdfFab;

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_attendance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        mDbHelper = new DatabaseHelper(this);
        mDb = mDbHelper.openDatabaseForReadWrite();


        savePdfFab = findViewById(R.id.save_pdf_fab);

        final Bundle classDetails = getIntent().getExtras();
        String collegeId = classDetails.getString(ExtraUtils.EXTRA_COLLEGE_ID);
        String semester = classDetails.getString(ExtraUtils.EXTRA_SEMESTER);
        String branch = classDetails.getString(ExtraUtils.EXTRA_BRANCH);
        String section = classDetails.getString(ExtraUtils.EXTRA_SECTION);


        int branchId = DbHelperMethods.getBranchId(mDb, branch);
        int classId = DbHelperMethods.getClassId(mDb, Integer.parseInt(collegeId),
                semester, String.valueOf(branchId),
                section);

        final List<Integer> totalLectForSub = showTotalLecturesForEachSub(classId);

        Cursor cursor = DbHelperMethods.getClassAttendanceCursor(mDb, classId);

        final List<StudentReport> stdReportList = setupStudentReportList(cursor);
        final ArrayList<String> subNameList = new ArrayList<>();

        Cursor subjectCursor = DbHelperMethods.getSubjectCursor(mDb, semester, branchId);
        if (stdReportList != null) {
            if (subjectCursor.getCount() != 0 && subjectCursor.moveToFirst()) {

                int currentSubId;
                String currentSubName;
                int subIdIndex = subjectCursor.getColumnIndex(SubjectEntry._ID);
                int subNameIndex = subjectCursor.getColumnIndex(SubjectEntry.SUB_NAME_COL);

                subjectCursor.moveToFirst();
                Cursor subAttendCursor;
                while (!subjectCursor.isAfterLast()) {

                    currentSubId = subjectCursor.getInt(subIdIndex);
                    currentSubName = subjectCursor.getString(subNameIndex);
                    subNameList.add(currentSubName);

                    subAttendCursor = DbHelperMethods
                            .getCurrentSubAttendCursor(mDb, currentSubId, classId);
                    StudentReport currentStdReport;
                    if (subAttendCursor.getCount() != 0 && subAttendCursor.moveToFirst()) {
                        int subAttendIndex = subAttendCursor.getColumnIndex("sub_attendance");
                        int subAttendance;
                        //update the student reports with subject wise attendance from above cursor
                        subAttendCursor.moveToFirst();
                        for (int j = 0; j < stdReportList.size() && !subAttendCursor.isAfterLast();
                             j++, subAttendCursor.moveToNext()) {
                            subAttendance = subAttendCursor.getInt(subAttendIndex);
                            currentStdReport = stdReportList.get(j);
                            currentStdReport.getmSubAttendance().add(subAttendance);
                        }
                        subAttendCursor.close();
                    } else {
                        for (int j = 0; j < stdReportList.size(); j++) {
                            currentStdReport = stdReportList.get(j);
                            currentStdReport.getmSubAttendance().add(0);
                        }
                    }
                    subjectCursor.moveToNext();
                }
            } else {
                Toast.makeText(this, "No subjects found!", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
            subjectCursor.close();

            final int totalClasses = DbHelperMethods.getTotalClasses(mDb, classId);

            ShowAttendanceAdapter adapter = new ShowAttendanceAdapter(this, stdReportList,
                    subNameList, totalClasses);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false);

            mRecyclerView = findViewById(R.id.rv_show_attendance);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(adapter);

            //setup savePdf button after getting attendance of all the students
            savePdfFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PdfUtils.generatePdf(ShowAttendanceActivity.this, mDb,
                            stdReportList, subNameList,
                            totalClasses, totalLectForSub, classDetails);
                }
            });
        } else {
            Toast.makeText(this, "Students Not Found!", Toast.LENGTH_SHORT).show();
        }

    }

    private List<Integer> showTotalLecturesForEachSub(int classId) {
        TableLayout totalSubLectTable = findViewById(R.id.sub_total_lect_table);
        Cursor cursor = DbHelperMethods.getTotalLecturesForEachSub(mDb, classId);

        TableRow rowHeader = new TableRow(this);
        rowHeader.setBackgroundColor(Color.parseColor("#c0c0c0"));
        rowHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        List<Integer> totalLectForSub = new ArrayList<>();

        if (cursor.getCount() != 0 && cursor.moveToFirst()) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                String subName = cursor.getString(cursor.getColumnIndex(SubjectEntry.SUB_NAME_COL));

                TextView subNameTv = ExtraUtils.getTextView(this, 14);
                subNameTv.setText(subName);
                rowHeader.addView(subNameTv);

                int subTotalLect = cursor.getInt(cursor.getColumnIndex("sub_total_lect"));
                totalLectForSub.add(subTotalLect);

                TextView tv = ExtraUtils.getTextView(this, 16);
                tv.setText(String.valueOf(subTotalLect));
                row.addView(tv);
            }
            totalSubLectTable.addView(rowHeader);
            totalSubLectTable.addView(row);
        }
        cursor.close();
        return totalLectForSub;
    }

    private List<StudentReport> setupStudentReportList(Cursor cursor) {

        List<StudentReport> studentReportList = new ArrayList<>();
        String name;
        String rollNo;
        int totalPresent;

        if (cursor.getCount() != 0 && cursor.moveToFirst()) {

            int nameIndex = cursor.getColumnIndex(StudentEntry.S_NAME_COL);
            int rollNoIndex = cursor.getColumnIndex(StudentEntry.S_ROLL_NO_COL);
            int totalPresentIndex = cursor.getColumnIndex("total_present");

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                name = cursor.getString(nameIndex);
                rollNo = cursor.getString(rollNoIndex);
                totalPresent = cursor.getInt(totalPresentIndex);

                studentReportList.add(new StudentReport(name, rollNo, totalPresent,
                        new ArrayList<Integer>()));
            }
            cursor.close();
            return studentReportList;
        }
        return null;
    }

}
