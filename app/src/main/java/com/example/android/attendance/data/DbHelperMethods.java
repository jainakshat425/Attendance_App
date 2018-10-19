package com.example.android.attendance.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.attendance.contracts.AttendanceRecordContract.AttendanceRecordEntry;
import com.example.android.attendance.contracts.BranchContract.BranchEntry;
import com.example.android.attendance.contracts.ClassContract.ClassEntry;
import com.example.android.attendance.contracts.CollegeContract.CollegeEntry;
import com.example.android.attendance.contracts.LectureContract.LectureEntry;
import com.example.android.attendance.contracts.SubjectContract.SubjectEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.example.android.attendance.utilities.ExtraUtils.getCurrentDay;

public class DbHelperMethods {

    private static final int CLASS_DOES_NOT_EXIST = -1;

    public static int getClassId(Context context, int collegeId, String semester, String branchId, String section) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db;
        try {
            db = dbHelper.openDataBaseReadOnly();
        } catch (SQLException sqle) {
            throw sqle;
        }

        String[] projection = new String[]{ClassEntry.ID};
        String selections = ClassEntry.COLLEGE_ID + "=?"  + " and "
                +ClassEntry.SEMESTER + "=?" + " and "
                +ClassEntry.BRANCH_ID + "=?" + " and "
                +ClassEntry.SECTION + "=?";

        String[] selectionArgs = {String.valueOf(collegeId),
                String.valueOf(semester),
                String.valueOf(branchId),
                String.valueOf(section)};

        Cursor cursor = db.query(ClassEntry.TABLE_NAME, projection, selections, selectionArgs,
                null, null, null);
        int classIdIndex = cursor.getColumnIndex(ClassEntry.ID);

        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            //if class is found
            return cursor.getInt(classIdIndex);
        } else {
            //if class is not found insert new class
            db = dbHelper.openDatabaseForReadWrite();
            ContentValues values = new ContentValues();
            values.put(ClassEntry.COLLEGE_ID, collegeId);
            values.put(ClassEntry.SEMESTER, semester);
            values.put(ClassEntry.BRANCH_ID, branchId);
            values.put(ClassEntry.SECTION, section);
            long newRowId = db.insert(ClassEntry.TABLE_NAME, null, values);
            return (int) newRowId;
        }
    }

    public static int getBranchId(Context context, String branchName) {

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db;
        int branchId = -1;
        try {
            db = dbHelper.openDataBaseReadOnly();
        } catch (SQLException sqle) {
            throw sqle;
        }

        Cursor cursor = db.query(BranchEntry.TABLE_NAME,
                new String[]{BranchEntry.ID},
                BranchEntry.BRANCH_NAME + "=?",
                new String[]{branchName},
                null,
                null,
                null
                );

        int branchIdIndex = cursor.getColumnIndex(BranchEntry.ID);

        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            branchId = cursor.getInt(branchIdIndex);
        }

        return branchId;
    }

    public static Cursor getAttendanceRecordsCursor(Context context, String facUserId) {

        /**
         * open the database for getting the records of attendance
         */
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        SQLiteDatabase db;
        try {
            db = databaseHelper.openDatabaseForReadWrite();
        } catch (SQLException sqle) {
            throw sqle;
        }

        String projection = AttendanceRecordEntry.TABLE_NAME + "." + AttendanceRecordEntry._ID + ","
                + AttendanceRecordEntry.FACULTY_ID_COL + ","
                + AttendanceRecordEntry.CLASS_ID_COL + ","
                + AttendanceRecordEntry.ATTENDANCE_TABLE_COL + ","
                + AttendanceRecordEntry.ATTENDANCE_COL + ","
                + AttendanceRecordEntry.DATE_COL + ","
                + AttendanceRecordEntry.DAY_COL + ","
                + AttendanceRecordEntry.LECTURE_COL + ","
                + AttendanceRecordEntry.STUDENTS_PRESENT_COL + ","
                + AttendanceRecordEntry.TOTAL_STUDENTS_COL + ","
                + AttendanceRecordEntry.SUBJECT_COL + ","
                + ClassEntry.COLLEGE_ID + ","
                + ClassEntry.SEMESTER + ","
                + ClassEntry.BRANCH_ID + ","
                + ClassEntry.SECTION + ","
                + CollegeEntry.NAME + ","
                + BranchEntry.BRANCH_NAME;


        String tableName = AttendanceRecordEntry.TABLE_NAME
                + " INNER JOIN " + ClassEntry.TABLE_NAME
                + " ON " + ClassEntry.TABLE_NAME + "." + ClassEntry.ID + " = "
                + AttendanceRecordEntry.CLASS_ID_COL
                + " INNER JOIN " + CollegeEntry.TABLE_NAME
                + " ON " + CollegeEntry.TABLE_NAME + "." + CollegeEntry.ID + " = "
                + ClassEntry.COLLEGE_ID
                + " INNER JOIN " + BranchEntry.TABLE_NAME
                + " ON " + BranchEntry.TABLE_NAME + "." + BranchEntry.ID + " = "
                + ClassEntry.BRANCH_ID;


        String query = "SELECT " + projection + " FROM " + tableName + " WHERE "
                + AttendanceRecordEntry.FACULTY_ID_COL + "=?";

        return db.rawQuery(query, new String[] {facUserId});
    }

    public static Cursor getLectureCursor(Context context, String facUserId) {

        /**
         * open the database for getting the records of attendance
         */
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        SQLiteDatabase db;
        try {
            db = databaseHelper.openDatabaseForReadWrite();
        } catch (SQLException sqle) {
            throw sqle;
        }

        String projection = LectureEntry.TABLE_NAME + "." + LectureEntry.ID + ","
                + LectureEntry.CLASS_ID + ","
                + LectureEntry.FAC_USER_ID + ","
                + LectureEntry.LECTURE_NUMBER + ","
                + LectureEntry.LECTURE_DAY + ","
                + ClassEntry.COLLEGE_ID + ","
                + ClassEntry.SEMESTER + ","
                + ClassEntry.TABLE_NAME + "." + ClassEntry.BRANCH_ID + ","
                + ClassEntry.SECTION + ","
                + CollegeEntry.NAME + ","
                + BranchEntry.BRANCH_NAME + ","
                + SubjectEntry.SUB_NAME_COL;


        String tableName = LectureEntry.TABLE_NAME
                + " INNER JOIN " + ClassEntry.TABLE_NAME
                + " ON " + ClassEntry.TABLE_NAME + "." + ClassEntry.ID + " = "
                + LectureEntry.CLASS_ID
                + " INNER JOIN " + CollegeEntry.TABLE_NAME
                + " ON " + CollegeEntry.TABLE_NAME + "." + CollegeEntry.ID + " = "
                + ClassEntry.COLLEGE_ID
                + " INNER JOIN " + BranchEntry.TABLE_NAME
                + " ON " + BranchEntry.TABLE_NAME + "." + BranchEntry.ID + " = "
                + ClassEntry.TABLE_NAME + "." + ClassEntry.BRANCH_ID
                + " INNER JOIN " + SubjectEntry.TABLE_NAME
                + " ON " + SubjectEntry.TABLE_NAME + "." + SubjectEntry._ID + " = "
                + LectureEntry.SUBJECT_ID;


        String query = "SELECT " + projection + " FROM " + tableName + " WHERE "
                + LectureEntry.FAC_USER_ID + "=?" + " and "
                + LectureEntry.LECTURE_DAY + "=?";

        return db.rawQuery(query, new String[] {facUserId, getCurrentDay()});
    }


}
