package com.example.android.attendance;

import android.app.Activity;
import android.content.DialogInterface;
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

import com.example.android.attendance.adapters.TakeAttendAdapter;
import com.example.android.attendance.pojos.Attendance;
import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.utilities.GsonUtils;
import com.example.android.attendance.volley.VolleyCallback;
import com.example.android.attendance.volley.VolleyTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    private TakeAttendAdapter mAdapter;

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

        if (bundle != null) {
            String date = bundle.getString(ExtraUtils.EXTRA_DATE);
            String dateDisplay = bundle.getString(ExtraUtils.EXTRA_DISPLAY_DATE);
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
            dateTv.setText(dateDisplay);
            dayTv.setText(String.format("%s,", day));
            semesterTv.setText(ExtraUtils.getSemester(semester));
            lectureTv.setText(ExtraUtils.getLecture(lectNo));

            mAdapter = new TakeAttendAdapter(this, new ArrayList<Attendance>());
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            DividerItemDecoration divider = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(divider);
            mRecyclerView.setAdapter(mAdapter);

            if (attendRecId != null) {
                setTitle(getString(R.string.update_attendance_title));
                isUpdateMode = true;
                VolleyTask.setupForUpdateAttendance(this, attendRecId, new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(JSONObject jObj) {
                        List<Attendance> records = GsonUtils
                                .extractAttendanceFromJSON(jObj);
                        mAdapter.swapList(records);
                    }
                });
            } else {
                setTitle(R.string.take_attendance_title);
                isUpdateMode = false;
                VolleyTask.setupForNewAttendance(this, lectNo, classId, date, day,
                        new VolleyCallback() {
                            @Override
                            public void onSuccessResponse(JSONObject jObj) {
                                List<Attendance> records = GsonUtils.extractAttendanceFromJSON(jObj);
                                mAdapter.swapList(records);
                            }
                        });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_attendance:
                VolleyTask.saveAttendance(this, isUpdateMode);
                break;
            case R.id.check_all:
                mAdapter.checkAll();
                break;
            case R.id.uncheck_all:
                mAdapter.unCheckAll();
                break;
            case android.R.id.home:
                if (isUpdateMode) finish();
                else VolleyTask.undoAttendanceAndFinish(this);
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

        getMenuInflater().inflate(R.menu.menu_take_attendance, menu);

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
                                else VolleyTask.undoAttendanceAndFinish(TakeAttendanceActivity.this);
                            }
                        }).create();
        dialog.show();
    }
}
