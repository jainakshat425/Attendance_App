package com.example.android.attendance.contracts;

import android.provider.BaseColumns;

public class BranchContract {

    public static abstract class BranchEntry implements BaseColumns {

        public static final String TABLE_NAME = "branches";

        public static final String ID = BaseColumns._ID;
        public static final String BRANCH_NAME = "b_name";
    }
}
