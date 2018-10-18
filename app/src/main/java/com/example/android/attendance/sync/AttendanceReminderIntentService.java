package com.example.android.attendance.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */

public class AttendanceReminderIntentService extends IntentService {

    public AttendanceReminderIntentService() {
        super("AttendanceReminderIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        ReminderTasks.executeTask(this, action);
    }
}
