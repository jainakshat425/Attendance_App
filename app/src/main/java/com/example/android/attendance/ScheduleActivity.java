package com.example.android.attendance;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.android.attendance.adapters.ScheduleAdapter;
import com.example.android.attendance.data.DatabaseHelper;
import com.example.android.attendance.data.DbHelperMethods;
import com.example.android.attendance.utilities.ExtraUtils;

import ca.antonious.materialdaypicker.MaterialDayPicker;
import ca.antonious.materialdaypicker.SingleSelectionMode;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ScheduleAdapter mScheduleAdapter;
    private RelativeLayout emptyView;

    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        final String facUserId = getIntent().getStringExtra(ExtraUtils.EXTRA_FAC_USER_ID);
        String currentDay = ExtraUtils.getCurrentDay();

        final MaterialDayPicker materialDayPicker = findViewById(R.id.day_picker);
        materialDayPicker.setSelectionMode(SingleSelectionMode.create());
        materialDayPicker.setBackgroundColor(Color.WHITE);
        materialDayPicker.setSelectedDays(MaterialDayPicker.Weekday.valueOf(currentDay));

        emptyView = findViewById(R.id.sch_empty_view);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView = findViewById(R.id.sch_recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        final SQLiteDatabase db = databaseHelper.openDataBaseReadOnly();

        mCursor = DbHelperMethods.getScheduleCursor(db, facUserId, currentDay);
        mCursor.moveToFirst();
        mScheduleAdapter = new ScheduleAdapter(this, mCursor);
        mRecyclerView.setAdapter(mScheduleAdapter);

        checkForEmptyList();

        materialDayPicker.setDayPressedListener(new MaterialDayPicker.DayPressedListener() {
            @Override
            public void onDayPressed(MaterialDayPicker.Weekday weekday, boolean isSelected) {
                mCursor = DbHelperMethods.getScheduleCursor(db, facUserId, weekday.toString());
                mCursor.moveToFirst();
                mScheduleAdapter.swapCursor(mCursor);
                checkForEmptyList();
            }
        });
    }

    private void checkForEmptyList() {
        if(mScheduleAdapter.getItemCount() <= 0)
            emptyView.setVisibility(View.VISIBLE);
        else
            emptyView.setVisibility(View.GONE);
    }

}
