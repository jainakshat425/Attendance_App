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

    @OnClick(R.id.show_button)
    void showAttendanceReport() {
        if (allInputsProvided()) {
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
        } else {
            LinearLayout parentLayout = findViewById(R.id.check_attendance_linear_layout);
            Snackbar.make(parentLayout, "Complete all fields!",
                    Snackbar.LENGTH_LONG).show();
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


        setupSemesterSpinner();

        final List<String> branchArr = new ArrayList<>();
        branchArr.add("Branch");
        VolleyTask.getBranchNames(mContext, collegeId, jObj -> {
            JSONArray jsonArray;
            try {
                jsonArray = jObj.getJSONArray("branches");

                for (int i = 0; i < jsonArray.length(); i++) {
                    branchArr.add(jsonArray.getString(i));
                }
                SpinnerArrayAdapter branchAdapter = new SpinnerArrayAdapter(mContext,
                        android.R.layout.simple_spinner_dropdown_item,
                        branchArr.toArray(new String[0]));
                branchSpinner.setAdapter(branchAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        ExtraUtils.emptySectionSpinner(this, sectionSpinner);

        //set click listeners on spinners
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    semester = parent.getItemAtPosition(position).toString();
                    refreshSectionSpinner();
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
                    refreshSectionSpinner();
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

    private void refreshSectionSpinner() {
        if (semester != null && branch != null) {
            final List<String> secArr = new ArrayList<>();
            secArr.add("Section");
            VolleyTask.getSections(mContext,
                    branch, semester, collegeId, jObj -> {
                        JSONArray jsonArray;
                        try {
                            jsonArray = jObj.getJSONArray("sections");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                secArr.add(jsonArray.getString(i));
                            }
                            SpinnerArrayAdapter sectionAdapter = new SpinnerArrayAdapter(mContext,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    secArr.toArray(new String[0]));
                            sectionSpinner.setAdapter(sectionAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
        } else
            ExtraUtils.emptySectionSpinner(mContext, sectionSpinner);
    }

    private void setupSemesterSpinner() {
        String[] semArr = getResources().getStringArray(R.array.semester_array);
        SpinnerArrayAdapter semesterAdapter = new SpinnerArrayAdapter(CheckAttendanceActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                semArr);
        semesterSpinner.setAdapter(semesterAdapter);

    }


}