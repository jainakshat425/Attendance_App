package com.example.android.attendance;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.attendance.adapters.MainListCursorAdapter;
import com.example.android.attendance.contracts.FacultyContract.FacultyEntry;
import com.example.android.attendance.data.DatabaseHelper;
import com.example.android.attendance.data.DbHelperMethods;
import com.example.android.attendance.sync.ReminderUtilities;
import com.example.android.attendance.utilities.ExtraUtils;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView facNameTv;
    private TextView facDeptTv;
    private TextView facIdTv;

    private DrawerLayout mDrawerLayout;

    private ListView mainListView;
    private MainListCursorAdapter cursorAdapter;

    private static final int NEW_ATTENDANCE_REQUEST_CODE = 1;
    private static final int UPDATE_ATTENDANCE_REQ_CODE = 2;
    private static final int LOGIN_REQUEST_CODE = 4;

    private DatabaseHelper mDatabaseHelper;

    private SharedPreferences mPreferences;
    private String facUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

       setupMainActivity();
    }

    private void setupMainActivity() {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (mPreferences.contains(ExtraUtils.EXTRA_FAC_USER_ID)) {
            facUserId = mPreferences.getString(ExtraUtils.EXTRA_FAC_USER_ID, "");
        }
        if (facUserId.isEmpty() || facUserId.equals("")) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, LOGIN_REQUEST_CODE);
        } else {
            ExtraUtils.updateWidget(this);

            mDatabaseHelper = new DatabaseHelper(this);
            String selection = FacultyEntry.F_USER_ID_COL + "=?";
            String[] selectionArgs = {facUserId};
            Cursor facCursor = mDatabaseHelper.openDataBaseReadOnly()
                    .query(FacultyEntry.TABLE_NAME,
                            null,
                            selection,
                            selectionArgs, null,null,null);

            if (facCursor.getCount() != 0 && facCursor.moveToFirst()) {
                facCursor.moveToFirst();
                String name = facCursor.getString(facCursor.getColumnIndexOrThrow(FacultyEntry.F_NAME_COL));
                String dept = facCursor.getString(facCursor.getColumnIndexOrThrow
                        (FacultyEntry.F_DEPARTMENT_COL));

                setupNavigationDrawer(name, facUserId, dept);

                mainListView = findViewById(R.id.main_list_view);

                RelativeLayout emptyView = findViewById(R.id.empty_view_main);
                mainListView.setEmptyView(emptyView);


                Cursor cursor = DbHelperMethods.getAttendanceRecordsCursor(this, facUserId);
                cursorAdapter = new MainListCursorAdapter(this, cursor);
                mainListView.setAdapter(cursorAdapter);

                setupFloatingActionButton(facUserId);
            }
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

        facNameTv = (TextView) drawerFacDetails.findViewById(R.id.fac_name_tv);
        facDeptTv = (TextView) drawerFacDetails.findViewById(R.id.fac_dept_tv);
        facIdTv = (TextView) drawerFacDetails.findViewById(R.id.fac_id_tv);

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
        if (requestCode == LOGIN_REQUEST_CODE && resultCode == Activity.RESULT_OK)
               setupMainActivity();
        else if (requestCode == LOGIN_REQUEST_CODE && resultCode == Activity.RESULT_CANCELED)
            finish();
        else {
            String userId = mPreferences.getString(ExtraUtils.EXTRA_FAC_USER_ID, "");
            if (!userId.isEmpty() || !userId.equals("")) {
                Cursor cursor = DbHelperMethods.getAttendanceRecordsCursor(this,
                        userId);
                cursorAdapter.swapCursor(cursor);
                cursorAdapter.notifyDataSetChanged();
            } else {
                RelativeLayout parentLayout = findViewById(R.id.main_layout);
                Snackbar.make(parentLayout, "Something Went Wrong!", Snackbar.LENGTH_LONG).show();
            }
        }
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

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
