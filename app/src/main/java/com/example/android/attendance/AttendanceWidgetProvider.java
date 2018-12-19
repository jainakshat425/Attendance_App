package com.example.android.attendance;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.attendance.contracts.BranchContract.BranchEntry;
import com.example.android.attendance.contracts.ClassContract.ClassEntry;
import com.example.android.attendance.contracts.CollegeContract.CollegeEntry;
import com.example.android.attendance.contracts.LectureContract.LectureEntry;
import com.example.android.attendance.contracts.SubjectContract.SubjectEntry;
import com.example.android.attendance.data.DatabaseHelper;
import com.example.android.attendance.data.DbHelperMethods;
import com.example.android.attendance.utilities.ExtraUtils;

/**
 * Implementation of App Widget functionality.
 */
@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class AttendanceWidgetProvider extends AppWidgetProvider {

    public static final int WIDGET_REQUEST_CODE = 5;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.attendance_widget);

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                WIDGET_REQUEST_CODE, mainIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_details, pendingIntent);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String facUserId = preferences.getString(ExtraUtils.EXTRA_FAC_USER_ID, null);
        int currentTime = Integer.parseInt(ExtraUtils.getCurrentTimeInHour());
        if (facUserId == null) {
            views.setTextViewText(R.id.widget_header, "Not Logged In");
            views.setViewVisibility(R.id.widget_details, View.GONE);
            views.setViewVisibility(R.id.take_button, View.GONE);
        } else if (currentTime > 16 || currentTime < 8) {
            views.setTextViewText(R.id.widget_header, "Off From Work");
            views.setViewVisibility(R.id.widget_details, View.GONE);
            views.setViewVisibility(R.id.take_button, View.GONE);
        } else {
            setupLectureDetails(context, views, facUserId);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void setupLectureDetails(Context context, RemoteViews views,
                                            String facUserId) {

        DatabaseHelper dbHelper = new DatabaseHelper(context);

        SQLiteDatabase db = dbHelper.openDataBaseReadOnly();

        Cursor cursor = DbHelperMethods.getLectureCursor(db, facUserId);

        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            cursor.moveToFirst();

            int collegeId = cursor.getInt(cursor.getColumnIndex(ClassEntry.COLLEGE_ID));
            int branchId = cursor.getInt(cursor.getColumnIndex(ClassEntry.BRANCH_ID));
            int semester = cursor.getInt(cursor.getColumnIndex(ClassEntry.SEMESTER));
            String section = cursor.getString(cursor.getColumnIndex(ClassEntry.SECTION));

            int classId = cursor.getInt(cursor.getColumnIndex(LectureEntry.CLASS_ID));
            String day = cursor.getString(cursor.getColumnIndex(LectureEntry.LECTURE_DAY));
            int lecture = cursor.getInt(cursor.getColumnIndex(LectureEntry.LECTURE_NUMBER));

            String branch = cursor.getString(cursor.getColumnIndex(BranchEntry.BRANCH_NAME));
            String subject = cursor.getString(cursor.getColumnIndex(SubjectEntry.SUB_NAME_COL));
            String collegeName = cursor.getString(cursor.getColumnIndex(CollegeEntry.NAME));
            String date = ExtraUtils.getCurrentDate();

            views.setViewVisibility(R.id.widget_details, View.VISIBLE);
            views.setTextViewText(R.id.widget_college_tv, collegeName);
            views.setTextViewText(R.id.widget_branch_tv, branch);
            views.setTextViewText(R.id.widget_section_tv, section);
            views.setTextViewText(R.id.widget_subject_tv, subject);
            views.setTextViewText(R.id.widget_day_tv, day);
            views.setTextViewText(R.id.widget_date_tv, date);
            views.setTextViewText(R.id.widget_semester_tv,
                    ExtraUtils.getSemester(String.valueOf(semester)));
            views.setTextViewText(R.id.widget_lecture_tv,
                    ExtraUtils.getLecture(String.valueOf(lecture)));

            int lectureId = DbHelperMethods.getLectureId(db, String.valueOf(classId),
                    String.valueOf(lecture), day);
            boolean attendanceAlreadyExist = DbHelperMethods
                    .isAttendanceAlreadyExists(db, lectureId, date);

            if (!attendanceAlreadyExist) {
                views.setTextViewText(R.id.widget_header, "Current Lecture");
                views.setViewVisibility(R.id.take_button, View.VISIBLE);

                // Create an Intent to launch TakeAttendanceActivity when clicked
                Intent intent = new Intent(context, TakeAttendanceActivity.class);
                intent.putExtra(ExtraUtils.EXTRA_COLLEGE_ID,
                        String.valueOf(collegeId));
                intent.putExtra(ExtraUtils.EXTRA_DATE, date);
                intent.putExtra(ExtraUtils.EXTRA_DAY, day);
                intent.putExtra(ExtraUtils.EXTRA_SEMESTER, String.valueOf(semester));
                intent.putExtra(ExtraUtils.EXTRA_BRANCH, branch);
                intent.putExtra(ExtraUtils.EXTRA_SECTION, section);
                intent.putExtra(ExtraUtils.EXTRA_SUBJECT, subject);
                intent.putExtra(ExtraUtils.EXTRA_CLASS_ID, String.valueOf(classId));
                intent.putExtra(ExtraUtils.EXTRA_BRANCH_ID, String.valueOf(branchId));
                intent.putExtra(ExtraUtils.EXTRA_LECTURE, String.valueOf(lecture));
                intent.putExtra(ExtraUtils.EXTRA_FAC_USER_ID, facUserId);
                PendingIntent pendingIntent = PendingIntent.getActivity(context,
                        WIDGET_REQUEST_CODE, intent, 0);
                views.setOnClickPendingIntent(R.id.take_button, pendingIntent);
            } else {
                views.setTextViewText(R.id.widget_header, "Attendance Done");
                views.setViewVisibility(R.id.take_button, View.GONE);
                views.setOnClickPendingIntent(R.id.take_button, null);
            }
        } else {
            views.setTextViewText(R.id.widget_header, "Attendance Not Found!");
            views.setViewVisibility(R.id.widget_details, View.GONE);
            views.setViewVisibility(R.id.take_button, View.GONE);
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

