package com.example.android.attendance.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.attendance.adapters.MainListAdapter;
import com.example.android.attendance.contracts.FacultyContract.FacultyEntry;
import com.example.android.attendance.network.RequestHandler;
import com.example.android.attendance.pojos.AttendanceRecord;

import org.json.JSONException;
import org.json.JSONObject;

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

                                Toast.makeText(context, jObj.getString("message"),
                                        Toast.LENGTH_SHORT).show();

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
}
