package com.example.android.attendance;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.attendance.adapters.MainListAdapter;
import com.example.android.attendance.pojos.AttendanceRecord;
import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.utilities.GsonUtils;
import com.example.android.attendance.volley.VolleyCallback;
import com.example.android.attendance.volley.VolleyTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    @BindView(R.id.main_list_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_view_main)
    RelativeLayout mEmptyView;

    private MainListAdapter mAdapter;

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

            int facId = mSharedPref.getFacId();
            final String facUserId = mSharedPref.getFacUserId();
            String facName = mSharedPref.getFacName();
            String facDept = mSharedPref.getFacDept();

            setupNavigationDrawer(facName, facUserId, facDept);

            mAdapter = new MainListAdapter(this, new ArrayList<AttendanceRecord>());
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            DividerItemDecoration divider = new DividerItemDecoration(this,
                    LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(divider);
            mRecyclerView.setAdapter(mAdapter);
            VolleyTask.getAttendanceList(this, facUserId, new VolleyCallback() {
                @Override
                public void onSuccessResponse(JSONObject jObj) {
                    List<AttendanceRecord> records =
                            GsonUtils.extractRecordsFromJSON(jObj);
                    mAdapter.swapList(records);
                }
            });

            //ReminderUtilities.scheduleAttendanceReminder(this);

        } else {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    private void setupNavigationDrawer(String facName, String facUserId, String facDept) {

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        View drawerFacDetails = navigationView.getHeaderView(0);

        TextView facNameTv = drawerFacDetails.findViewById(R.id.fac_name_tv);
        TextView facDeptTv = drawerFacDetails.findViewById(R.id.fac_dept_tv);
        TextView facIdTv = drawerFacDetails.findViewById(R.id.fac_id_tv);

        facNameTv.setText(facName);
        facDeptTv.setText(facDept);
        facIdTv.setText(facUserId);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_check_attendance:
                Intent checkAttendanceIntent = new Intent(this,
                        CheckAttendanceActivity.class);
                startActivity(checkAttendanceIntent);
                break;
            case R.id.nav_schedule:
                String facUserId = mSharedPref.getFacUserId();
                Intent scheduleIntent = new Intent(this, ScheduleActivity.class);
                scheduleIntent.putExtra(ExtraUtils.EXTRA_FAC_EMAIL, facUserId);
                startActivity(scheduleIntent);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_new_attend:
                startActivity(new Intent(MainActivity.this,
                        NewAttendanceActivity.class));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        VolleyTask.getAttendanceList(this, mSharedPref.getFacUserId(), new VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject jObj) {
                List<AttendanceRecord> records =
                        GsonUtils.extractRecordsFromJSON(jObj);
                mAdapter.swapList(records);
            }
        });
        super.onResume();
    }
}
