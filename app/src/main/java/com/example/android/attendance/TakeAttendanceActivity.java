package com.example.android.attendance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.attendance.adapters.TakeAttendAdapter;
import com.example.android.attendance.pojos.Attendance;
import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.utilities.GsonUtils;
import com.example.android.attendance.volley.VolleyTask;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class TakeAttendanceActivity extends AppCompatActivity {

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
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String date = bundle.getString(ExtraUtils.EXTRA_DATE);
            String dateDisplay = bundle.getString(ExtraUtils.EXTRA_DISPLAY_DATE);
            String day = bundle.getString(ExtraUtils.EXTRA_DAY);
            String lectNo = bundle.getString(ExtraUtils.EXTRA_LECTURE_NO);
            String classId = bundle.getString(ExtraUtils.EXTRA_CLASS_ID);
            String attendRecId = bundle.getString(ExtraUtils.EXTRA_ATTEND_REC_ID);

            dateTv.setText(dateDisplay);
            dayTv.setText(day);
            lectureTv.setText(ExtraUtils.getLecture(lectNo));

            mAdapter = new TakeAttendAdapter(this, new ArrayList<>());
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            DividerItemDecoration divider = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(divider);
            mRecyclerView.setAdapter(mAdapter);

            if (attendRecId != null) {
                setTitle(getString(R.string.update_attendance_title));
                isUpdateMode = true;
                VolleyTask.setupForUpdateAttendance(this, attendRecId, jObj -> {
                    List<Attendance> records = GsonUtils
                            .extractAttendanceFromJSON(jObj);
                    mAdapter.swapList(records);
                });
            } else {
                setTitle(R.string.take_attendance_title);
                isUpdateMode = false;
                VolleyTask.setupForNewAttendance(this, lectNo, classId, date, day,
                        jObj -> {
                            List<Attendance> records = GsonUtils.extractAttendanceFromJSON(jObj);
                            mAdapter.swapList(records);
                        });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_attendance:
                saveAttendance();
                break;
            case R.id.check_all:
                mAdapter.checkAll();
                break;
            case R.id.uncheck_all:
                mAdapter.unCheckAll();
                break;
            case android.R.id.home:
                if (isUpdateMode) finish();
                else {
                   undoChangesAndFinish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void undoChangesAndFinish() {
        int recordId = TakeAttendAdapter.getmAttendanceList()[1].getAttendanceRecordId();
        VolleyTask.undoAttendance(this, recordId, jObj -> {
            Toast.makeText(this, "Attendance not saved.",
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void saveAttendance() {
        Gson gson = new Gson();
        Attendance[] attendances = TakeAttendAdapter.getmAttendanceList();
        final String attJsonObj = gson.toJson(attendances);
        VolleyTask.saveAttendance(this, isUpdateMode, attJsonObj, jObj -> {
            finish();
            startActivity(new Intent(TakeAttendanceActivity.this, MainActivity.class));
        });
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
                        (dialog1, which) -> dialog1.cancel()).setNegativeButton("Yes",
                        (dialog12, which) -> {

                            setResult(Activity.RESULT_CANCELED);

                            if (isUpdateMode) finish();

                            else undoChangesAndFinish();

                        }).create();
        dialog.show();
    }
}
