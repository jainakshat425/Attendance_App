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
import com.example.android.attendance.contracts.CollegeContract.CollegeEntry;
import com.example.android.attendance.contracts.LectureContract.LectureEntry;
import com.example.android.attendance.network.RequestHandler;
import com.example.android.attendance.pojos.Attendance;
import com.example.android.attendance.utilities.ExtraUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TakeAttendanceActivity extends AppCompatActivity {

    private Context mContext;

    private TextView collegeTv;
    private TextView semesterTv;
    private TextView branchTv;
    private TextView sectionTv;
    private TextView subjectTv;
    private TextView dateTv;
    private TextView lectureTv;
    private TextView dayTv;

    private RecyclerView mRecyclerView;
    private TakeAttendAdapter mAdapter;

    private Bundle bundle;
    private String attendRecId;

    private boolean isUpdateMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        mContext = this;
        /**
         * initialize all text views
         */
        initializeAllViews();
        /**
         * get data from intent i.e. the activity which called this..
         */
        bundle = getIntent().getExtras();

        String collegeId = bundle.getString(ExtraUtils.EXTRA_COLLEGE_ID);
        String date = bundle.getString(ExtraUtils.EXTRA_DATE);
        String day = bundle.getString(ExtraUtils.EXTRA_DAY);
        String semester = bundle.getString(ExtraUtils.EXTRA_SEMESTER);
        String branch = bundle.getString(ExtraUtils.EXTRA_BRANCH);
        String section = bundle.getString(ExtraUtils.EXTRA_SECTION);
        String subject = bundle.getString(ExtraUtils.EXTRA_SUBJECT);
        String lecture = bundle.getString(ExtraUtils.EXTRA_LECTURE);
        String classId = bundle.getString(ExtraUtils.EXTRA_CLASS_ID);
        attendRecId = bundle.getString(ExtraUtils.EXTRA_ATTEND_REC_ID);

        String collegeString = (Integer.parseInt(collegeId) == CollegeEntry.COLLEGE_GCT) ?
                getString(R.string.college_gct) : getString(R.string.college_git);

        /**
         * populate the text views with the data from the intent
         */
        collegeTv.setText(collegeString);
        branchTv.setText(branch);
        sectionTv.setText(section);
        subjectTv.setText(subject);
        dateTv.setText(date);
        dayTv.setText(String.format("%s,", day));
        semesterTv.setText(ExtraUtils.getSemester(semester));
        lectureTv.setText(ExtraUtils.getLecture(lecture));

        mAdapter = new TakeAttendAdapter(this, new ArrayList<Attendance>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration divider = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setAdapter(mAdapter);

        if (attendRecId != null) {
            //changes activity title
            setTitle(getString(R.string.update_attendance_title));
            isUpdateMode = true;
            setupForUpdateAttendance(attendRecId);
        } else {
            setTitle(R.string.take_attendance_title);
            isUpdateMode = false;

            setupForNewAttendance(lecture, classId, date, day);

        }

    }

    private void setupForUpdateAttendance(final String attendRecId) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.SETUP_UPDATE_ATTEND_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);

                            if (!jObj.getBoolean("error")) {

                                List<Attendance> records = extractAttendanceFromJSON(jObj);
                                mAdapter.swapList(records);

                            } else {
                                Toast.makeText(TakeAttendanceActivity.this, jObj.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(TakeAttendanceActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put(AttendanceEntry.ATTENDANCE_RECORD_ID, attendRecId);

                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
    }

    private void setupForNewAttendance(final String lectureNo, final String classId,
                                       final String date, final String day) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.SETUP_NEW_ATTEND_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);

                            if (!jObj.getBoolean("error")) {

                                List<Attendance> records = extractAttendanceFromJSON(jObj);
                                mAdapter.swapList(records);

                            } else {
                                Toast.makeText(TakeAttendanceActivity.this, jObj.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(TakeAttendanceActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put(LectureEntry.CLASS_ID, classId);
                params.put(LectureEntry.LECTURE_NUMBER, lectureNo);
                params.put(LectureEntry.LECTURE_DAY, day);
                params.put(AttendanceRecordEntry.DATE_COL, date);

                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
    }

    private List<Attendance> extractAttendanceFromJSON(JSONObject jObj) {
        try {
            String recordsArray = jObj.getString("attendance");

            Gson gson = new Gson();
            Attendance[] targetArray = gson.fromJson(recordsArray, Attendance[].class);

            return Arrays.asList(targetArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_attendance:
                saveAttendance();
            case android.R.id.home:
                if (isUpdateMode) finish();
                else undoAttendanceAndFinish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void undoAttendanceAndFinish() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        final int recId = TakeAttendAdapter.getAttendanceList()[1].getAttendanceRecordId();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.DELETE_RECORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);

                            if (!jObj.getBoolean("error")) {
                                finish();
                            } else {
                                Toast.makeText(TakeAttendanceActivity.this,
                                        jObj.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(TakeAttendanceActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put(AttendanceRecordEntry.ID, String.valueOf(recId));

                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(request);
    }

    private void saveAttendance() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        Gson gson = new Gson();
        Attendance[] attendances = TakeAttendAdapter.getAttendanceList();
        final String attJsonObj = gson.toJson(attendances);

        if (isUpdateMode) {
            StringRequest request = new StringRequest(Request.Method.POST,
                    ExtraUtils.UPDATE_ATTEND_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jObj = new JSONObject(response);

                                if (!jObj.getBoolean("error")) {

                                    // ExtraUtils.updateWidget(this);
                                    Intent intent = new Intent();
                                    intent.putExtras(bundle);

                                    setResult(Activity.RESULT_OK, intent);
                                    finish();

                                } else {
                                    Toast.makeText(TakeAttendanceActivity.this,
                                            jObj.getString("message"),
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(TakeAttendanceActivity.this,
                            error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    params.put("update_attendance", attJsonObj);

                    return params;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(request);
        } else {
            StringRequest request = new StringRequest(Request.Method.POST,
                    ExtraUtils.SAVE_NEW_ATTEND_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jObj = new JSONObject(response);

                                if (!jObj.getBoolean("error")) {
                                    // ExtraUtils.updateWidget(this);
                                    Intent intent = new Intent();
                                    intent.putExtras(bundle);

                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                } else {
                                    Toast.makeText(TakeAttendanceActivity.this,
                                            jObj.getString("message"),
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(TakeAttendanceActivity.this,
                            error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    params.put("new_attendance", attJsonObj);

                    return params;
                }
            };
            RequestHandler.getInstance(this).addToRequestQueue(request);
        }
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
                                if (isUpdateMode)
                                    finish();
                                // else
                                //undoAttendanceAndFinish();
                            }
                        }).create();
        dialog.show();
    }

    private void initializeAllViews() {
        collegeTv = findViewById(R.id.college_text_view);
        semesterTv = findViewById(R.id.semester_text_view);
        branchTv = findViewById(R.id.branch_text_view);
        sectionTv = findViewById(R.id.section_text_view);
        subjectTv = findViewById(R.id.subject_text_view);
        dateTv = findViewById(R.id.date_text_view);
        lectureTv = findViewById(R.id.lecture_text_view);
        dayTv = findViewById(R.id.day_text_view);

        mRecyclerView = findViewById(R.id.students_list_view);
    }


}
