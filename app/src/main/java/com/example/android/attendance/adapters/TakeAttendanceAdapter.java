package com.example.android.attendance.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.attendance.R;
import com.example.android.attendance.contracts.StudentContract.StudentEntry;

import java.util.ArrayList;

public class TakeAttendanceAdapter extends CursorAdapter {

    private static ArrayList<Integer> attendanceStatesList;

    public TakeAttendanceAdapter(Activity context, Cursor cursor,
                                 ArrayList<Integer> attendanceStates) {
        super(context, cursor, 0);
        attendanceStatesList = attendanceStates;
    }

    //inflates a new view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.take_attendance_list_item,
                parent, false);
    }

    //bind the data to views
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        int nameIndex = cursor.getColumnIndexOrThrow(StudentEntry.S_NAME_COL);
        String name = cursor.getString(nameIndex);
        TextView nameTv = (TextView) view.findViewById(R.id.tv_name);
        nameTv.setText(name);

        int rollNoIndex = cursor.getColumnIndexOrThrow(StudentEntry.S_ROLL_NO_COL);
        String rollNo = cursor.getString(rollNoIndex);
        TextView rollNoTv = (TextView) view.findViewById(R.id.tv_roll_no);
        rollNoTv.setText(rollNo);

        int position = cursor.getPosition();

        int serialNo = position;
        TextView serialTv = (TextView) view.findViewById(R.id.tv_serial_no);
        serialTv.setText(String.valueOf(serialNo +1));

        CheckBox presentCheckbox = view.findViewById(R.id.present_checkbox);

        //set tag on check box so as to identify which checkbox belongs to which view
        presentCheckbox.setTag(position);

        presentCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(String.valueOf(v.getTag()));
                if (attendanceStatesList.get(position) == 1) {
                    attendanceStatesList.set(position, 0);
                } else {
                    attendanceStatesList.set(position, 1);
                }

            }
        });
        if (attendanceStatesList.get(position) == 1) {
            presentCheckbox.setChecked(true);
        } else {
            presentCheckbox.setChecked(false);
        }
    }

    //returns the attendance state of i'th student
    public static int getAttendanceState(int i) {
        return attendanceStatesList.get(i);
    }

}
