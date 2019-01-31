package com.example.android.attendance.volley;

import android.app.ProgressDialog;
import android.content.Context;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import com.example.android.attendance.SharedPrefManager;
import com.example.android.attendance.utilities.ExtraUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Akshat Jain on 29-Dec-18.
 */
public class VolleyTask {

    public static void login(final Context context, final String username, final String password,
                             final VolleyCallback volleyCallback) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Logging in...");
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.FAC_LOGIN_URL, response -> {
            pDialog.dismiss();
            try {
                JSONObject jObj = new JSONObject(response);
                if (!jObj.getBoolean("error")) {
                    volleyCallback.onSuccessResponse(jObj);
                } else {
                    Toast.makeText(context, jObj.getString("message"),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            pDialog.dismiss();
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put(SharedPrefManager.FAC_EMAIL, username);
                params.put(SharedPrefManager.FAC_PASSWORD, password);

                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }

    public static void getAttendanceList(final Context context, final String facEmail,
                                         final VolleyCallback callback) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Setting up...");
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.GET_ATT_REC_URL, response -> {
            pDialog.dismiss();
            try {
                JSONObject jObj = new JSONObject(response);

                if (!jObj.getBoolean("error")) {
                    callback.onSuccessResponse(jObj);
                } else
                    Toast.makeText(context, jObj.getString("message"),
                            Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Something went wrong.",
                        Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            pDialog.dismiss();
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(SharedPrefManager.FAC_EMAIL, facEmail);
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }

    public static void getBranchNames(final Context mContext, final int collId,
                                      VolleyCallback callback) {
        ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Setting up...");
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.GET_BRANCH_NAMES_URL, response -> {
            pDialog.dismiss();
            try {
                JSONObject jObj = new JSONObject(response);
                if (!jObj.getBoolean("error")) {
                    callback.onSuccessResponse(jObj);
                } else {
                    Toast.makeText(mContext,
                            jObj.getString("message"),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            pDialog.dismiss();
            Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("college_id", String.valueOf(collId));
                return params;
            }
        };
        RequestHandler.getInstance(mContext).addToRequestQueue(request);
    }

    public static void getSections(final Context mContext, final String branch,
                                   final String semester, final int collId,
                                   VolleyCallback callback) {
        ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Please wait...");
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.GET_SECS_URL,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);

                        if (!jObj.getBoolean("error")) {
                            callback.onSuccessResponse(jObj);
                        } else {
                            Toast.makeText(mContext, jObj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("branch_name", branch);
                params.put("semester", semester);
                params.put("college_id", String.valueOf(collId));
                return params;
            }
        };
        RequestHandler.getInstance(mContext).addToRequestQueue(request);
    }

    public static void getLectureNumbers(Context mContext, String branch, String semester,
                                         String section, int collId, String day, VolleyCallback callback) {

        ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Please wait...");
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.GET_LECTURE_NUMBERS_URL,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);

                        if (!jObj.getBoolean("error")) {
                            callback.onSuccessResponse(jObj);
                        } else {
                            Toast.makeText(mContext, jObj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("branch_name", branch);
                params.put("semester", semester);
                params.put("section", section);
                params.put("college_id", String.valueOf(collId));
                params.put("day", day);
                return params;
            }
        };
        RequestHandler.getInstance(mContext).addToRequestQueue(request);
    }

    public static void checkAttendAlreadyExists(final Context mContext, final String date, final String day,
                                                final String semester, final String branch, final String section,
                                                final String lectNo, final int collegeId, final int lectId,
                                                VolleyCallback callback) {
        ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Please wait...");
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.CHECK_ATTEND_ALREADY_EXIST, response -> {
            pDialog.dismiss();
            try {
                JSONObject jObj = new JSONObject(response);
                if (!jObj.getBoolean("error")) {

                    callback.onSuccessResponse(jObj);

                } else {
                    Toast.makeText(mContext, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(mContext, "Something went wrong.",
                        Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            pDialog.dismiss();
            Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                if (lectId == -1) {
                    params.put("b_name", branch);
                    params.put("college_id", String.valueOf(collegeId));
                    params.put("semester", semester);
                    params.put("section", section);
                    params.put("day", day);
                    params.put("lect_no", lectNo);
                } else {
                    params.put("lect_id", String.valueOf(lectId));
                }
                params.put("date", date);

                return params;
            }
        };
        RequestHandler.getInstance(mContext).addToRequestQueue(request);
    }


    public static void saveAttendance(final Context context, final boolean isUpdateMode,
                                      String attJsonObj, VolleyCallback callback) {
        ProgressDialog pDialog = new ProgressDialog(context);

        if (isUpdateMode) pDialog.setMessage("Updating...");
        else pDialog.setMessage("Saving...");

        pDialog.show();

        String url;
        if (isUpdateMode) url = ExtraUtils.UPDATE_ATTEND_URL;
        else url = ExtraUtils.SAVE_NEW_ATTEND_URL;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        if (!jObj.getBoolean("error")) {
                            callback.onSuccessResponse(jObj);
                        } else {
                            Toast.makeText(context, jObj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
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
        request.setRetryPolicy(new DefaultRetryPolicy(15000,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }

    public static void undoAttendance(final Context context, int recordId, VolleyCallback callback) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.DELETE_RECORD_URL,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);

                        if (!jObj.getBoolean("error")) {
                            callback.onSuccessResponse(jObj);
                        } else {
                            Toast.makeText(context, jObj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("_id", String.valueOf(recordId));
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }


    public static void setupForUpdateAttendance(final Context context, final String attendRecId,
                                                final VolleyCallback callback) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Setting up...");
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.SETUP_UPDATE_ATTEND_URL,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);

                        if (!jObj.getBoolean("error")) {

                            callback.onSuccessResponse(jObj);

                        } else {
                            Toast.makeText(context,
                                    jObj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("attendance_record_id", attendRecId);
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }

    public static void setupForNewAttendance(final Context context, final String lectureNo,
                                             final String classId, final String date,
                                             final String day, final VolleyCallback callback) {

        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Setting up...");
        pDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.SETUP_NEW_ATTEND_URL,
                response -> {
            pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);

                        if (!jObj.getBoolean("error")) {

                            callback.onSuccessResponse(jObj);

                        } else {
                            Toast.makeText(context, jObj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("class_id", classId);
                params.put("lect_no", lectureNo);
                params.put("day", day);
                params.put("date", date);

                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(15000,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }

    public static void showSchedule(final Context context, final String facEmail, final String day,
                                    VolleyCallback callback) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.show();
        final StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.GET_FAC_SCH_URL, response -> {
            pDialog.dismiss();
            try {
                JSONObject jObj = new JSONObject(response);

                if (!jObj.getBoolean("error")) {
                    callback.onSuccessResponse(jObj);
                } else {
                    Toast.makeText(context, jObj.getString("message"),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context, "Something went wrong.",
                        Toast.LENGTH_SHORT).show();
            }

        }, error -> {
            pDialog.dismiss();
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fac_email", facEmail);
                params.put("day", day);
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }

    public static void checkValidClass(final Context mContext, final int collegeId, final String semester,
                                       final String branch, final String section, VolleyCallback callback) {
        ProgressDialog pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Please wait...");
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.CHECK_VALID_CLASS_URL,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);

                        if (!jObj.getBoolean("error")) {
                            callback.onSuccessResponse(jObj);
                        } else {
                            Toast.makeText(mContext, jObj.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Something went wrong.",
                                Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("b_name", branch);
                params.put("college_id", String.valueOf(collegeId));
                params.put("semester", semester);
                params.put("section", section);
                return params;
            }
        };

        RequestHandler.getInstance(mContext).addToRequestQueue(request);
    }

    public static void showReport(final Context context, final int branchId, final int classId,
                                  final int collId, final boolean isDayWise, final String fromDate,
                                  final String toDate, final VolleyCallback callback) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Setting up...");
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.GET_STD_REPORT_URL,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        if (!jObj.getBoolean("error")) {

                            callback.onSuccessResponse(jObj);

                        } else {
                            Toast.makeText(context, jObj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Something went wrong.",
                                Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("class_id", String.valueOf(classId));
                params.put("branch_id", String.valueOf(branchId));
                params.put("college_id", String.valueOf(collId));
                if (isDayWise) {
                    params.put("from_date", fromDate);
                    params.put("to_date", toDate);
                }
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }

    public static void changeFacultyPassword(Context context, int facId, String currentPass,
                                             String newPass, VolleyCallback callback) {
        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Verifying & Updating...");
        pDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST,
                ExtraUtils.CHANGE_FACULTY_PASS_URL,
                response -> {
                    pDialog.dismiss();
                    try {
                        JSONObject jObj = new JSONObject(response);
                        Toast.makeText(context, jObj.getString("message"),
                                Toast.LENGTH_SHORT).show();
                        if (!jObj.getBoolean("error")) {
                            callback.onSuccessResponse(jObj);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            pDialog.dismiss();
            Toast.makeText(context, error.getMessage(),
                    Toast.LENGTH_LONG).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("fac_id", String.valueOf(facId));
                params.put("current_password", currentPass);
                params.put("new_password", newPass);
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(request);
    }
}
