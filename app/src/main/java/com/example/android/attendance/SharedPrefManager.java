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

        if (sharedPref.getString(FacultyEntry.F_EMAIL_COL, null) != null)
            return true;
        else
            return false;
    }

    public boolean saveLoginUserDetails(int facId, String facName, String facUsername,
                                        String facDept, int fCollId, String mobNo) {

        SharedPreferences.Editor sharedPref = mCtx.getSharedPreferences(MY_SHARED_PREF,
                Context.MODE_PRIVATE).edit();

        sharedPref.putInt(FacultyEntry._ID, facId);
        sharedPref.putString(FacultyEntry.F_EMAIL_COL, facUsername);
        sharedPref.putString(FacultyEntry.F_NAME_COL, facName);
        sharedPref.putString("dept_name", facDept);
        sharedPref.putInt(FacultyEntry.F_COLLEGE_ID, fCollId);
        sharedPref.putString(FacultyEntry.F_MOB_NO, mobNo);

        sharedPref.apply();
        return true;
    }

    public int getFacId() {
        SharedPreferences sharedPref = mCtx
                .getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);

        return sharedPref.getInt(FacultyEntry._ID, -1);
    }

    public int getCollId() {
        SharedPreferences sharedPref = mCtx
                .getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);

        return sharedPref.getInt(FacultyEntry.F_COLLEGE_ID, -1);
    }

    public String getFacUserId() {
        SharedPreferences sharedPref = mCtx
                .getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);

        return sharedPref.getString(FacultyEntry.F_EMAIL_COL, null);
    }

    public String getFacName() {
        SharedPreferences sharedPref = mCtx
                .getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);

        return sharedPref.getString(FacultyEntry.F_NAME_COL, null);
    }

    public String getFacDept() {
        SharedPreferences sharedPref = mCtx
                .getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);

        return sharedPref.getString("dept_name", null);
    }

    public String getFacMobNo() {
        SharedPreferences sharedPref = mCtx
                .getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);

        return sharedPref.getString(FacultyEntry.F_MOB_NO, null);
    }
}
