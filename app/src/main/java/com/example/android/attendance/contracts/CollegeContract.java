package com.example.android.attendance.contracts;

import android.provider.BaseColumns;

public class CollegeContract {

    public static abstract class CollegeEntry implements BaseColumns {

        public static final String TABLE_NAME = "colleges";

        public static final String ID = BaseColumns._ID;
        public static final String NAME = "coll_name";
        public static final String FULL_NAME = "coll_full_name";

        //constants representing college
        public static final int COLLEGE_GIT = 1;
        public static final int COLLEGE_GCT = 2;
    }
}
