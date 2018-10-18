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
import com.example.android.attendance.contracts.AttendanceContract.AttendanceEntry;

import java.util.ArrayList;

public class TakeAttendanceAdapter extends CursorAdapter {

    private static String ATTENDANCE_TABLE;
    private static String NEW_COLUMN;
    private static ArrayList<Integer> attendanceStatesList;
    private static ArrayList<Integer> totalAttendanceList;

    public TakeAttendanceAdapter(Activity context, Cursor cursor, String attendanceTable,
                                 String newColumn, ArrayList<Integer> attendanceStates,
                                 ArrayList<Integer> totalAttendances) {
        super(context, cursor, 0);
        ATTENDANCE_TABLE = attendanceTable;
        NEW_COLUMN = newColumn;
        attendanceStatesList = attendanceStates;
        totalAttendanceList = totalAttendances;

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

        int nameIndex = cursor.getColumnIndexOrThrow(AttendanceEntry.NAME_COL);
        String name = cursor.getString(nameIndex);
        TextView nameTv = (TextView) view.findViewById(R.id.name_text_view);
        nameTv.setText(name);

        int rollNoIndex = cursor.getColumnIndexOrThrow(AttendanceEntry.ROLL_NO_COL);
        String rollNo = cursor.getString(rollNoIndex);
        TextView rollNoTv = (TextView) view.findViewById(R.id.roll_no_text);
        rollNoTv.setText(rollNo);

        int position = cursor.getPosition();

        int serialNo = position;
        TextView serialTv = (TextView) view.findViewById(R.id.serial_no_tv);
        serialTv.setText(String.valueOf(serialNo +1));

        CheckBox presentCheckbox = view.findViewById(R.id.present_checkbox);

        //set tag on check box so as to identify which checkbox belongs to which view
        presentCheckbox.setTag(position);

        presentCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(String.valueOf(v.getTag()));
                int currentTotalAttendance = totalAttendanceList.get(position);
                if (attendanceStatesList.get(position) == 1) {
                    attendanceStatesList.set(position, 0);
                    if (currentTotalAttendance != 0) {
                        totalAttendanceList.set(position, --currentTotalAttendance);
                    }
                } else {
                    attendanceStatesList.set(position, 1);
                    totalAttendanceList.set(position, ++currentTotalAttendance);
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

    //return the total attendance of the i'th student
    public static int getTotalAttendance(int i) { return totalAttendanceList.get(i); }

    //return the new column of ATTENDANCE
    public static String getAttendanceColumn() {
        return NEW_COLUMN;
    }

    //return the present attendance table
    public static String getAttendanceTableName() {
        return ATTENDANCE_TABLE;
    }
}
