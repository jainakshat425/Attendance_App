package com.example.android.attendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.android.attendance.adapters.MainListAdapter;
import com.example.android.attendance.contracts.FacultyContract.FacultyEntry;
import com.example.android.attendance.network.RequestHandler;
import com.example.android.attendance.sync.ReminderUtilities;
import com.example.android.attendance.utilities.ExtraUtils;
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


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView facNameTv;
    private TextView facDeptTv;
    private TextView facIdTv;

    private DrawerLayout mDrawerLayout;

    @BindView(R.id.main_list_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_view_main)
    RelativeLayout mEmptyView;

    private MainListAdapter mAdapter;

    private static final int NEW_ATTENDANCE_REQUEST_CODE = 1;
    private static final int UPDATE_ATTENDANCE_REQ_CODE = 2;

    private SharedPrefManager mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        mSharedPref = SharedPrefManager.getInstance(this);
        if (mSharedPref.isLoggedIn()) {
            setupMainActivity();
        } else {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void setupMainActivity() {

        int facId = mSharedPref.getFacId();
        final String facUserId = mSharedPref.getFacUserId();
        String facName = mSharedPref.getFacName();
        String facDept = mSharedPref.getFacDept();

        ExtraUtils.updateWidget(this);
        setupNavigationDrawer(facName, facUserId, facDept);

        mAdapter = new MainListAdapter(this, new ArrayList<AttendanceRecord>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration divider = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(divider);
        mRecyclerView.setAdapter(mAdapter);

        final ProgressDialog progressDialog = new ProgressDialog(this);
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

                                Toast.makeText(MainActivity.this, jObj.getString("message"),
                                        Toast.LENGTH_SHORT).show();

                                List<AttendanceRecord> records = extractRecordsFromJSON(jObj);

                                mAdapter.swapList(records);

                                setupFloatingActionButton(facUserId);

                            } else {
                                Toast.makeText(MainActivity.this, jObj.getString("message"),
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
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put(FacultyEntry.F_USERNAME_COL, facUserId);

                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(request);
    }

    private List<AttendanceRecord> extractRecordsFromJSON(JSONObject jObj) {

        try {
            String recordsArray = jObj.getString("attendanceRecord");

            Gson gson = new Gson();
            AttendanceRecord[] targetArray = gson.fromJson(recordsArray, AttendanceRecord[].class);

            return Arrays.asList(targetArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void setupNavigationDrawer(String facName, String facUserId, String facDept) {

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


        View drawerFacDetails = navigationView.getHeaderView(0);

        facNameTv = drawerFacDetails.findViewById(R.id.fac_name_tv);
        facDeptTv = drawerFacDetails.findViewById(R.id.fac_dept_tv);
        facIdTv = drawerFacDetails.findViewById(R.id.fac_id_tv);

        facNameTv.setText(facName);
        facDeptTv.setText(facDept);
        facIdTv.setText(facUserId);

        ReminderUtilities.scheduleAttendanceReminder(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * initialise floatingActionButton and link it to newAttendanceActivity
     */
    private void setupFloatingActionButton(final String facUserId) {

        FloatingActionButton newAttendanceFab = findViewById(R.id.fab_main_activity);
        newAttendanceFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newAttendanceIntent = new Intent();
                newAttendanceIntent.setClass
                        (MainActivity.this, NewAttendanceActivity.class);
                newAttendanceIntent.putExtra(ExtraUtils.EXTRA_FAC_USER_ID, facUserId);
                startActivityForResult(newAttendanceIntent, NEW_ATTENDANCE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    public static int getUpdateAttendanceReqCode() {
        return UPDATE_ATTENDANCE_REQ_CODE;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_check_attendance:
                Intent checkAttendanceIntent = new Intent(this, CheckAttendanceActivity.class);
                startActivity(checkAttendanceIntent);
                break;
            case R.id.nav_schedule:
                String facUserId = mSharedPref.getFacUserId();
                Intent scheduleIntent = new Intent(this, ScheduleActivity.class);
                scheduleIntent.putExtra(ExtraUtils.EXTRA_FAC_USER_ID, facUserId);
                startActivity(scheduleIntent);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
