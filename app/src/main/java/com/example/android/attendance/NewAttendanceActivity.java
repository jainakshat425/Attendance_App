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
import com.example.android.attendance.contracts.CollegeContract.CollegeEntry;
import com.example.android.attendance.contracts.SubjectContract.SubjectEntry;
import com.example.android.attendance.data.DatabaseHelper;
import com.example.android.attendance.data.DbHelperMethods;
import com.example.android.attendance.utilities.ExtraUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewAttendanceActivity extends AppCompatActivity {

    public static final int LECTURE_NOT_FOUND = 1;
    public static final int BRANCH_NOT_FOUND = 2;
    public static final int CLASS_NOT_FOUND = 3;
    public static final int INVALID_INPUTS = 4;
    public static final int ATTENDANCE_ALREADY_EXISTS = 5;
    private static final int ALL_INPUTS_VALID = 0;

    /**
     * Declare all spinners, there adapters and variable for storing selected item
     */

    /**
     * College
     */
    private Spinner collegeSpinner;
    private SpinnerArrayAdapter collegeAdapter;
    private int collegeSelected = -1;

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


    //declare date edit text
    private EditText dateEditText;
    private Calendar myCalendar;
    private String currentDateString = null;
    private String daySelected = null;

    //declare buttons and edit text field for lecture selection
    private EditText lectureEt;
    private Button plusButton;
    private Button minusButton;

    private static final int TAKE_ATTENDANCE_REQ_CODE = 3;

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    int branchId;
    int classId;
    int lectureId;

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
        setupCollegeSpinner();

        //setup fab button for TakeAttendanceActivity
        setupFabButton();

        setDefaultDate();

        //setup date picker dialog
        setupDatePickerDialog();

        //setup lecture chooser
        setupLectureChooser();
    }

    private void setDefaultDate() {
        dateEditText = findViewById(R.id.edit_date);

        currentDateString = ExtraUtils.getCurrentDate();
        daySelected = ExtraUtils.getCurrentDay();
        dateEditText.setText(currentDateString);
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
     * opens date picker dialog when editText is clicked
     */
    private void setupDatePickerDialog() {

        myCalendar = Calendar.getInstance();

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
        daySelected = (simpleDayFormat.format(myCalendar.getTime())).toUpperCase();
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

                if (allInputsProvided()) {
                    int resultCode = allInputsValid();
                    if (resultCode == ALL_INPUTS_VALID) {
                        Intent takeAttendanceIntent = new Intent();
                        takeAttendanceIntent.setClass(NewAttendanceActivity.this,
                                TakeAttendanceActivity.class);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_COLLEGE_ID,
                                String.valueOf(collegeSelected));
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_DATE, currentDateString);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_DAY, daySelected);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_SEMESTER, semesterSelected);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_BRANCH, branchSelected);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_SECTION, sectionSelected);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_SUBJECT, subjectSelected);
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_CLASS_ID, String.valueOf(classId));
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_BRANCH_ID, String.valueOf(branchId));
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_LECTURE,
                                lectureEt.getText().toString());
                        takeAttendanceIntent.putExtra(ExtraUtils.EXTRA_FAC_USER_ID,
                                getIntent().getStringExtra(ExtraUtils.EXTRA_FAC_USER_ID));
                        startActivityForResult(takeAttendanceIntent, TAKE_ATTENDANCE_REQ_CODE);
                    } else {
                        showError(resultCode);
                    }
                } else {
                    Toast.makeText(NewAttendanceActivity.this, "Complete all fields",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void showError(int resultCode) {
        String error;
        switch (resultCode) {
            case LECTURE_NOT_FOUND:
                error = "Lecture Not Found!";
                break;
            case CLASS_NOT_FOUND:
                error = "Class Not Found!";
                break;
            case BRANCH_NOT_FOUND:
                error = "Branch Not Found!";
                break;
            case ATTENDANCE_ALREADY_EXISTS:
                error = "Attendance Already Exists!";
                break;
            default:
                error = "Unknown Error!";
                break;
        }
        RelativeLayout parentLayout = findViewById(R.id.relative_layout);
        Snackbar.make(parentLayout, error, Snackbar.LENGTH_LONG).show();

    }

    /**
     * check all inputs are valid or not
     */
    private boolean allInputsProvided() {
        int currentLectureValue = Integer.parseInt(lectureEt.getText().toString());
        if (collegeSelected < 1 || collegeSelected > 2 || currentDateString == null ||
                semesterSelected == null || branchSelected == null || subjectSelected == null ||
                sectionSelected == null || currentLectureValue > 8 || currentLectureValue < 1) {
            return false;
        }
        return true;
    }

    /**
     * setup college spinner
     */
    private void setupCollegeSpinner() {
        String[] collegeArray = getResources().getStringArray(R.array.colleges_array);
        collegeSpinner = findViewById(R.id.college_spinner);
        collegeAdapter = new SpinnerArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, collegeArray);
        collegeSpinner.setAdapter(collegeAdapter);
        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    collegeSelected = parent.getItemAtPosition(position).toString().equals("GIT") ?
                            CollegeEntry.COLLEGE_GIT :
                            CollegeEntry.COLLEGE_GCT;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    /**
     * setup subject spinner
     */
    private void setupSubjectSpinner() {

        subjectSpinner = findViewById(R.id.subject_spinner);
        if (semesterSelected != null && branchSelected != null) {

            /**
             * query the database and store result in cursor
             */
            String branchId = String.valueOf(DbHelperMethods.getBranchId(db, branchSelected));
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
                subjectCursor.close();
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

    private int allInputsValid() {
        branchId = DbHelperMethods.getBranchId(db, branchSelected);
        if (branchId > 0) {
            classId = DbHelperMethods.getClassId(db, collegeSelected,
                    semesterSelected,
                    String.valueOf(branchId),
                    sectionSelected);
            if (classId > 0) {
                lectureId = DbHelperMethods.getLectureId(db, String.valueOf(classId),
                        lectureEt.getText().toString(), daySelected);

                if (lectureId > 0) {

                    boolean attendanceAlreadyExist = DbHelperMethods.isAttendanceAlreadyExists
                            (db, lectureId, dateEditText.getText().toString());

                    if (attendanceAlreadyExist) return ATTENDANCE_ALREADY_EXISTS;
                    else return ALL_INPUTS_VALID;

                } else {
                    return LECTURE_NOT_FOUND;
                }
            } else {
                return CLASS_NOT_FOUND;
            }
        } else {
            return BRANCH_NOT_FOUND;
        }
    }
}
