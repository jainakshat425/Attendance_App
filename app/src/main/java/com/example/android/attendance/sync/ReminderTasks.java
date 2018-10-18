package com.example.android.attendance.sync;

import android.content.Context;

import com.example.android.attendance.utilities.NotificationUtils;

public class ReminderTasks {
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    static final String ACTION_ATTENDANCE_REMINDER = "attendance-reminder";

    public static void executeTask(Context context, String action) {
        if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        } else if (ACTION_ATTENDANCE_REMINDER.equals(action)) {
            issueAttendanceReminder(context);
        }
    }

    private static void issueAttendanceReminder(Context context) {
        NotificationUtils.remindFacultyReminder(context);
    }

}
