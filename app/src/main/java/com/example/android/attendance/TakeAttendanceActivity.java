package com.example.android.attendance;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.example.android.attendance.data.DatabaseHelper;
import com.example.android.attendance.utilities.ExtraUtils;

import java.util.ArrayList;


public class TakeAttendanceActivity extends AppCompatActivity {

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
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Cursor currentTableCursor = null;

    private Bundle bundle;

    private boolean isUpdateMode = false;
    private boolean studentsExist = false;


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
        String facUserId = bundle.getString(ExtraUtils.EXTRA_FAC_USER_ID);
        String lecture = bundle.getString(ExtraUtils.EXTRA_LECTURE);
        String classId = bundle.getString(ExtraUtils.EXTRA_CLASS_ID);
        String branchId = bundle.getString(ExtraUtils.EXTRA_BRANCH_ID);

        //values which only exist when we activity is launched for updating attendance
        String existingTable = bundle.getString(ExtraUtils.EXTRA_TABLE_NAME);
        String existingAttendanceColumn = bundle.getString(ExtraUtils.EXTRA_ATTENDANCE_COL_NAME);


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

        /**
         * open the database for getting the table of Students for Attendance
         */
        databaseHelper = new DatabaseHelper(this);

        try {
            db = databaseHelper.openDatabaseForReadWrite();
        } catch (SQLException sqle) {
            Log.e("TakeAttendanceActivity", "error opening database ", sqle);
            throw sqle;
        }

