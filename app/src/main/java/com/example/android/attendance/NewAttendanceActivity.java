package com.example.android.attendance;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.volley.VolleyTask;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewAttendanceActivity extends AppCompatActivity {

    private Context mContext;
    /**
     * semester
     */
    @BindView(R.id.semester_spinner)
    Spinner semesterSpinner;
    private String semester = null;

    /**
     * branch
     */
    @BindView(R.id.branch_spinner)
    Spinner branchSpinner;
    private String branch = null;

    /**
     * section
     */
    @BindView(R.id.section_spinner)
    Spinner sectionSpinner;
    private String section = null;

    /**
     * subject
     */
    @BindView(R.id.subject_spinner)
    Spinner subjectSpinner;
    private String subject = null;


    //declare date edit text
    @BindView(R.id.edit_date)
    EditText dateEditText;
    private Calendar myCalendar;
    private String date = null;
    private String dateDisplay = null;
    private String day = null;

    //declare buttons and edit text field for lecture selection
    private EditText lectureEt;
    private Button plusButton;
    private Button minusButton;
    ProgressDialog progressDialog;

    SharedPrefManager sharedPrefManager;
    int collegeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_attendance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        mContext = this;
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting up...");
        progressDialog.show();

        sharedPrefManager = SharedPrefManager.getInstance(mContext);
        collegeId = sharedPrefManager.getCollId();

        //setup all spinners
        VolleyTask.setupSemesterSpinner(mContext, semesterSpinner, progressDialog);
        VolleyTask.setupBranchSpinner(mContext, branchSpinner, progressDialog);
        ExtraUtils.emptySectionSpinner(mContext, sectionSpinner);
        ExtraUtils.emptySubjectSpinner(mContext, subjectSpinner);

        //set click listeners on spinners
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subject = null;
                if (position != 0) {
                    semester = parent.getItemAtPosition(position).toString();
                    VolleyTask.setupSubjectSpinner(mContext, subjectSpinner, progressDialog,
                            branch, semester);
                    VolleyTask.setupSectionSpinner(mContext, sectionSpinner, progressDialog,
                            branch, semester);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subject = null;
                if (position != 0) {
                    branch = parent.getItemAtPosition(position).toString();
                    VolleyTask.setupSubjectSpinner(mContext, subjectSpinner, progressDialog,
                            branch, semester);
                    VolleyTask.setupSectionSpinner(mContext, sectionSpinner, progressDialog,
                            branch, semester);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    section = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    subject = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //setup fab button for TakeAttendanceActivity
        setupFabButton();



        //setup date picker dialog
        setupDatePickerDialog();
        setDefaultDate();

        //setup lecture chooser
        setupLectureChooser();
    }

    private void setDefaultDate() {
        date = ExtraUtils.getCurrentDate();
        day = ExtraUtils.getCurrentDay();

        dateDisplay = ExtraUtils.dateDisplayFormat.format(myCalendar.getTime());
        dateEditText.setText(dateDisplay);
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

                updateDateEt();
            }

        };

        dateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DatePickerDialog dpDialog = new DatePickerDialog(mContext, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dpDialog.show();
            }
        });
    }

    /**
     * updates the editText with selected date
     */
    private void updateDateEt() {
        date = ExtraUtils.dateFormat.format(myCalendar.getTime());

        day = (ExtraUtils.dayFormat.format(myCalendar.getTime())).toUpperCase();

        dateDisplay = ExtraUtils.dateDisplayFormat.format(myCalendar.getTime());
        dateEditText.setText(dateDisplay);
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
                    VolleyTask.takeNewAttendance(mContext, date, day, semester, branch,
                            section, subject, lectureEt.getText().toString().trim(),
                            collegeId, dateDisplay, -1, -1);
                } else {
                    RelativeLayout parentLayout = findViewById(R.id.relative_layout);
                    Snackbar.make(parentLayout, "Complete all fields.",
                            Snackbar.LENGTH_LONG).show();
                }
            }

        });
    }


    /**
     * check all inputs are valid or not
     */
    private boolean allInputsProvided() {
        int currentLectureValue = Integer.parseInt(lectureEt.getText().toString());
        if (date == null || semester == null ||
                branch == null || subject == null ||
                section == null || currentLectureValue > 8 || currentLectureValue < 1) {
            return false;
        }
        return true;
    }
}
