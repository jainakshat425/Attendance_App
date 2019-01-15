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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.volley.VolleyTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @OnClick(R.id.fab_new_attendance)
    void newAttendance() {
        if (allInputsProvided()) {
            final ProgressDialog progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            String lectNo = lectureEt.getText().toString().trim();

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
                        intent.putExtra(ExtraUtils.EXTRA_SEMESTER, semester);
                        intent.putExtra(ExtraUtils.EXTRA_BRANCH, branch);
                        intent.putExtra(ExtraUtils.EXTRA_SECTION, section);
                        intent.putExtra(ExtraUtils.EXTRA_SUBJECT, subject);
                        intent.putExtra(ExtraUtils.EXTRA_LECTURE_NO, lectNo);

                        mContext.startActivity(intent);
                    });
        } else {
            RelativeLayout parentLayout = findViewById(R.id.relative_layout);
            Snackbar.make(parentLayout, "Complete all fields.",
                    Snackbar.LENGTH_LONG).show();
        }
    }

    //declare buttons and edit text field for lecture selection
    private EditText lectureEt;
    private Button plusButton;
    private Button minusButton;

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

        sharedPrefManager = SharedPrefManager.getInstance(mContext);
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

        ExtraUtils.emptySectionSpinner(mContext, sectionSpinner);
        ExtraUtils.emptySubjectSpinner(mContext, subjectSpinner);

        //set click listeners on spinners
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subject = null;
                if (position != 0) {
                    semester = parent.getItemAtPosition(position).toString();
                    refreshSubjectSpinner();
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
                subject = null;
                if (position != 0) {
                    branch = parent.getItemAtPosition(position).toString();
                    refreshSubjectSpinner();
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

        plusButton.setOnClickListener(v -> {

            int currentLectureValue = Integer.parseInt(lectureEt.getText().toString());
            if (currentLectureValue < 8) {
                lectureEt.setText(String.valueOf(++currentLectureValue));
                plusButton.setEnabled(true);
            } else {
                plusButton.setEnabled(false);
            }
            minusButton.setEnabled(true);
        });

        minusButton.setOnClickListener(v -> {
            int currentLectureValue = Integer.parseInt(lectureEt.getText().toString());
            if (currentLectureValue > 1) {
                lectureEt.setText(String.valueOf(--currentLectureValue));
                minusButton.setEnabled(true);
            } else {
                minusButton.setEnabled(false);
            }
            plusButton.setEnabled(true);
        });
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

        dateEditText.setOnClickListener(v -> {

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
        dateEditText.setText(dateDisplay);
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

    private void setupSemesterSpinner() {
        String[] semArr = getResources().getStringArray(R.array.semester_array);
        SpinnerArrayAdapter semesterAdapter = new SpinnerArrayAdapter(NewAttendanceActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                semArr);
        semesterSpinner.setAdapter(semesterAdapter);

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

    private void refreshSubjectSpinner() {
        final List<String> subArray = new ArrayList<>();
        subArray.add("Subject");
        if (semester != null && branch != null) {

            VolleyTask.getSubjects(mContext, branch, semester, collegeId, jObj -> {
                JSONArray subJSONArray;
                try {
                    subJSONArray = jObj.getJSONArray("subjects");

                    for (int i = 0; i < subJSONArray.length(); i++) {
                        JSONObject subObj = subJSONArray.getJSONObject(i);
                        subArray.add(subObj.getString("sub_name"));
                    }
                    SpinnerArrayAdapter subjectAdapter = new SpinnerArrayAdapter(mContext,
                            android.R.layout.simple_spinner_dropdown_item,
                            subArray.toArray(new String[0]));
                    subjectSpinner.setAdapter(subjectAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } else
            ExtraUtils.emptySubjectSpinner(mContext, subjectSpinner);
    }
}
