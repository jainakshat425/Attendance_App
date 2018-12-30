package com.example.android.attendance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.attendance.adapters.TakeAttendAdapter;
import com.example.android.attendance.contracts.AttendanceContract.AttendanceEntry;
import com.example.android.attendance.contracts.AttendanceRecordContract.AttendanceRecordEntry;
import com.example.android.attendance.contracts.LectureContract.LectureEntry;
import com.example.android.attendance.network.RequestHandler;
import com.example.android.attendance.pojos.Attendance;
import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.utilities.GsonUtils;
import com.example.android.attendance.utilities.VolleyUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TakeAttendanceActivity extends AppCompatActivity {

    @BindView(R.id.college_text_view)
    TextView collegeTv;
    @BindView(R.id.semester_text_view)
    TextView semesterTv;
    @BindView(R.id.branch_text_view)
    TextView branchTv;
    @BindView(R.id.section_text_view)
    TextView sectionTv;
    @BindView(R.id.subject_text_view)
    TextView subjectTv;
    @BindView(R.id.date_text_view)
    TextView dateTv;
    @BindView(R.id.lecture_text_view)
    TextView lectureTv;
    @BindView(R.id.day_text_view)
    TextView dayTv;

    @BindView(R.id.students_list_view)
    RecyclerView mRecyclerView;

    private boolean isUpdateMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        String date = bundle.getString(ExtraUtils.EXTRA_DATE);
        String day = bundle.getString(ExtraUtils.EXTRA_DAY);
        String semester = bundle.getString(ExtraUtils.EXTRA_SEMESTER);
        String branch = bundle.getString(ExtraUtils.EXTRA_BRANCH);
        String section = bundle.getString(ExtraUtils.EXTRA_SECTION);
        String subject = bundle.getString(ExtraUtils.EXTRA_SUBJECT);
        String lectNo = bundle.getString(ExtraUtils.EXTRA_LECTURE_NO);
        String classId = bundle.getString(ExtraUtils.EXTRA_CLASS_ID);
        String attendRecId = bundle.getString(ExtraUtils.EXTRA_ATTEND_REC_ID);


        /**
         * populate the text views with the data from the intent
         */
        collegeTv.setText("GIT");
        branchTv.setText(branch);
        sectionTv.setText(section);
        subjectTv.setText(subject);
        dateTv.setText(date);
        dayTv.setText(String.format("%s,", day));
        semesterTv.setText(ExtraUtils.getSemester(semester));
        lectureTv.setText(ExtraUtils.getLecture(lectNo));

        TakeAttendAdapter mAdapter = new TakeAttendAdapter(this, new ArrayList<Attendance>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration divider = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setAdapter(mAdapter);

        if (attendRecId != null) {
            setTitle(getString(R.string.update_attendance_title));
            isUpdateMode = true;
            VolleyUtils.setupForUpdateAttendance(this, attendRecId, mAdapter);
        } else {
            setTitle(R.string.take_attendance_title);
            isUpdateMode = false;
            VolleyUtils.setupForNewAttendance(this, lectNo, classId, date, day, mAdapter);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_attendance:
                VolleyUtils.saveAttendance(this, isUpdateMode);
                break;
            case android.R.id.home:
                if (isUpdateMode) finish();
                else VolleyUtils.undoAttendanceAndFinish(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_take_attendance_activity, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void showAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Attendance will be lost!")
                .setMessage("Do you want to exit?")
                .setPositiveButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setNegativeButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(Activity.RESULT_CANCELED);
                                if (isUpdateMode) finish();
                                else VolleyUtils.undoAttendanceAndFinish(TakeAttendanceActivity.this);
                            }
                        }).create();
        dialog.show();
    }
}
