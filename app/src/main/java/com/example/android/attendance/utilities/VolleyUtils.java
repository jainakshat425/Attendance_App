package com.example.android.attendance.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.attendance.CheckAttendanceActivity;
import com.example.android.attendance.NewAttendanceActivity;
import com.example.android.attendance.R;
import com.example.android.attendance.StudentReportActivity;
import com.example.android.attendance.TakeAttendanceActivity;
import com.example.android.attendance.adapters.MainListAdapter;
import com.example.android.attendance.adapters.ScheduleAdapter;
import com.example.android.attendance.adapters.ReportAdapter;
import com.example.android.attendance.adapters.SpinnerArrayAdapter;

import com.example.android.attendance.adapters.TakeAttendAdapter;
import com.example.android.attendance.contracts.AttendanceContract.AttendanceEntry;
import com.example.android.attendance.contracts.AttendanceRecordContract.AttendanceRecordEntry;
import com.example.android.attendance.contracts.BranchContract.BranchEntry;
import com.example.android.attendance.contracts.ClassContract.ClassEntry;
import com.example.android.attendance.contracts.FacultyContract.FacultyEntry;

import com.example.android.attendance.contracts.LectureContract.LectureEntry;
import com.example.android.attendance.contracts.SubjectContract.SubjectEntry;
import com.example.android.attendance.network.RequestHandler;
import com.example.android.attendance.pojos.Attendance;
import com.example.android.attendance.pojos.AttendanceRecord;
import com.example.android.attendance.pojos.Report;
import com.example.android.attendance.pojos.Schedule;
import com.example.android.attendance.pojos.SubReport;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Akshat Jain on 29-Dec-18.
 */
public class VolleyUtils {


