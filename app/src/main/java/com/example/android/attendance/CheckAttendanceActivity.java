package com.example.android.attendance;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.android.attendance.adapters.SpinnerArrayAdapter;
import com.example.android.attendance.contracts.CollegeContract.CollegeEntry;
import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.utilities.VolleyUtils;

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
        VolleyUtils.setupSemesterSpinner(this, semesterSpinner, progressDialog);
        VolleyUtils.setupBranchSpinner(this, branchSpinner, progressDialog);
        ExtraUtils.emptySectionSpinner(this, sectionSpinner);


        //set click listeners on spinners
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    semester = parent.getItemAtPosition(position).toString();
                    VolleyUtils.setupSectionSpinner(CheckAttendanceActivity.this,
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
                    VolleyUtils.setupSectionSpinner(CheckAttendanceActivity.this,
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


        (findViewById(R.id.show_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allInputsProvided()) {
                    VolleyUtils.checkValidClass(CheckAttendanceActivity.this, collegeId,
                            semester, branch, section);
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
        }
        return true;
    }
}