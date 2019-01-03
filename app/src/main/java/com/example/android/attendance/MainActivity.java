package com.example.android.attendance;

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

import com.example.android.attendance.adapters.MainListAdapter;
import com.example.android.attendance.pojos.AttendanceRecord;
import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.volley.VolleyTask;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    @BindView(R.id.main_list_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_view_main)
    RelativeLayout mEmptyView;

    @BindView(R.id.fab_main_activity)
    FloatingActionButton newAttendFab;

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
            newAttendFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this,
                            NewAttendanceActivity.class));
                }
            });

            mAdapter = new MainListAdapter(this, new ArrayList<AttendanceRecord>());
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            DividerItemDecoration divider = new DividerItemDecoration(this,
                    LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(divider);
            mRecyclerView.setAdapter(mAdapter);
            VolleyTask.setupMainActivity(this, facUserId, mAdapter);

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
                scheduleIntent.putExtra(ExtraUtils.EXTRA_FAC_USER_ID, facUserId);
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

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        VolleyTask.setupMainActivity(this, mSharedPref.getFacUserId(), mAdapter);
        super.onResume();
    }
}
