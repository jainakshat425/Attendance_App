package com.example.android.attendance.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExtraUtils {

    public static final String EXTRA_COLLEGE_ID = "extra_college_id";
    public static final String EXTRA_CLASS_ID = "extra_class_id";
    public static final String EXTRA_BRANCH_ID = "extra_branch_id";
    public static final String EXTRA_SUBJECT_ID = "extra_subject_id";

    public static final String EXTRA_COLLEGE = "extra_college";
    public static final String EXTRA_SEMESTER = "extra_semester";
    public static final String EXTRA_BRANCH = "extra_branch";
    public static final String EXTRA_SECTION = "extra_section";

    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_DAY = "extra_day";
    public static final String EXTRA_SUBJECT = "extra_subject";
    public static final String EXTRA_LECTURE = "extra_lecture";

    public static final String EXTRA_TABLE_NAME = "extra_table_name";
    public static final String EXTRA_ATTENDANCE_COL_NAME = "extra_attendance_col_name";

    public static final String EXTRA_FAC_USER_ID = "extra_fac_user_id";
    public static final String EXTRA_FAC_NAME = "extra_fac_name";
    public static final String EXTRA_FAC_DEPT = "extra_fac_dept";


    public static String getLecture(String lecture) {
        if (Integer.parseInt(lecture) == 1) {
            lecture = lecture + "st Lecture";
        } else if (Integer.parseInt(lecture) == 2) {
            lecture = lecture + "nd Lecture";
        } else if (Integer.parseInt(lecture) == 3) {
            lecture = lecture + "rd Lecture";
        } else {
            lecture = lecture + "th Lecture";
        }
        return lecture;
    }

    public static String getSemester(String semester) {
        if (Integer.parseInt(semester) == 1) {
            semester = semester + "st Sem";
        } else if (Integer.parseInt(semester) == 2) {
            semester = semester + "nd Sem";
        } else if (Integer.parseInt(semester) == 3) {
            semester = semester + "rd Sem";
        } else {
            semester = semester + "th Sem";
        }
        return semester;
    }

    public static String getCurrentDay() {
        SimpleDateFormat simpleDayFormat = new SimpleDateFormat("EEEE", Locale.US);
        String day = simpleDayFormat.format(Calendar.getInstance().getTime());
        return day;
    }

    public static String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String date = simpleDateFormat.format(Calendar.getInstance().getTime());
        return date;
    }
}