    public static void setupMainActivity(final Context context, final String facUserId,
                                         final MainListAdapter mAdapter) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.GET_ATT_REC_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);

                            if (!jObj.getBoolean("error")) {
                                List<AttendanceRecord> records =
                                        GsonUtils.extractRecordsFromJSON(jObj);
                                mAdapter.swapList(records);
                            } else {
                                Toast.makeText(context, jObj.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Something went wrong.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(FacultyEntry.F_USERNAME_COL, facUserId);
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }

    public static void setupSemesterSpinner(final Context mContext, final Spinner semesterSpinner,
                                            final ProgressDialog progressDialog) {
        final List<String> semArr = new ArrayList<>();
        semArr.add("Semester");
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.GET_SEMS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);

                            if (!jObj.getBoolean("error")) {

                                JSONArray jsonArray = jObj.getJSONArray("semesters");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    semArr.add(jsonArray.getString(i));
                                }
                                SpinnerArrayAdapter semesterAdapter = new SpinnerArrayAdapter(mContext,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        semArr.toArray(new String[0]));
                                semesterSpinner.setAdapter(semesterAdapter);
                            } else {
                                Toast.makeText(mContext,
                                        jObj.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.getMessage(),
                        Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
        RequestHandler.getInstance(mContext).addToRequestQueue(request);
    }

    public static void setupBranchSpinner(final Context mContext, final Spinner branchSpinner,
                                          final ProgressDialog progressDialog) {
        final List<String> branchArr = new ArrayList<>();
        branchArr.add("Branch");
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.GET_BRANCHES_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);

                            if (!jObj.getBoolean("error")) {

                                JSONArray jsonArray = jObj.getJSONArray("branches");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    branchArr.add(jsonArray.getString(i));
                                }
                                SpinnerArrayAdapter branchAdapter = new SpinnerArrayAdapter(mContext,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        branchArr.toArray(new String[0]));
                                branchSpinner.setAdapter(branchAdapter);
                            } else {
                                Toast.makeText(mContext,
                                        jObj.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            if (progressDialog != null) progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, error.getMessage(),
                        Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
        RequestHandler.getInstance(mContext).addToRequestQueue(request);
    }

    public static void setupSectionSpinner(final Context mContext, final Spinner sectionSpinner,
                                           final ProgressDialog progressDialog, final String branchSelected,
                                           final String semesterSelected) {
        if (semesterSelected != null && branchSelected != null) {
            final List<String> secArr = new ArrayList<>();
            secArr.add("Section");
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST,
                    ExtraUtils.GET_SECS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jObj = new JSONObject(response);

                                if (!jObj.getBoolean("error")) {

                                    JSONArray jsonArray = jObj.getJSONArray("sections");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        secArr.add(jsonArray.getString(i));
                                    }
                                    SpinnerArrayAdapter sectionAdapter = new SpinnerArrayAdapter(mContext,
                                            android.R.layout.simple_spinner_dropdown_item,
                                            secArr.toArray(new String[0]));
                                    sectionSpinner.setAdapter(sectionAdapter);
                                } else {
                                    Toast.makeText(mContext, jObj.getString("message"),
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } finally {
                                progressDialog.dismiss();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, error.getMessage(),
                            Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("branch_name", branchSelected);
                    params.put("semester", semesterSelected);
                    return params;
                }
            };
            RequestHandler.getInstance(mContext).addToRequestQueue(request);
        } else
            ExtraUtils.emptySectionSpinner(mContext, sectionSpinner);
    }

    public static void setupSubjectSpinner(final Context mContext, final Spinner subjectSpinner,
                                           final ProgressDialog progressDialog, final String branchSelected,
                                           final String semesterSelected) {
        if (semesterSelected != null && branchSelected != null) {
            final List<String> subArray = new ArrayList<>();
            subArray.add("Subject");
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            StringRequest request = new StringRequest(Request.Method.POST,
                    ExtraUtils.GET_SUB_NAME_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jObj = new JSONObject(response);

                                if (!jObj.getBoolean("error")) {

                                    JSONArray subJSONArray = jObj.getJSONArray("subjects");
                                    for (int i = 0; i < subJSONArray.length(); i++) {
                                        JSONObject subObj = subJSONArray.getJSONObject(i);
                                        subArray.add(subObj.getString("sub_name"));
                                    }
                                    SpinnerArrayAdapter subjectAdapter = new SpinnerArrayAdapter(mContext,
                                            android.R.layout.simple_spinner_dropdown_item,
                                            subArray.toArray(new String[0]));
                                    subjectSpinner.setAdapter(subjectAdapter);

                                } else {
                                    Toast.makeText(mContext,
                                            jObj.getString("message"),
                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(mContext, error.getMessage(),
                            Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(BranchEntry.BRANCH_NAME, branchSelected);
                    params.put(SubjectEntry.SUB_SEMESTER_COL, semesterSelected);
                    return params;
                }
            };
            RequestHandler.getInstance(mContext).addToRequestQueue(request);
        } else
            ExtraUtils.emptySubjectSpinner(mContext, subjectSpinner);
    }

    public static void takeNewAttendance(final Context mContext, final String date, final String day,
                                         final String semester, final String branch, final String section,
                                         final String subject, final String lectNo, final int collegeId,
                                         final String dateDisplay) {

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.CHECK_ATTEND_ALREADY_EXIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);

                            if (!jObj.getBoolean("error")) {

                                int classId = jObj.getInt("class_id");
                                int branchId = jObj.getInt("branch_id");
                                int lectId = jObj.getInt("lect_id");

                                Intent i = new Intent();
                                i.setClass(mContext, TakeAttendanceActivity.class);
                                i.putExtra(ExtraUtils.EXTRA_DATE, date);
                                i.putExtra(ExtraUtils.EXTRA_DISPLAY_DATE, dateDisplay);
                                i.putExtra(ExtraUtils.EXTRA_DAY, day);
                                i.putExtra(ExtraUtils.EXTRA_SEMESTER, semester);
                                i.putExtra(ExtraUtils.EXTRA_BRANCH, branch);
                                i.putExtra(ExtraUtils.EXTRA_SECTION, section);
                                i.putExtra(ExtraUtils.EXTRA_SUBJECT, subject);
                                i.putExtra(ExtraUtils.EXTRA_LECTURE_NO, lectNo);
                                i.putExtra(ExtraUtils.EXTRA_CLASS_ID, String.valueOf(classId));
                                i.putExtra(ExtraUtils.EXTRA_BRANCH_ID, String.valueOf(branchId));

                                ((NewAttendanceActivity) mContext)
                                        .startActivityForResult(i,
                                                NewAttendanceActivity.TAKE_ATTENDANCE_REQ_CODE);
                            } else {
                                RelativeLayout parentLayout = ((NewAttendanceActivity) mContext)
                                        .findViewById(R.id.relative_layout);
                                Snackbar.make(parentLayout, jObj.getString("message"),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "Something went wrong.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(BranchEntry.BRANCH_NAME, branch);
                params.put(ClassEntry.COLLEGE_ID, String.valueOf(collegeId));
                params.put(ClassEntry.SEMESTER, semester);
                params.put(ClassEntry.SECTION, section);
                params.put(LectureEntry.LECTURE_DAY, day);
                params.put(LectureEntry.LECTURE_NUMBER, lectNo);
                params.put(AttendanceRecordEntry.DATE_COL, date);
                return params;
            }
        };

        RequestHandler.getInstance(mContext).addToRequestQueue(request);
    }


    public static void saveAttendance(final Context context, final boolean isUpdateMode) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        Gson gson = new Gson();
        Attendance[] attendances = TakeAttendAdapter.getAttendanceList();
        final String attJsonObj = gson.toJson(attendances);

        String url;
        if (isUpdateMode)
            url = ExtraUtils.UPDATE_ATTEND_URL;
        else
            url = ExtraUtils.SAVE_NEW_ATTEND_URL;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);

                            if (!jObj.getBoolean("error")) {

                                ((TakeAttendanceActivity) context).setResult(Activity.RESULT_OK);
                                ((TakeAttendanceActivity) context).finish();

                            } else {
                                Toast.makeText(context, jObj.getString("message"),
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
                Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if (isUpdateMode)
                    params.put("update_attendance", attJsonObj);
                else
                    params.put("new_attendance", attJsonObj);
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }

    public static void undoAttendanceAndFinish(final Context context) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
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
                                Toast.makeText(context, "Attendance not saved.",
                                        Toast.LENGTH_SHORT).show();
                                ((TakeAttendanceActivity) context).finish();
                            } else {
                                Toast.makeText(context, jObj.getString("message"),
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
                Toast.makeText(context,
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
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }


    public static void setupForUpdateAttendance(final Context context, final String attendRecId,
                                                final TakeAttendAdapter mAdapter) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
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

                                List<Attendance> records = GsonUtils
                                        .extractAttendanceFromJSON(jObj);
                                mAdapter.swapList(records);

                            } else {
                                Toast.makeText(context,
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
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(AttendanceEntry.ATTENDANCE_RECORD_ID, attendRecId);
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }

    public static void setupForNewAttendance(final Context context, final String lectureNo,
                                             final String classId, final String date,
                                             final String day, final TakeAttendAdapter mAdapter) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
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

                                List<Attendance> records = GsonUtils.extractAttendanceFromJSON(jObj);
                                mAdapter.swapList(records);

                            } else {
                                Toast.makeText(context, jObj.getString("message"),
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
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
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
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }

    public static void showSchedule(final Context context, final String facUserId, final String day,
                                         final ScheduleAdapter mAdapter, final RelativeLayout emptyView) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.GET_FAC_SCH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        List<Schedule> schedules = null;
                        try {
                            JSONObject jObj = new JSONObject(response);

                            if (!jObj.getBoolean("error")) {
                                schedules = GsonUtils.extractScheduleFromJSON(jObj);
                            } else {
                                Toast.makeText(context, jObj.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Something went wrong.",
                                    Toast.LENGTH_SHORT).show();
                        } finally {
                            mAdapter.swapList(schedules);

                            if(schedules == null || schedules.size() < 1)
                                emptyView.setVisibility(View.VISIBLE);
                            else
                                emptyView.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                mAdapter.swapList(null);
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(LectureEntry.FAC_USER_ID, facUserId);
                params.put(LectureEntry.LECTURE_DAY, day);
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }

    public static void checkValidClass(final Context mContext, final int collegeId, final String semester,
                                       final String branch, final String section) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.CHECK_VALID_CLASS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jObj = new JSONObject(response);

                            if (!jObj.getBoolean("error")) {

                                int classId = jObj.getInt("class_id");
                                int branchId = jObj.getInt("branch_id");

                                Intent i = new Intent();
                                i.setClass(mContext, StudentReportActivity.class);
                                i.putExtra(ExtraUtils.EXTRA_SEMESTER, semester);
                                i.putExtra(ExtraUtils.EXTRA_BRANCH, branch);
                                i.putExtra(ExtraUtils.EXTRA_SECTION, section);
                                i.putExtra(ExtraUtils.EXTRA_COLLEGE_ID, collegeId);
                                i.putExtra(ExtraUtils.EXTRA_CLASS_ID, classId);
                                i.putExtra(ExtraUtils.EXTRA_BRANCH_ID, branchId);

                                mContext.startActivity(i);
                            } else {
                                LinearLayout parentLayout = ((CheckAttendanceActivity) mContext)
                                        .findViewById(R.id.check_attendance_linear_layout);
                                Snackbar.make(parentLayout, jObj.getString("message"),
                                        Snackbar.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(mContext, "Something went wrong.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(BranchEntry.BRANCH_NAME, branch);
                params.put(ClassEntry.COLLEGE_ID, String.valueOf(collegeId));
                params.put(ClassEntry.SEMESTER, semester);
                params.put(ClassEntry.SECTION, section);
                return params;
            }
        };

        RequestHandler.getInstance(mContext).addToRequestQueue(request);
    }

    public static void showReport(final Context context, final Bundle classDetails,
                                  final ReportAdapter mAdapter, final FloatingActionButton saveFab) {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.GET_STD_REPORT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JSONObject jObj = new JSONObject(response);

                            if (!jObj.getBoolean("error")) {
                                final List<Report> reports = GsonUtils.extractReportsFromJson(jObj);
                                final List<SubReport> subReports = GsonUtils.extractSubReportsFromJson(jObj);
                                final int attendTaken = jObj.getInt("attend_taken");
                                final String collName = jObj.getString("coll_full_name");

                                ((StudentReportActivity)context).showSubReport(subReports);
                                mAdapter.swapList(reports, attendTaken);

                                //setup savePdf button after getting report of all the students
                                saveFab.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        PdfUtils.generatePdf(context, reports, subReports,
                                                attendTaken, collName, classDetails);
                                    }
                                });
                            } else {
                                Toast.makeText(context, jObj.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Something went wrong.",
                                    Toast.LENGTH_SHORT).show();
                        } finally {
                            progressDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                int branchId = classDetails.getInt(ExtraUtils.EXTRA_BRANCH_ID);
                int classId = classDetails.getInt(ExtraUtils.EXTRA_CLASS_ID);
                int collId = classDetails.getInt(ExtraUtils.EXTRA_COLLEGE_ID);

                Map<String, String> params = new HashMap<>();
                params.put("class_id", String.valueOf(classId));
                params.put("branch_id", String.valueOf(branchId));
                params.put("college_id", String.valueOf(collId));
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }
}
