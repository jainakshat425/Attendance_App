package com.example.android.attendance;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.attendance.adapters.TakeAttendanceAdapter;
import com.example.android.attendance.contracts.AttendanceContract.AttendanceEntry;
import com.example.android.attendance.contracts.AttendanceRecordContract.AttendanceRecordEntry;
import com.example.android.attendance.contracts.CollegeContract.CollegeEntry;
import com.example.android.attendance.contracts.StudentContract.StudentEntry;
import com.example.android.attendance.data.DbHelperMethods;
import com.example.android.attendance.utilities.ExtraUtils;

import java.util.ArrayList;


public class TakeAttendanceActivity extends AppCompatActivity {

    private static final int PRESENT = 1;
    private static final int ABSENT = 0;

    private TextView collegeTv;
    private TextView semesterTv;
    private TextView branchTv;
    private TextView sectionTv;
    private TextView subjectTv;
    private TextView dateTv;
    private TextView lectureTv;
    private TextView dayTv;

    private ListView stdListView;
    private TakeAttendanceAdapter takeAttendanceAdapter;
    private Cursor currentTableCursor = null;

    private Bundle bundle;
    private String attendRecId;

    private boolean isUpdateMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        /**
         * initialize all text views
         */
        initializeAllViews();
        /**
         * get data from intent i.e. the activity which called this..
         */
        bundle = getIntent().getExtras();

        String collegeId = bundle.getString(ExtraUtils.EXTRA_COLLEGE_ID);
        String date = bundle.getString(ExtraUtils.EXTRA_DATE);
        String day = bundle.getString(ExtraUtils.EXTRA_DAY);
        String semester = bundle.getString(ExtraUtils.EXTRA_SEMESTER);
        String branch = bundle.getString(ExtraUtils.EXTRA_BRANCH);
        String section = bundle.getString(ExtraUtils.EXTRA_SECTION);
        String subject = bundle.getString(ExtraUtils.EXTRA_SUBJECT);
        String lecture = bundle.getString(ExtraUtils.EXTRA_LECTURE);
        String classId = bundle.getString(ExtraUtils.EXTRA_CLASS_ID);
        attendRecId = bundle.getString(ExtraUtils.EXTRA_ATTEND_REC_ID);

        String collegeString = (Integer.parseInt(collegeId) == CollegeEntry.COLLEGE_GCT) ?
                getString(R.string.college_gct) : getString(R.string.college_git);

        /**
         * populate the text views with the data from the intent
         */
        collegeTv.setText(collegeString);
        branchTv.setText(branch);
        sectionTv.setText(section);
        subjectTv.setText(subject);
        dateTv.setText(date);
        dayTv.setText(day + ",");
        semesterTv.setText(ExtraUtils.getSemester(semester));
        lectureTv.setText(ExtraUtils.getLecture(lecture));

