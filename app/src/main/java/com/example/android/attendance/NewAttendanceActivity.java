package com.example.android.attendance;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.attendance.adapters.SpinnerArrayAdapter;
import com.example.android.attendance.contracts.AttendanceRecordContract.AttendanceRecordEntry;
import com.example.android.attendance.contracts.CollegeContract.CollegeEntry;
import com.example.android.attendance.contracts.SubjectContract.SubjectEntry;
import com.example.android.attendance.data.DatabaseHelper;
import com.example.android.attendance.data.DbHelperMethods;
import com.example.android.attendance.utilities.ExtraUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewAttendanceActivity extends AppCompatActivity {

    /**
     * Declare all spinners, there adapters and variable for storing selected item
     */

    /**
     * semester
     */
    private Spinner semesterSpinner;
    private SpinnerArrayAdapter semesterAdapter;
    private String semesterSelected = null;

    /**
     * branch
     */
    private Spinner branchSpinner;
    private SpinnerArrayAdapter branchAdapter;
    private String branchSelected = null;

    /**
     * section
     */
    private Spinner sectionSpinner;
    private SpinnerArrayAdapter sectionAdapter;
    private String sectionSelected = null;

    /**
     * subject
     */
    private Spinner subjectSpinner;
    private SpinnerArrayAdapter subjectAdapter;
    private String subjectSelected = null;


    //declare switch and it's surrounding text views
    private Switch collegeSwitch;
    private int collegeSelected = CollegeEntry.COLLEGE_GIT;
    private TextView gitTv;
    private TextView gctTv;

    //declare date edit text
    private EditText dateEditText;
    private Calendar myCalendar;
    private String currentDateString = null;
    private String currentDay = null;

    //declare buttons and edit text field for lecture selection
    private EditText lectureEt;
    private Button plusButton;
    private Button minusButton;

    private static final int TAKE_ATTENDANCE_REQ_CODE = 3;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    String branchId;
    String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_attendance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        /**
         * open the database for populating the spinner with subjects according to semester and
         * branch
         */
        databaseHelper = new DatabaseHelper(this);

        try {
            db = databaseHelper.openDataBaseReadOnly();
        } catch (SQLException sqle) {
            throw sqle;
        }


        //setup all spinners
        setupSemesterSpinner();
        setupBranchSpinner();
        setupSectionSpinner();
        setupSubjectSpinner();

        //setup fab button for TakeAttendanceActivity
        setupFabButton();

        //setup date picker dialog
        setupDatePickerDialog();

        //setup college switch
        setupCollegeSwitch();

        //setup lecture chooser
        setupLectureChooser();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK, data);
            finish();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * setup lecture chooser
     */
    private void setupLectureChooser() {
        lectureEt = findViewById(R.id.lecture_et);
        plusButton = findViewById(R.id.plus_lecture_button);
        minusButton = findViewById(R.id.minus_lecture_button);

        lectureEt.setText("1");
        minusButton.setEnabled(false);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentLectureValue = Integer.parseInt(lectureEt.getText().toString());
                if (currentLectureValue < 8) {
                    lectureEt.setText(String.valueOf(++currentLectureValue));
                    plusButton.setEnabled(true);
                } else {
                    plusButton.setEnabled(false);
                }
                minusButton.setEnabled(true);
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentLectureValue = Integer.parseInt(lectureEt.getText().toString());
                if (currentLectureValue > 1) {
                    lectureEt.setText(String.valueOf(--currentLectureValue));
                    minusButton.setEnabled(true);
                } else {
                    minusButton.setEnabled(false);
                }
                plusButton.setEnabled(true);
            }
        });
    }

    /**
     * setup college switch
     */
    private void setupCollegeSwitch() {
        gitTv = (TextView) findViewById(R.id.git_tv);
        gctTv = (TextView) findViewById(R.id.gct_tv);

        //setup switch for selection of college
        collegeSwitch = findViewById(R.id.college_switch);
        collegeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                collegeSelected = isChecked ? CollegeEntry.COLLEGE_GCT : CollegeEntry.COLLEGE_GIT;

                if (collegeSelected == CollegeEntry.COLLEGE_GCT) {
                    gctTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    gitTv.setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    gctTv.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    gitTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        });
    }

    /**
     * opens date picker dialog when editText is clicked
     */
    private void setupDatePickerDialog() {

        myCalendar = Calendar.getInstance();

        dateEditText = findViewById(R.id.edit_date);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateLabel();
            }

        };

        dateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new DatePickerDialog(NewAttendanceActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    /**
     * updates the editText with selected date
     */
    private void updateLabel() {
        String dateFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
        currentDateString = simpleDateFormat.format(myCalendar.getTime());

        SimpleDateFormat simpleDayFormat = new SimpleDateFormat("EEEE", Locale.US);
        currentDay = simpleDayFormat.format(myCalendar.getTime());
        dateEditText.setText(currentDateString);
    }

    /**
     * setup fab button which LINKs to TakeAttendanceActivity
     */
    private void setupFabButton() {
        //initialise floatingActionButton and link it to takeAttendance
        final FloatingActionButton takeAttendanceFab = findViewById(R.id.fab_new_attendance);
        takeAttendanceFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (allInputsValid()) {
                    if (!attendanceAlreadyExists()) {
                        Intent takeAttendanceIntent = new Intent();
                        takeAttendanceIntent.setClass(NewAttendanceActivity.this,
                                TakeAttendanceActivity.class);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_COLLEGE_ID,
                                String.valueOf(collegeSelected));
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_DATE, currentDateString);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_DAY, currentDay);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_SEMESTER, semesterSelected);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_BRANCH, branchSelected);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_SECTION, sectionSelected);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_SUBJECT, subjectSelected);

                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_CLASS_ID, classId);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_BRANCH_ID, branchId);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_LECTURE,
                                lectureEt.getText().toString());
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_FAC_USER_ID,
                                getIntent().getStringExtra(ExtraUtils.EXTRA_FAC_USER_ID));
                        startActivityForResult(takeAttendanceIntent, TAKE_ATTENDANCE_REQ_CODE);
                    } else {
                        RelativeLayout parentlayout = findViewById(R.id.relative_layout);
                        Snackbar snackbar = Snackbar.make(parentlayout,
                                "Attendance already exists!",
                                Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                } else {
                    Toast.makeText(NewAttendanceActivity.this, "Complete all fields",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    /**
     * check all inputs are valid or not
     */
    private boolean allInputsValid() {
        int currentLectureValue = Integer.parseInt(lectureEt.getText().toString());
        if (collegeSelected < 1 || collegeSelected > 2 || currentDateString == null ||
                semesterSelected == null || branchSelected == null || subjectSelected == null ||
                sectionSelected == null || currentLectureValue > 8 || currentLectureValue < 1) {
            return false;
        }
        return true;
    }

    /**
     * setup subject spinner
     */
    private void setupSubjectSpinner() {

        subjectSpinner = (Spinner) findViewById(R.id.subject_spinner);
        if (semesterSelected != null && branchSelected != null) {

            /**
             * query the database and store result in cursor
             */
            String branchId = String.valueOf(DbHelperMethods.getBranchId(this, branchSelected));
            String[] projection = {SubjectEntry.SUB_NAME_COL};
            String selection = SubjectEntry.SUB_SEMESTER_COL + "=?" + " and "
                    + SubjectEntry.BRANCH_ID_COL + "=?";
            String[] selectionArgs = {semesterSelected, branchId};

            Cursor subjectCursor = db.query(SubjectEntry.TABLE_NAME, projection, selection,
                    selectionArgs, null, null, null);

            String[] subject;
            if (subjectCursor.getCount() > 0 && subjectCursor.moveToFirst()) {
                subject = new String[subjectCursor.getCount() + 1];
                subject[0] = "Subject";
                subjectCursor.moveToFirst();
                for (int i = 1; !subjectCursor.isAfterLast(); i++) {
                    subject[i] = subjectCursor.getString(
                            subjectCursor.getColumnIndex(SubjectEntry.SUB_NAME_COL));
                    subjectCursor.moveToNext();
                }
                subjectAdapter = new SpinnerArrayAdapter(this,
                        android.R.layout.simple_spinner_dropdown_item, subject);
                subjectSpinner.setAdapter(subjectAdapter);
                subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            subjectSelected = parent.getItemAtPosition(position).toString();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } else {
                emptySubjectSpinner();
            }
        } else {
            emptySubjectSpinner();
        }
    }

    /**
     * empties the subject spinner
     */
    private void emptySubjectSpinner() {
        String[] subject = {"Subject"};
        subjectAdapter = new SpinnerArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, subject);
        subjectSpinner.setAdapter(subjectAdapter);
    }

    /**
     * setup semester spinner
     */
    private void setupSemesterSpinner() {
        String[] semesterArray = getResources().getStringArray(R.array.semester_array);
        semesterSpinner = findViewById(R.id.semester_spinner);
        semesterAdapter = new SpinnerArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, semesterArray);
        semesterSpinner.setAdapter(semesterAdapter);
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectSelected = null;
                if (position != 0) {
                    semesterSelected = parent.getItemAtPosition(position).toString();
                    setupSubjectSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * setup section spinner
     */
    private void setupSectionSpinner() {

        String[] sectionArray = getResources().getStringArray(R.array.section_array);
        sectionSpinner = findViewById(R.id.section_spinner);
        sectionAdapter = new SpinnerArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, sectionArray);
        sectionSpinner.setAdapter(sectionAdapter);
        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    sectionSelected = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * setup branch spinner
     */
    private void setupBranchSpinner() {

        String[] branchArray = getResources().getStringArray(R.array.branch_array);
        branchSpinner = findViewById(R.id.branch_spinner);
        branchAdapter = new SpinnerArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, branchArray);
        branchSpinner.setAdapter(branchAdapter);
        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectSelected = null;
                if (position != 0) {
                    branchSelected = parent.getItemAtPosition(position).toString();
                    setupSubjectSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    /**
     * check that whether attendance already exists or not
     */
    private boolean attendanceAlreadyExists() {

        branchId = String.valueOf(DbHelperMethods.getBranchId(this, branchSelected));
        classId = String.valueOf(DbHelperMethods.getClassId(this, collegeSelected,
                semesterSelected, branchId, sectionSelected));

        String selection = AttendanceRecordEntry.CLASS_ID_COL + "=?" + " and " +
                AttendanceRecordEntry.DATE_COL + "=?" + " and " +
                AttendanceRecordEntry.SUBJECT_COL + "=?" + " and " +
                AttendanceRecordEntry.LECTURE_COL + "=?";

        String[] selectionArgs = new String[]{classId,
                dateEditText.getText().toString(),
                subjectSelected,
                lectureEt.getText().toString()};

        Cursor cursor = db.query(AttendanceRecordEntry.TABLE_NAME, null, selection,
                selectionArgs, null, null, null);

        return (cursor.getCount() > 0 );
    }
}
