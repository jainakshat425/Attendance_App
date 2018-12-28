package com.example.android.attendance.utilities;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.android.attendance.AttendanceWidgetProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExtraUtils {

    public static final String EXTRA_COLLEGE_ID = "extra_college_id";
    public static final String EXTRA_CLASS_ID = "extra_class_id";
    public static final String EXTRA_BRANCH_ID = "extra_branch_id";
    public static final String EXTRA_SUBJECT_ID = "extra_subject_id";
    public static final String EXTRA_ATTEND_REC_ID = "extra_attend_rec_id";

    public static final String EXTRA_COLLEGE = "extra_college";
    public static final String EXTRA_SEMESTER = "extra_semester";
    public static final String EXTRA_BRANCH = "extra_branch";
    public static final String EXTRA_SECTION = "extra_section";

    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_DAY = "extra_day";
    public static final String EXTRA_SUBJECT = "extra_subject";
    public static final String EXTRA_LECTURE = "extra_lecture";

    public static final String EXTRA_FAC_USER_ID = "extra_fac_user_id";
    public static final String EXTRA_FAC_NAME = "extra_fac_name";
    public static final String EXTRA_FAC_DEPT = "extra_fac_dept";

    private static final String DB_URL = "http://192.168.43.156/android/v1/";
    public static final String ADD_ATT_REC_URL = DB_URL + "addAttendanceRecord.php";
    public static final String FAC_LOGIN_URL = DB_URL + "facLogin.php";
    public static final String GET_ATT_REC_URL = DB_URL + "getAttendanceRecords.php";

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
        return day.toUpperCase();
    }

    public static String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String date = simpleDateFormat.format(Calendar.getInstance().getTime());
        return date;
    }

    public static String getCurrentTimeInHour() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("H", Locale.US);
        String time = timeFormat.format(Calendar.getInstance().getTime());
        return time;
    }

    public static String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        String time = timeFormat.format(Calendar.getInstance().getTime());
        return time;
    }

    public static void updateWidget(Activity context) {
        Intent intent = new Intent(context, AttendanceWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
         // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
         // since it seems the onUpdate() is only fired on that:
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, AttendanceWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }


    public static TextView getTextView(Context context, int textSize) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(textSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextColor(context.getColor(android.R.color.black));
        }
        tv.setPadding(4, 4, 4, 4);
        return tv;
    }
}