        if (attendRecId != null) {
            //changes activity title
            setTitle(getString(R.string.update_attendance_title));
            isUpdateMode = true;
            setupAttendance(attendRecId, classId);
        } else {
            setTitle(R.string.take_attendance_title);
            isUpdateMode = false;
            String lectureId = String.valueOf(DbHelperMethods
                    .getLectureId(this, classId, lecture, day));

            attendRecId = String.valueOf(DbHelperMethods
                    .createAttendanceRecord(this, lectureId, date));
            setupAttendance(attendRecId, classId);
        }

    }

    private void setupAttendance(String attendRecId, String classId) {

        ArrayList<Integer> attendanceStatesList;
        int attendanceState, attendanceIndex = 0;

        if (isUpdateMode) {
            currentTableCursor = DbHelperMethods.getStudentForUpdateAttendance(this, attendRecId);
        } else {
            currentTableCursor = DbHelperMethods.getStudentForNewAttendance(this, classId);

        }

        if (currentTableCursor.getCount() != 0 && currentTableCursor.moveToFirst()) {
            attendanceStatesList = new ArrayList<Integer>();

            currentTableCursor.moveToFirst();
            if (isUpdateMode) {
                //get attendanceState index so as to find out present attendanceStates
                attendanceIndex = currentTableCursor.getColumnIndexOrThrow
                        (AttendanceEntry.ATTENDANCE_STATE);
                for (int i = 0; i < currentTableCursor.getCount(); i++) {
                    // initializes array with existing attendance States
                    attendanceState = currentTableCursor.getInt(attendanceIndex);
                    attendanceStatesList.add(i, attendanceState);
                    currentTableCursor.moveToNext();
                }
            } else {
                // initializes all items value with 0
                for (int i = 0; i < currentTableCursor.getCount(); i++) {
                    attendanceStatesList.add(i, 0);
                    currentTableCursor.moveToNext();
                }
            }
            takeAttendanceAdapter = new TakeAttendanceAdapter(this, currentTableCursor,
                    attendanceStatesList);

            stdListView.setAdapter(takeAttendanceAdapter);
        } else {
            Toast.makeText(this,"Students Not Exist!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_take_attendance_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_attendance:
                saveAttendance();
                updateAttendanceRecord();
                ExtraUtils.updateWidget(this);

                Intent intent = new Intent();
                intent.putExtras(bundle);

                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case android.R.id.home:
                if (isUpdateMode) finish();
                else undoAttendanceAndFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeAllViews() {
        collegeTv = (TextView) findViewById(R.id.college_text_view);
        semesterTv = (TextView) findViewById(R.id.semester_text_view);
        branchTv = (TextView) findViewById(R.id.branch_text_view);
        sectionTv = (TextView) findViewById(R.id.section_text_view);
        subjectTv = (TextView) findViewById(R.id.subject_text_view);
        dateTv = (TextView) findViewById(R.id.date_text_view);
        lectureTv = (TextView) findViewById(R.id.lecture_text_view);
        dayTv = (TextView) findViewById(R.id.day_text_view);

        stdListView = findViewById(R.id.students_list_view);
    }

    private void saveAttendance() {
        ContentValues newValues;
        int attendanceState;
        SQLiteDatabase db = DbHelperMethods.getDbReadWrite(this);

        if (isUpdateMode) {
            if (currentTableCursor.moveToFirst()) {
                currentTableCursor.moveToFirst();
                int idIndex = currentTableCursor.getColumnIndex(AttendanceEntry.ID);

                String currentId;
                for (int i = 0; i < currentTableCursor.getCount(); i++) {

                    currentId = String.valueOf(currentTableCursor.getInt(idIndex));
                    attendanceState = TakeAttendanceAdapter.getAttendanceState(i);

                    newValues = new ContentValues();
                    newValues.put(AttendanceEntry.ATTENDANCE_STATE, attendanceState);

                    db.update(AttendanceEntry.TABLE_NAME,
                            newValues, AttendanceEntry.ID + "=?", new String[]{currentId});
                    currentTableCursor.moveToNext();
                }
            }
        } else {
            if (currentTableCursor.moveToFirst()) {
                currentTableCursor.moveToFirst();
                int idIndex = currentTableCursor.getColumnIndex(StudentEntry.ID);

                String studentId;
                for (int i = 0; i < currentTableCursor.getCount(); i++) {

                    studentId = String.valueOf(currentTableCursor.getInt(idIndex));
                    attendanceState = TakeAttendanceAdapter.getAttendanceState(i);

                    newValues = new ContentValues();
                    newValues.put(AttendanceEntry.STUDENT_ID, studentId);
                    newValues.put(AttendanceEntry.ATTENDANCE_STATE, attendanceState);
                    newValues.put(AttendanceEntry.ATTENDANCE_RECORD_ID, attendRecId);

                    db.insert(AttendanceEntry.TABLE_NAME, null, newValues);
                    currentTableCursor.moveToNext();
                }
            }
        }
    }

    private void updateAttendanceRecord() {

        ContentValues record = new ContentValues();

        record.put(AttendanceRecordEntry.TOTAL_STUDENTS_COL, getTotalStudentCount());
        record.put(AttendanceRecordEntry.STUDENTS_PRESENT_COL, studentsPresent());

        String where = AttendanceRecordEntry.ID + "=?";
        String[] whereArgs = new String[]{attendRecId};

        int rowUpdated = DbHelperMethods.getDbReadWrite(this)
                .update(AttendanceRecordEntry.TABLE_NAME, record, where, whereArgs);

        Toast.makeText(this, "Rows Updated: " + rowUpdated, Toast.LENGTH_SHORT).show();
    }

    private int getTotalStudentCount() {
        return currentTableCursor.getCount();
    }

    private int studentsPresent() {
        String selection = AttendanceEntry.ATTENDANCE_RECORD_ID + "=?" + " and "
                + AttendanceEntry.ATTENDANCE_STATE + "=?";
        String[] selectionArgs = {attendRecId, String.valueOf(PRESENT)};
        Cursor studentsPresent = DbHelperMethods.getDbReadOnly(this)
                .query(AttendanceEntry.TABLE_NAME, null, selection, selectionArgs,
                        null, null, null);
        return studentsPresent.getCount();
    }

    private void showAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Attendance will be lost!")
                .setMessage("Do you want to exit?")
                .setPositiveButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setNegativeButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(Activity.RESULT_CANCELED);
                                if (isUpdateMode)
                                    finish();
                                else
                                    undoAttendanceAndFinish();
                            }
                        }).create();
        dialog.show();
    }

    private void undoAttendanceAndFinish() {
        int rowDeleted = DbHelperMethods.getDbReadWrite(this)
                .delete(AttendanceRecordEntry.TABLE_NAME, AttendanceRecordEntry.ID + "=?",
                        new String[]{attendRecId});
        if (rowDeleted > 0) {
            Toast.makeText(this, "Attendance not saved!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        showAlertDialog();
    }

}
