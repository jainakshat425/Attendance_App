package com.example.android.attendance;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.volley.VolleyTask;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CheckAttendanceActivity extends AppCompatActivity {

    /**
     * semester
     */
    @BindView(R.id.semester_spin)
    Spinner semesterSpinner;
    private String semester = null;

    /**
     * branch
     */
    @BindView(R.id.branch_spin)
    Spinner branchSpinner;
    private String branch = null;

    /**
     * section
     */
    @BindView(R.id.section_spin)
    Spinner sectionSpinner;
    private String section = null;

    @BindView(R.id.from_edit_date)
    EditText fromDateEt;
    private String fromDate = null;
    private String fromDateDisplay = null;

    @BindView(R.id.to_edit_date)
    EditText toDateEt;
    private String toDate = null;
    private String toDateDisplay = null;

    @BindView(R.id.show_date_cb)
    CheckBox showDateCb;
    boolean isDateWise = false;

    @BindView(R.id.date_layout_container)
    LinearLayout dateLayoutContainer;

    ProgressDialog progressDialog;

    SharedPrefManager sharedPrefManager;
    int collegeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Setting up...");
        progressDialog.show();

        sharedPrefManager = SharedPrefManager.getInstance(this);
        collegeId = sharedPrefManager.getCollId();

        //setup all spinners
        //setup all spinners
        VolleyTask.setupSemesterSpinner(this, semesterSpinner, progressDialog);
        VolleyTask.setupBranchSpinner(this, branchSpinner, progressDialog);
        ExtraUtils.emptySectionSpinner(this, sectionSpinner);


        //set click listeners on spinners
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    semester = parent.getItemAtPosition(position).toString();
                    VolleyTask.setupSectionSpinner(CheckAttendanceActivity.this,
                            sectionSpinner, progressDialog,
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
                if (position != 0) {
                    branch = parent.getItemAtPosition(position).toString();
                    VolleyTask.setupSectionSpinner(CheckAttendanceActivity.this,
                            sectionSpinner, progressDialog,
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

        //setup date picker dialog
        setupDatePickerDialog(fromDateEt);
        setupDatePickerDialog(toDateEt);
        setDefaultToDate();

        showDateCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dateLayoutContainer.setVisibility(View.VISIBLE);
                    isDateWise = true;
                } else {
                    dateLayoutContainer.setVisibility(View.GONE);
                    isDateWise = false;
                }
            }
        });

        (findViewById(R.id.show_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allInputsProvided()) {
                    VolleyTask.checkValidClass(CheckAttendanceActivity.this, collegeId,
                            semester, branch, section, isDateWise, fromDate, toDate);
                } else {
                    LinearLayout parentLayout = findViewById(R.id.check_attendance_linear_layout);
                    Snackbar.make(parentLayout, "Complete all fields!",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }


    /**
     * check all inputs are valid or not
     */
    private boolean allInputsProvided() {
        if (semester == null || branch == null || section == null) {
            return false;
        } else if (isDateWise && (fromDate == null || toDate == null)) {
            return false;
        }
        return true;
    }


    private void setupDatePickerDialog(final EditText dateEditText) {

        final Calendar calender = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                calender.set(Calendar.YEAR, year);
                calender.set(Calendar.MONTH, monthOfYear);
                calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateDateEt(dateEditText, calender);
            }

        };

        dateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DatePickerDialog dpDialog = new DatePickerDialog(
                        CheckAttendanceActivity.this,
                        dateSetListener,
                        calender.get(Calendar.YEAR),
                        calender.get(Calendar.MONTH),
                        calender.get(Calendar.DAY_OF_MONTH));
                dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dpDialog.show();
            }
        });
    }

    private void updateDateEt(EditText dateEditText, Calendar calendar) {
        if (dateEditText.getId() == R.id.from_edit_date) {

            fromDate = ExtraUtils.dateFormat.format(calendar.getTime());
            fromDateDisplay = ExtraUtils.dateDisplayFormat.format(calendar.getTime());
            dateEditText.setText(fromDateDisplay);
        } else {

            toDate = ExtraUtils.dateFormat.format(calendar.getTime());
            toDateDisplay = ExtraUtils.dateDisplayFormat.format(calendar.getTime());
            dateEditText.setText(toDateDisplay);
        }
    }

    private void setDefaultToDate() {
        toDate = ExtraUtils.getCurrentDate();
        toDateDisplay = ExtraUtils.dateDisplayFormat.format(Calendar.getInstance().getTime());
        toDateEt.setText(toDateDisplay);
    }

}