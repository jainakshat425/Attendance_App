package com.example.android.attendance.contracts;

import android.provider.BaseColumns;

public class FacultyContract {
    public static abstract class FacultyEntry implements BaseColumns {

        public static final String TABLE_NAME = "faculties";

        public static final String _ID = BaseColumns._ID;
        public static final String F_NAME_COL = "fac_name";
        public static final String F_USERNAME_COL = "fac_user_id";
        public static final String F_PASSWORD_COL = "fac_password";
        public static final String F_DEPARTMENT_COL = "fac_department";

    }
}
