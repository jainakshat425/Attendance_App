package com.example.android.attendance.contracts;

import android.provider.BaseColumns;

public class AttendanceContract {
    public static abstract class AttendanceEntry implements BaseColumns {


        public static final String TABLE_NAME = "attendance";

        public static final String ID = BaseColumns._ID;
        public static final String STUDENT_ID = "std_id";
        public static final String ATTENDANCE_RECORD_ID  = "attendance_record_id";
        public static final String ATTENDANCE_STATE = "attendance_state";
    }
}
