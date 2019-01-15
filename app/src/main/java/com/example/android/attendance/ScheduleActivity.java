package com.example.android.attendance;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
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

    List<Schedule> mSchedules;
    String facUserId;

    @BindView(R.id.sch_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.sch_empty_view)
    RelativeLayout emptyView;

    private ScheduleAdapter mAdapter;

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

        facUserId = SharedPrefManager.getInstance(this).getFacUserId();
        String currentDay = ExtraUtils.getCurrentDay();

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

        refreshList(facUserId, currentDay);

        materialDayPicker.setDayPressedListener((weekday, isSelected) -> {
            if (isSelected)  refreshList(facUserId, weekday.toString());
        });
    }

    private void refreshList(String facUserId, String currentDay) {
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
