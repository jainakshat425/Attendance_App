package com.example.android.attendance;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {


    public static final String FAC_ID = "_id";
    public static final String FAC_NAME = "fac_name";
    public static final String FAC_EMAIL = "fac_email";
    public static final String FAC_PASSWORD = "fac_password";
    public static final String FAC_DEPT = "dept_name";
    public static final String FAC_COLL_ID = "college_id";
    public static final String FAC_MOB_NO = "mob_no";

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

        if (sharedPref.getString(FAC_EMAIL, null) != null)
            return true;
        else
            return false;
    }

    public boolean saveLoginUserDetails(int facId, String facName, String facUsername,
                                        String facDept, int fCollId, String mobNo) {

        SharedPreferences.Editor sharedPref = mCtx.getSharedPreferences(MY_SHARED_PREF,
                Context.MODE_PRIVATE).edit();

        sharedPref.putInt(FAC_ID, facId);
        sharedPref.putString(FAC_EMAIL, facUsername);
        sharedPref.putString(FAC_NAME, facName);
        sharedPref.putString("dept_name", facDept);
        sharedPref.putInt(FAC_COLL_ID, fCollId);
        sharedPref.putString(FAC_MOB_NO, mobNo);

        sharedPref.apply();
        return true;
    }

    public int getFacId() {
        SharedPreferences sharedPref = mCtx
                .getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);

        return sharedPref.getInt(FAC_ID, -1);
    }

    public int getCollId() {
        SharedPreferences sharedPref = mCtx
                .getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);

        return sharedPref.getInt(FAC_COLL_ID, -1);
    }

    public String getFacEmail() {
        SharedPreferences sharedPref = mCtx
                .getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);

        return sharedPref.getString(FAC_EMAIL, null);
    }

    public String getFacName() {
        SharedPreferences sharedPref = mCtx
                .getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);

        return sharedPref.getString(FAC_NAME, null);
    }

    public String getFacDept() {
        SharedPreferences sharedPref = mCtx
                .getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);

        return sharedPref.getString("dept_name", null);
    }

    public String getFacMobNo() {
        SharedPreferences sharedPref = mCtx
                .getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE);

        return sharedPref.getString(FAC_MOB_NO, null);
    }

    public void clearCredentials() {
        mCtx.getSharedPreferences(MY_SHARED_PREF, Context.MODE_PRIVATE).edit().clear().apply();
    }
}
