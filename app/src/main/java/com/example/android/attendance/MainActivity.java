package com.example.android.attendance;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
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
import com.google.android.material.textfield.TextInputLayout;

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

        checkLoggedIn();
    }

    private void checkLoggedIn() {
        mSharedPref = SharedPrefManager.getInstance(this);
        if (mSharedPref.isLoggedIn()) {

            String facEmail = mSharedPref.getFacEmail();
            String facName = mSharedPref.getFacName();
            String facDept = mSharedPref.getFacDept();

            setupNavigationDrawer(facName, facEmail, facDept);

            mAdapter = new MainListAdapter(this, new ArrayList<>());
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            DividerItemDecoration divider = new DividerItemDecoration(this,
                    LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.addItemDecoration(divider);
            mRecyclerView.setAdapter(mAdapter);

            refreshList();

        } else {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }


    private void setupNavigationDrawer(String facName, String facEmail, String facDept) {

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
        facIdTv.setText(facEmail);
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
                String facEmail = mSharedPref.getFacEmail();
                Intent scheduleIntent = new Intent(this, ScheduleActivity.class);
                scheduleIntent.putExtra(ExtraUtils.EXTRA_FAC_EMAIL, facEmail);
                startActivity(scheduleIntent);
                break;
            case R.id.nav_logout:
                logout();
                return true;
            case R.id.nav_change_pass:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                showChangePasswordDialog();
                return true;
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
        refreshList();
        super.onResume();
    }

    private void refreshList() {
        VolleyTask.getAttendanceList(this, mSharedPref.getFacEmail(), jObj -> {
            List<AttendanceRecord> records =
                    GsonUtils.extractRecordsFromJSON(jObj);
            mAdapter.swapList(records);

            if (mAdapter.getItemCount() < 1)
                mEmptyView.setVisibility(View.VISIBLE);
            else
                mEmptyView.setVisibility(View.GONE);
        });
    }

    private void logout() {
        mSharedPref.clearCredentials();
        checkLoggedIn();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View dialogView = View.inflate(this, R.layout.change_password_layout, null);

        TextInputLayout currentPassIn = dialogView.findViewById(R.id.current_pass_input);
        TextInputLayout newPassIn = dialogView.findViewById(R.id.new_pass_input);
        TextInputLayout confirmPassIn = dialogView.findViewById(R.id.confirm_pass_input);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();

        dialogView.findViewById(R.id.cp_modify_button).setOnClickListener(view -> {
            if (currentPassIn.getEditText() != null
                    && newPassIn.getEditText() != null
                    && confirmPassIn.getEditText() != null) {

                String currentPass = currentPassIn.getEditText().getText().toString().trim();
                String newPass = newPassIn.getEditText().getText().toString().trim();
                String confirmPass = confirmPassIn.getEditText().getText().toString().trim();

                boolean valid = validatePassInputs(currentPass, newPass, confirmPass,
                        currentPassIn, newPassIn, confirmPassIn);

                if (valid) {
                    VolleyTask.changeFacultyPassword(MainActivity.this, mSharedPref.getFacId(),
                            currentPass, newPass,
                            jObj -> dialog.dismiss());
                }
            }
        });

        dialogView.findViewById(R.id.cp_cancel_button).setOnClickListener(view ->
                dialog.dismiss());
    }

    private boolean validatePassInputs(String currentPass, String newPass, String confirmPass,
                                       TextInputLayout currentPassIn, TextInputLayout newPassIn,
                                       TextInputLayout confirmPassIn) {

        if (TextUtils.isEmpty(currentPass) || currentPass.length() < 8) {
            currentPassIn.setError("Password must contain atleast 8-characters.");
            return false;

        } else if (TextUtils.isEmpty(newPass) || newPass.length() < 8) {
            currentPassIn.setError(null);
            newPassIn.setError("Password must contain atleast 8-characters.");
            return false;

        } else if (TextUtils.isEmpty(confirmPass) || confirmPass.length() < 8) {
            currentPassIn.setError(null);
            newPassIn.setError(null);
            confirmPassIn.setError("Password must contain atleast 8-characters.");
            return false;

        } else if (!newPass.equals(confirmPass)) {
            currentPassIn.setError(null);
            newPassIn.setError(null);
            confirmPassIn.setError("Password doesn't match.");
            return false;

        } else {
            currentPassIn.setError(null);
            newPassIn.setError(null);
            confirmPassIn.setError(null);
            return true;
        }
    }


}
