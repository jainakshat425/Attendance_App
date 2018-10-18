package com.example.android.attendance.contracts;

import android.provider.BaseColumns;


public class ClassContract {
    public static abstract class ClassEntry implements BaseColumns {

        public static final String TABLE_NAME = "classes";
        public static final String ID = BaseColumns._ID;
        public static final String COLLEGE_ID = "college_id";
        public static final String SEMESTER = "sem";
        public static final String BRANCH_ID = "branch_id";
        public static final String SECTION = "section";
    }

}
