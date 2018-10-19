package com.example.android.attendance.contracts;

import android.provider.BaseColumns;

public class LectureContract {


    public static abstract class LectureEntry implements BaseColumns {

        public static final String TABLE_NAME = "lectures";


        public static final String ID = BaseColumns._ID;
        public static final String FAC_USER_ID = "fac_user_id";
        public static final String CLASS_ID = "class_id";
        public static final String SUBJECT_ID = "subject_id";
        public static final String LECTURE_START_TIME = "lect_start_time";
        public static final String LECTURE_END_TIME = "lect_end_time";
        public static final String LECTURE_NUMBER = "lect_no";
        public static final String LECTURE_DAY = "day";
    }
}
