package com.example.android.attendance;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.RemoteViews;

import com.example.android.attendance.contracts.BranchContract;
import com.example.android.attendance.contracts.BranchContract.BranchEntry;
import com.example.android.attendance.contracts.ClassContract;
import com.example.android.attendance.contracts.ClassContract.ClassEntry;
import com.example.android.attendance.contracts.CollegeContract;
import com.example.android.attendance.contracts.CollegeContract.CollegeEntry;
import com.example.android.attendance.contracts.LectureContract;
import com.example.android.attendance.contracts.LectureContract.LectureEntry;
import com.example.android.attendance.contracts.SubjectContract;
import com.example.android.attendance.contracts.SubjectContract.SubjectEntry;
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String facUserId = preferences.getString(ExtraUtils.EXTRA_FAC_USER_ID, null);
        if (facUserId == null) {
            views.setTextViewText(R.id.widget_header, "Not Logged In");
            views.setViewVisibility(R.id.lets_go_button, View.GONE);
        } else {
            views.setTextViewText(R.id.widget_header, "Lecture Details");
            views.setViewVisibility(R.id.lets_go_button, View.VISIBLE);

            showLectureDetails(context, views, facUserId);
        }


        // Create an Intent to launch MainActivity when clicked
        Intent intent = new Intent(context, TakeAttendanceActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.lets_go_button, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void showLectureDetails(Context context, RemoteViews views, String facUserId) {
        Cursor cursor = DbHelperMethods.getLectureCursor(context, facUserId);

        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            cursor.moveToFirst();

            int collegeId = cursor.getInt(cursor.getColumnIndex(ClassEntry.COLLEGE_ID));
            int semester = cursor.getInt(cursor.getColumnIndex(ClassEntry.SEMESTER));
            int branchId = cursor.getInt(cursor.getColumnIndex(ClassEntry.BRANCH_ID));
            String section = cursor.getString(cursor.getColumnIndex(ClassEntry.SECTION));

            String day = cursor.getString(cursor.getColumnIndex(LectureEntry.LECTURE_DAY));
            int lecture = cursor.getInt(cursor.getColumnIndex(LectureEntry.LECTURE_NUMBER));
            int classId = cursor.getInt(cursor.getColumnIndex(LectureEntry.CLASS_ID));

            String branch = cursor.getString(cursor.getColumnIndex(BranchEntry.BRANCH_NAME));
            String subject = cursor.getString(cursor.getColumnIndex(SubjectEntry.SUB_NAME_COL));
            String collegeName = cursor.getString(cursor.getColumnIndex(CollegeEntry.NAME));

            views.setTextViewText(R.id.college_text_view, collegeName);
            views.setTextViewText(R.id.semester_text_view, ExtraUtils.getSemester(String.valueOf(semester)));
            views.setTextViewText(R.id.widget_branch_tv, branch);
            views.setTextViewText(R.id.widget_section_tv, section);
            views.setTextViewText(R.id.widget_subject_tv, subject);
            views.setTextViewText(R.id.widget_day_tv, day);
            views.setTextViewText(R.id.widget_lecture_tv, ExtraUtils.getLecture(String.valueOf(lecture)));
            views.setTextViewText(R.id.widget_date_tv, ExtraUtils.getCurrentDate());
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

