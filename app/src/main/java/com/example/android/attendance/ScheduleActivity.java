package com.example.android.attendance;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.attendance.data.DatabaseHelper;
import com.example.android.attendance.data.DbHelperMethods;
import com.example.android.attendance.utilities.ExtraUtils;

import ca.antonious.materialdaypicker.MaterialDayPicker;
import ca.antonious.materialdaypicker.SingleSelectionMode;

public class ScheduleActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        final String facUserId = getIntent().getStringExtra(ExtraUtils.EXTRA_FAC_USER_ID);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        MaterialDayPicker materialDayPicker = findViewById(R.id.day_picker);
        materialDayPicker.setSelectionMode(SingleSelectionMode.create());

        materialDayPicker.setDayPressedListener(new MaterialDayPicker.DayPressedListener() {
            @Override
            public void onDayPressed(MaterialDayPicker.Weekday weekday, boolean isSelected) {
                setupScheduleForDay(facUserId, weekday.toString());
            }
        });
    }

    private void setupScheduleForDay(String facUserId, String day) {

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.openDataBaseReadOnly();

        Cursor cursor = DbHelperMethods.getScheduleCursor(db, facUserId, day);


    }
}
