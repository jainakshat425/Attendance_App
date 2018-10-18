package com.example.android.attendance.data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.attendance.contracts.AttendanceRecordContract.AttendanceRecordEntry;

import com.example.android.attendance.contracts.BranchContract.BranchEntry;

import com.example.android.attendance.contracts.ClassContract.ClassEntry;

import com.example.android.attendance.contracts.CollegeContract.CollegeEntry;

public class DbHelperMethods {

    public static int getClassId(Context context, int college, String semester, String branch_id, String section) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db;
        int classId = -1;
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

        String[] selectionArgs = {String.valueOf(college),
                String.valueOf(semester),
                String.valueOf(branch_id),
                String.valueOf(section)};

        Cursor cursor = db.query(ClassEntry.TABLE_NAME, projection, selections, selectionArgs,
                null, null, null);
        int classIdIndex = cursor.getColumnIndex(ClassEntry.ID);

        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            classId = cursor.getInt(classIdIndex);
        }

        return classId;
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
}
