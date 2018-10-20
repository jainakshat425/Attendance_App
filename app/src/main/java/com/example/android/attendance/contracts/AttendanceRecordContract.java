package com.example.android.attendance.contracts;

import android.provider.BaseColumns;

public class AttendanceRecordContract {

    public class AttendanceRecordEntry implements BaseColumns {

        public static final String TABLE_NAME = "attendance_records";

        public static final String ID = BaseColumns._ID;
        public static final String LECTURE_ID_COL = "lecture_id";
        public static final String TOTAL_STUDENTS_COL = "total_students";
        public static final String STUDENTS_PRESENT_COL = "students_present";
        public static final String DATE_COL = "date";
    }
}
