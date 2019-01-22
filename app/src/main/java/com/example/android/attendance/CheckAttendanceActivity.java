package com.example.android.attendance;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.android.attendance.adapters.SpinnerArrayAdapter;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.volley.VolleyTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CheckAttendanceActivity extends AppCompatActivity {

    private Context mContext;

    @BindView(R.id.check_attendance_linear_layout)
    LinearLayout layout;

    /**
     * semester
     */
    @BindView(R.id.semester_spin)
    Spinner semesterSpinner;
    private String semester = "";

    /**
     * branch
     */
    @BindView(R.id.branch_spin)
    Spinner branchSpinner;
    private SpinnerArrayAdapter branchAdapter;
    private String branch = "";

    /**
     * section
     */
    @BindView(R.id.section_spin)
    Spinner sectionSpinner;
    private SpinnerArrayAdapter sectionAdapter;
    private String section = "";

    @BindView(R.id.from_edit_date)
    EditText fromDateEt;
    private String fromDate = "";
    private String fromDateDisplay = "";

    @BindView(R.id.to_edit_date)
    EditText toDateEt;
    private String toDate = "";
    private String toDateDisplay = "";

    @BindView(R.id.show_date_cb)
    CheckBox showDateCb;
    boolean isDateWise = false;

    @BindView(R.id.date_layout_container)
    LinearLayout dateLayoutContainer;

    @OnClick(R.id.show_button)
    void showAttendanceReport() {
        if (validateInputs()) {
            VolleyTask.checkValidClass(CheckAttendanceActivity.this, collegeId,
                    semester, branch, section, jObj -> {

                        Intent i = new Intent();
                        i.setClass(mContext, StudentReportActivity.class);

                        i.putExtra(ExtraUtils.EXTRA_SEMESTER, semester);
                        i.putExtra(ExtraUtils.EXTRA_BRANCH, branch);
                        i.putExtra(ExtraUtils.EXTRA_SECTION, section);
                        i.putExtra(ExtraUtils.EXTRA_COLLEGE_ID, collegeId);

                        i.putExtra(ExtraUtils.EXTRA_IS_DATE_WISE, isDateWise);
                        if (isDateWise) {
                            i.putExtra(ExtraUtils.EXTRA_FROM_DATE, fromDate);
                            i.putExtra(ExtraUtils.EXTRA_TO_DATE, toDate);
                        }

                        try {
                            int classId = jObj.getInt("class_id");
                            int branchId = jObj.getInt("branch_id");

                            i.putExtra(ExtraUtils.EXTRA_CLASS_ID, classId);
                            i.putExtra(ExtraUtils.EXTRA_BRANCH_ID, branchId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        startActivity(i);
                    });
        }
    }

    SharedPrefManager sharedPrefManager;
    int collegeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_attendance);
        ButterKnife.bind(this);

        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        sharedPrefManager = SharedPrefManager.getInstance(this);
        collegeId = sharedPrefManager.getCollId();

        setupBranchSpinner();
        setupSemesterSpinner();
        setupSectionSpinner();

        refreshBranchSpinner();

        //setup date picker dialog
        setupDatePickerDialog(fromDateEt);
        setupDatePickerDialog(toDateEt);
        setDefaultToDate();

        showDateCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                dateLayoutContainer.setVisibility(View.VISIBLE);
                isDateWise = true;
            } else {
                dateLayoutContainer.setVisibility(View.GONE);
                isDateWise = false;
            }
        });
    }

    private void setupDatePickerDialog(final EditText dateEditText) {

        final Calendar calender = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener dateSetListener =
                (view, year, monthOfYear, dayOfMonth) -> {

                    calender.set(Calendar.YEAR, year);
                    calender.set(Calendar.MONTH, monthOfYear);
                    calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    updateDateEt(dateEditText, calender);
                };

        dateEditText.setOnClickListener(v -> {
            DatePickerDialog dpDialog = new DatePickerDialog(
                    CheckAttendanceActivity.this,
                    dateSetListener,
                    calender.get(Calendar.YEAR),
                    calender.get(Calendar.MONTH),
                    calender.get(Calendar.DAY_OF_MONTH));
            dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            dpDialog.show();
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


    private void setupSemesterSpinner() {
        String[] semArr = getResources().getStringArray(R.array.semester_array);
        SpinnerArrayAdapter semesterAdapter = new SpinnerArrayAdapter(mContext,
                android.R.layout.simple_spinner_dropdown_item,
                semArr);
        semesterSpinner.setAdapter(semesterAdapter);

        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                if (pos != 0) {
                    semester = (String) parent.getItemAtPosition(pos);
                    refreshSectionsSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupBranchSpinner() {
        List<String> brList = new ArrayList<>();
        brList.add("Branch");
        String[] brArr = brList.toArray(new String[0]);
        branchAdapter = new SpinnerArrayAdapter(mContext,
                android.R.layout.simple_spinner_dropdown_item,
                brArr);
        branchSpinner.setAdapter(branchAdapter);

        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                if (pos != 0) {
                    branch = (String) parent.getItemAtPosition(pos);
                    refreshSectionsSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupSectionSpinner() {
        String[] secArr = {"Section"};
        sectionAdapter = new SpinnerArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, secArr);
        sectionSpinner.setAdapter(sectionAdapter);

        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                if (pos != 0)
                    section = (String) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void refreshBranchSpinner() {
        VolleyTask.getBranchNames(this, collegeId, jObj -> {
            try {
                JSONArray brJsonArr = jObj.getJSONArray("branch_names");
                List<String> brList = new ArrayList<>();
                brList.add("Branch");
                for (int i = 0; i < brJsonArr.length(); i++) {
                    brList.add(brJsonArr.getString(i));
                }
                String[] brArr = brList.toArray(new String[0]);
                branchAdapter = new SpinnerArrayAdapter(mContext,
                        android.R.layout.simple_spinner_dropdown_item,
                        brArr);
                branchSpinner.setAdapter(branchAdapter);
                branch = "";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

    }

    private void refreshSectionsSpinner() {
        if (!TextUtils.isEmpty(semester) && !TextUtils.isEmpty(branch)) {
            final List<String> secList = new ArrayList<>();
            secList.add("Section");
            VolleyTask.getSections(mContext, branch,
                    semester, collegeId, jObj -> {

                        try {
                            JSONArray jsonArray = jObj.getJSONArray("sections");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                secList.add(jsonArray.getString(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String[] secArr = secList.toArray(new String[0]);
                        sectionAdapter = new SpinnerArrayAdapter(mContext,
                                android.R.layout.simple_spinner_dropdown_item,
                                secArr);
                        sectionSpinner.setAdapter(sectionAdapter);
                        section = "";
                    });
        } else
            setupSectionSpinner();
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(semester)) {
            Snackbar.make(layout, "Semester not selected!", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(branch)) {
            Snackbar.make(layout, "Branch not selected!", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(section)) {
            Snackbar.make(layout, "Section not selected!", Snackbar.LENGTH_SHORT).show();
            return false;
        } else if (isDateWise && (TextUtils.isEmpty(fromDate) || TextUtils.isEmpty(toDate))) {
            Snackbar.make(layout, "Select both dates for date wise report!",
                    Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}