package com.example.android.attendance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.RelativeLayout;

import com.example.android.attendance.adapters.ScheduleAdapter;
import com.example.android.attendance.pojos.Schedule;
import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.utilities.GsonUtils;
import com.example.android.attendance.volley.VolleyTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.antonious.materialdaypicker.MaterialDayPicker;
import ca.antonious.materialdaypicker.SingleSelectionMode;

public class ScheduleActivity extends AppCompatActivity {

    private List<Schedule> mSchedules;
    private String facUserId;
    private String currentDay;
    private ScheduleAdapter mAdapter;

    @BindView(R.id.sch_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.sch_empty_view)
    RelativeLayout emptyView;

    @BindView(R.id.no_network_view)
    RelativeLayout noNetworkLayout;

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isNotConnected = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (isNotConnected) {
                noNetworkLayout.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
            } else {
                noNetworkLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                refreshList(facUserId, currentDay);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        facUserId = SharedPrefManager.getInstance(this).getFacEmail();
        currentDay = ExtraUtils.getCurrentDay();

        final MaterialDayPicker materialDayPicker = findViewById(R.id.day_picker);
        materialDayPicker.setSelectionMode(SingleSelectionMode.create());
        materialDayPicker.setBackgroundColor(Color.WHITE);
        materialDayPicker.setSelectedDays(MaterialDayPicker.Weekday.valueOf(currentDay));

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration divider = new DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(divider);

        mSchedules = new ArrayList<>();
        mAdapter = new ScheduleAdapter(this, mSchedules, currentDay);
        mRecyclerView.setAdapter(mAdapter);


        materialDayPicker.setDayPressedListener((weekday, isSelected) -> {
            currentDay = weekday.toString();
            if (isSelected)  refreshList(facUserId, currentDay);
        });
    }

    private void refreshList(String facUserId, String currentDay) {
        if(ExtraUtils.isNetworkAvailable(this)) {
            VolleyTask.showSchedule(this, facUserId, currentDay, jObj -> {
                mSchedules = GsonUtils.extractScheduleFromJSON(jObj);
                mAdapter.swapList(mSchedules, currentDay);

                if (mAdapter.getItemCount() < 1)
                    emptyView.setVisibility(View.VISIBLE);
                else
                    emptyView.setVisibility(View.GONE);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

        refreshList(facUserId, currentDay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }
}
