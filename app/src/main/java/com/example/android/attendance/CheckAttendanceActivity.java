package com.example.android.attendance;


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
import android.widget.Spinner;

import com.example.android.attendance.adapters.SpinnerArrayAdapter;
import com.example.android.attendance.contracts.CollegeContract.CollegeEntry;
import com.example.android.attendance.utilities.ExtraUtils;


public class CheckAttendanceActivity extends AppCompatActivity {
    /**
     * semester
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        //setup all spinners
        setupCollegeSpinner();
        setupSemesterSpinner();
        setupBranchSpinner();
        setupSectionSpinner();


        (findViewById(R.id.show_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allInputsProvided()) {
                    Intent showAttendanceIntent = new Intent(CheckAttendanceActivity.this,
                            ShowAttendanceActivity.class);

                    showAttendanceIntent.putExtra(ExtraUtils.EXTRA_COLLEGE_ID,
                            String.valueOf(collegeSelected));
                    showAttendanceIntent.putExtra(ExtraUtils.EXTRA_SEMESTER,
                            String.valueOf(semesterSelected));
                    showAttendanceIntent.putExtra(ExtraUtils.EXTRA_BRANCH,
                            String.valueOf(branchSelected));
                    showAttendanceIntent.putExtra(ExtraUtils.EXTRA_SECTION,
                            String.valueOf(sectionSelected));

                    startActivity(showAttendanceIntent);
                } else {
                    LinearLayout parentLayout = findViewById(R.id.check_attendance_linear_layout);
                    Snackbar.make(parentLayout, "Complete all fields!",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * setup college spinner
     */
    private void setupCollegeSpinner() {
        String[] collegeArray = getResources().getStringArray(R.array.colleges_array);
        collegeSpinner = findViewById(R.id.college_spin);
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
     * setup semester spinner
     */
    private void setupSemesterSpinner() {
        String[] semesterArray = getResources().getStringArray(R.array.semester_array);
        semesterSpinner = findViewById(R.id.semester_spin);
        semesterAdapter = new SpinnerArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, semesterArray);
        semesterSpinner.setAdapter(semesterAdapter);
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    semesterSelected = parent.getItemAtPosition(position).toString();
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
        sectionSpinner = findViewById(R.id.section_spin);
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
        branchSpinner = findViewById(R.id.branch_spin);
        branchAdapter = new SpinnerArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, branchArray);
        branchSpinner.setAdapter(branchAdapter);
        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    branchSelected = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * check all inputs are valid or not
     */
    private boolean allInputsProvided() {
        if (collegeSelected < 1 || collegeSelected > 2 || semesterSelected == null ||
                branchSelected == null || sectionSelected == null) {
            return false;
        }
        return true;
    }
}