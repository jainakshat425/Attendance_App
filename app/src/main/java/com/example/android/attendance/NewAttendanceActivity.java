package com.example.android.attendance;

import android.app.DatePickerDialog;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.volley.VolleyTask;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewAttendanceActivity extends AppCompatActivity {

    private Context mContext;
    int collegeId;

    @BindView(R.id.new_attendance_layout)
    LinearLayout layout;
    /**
     * semester
     */
    @BindView(R.id.semester_spinner)
    Spinner semesterSpinner;
    private String semester = "";

    /**
     * branch
     */
    @BindView(R.id.branch_spinner)
    Spinner branchSpinner;
    private String branch = "";
    private SpinnerArrayAdapter branchAdapter;

    /**
     * section
     */
    @BindView(R.id.section_spinner)
    Spinner sectionSpinner;
    private SpinnerArrayAdapter sectionAdapter;
    private String section = "";

    /**
     * lecture number
     */
    @BindView(R.id.lecture_spinner)
    Spinner lectureSpinner;
    private SpinnerArrayAdapter lectureAdapter;
    private String lectNo = "";

    //declare date edit text
    @BindView(R.id.edit_date)
    EditText dateIn;
    private Calendar myCalendar;
    private String date = "";
    private String dateDisplay = "";
    private String day = "";

    @OnClick(R.id.fab_new_attendance)
    void newAttendance() {

        if (validateInputs()) {
            VolleyTask.takeNewAttendance(mContext, date, day, semester, branch,
                    section, lectNo, collegeId, -1,
                    jObj -> {
                        Intent intent = new Intent();
                        intent.setClass(mContext, TakeAttendanceActivity.class);
                        if (jObj.has("class_id")) {
                            try {
                                int classId = jObj.getInt("class_id");
                                intent.putExtra(ExtraUtils.EXTRA_CLASS_ID, String.valueOf(classId));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        intent.putExtra(ExtraUtils.EXTRA_DATE, date);
                        intent.putExtra(ExtraUtils.EXTRA_DISPLAY_DATE, dateDisplay);
                        intent.putExtra(ExtraUtils.EXTRA_DAY, day);
                        intent.putExtra(ExtraUtils.EXTRA_LECTURE_NO, lectNo);

                        startActivity(intent);
                    });
        }
    }

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

        collegeId = SharedPrefManager.getInstance(mContext).getCollId();

        setupBranchSpinner();
        setupSemesterSpinner();
        setupSectionSpinner();
        setupLectureSpinner();

        refreshBranchSpinner();

        //setup date picker dialog
        setupDatePickerDialog();
        setDefaultDate();
    }

    private void setDefaultDate() {
        date = ExtraUtils.getCurrentDate();
        day = ExtraUtils.getCurrentDay();

        dateDisplay = ExtraUtils.dateDisplayFormat.format(myCalendar.getTime());
        dateIn.setText(dateDisplay);
    }

    /**
     * opens date picker dialog when editText is clicked
     */
    private void setupDatePickerDialog() {

        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateDateEt();
        };

        dateIn.setOnClickListener(v -> {

            DatePickerDialog dpDialog = new DatePickerDialog(mContext, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            dpDialog.show();
        });
    }

    /**
     * updates the editText with selected date
     */
    private void updateDateEt() {
        date = ExtraUtils.dateFormat.format(myCalendar.getTime());

        day = (ExtraUtils.dayFormat.format(myCalendar.getTime())).toUpperCase();

        dateDisplay = ExtraUtils.dateDisplayFormat.format(myCalendar.getTime());
        dateIn.setText(dateDisplay);
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
                    refreshLectureSpinner();
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
                    refreshLectureSpinner();
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
                if (pos != 0) {
                    section = (String) parent.getItemAtPosition(pos);
                    refreshLectureSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupLectureSpinner() {
        String[] lectArr = {"Lecture"};
        lectureAdapter = new SpinnerArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, lectArr);
        lectureSpinner.setAdapter(lectureAdapter);

        lectureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
                if (pos != 0)
                    lectNo = (String) parent.getItemAtPosition(pos);
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
                    });
        } else
            setupSectionSpinner();
    }

    private void refreshLectureSpinner() {
        if (!TextUtils.isEmpty(semester) && !TextUtils.isEmpty(branch) && !TextUtils.isEmpty(section) ) {
            final List<String> lectList = new ArrayList<>();
            lectList.add("Lecture");
            VolleyTask.getLectureNumbers(mContext, branch,
                    semester, section, collegeId, jObj -> {

                        try {
                            JSONArray jsonArray = jObj.getJSONArray("lecture_numbers");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                lectList.add(jsonArray.getString(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String[] lectArr = lectList.toArray(new String[0]);
                        lectureAdapter = new SpinnerArrayAdapter(mContext,
                                android.R.layout.simple_spinner_dropdown_item,
                                lectArr);
                        lectureSpinner.setAdapter(lectureAdapter);
                    });
        } else
            setupLectureSpinner();
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
        } else if (TextUtils.isEmpty(lectNo)) {
            Snackbar.make(layout, "Lecture not selected!", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(date)) {
            dateIn.setError("Choose valid date!");
            return false;
        } else {
            dateIn.setError(null);
            return true;
        }
    }

}
