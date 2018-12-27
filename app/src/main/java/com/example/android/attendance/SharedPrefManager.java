package com.example.android.attendance;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.attendance.contracts.FacultyContract.FacultyEntry;

public class SharedPrefManager {
    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private static final String MY_SHARED_PREF = "credentialPref";

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public boolean isLoggedIn() {

        SharedPreferences sharedPref = mCtx
                .getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);

        if (sharedPref.getString(FacultyEntry.F_USERNAME_COL, null) != null)
            return true;
        else
            return false;
    }

    public boolean saveLoginUserDetails(int facId, String facName, String facUsername, String facDept) {

        SharedPreferences.Editor sharedPref = mCtx.getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE).edit();

        sharedPref.putInt(FacultyEntry._ID, facId);
        sharedPref.putString(FacultyEntry.F_USERNAME_COL, facUsername);
        sharedPref.putString(FacultyEntry.F_NAME_COL, facName);
        sharedPref.putString(FacultyEntry.F_DEPARTMENT_COL, facDept);

        sharedPref.apply();
        return true;
    }
}