        /**
         * this condition implies that this is update mode of attendance
         */
        if (existingTable != null && existingAttendanceColumn != null) {
            //changes activity title
            setTitle(getString(R.string.update_attendance_title));
            isUpdateMode = true;
            studentsExist = true;
            setupAttendance(existingTable, existingAttendanceColumn);

        } else {
            isUpdateMode = false;
            //build name for table
            String ATTENDANCE_TABLE = "class_" + classId;

            /**
             *  check table exists or not
             *  if exist alter table
             *  else create table and then alter table
             */

            if (tableAlreadyExists(ATTENDANCE_TABLE)) {
                /**
                 * ALTER Table
                 */
                studentsExist = true;
                String attendanceColumn = addNewAttendanceColumn(ATTENDANCE_TABLE, subject,
                        date, lecture);
                setupAttendance(ATTENDANCE_TABLE, attendanceColumn);
            } else if (createTableWithStudents(ATTENDANCE_TABLE, classId)) {
                /**
                 * create table with students data and then Alter table
                 */
                String attendanceColumn = addNewAttendanceColumn(ATTENDANCE_TABLE, subject,
                        date, lecture);
                setupAttendance(ATTENDANCE_TABLE, attendanceColumn);

            } else {
                studentsExist = false;
                Toast.makeText(this, "Students not exist.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupAttendance(String attendanceTableName, String attendanceColumn) {

        ArrayList<Integer> attendanceStatesList, totalAttendanceList;
        int attendanceState, attendanceIndex = 0;


        String orderBy = AttendanceEntry.ROLL_NO_COL + " ASC";

        String[] projection = new String[]{AttendanceEntry._ID,
                AttendanceEntry.NAME_COL, AttendanceEntry.ROLL_NO_COL,
                AttendanceEntry.TOTAL_ATTENDANCE_COL, attendanceColumn};

        bundle.putString(ExtraUtils.EXTRA_ATTENDANCE_COL_NAME, attendanceColumn);

        currentTableCursor = db.query(attendanceTableName, projection, null,
                null, null, null, orderBy);

        if (currentTableCursor.moveToFirst()) {

            if (isUpdateMode) {
                //get attendance state of all the students
                attendanceIndex = currentTableCursor.getColumnIndexOrThrow
                        (attendanceColumn);

            }
            attendanceStatesList = new ArrayList<Integer>();

            //get total Attendance of all the students
            int totalAttendanceIndex = currentTableCursor.getColumnIndexOrThrow(
                    AttendanceEntry.TOTAL_ATTENDANCE_COL);
            int currentTotalAttendance;
            totalAttendanceList = new ArrayList<Integer>();

            currentTableCursor.moveToFirst();
            for (int i = 0; i < currentTableCursor.getCount(); i++) {
                if (isUpdateMode) {
                    // initializes array with existing attendance States
                    attendanceState = currentTableCursor.getInt(attendanceIndex);
                    attendanceStatesList.add(i, attendanceState);
                } else {
                    // initializes all items value with 0
                    attendanceStatesList.add(i, 0);
                }

                //initializes array with existing total attendance count
                currentTotalAttendance = currentTableCursor.getInt(totalAttendanceIndex);
                totalAttendanceList.add(i, currentTotalAttendance);

                currentTableCursor.moveToNext();
            }
            takeAttendanceAdapter = new TakeAttendanceAdapter(this, currentTableCursor,
                    attendanceTableName, attendanceColumn, attendanceStatesList,
                    totalAttendanceList);

            stdListView.setAdapter(takeAttendanceAdapter);
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
            case R.id.done_take_attendance:

                if (studentsExist) {
                    updateAttendanceChanges();
                    updateAttendanceRecord();
                }

                Intent intent = new Intent();
                intent.putExtras(bundle);

                setResult(Activity.RESULT_OK, intent);
                finish();

            case android.R.id.home:
                if (studentsExist) {
                    updateAttendanceRecord();
                }
                setResult(Activity.RESULT_CANCELED);
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (studentsExist) {
            updateAttendanceRecord();
        }
        setResult(Activity.RESULT_CANCELED);
        finish();
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

    /**
     * create table of students with given classId
     *
     * @param newTableName name of the new table
     * @param classId      class id representing a particular class
     * @return if that particular class has students then table will be created and it will return true
     * else return false and sets studentsExist to false
     */
    private boolean createTableWithStudents(String newTableName, String classId) {

        /**
         * get data from students table
         */
        String[] stdProjection = {StudentEntry.S_NAME_COL, StudentEntry.S_ROLL_NO_COL};
        String selection = StudentEntry.S_CLASS_ID + "=?";
        String[] selectionArgs = {classId};

        Cursor studentData = db.query(StudentEntry.TABLE_NAME, stdProjection,
                selection, selectionArgs, null, null, null);

        /**
         * insert the student data from students table into New Attendance Table
         */
        if (studentData.getCount() > 0 && studentData.moveToFirst()) {

            //CREATE Table
            String CREATE_NEW_TABLE = "CREATE TABLE " + newTableName + "("
                    + AttendanceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + AttendanceEntry.NAME_COL + " TEXT NOT NULL, "
                    + AttendanceEntry.ROLL_NO_COL + " TEXT NOT NULL UNIQUE, "
                    + AttendanceEntry.TOTAL_ATTENDANCE_COL + " INTEGER DEFAULT 0)";

            try {
                db.execSQL(CREATE_NEW_TABLE);
            } catch (android.database.sqlite.SQLiteException e) {

                Log.e("TakeAttendanceActivity", "Table Already Exists", e);
            }

            ContentValues newStudentValues;
            int nameIndex;
            String name;
            int rollNoIndex;
            String rollNo;

            for (studentData.moveToFirst(); !studentData.isAfterLast();
                 studentData.moveToNext()) {
                nameIndex = studentData.getColumnIndex(StudentEntry.S_NAME_COL);
                name = studentData.getString(nameIndex);
                rollNoIndex = studentData.getColumnIndex(StudentEntry.S_ROLL_NO_COL);
                rollNo = studentData.getString(rollNoIndex);

                newStudentValues = new ContentValues();
                newStudentValues.put(AttendanceEntry.NAME_COL, name);
                newStudentValues.put(AttendanceEntry.ROLL_NO_COL, rollNo);
                db.insert(newTableName, null, newStudentValues);
            }
            studentsExist = true;
            return true;
        }
        studentsExist = false;
        return false;
    }

    private String addNewAttendanceColumn(String newTableName, String subject, String date,
                                          String lecture) {

        String columnName = subject + "_" + lecture + "_" + date;
        String NEW_COLUMN = columnName.replace("-", "_");

        if (!columnAlreadyExists(newTableName, NEW_COLUMN)) {

            String ADD_ATTENDANCE_COLUMN = "ALTER TABLE " + newTableName + " ADD COLUMN "
                    + NEW_COLUMN + " INTEGER DEFAULT 0;";

            try {
                db.execSQL(ADD_ATTENDANCE_COLUMN);
                db.needUpgrade(db.getVersion() + 1);
                insertAttendanceRecord(newTableName, NEW_COLUMN);
            } catch (android.database.sqlite.SQLiteException e) {
                Log.e("TakeAttendanceActivity", "Column Already Exists", e);
            }

        } else {
            Toast.makeText(this, "Attendance already Exists", Toast.LENGTH_SHORT).show();
        }
        return NEW_COLUMN;
    }

    private boolean columnAlreadyExists(String newTableName, String newColumn) {
        String selection = AttendanceRecordEntry.ATTENDANCE_TABLE_COL + "=?" + " and " +
                AttendanceRecordEntry.ATTENDANCE_COL + "=?";
        Cursor cursor = db.query(AttendanceRecordEntry.TABLE_NAME,
                new String[]{AttendanceRecordEntry._ID},
                selection,
                new String[]{newTableName, newColumn}, null, null, null);

        return (cursor.getCount() > 0);
    }


    private boolean tableAlreadyExists(String newTableName) {


        Cursor cursor = db.query(AttendanceRecordEntry.TABLE_NAME,
                new String[]{AttendanceRecordEntry._ID},
                AttendanceRecordEntry.ATTENDANCE_TABLE_COL + "=?",
                new String[]{newTableName}, null, null, null);

        return (cursor.getCount() > 0);

    }

    private void updateAttendanceChanges() {
        ContentValues newValues;
        int attendanceState;
        int totalAttendance;

        String attendanceColumn = TakeAttendanceAdapter.getAttendanceColumn();
        String attendanceTable = TakeAttendanceAdapter.getAttendanceTableName();

        if (currentTableCursor.moveToFirst()) {
            currentTableCursor.moveToFirst();
            int idIndex = currentTableCursor.getColumnIndex(AttendanceEntry._ID);
            String currentId;
            for (int i = 0; i < currentTableCursor.getCount(); i++) {

                currentId = String.valueOf(currentTableCursor.getInt(idIndex));
                attendanceState = TakeAttendanceAdapter.getAttendanceState(i);
                totalAttendance = TakeAttendanceAdapter.getTotalAttendance(i);

                newValues = new ContentValues();
                newValues.put(attendanceColumn, attendanceState);
                newValues.put(AttendanceEntry.TOTAL_ATTENDANCE_COL, totalAttendance);


                db.update(attendanceTable, newValues,
                        AttendanceEntry._ID + "=?", new String[]{currentId});
                currentTableCursor.moveToNext();
            }
        }
    }


    private void updateAttendanceRecord() {

        String attendanceColumn = TakeAttendanceAdapter.getAttendanceColumn();
        String attendanceTable = TakeAttendanceAdapter.getAttendanceTableName();

        ContentValues record = new ContentValues();

        record.put(AttendanceRecordEntry.TOTAL_STUDENTS_COL, totalStudents(attendanceTable));
        record.put(AttendanceRecordEntry.STUDENTS_PRESENT_COL,
                studentsPresent(attendanceTable, attendanceColumn));

        String where = AttendanceRecordEntry.ATTENDANCE_TABLE_COL + "=?" + " and "
                + AttendanceRecordEntry.ATTENDANCE_COL + "=?";
        String[] whereArgs = new String[]{attendanceTable, attendanceColumn};

        int rowUpdated = db.update(AttendanceRecordEntry.TABLE_NAME, record, where, whereArgs);
        Toast.makeText(this, "Rows Updated: " + rowUpdated, Toast.LENGTH_SHORT).show();

    }

    private int totalStudents(String newTableName) {
        String[] projection = {AttendanceEntry._ID};
        Cursor totalStudents = db.query(newTableName, projection, null, null,
                null, null, null);
        return totalStudents.getCount();
    }

    private void insertAttendanceRecord(String newTableName,
                                        String attendanceColumn) {

        ContentValues record = new ContentValues();
        record.put(AttendanceRecordEntry.FACULTY_ID_COL, bundle.getString(ExtraUtils.EXTRA_FAC_USER_ID));
        record.put(AttendanceRecordEntry.CLASS_ID_COL, bundle.getString(ExtraUtils.EXTRA_CLASS_ID));
        record.put(AttendanceRecordEntry.ATTENDANCE_TABLE_COL, newTableName);
        record.put(AttendanceRecordEntry.ATTENDANCE_COL, attendanceColumn);
        record.put(AttendanceRecordEntry.DATE_COL, bundle.getString(ExtraUtils.EXTRA_DATE));
        record.put(AttendanceRecordEntry.DAY_COL, bundle.getString(ExtraUtils.EXTRA_DAY));
        record.put(AttendanceRecordEntry.LECTURE_COL, bundle.getString(ExtraUtils.EXTRA_LECTURE));
        record.put(AttendanceRecordEntry.SUBJECT_COL, bundle.getString(ExtraUtils.EXTRA_SUBJECT));

        long rowId = db.insert(AttendanceRecordEntry.TABLE_NAME, null, record);
    }

    /**
     * @param newTableName     name of current students attendance table
     * @param attendanceColumn name of current attendance column
     * @return number of students present
     */
    private int studentsPresent(String newTableName, String attendanceColumn) {
        String[] projection = {AttendanceEntry._ID};
        String selection = attendanceColumn + "=?";
        String[] selectionArgs = {String.valueOf(1)};
        Cursor studentsPresent = db.query(newTableName, projection, selection, selectionArgs,
                null, null, null);
        return studentsPresent.getCount();
    }
}
