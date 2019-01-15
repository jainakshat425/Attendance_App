package com.example.android.attendance;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.widget.RelativeLayout;

import com.example.android.attendance.adapters.ScheduleAdapter;
import com.example.android.attendance.pojos.Schedule;
import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.volley.VolleyTask;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.antonious.materialdaypicker.MaterialDayPicker;
import ca.antonious.materialdaypicker.SingleSelectionMode;

public class ScheduleActivity extends AppCompatActivity {

    @BindView(R.id.sch_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.sch_empty_view)
    RelativeLayout emptyView;

    private ScheduleAdapter mScheduleAdapter;

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

        final String facUserId = getIntent().getStringExtra(ExtraUtils.EXTRA_FAC_USER_ID);
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
        mScheduleAdapter = new ScheduleAdapter(this, new ArrayList<Schedule>(), currentDay);
        mRecyclerView.setAdapter(mScheduleAdapter);

        VolleyTask.showSchedule(this, facUserId, currentDay, mScheduleAdapter, emptyView);

        materialDayPicker.setDayPressedListener(new MaterialDayPicker.DayPressedListener() {
            @Override
            public void onDayPressed(MaterialDayPicker.Weekday weekday, boolean isSelected) {
                if (isSelected)
                     VolleyTask.showSchedule(ScheduleActivity.this, facUserId,
                             weekday.toString(), mScheduleAdapter, emptyView);
            }
        });
    }
}
