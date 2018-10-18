package com.example.android.attendance.contracts;

import android.provider.BaseColumns;

public class StudentContract {
    public static abstract class StudentEntry implements BaseColumns {

        //define constants for storing the table and attributes name
        public static final String TABLE_NAME = "students";

        public static final String _ID = BaseColumns._ID;
        public static final String S_NAME_COL = "std_name";
        public static final String S_ROLL_NO_COL = "std_roll_no";
        public static final String S_CLASS_ID = "class_id";



    }
}
