package com.example.android.attendance.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.attendance.contracts.AttendanceContract.AttendanceEntry;
import com.example.android.attendance.contracts.AttendanceRecordContract.AttendanceRecordEntry;
import com.example.android.attendance.contracts.BranchContract.BranchEntry;
import com.example.android.attendance.contracts.ClassContract.ClassEntry;
import com.example.android.attendance.contracts.CollegeContract.CollegeEntry;
import com.example.android.attendance.contracts.LectureContract.LectureEntry;
import com.example.android.attendance.contracts.StudentContract.StudentEntry;
import com.example.android.attendance.contracts.SubjectContract.SubjectEntry;

import static com.example.android.attendance.utilities.ExtraUtils.getCurrentDay;

public class DbHelperMethods {


    public static int getClassId(Context context, int collegeId, String semester,
                                 String branchId, String section) {

        String[] projection = new String[]{ClassEntry.ID};
        String selections = ClassEntry.COLLEGE_ID + "=?" + " and "
                + ClassEntry.SEMESTER + "=?" + " and "
                + ClassEntry.BRANCH_ID + "=?" + " and "
                + ClassEntry.SECTION + "=?";

        String[] selectionArgs = {String.valueOf(collegeId),
                String.valueOf(semester),
                String.valueOf(branchId),
                String.valueOf(section)};

        Cursor cursor = getDbReadOnly(context).query(ClassEntry.TABLE_NAME, projection, selections, selectionArgs,
                null, null, null);
        int classIdIndex = cursor.getColumnIndex(ClassEntry.ID);

        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            //if class is found
            return cursor.getInt(classIdIndex);
        } else {
            //if class is not found insert new class
            ContentValues values = new ContentValues();
            values.put(ClassEntry.COLLEGE_ID, collegeId);
            values.put(ClassEntry.SEMESTER, semester);
            values.put(ClassEntry.BRANCH_ID, branchId);
            values.put(ClassEntry.SECTION, section);
            long newRowId = getDbReadWrite(context).insert(ClassEntry.TABLE_NAME, null, values);
            return (int) newRowId;
        }
    }

    public static int getBranchId(Context context, String branchName) {
        int branchId = -1;

        Cursor cursor = getDbReadOnly(context).query(BranchEntry.TABLE_NAME,
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

    public static int getSubjectId(Context context, String subject, String branchId,
                                   String semester) {
        int subjectId = -1;

        String selection = SubjectEntry.SUB_NAME_COL + "=?"
                + SubjectEntry.BRANCH_ID_COL + "=?"
                + SubjectEntry.SUB_SEMESTER_COL + "=?";

        String[] selectionArgs = new String[] {subject, branchId, semester};

        Cursor cursor = getDbReadOnly(context).query(SubjectEntry.TABLE_NAME,
                new String[]{SubjectEntry._ID},
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int subjectIdIndex = cursor.getColumnIndex(SubjectEntry._ID);

        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            subjectId = cursor.getInt(subjectIdIndex);
        }
        return subjectId;
    }

    public static int getLectureId(Context context, String classId, String lectureNo,
                                   String daySelected) {
        int lectureId = -1;

        String[] projection = new String[] {LectureEntry.ID};
        String selection = LectureEntry.CLASS_ID + "=?" + " and "
                + LectureEntry.LECTURE_NUMBER + "=?" + " and "
                + LectureEntry.LECTURE_DAY + "=?";
        String[] selectionArgs = new String[]{classId, lectureNo, daySelected};

        Cursor cursor = getDbReadOnly(context).query(LectureEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs, null,null,null);

        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            cursor.moveToFirst();
            lectureId = cursor.getInt(cursor.getColumnIndex(LectureEntry.ID));
        }
        return lectureId;
    }

    public static Cursor getAttendanceRecordsCursor(Context context, String facUserId) {

        String projection = AttendanceRecordEntry.TABLE_NAME + "." + AttendanceRecordEntry.ID + ","
                + AttendanceRecordEntry.LECTURE_ID_COL + ","
                + AttendanceRecordEntry.DATE_COL + ","
                + AttendanceRecordEntry.STUDENTS_PRESENT_COL + ","
                + AttendanceRecordEntry.TOTAL_STUDENTS_COL + ","
                + LectureEntry.FAC_USER_ID + ","
                + LectureEntry.CLASS_ID + ","
                + LectureEntry.SUBJECT_ID + ","
                + LectureEntry.LECTURE_DAY + ","
                + LectureEntry.LECTURE_NUMBER + ","
                + ClassEntry.COLLEGE_ID + ","
                + ClassEntry.SEMESTER + ","
                + ClassEntry.TABLE_NAME + "." + ClassEntry.BRANCH_ID + ","
                + ClassEntry.SECTION + ","
                + CollegeEntry.NAME + ","
                + BranchEntry.BRANCH_NAME + ","
                + SubjectEntry.SUB_NAME_COL;


        String tableName = AttendanceRecordEntry.TABLE_NAME
                + " INNER JOIN " + LectureEntry.TABLE_NAME
                + " ON " + LectureEntry.TABLE_NAME + "." + LectureEntry.ID + " = "
                + AttendanceRecordEntry.LECTURE_ID_COL
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
                + LectureEntry.FAC_USER_ID + "=?";

        return getDbReadOnly(context).rawQuery(query, new String[]{facUserId});
    }

    public static Cursor getLectureCursor(Context context, String facUserId) {


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

        return getDbReadOnly(context).rawQuery(query, new String[]{facUserId, getCurrentDay()});
    }

    public static Cursor getStudentForUpdateAttendance(Context context, String attendRecId) {
        String tableName = AttendanceEntry.TABLE_NAME
                + " INNER JOIN " + StudentEntry.TABLE_NAME
                + " ON " + StudentEntry.TABLE_NAME + "." + StudentEntry.ID + " = "
                + AttendanceEntry.STUDENT_ID;

        String projection = AttendanceEntry.TABLE_NAME + "." + AttendanceEntry.ID + ","
                + StudentEntry.S_NAME_COL + ","
                + StudentEntry.S_ROLL_NO_COL + ","
                + AttendanceEntry.ATTENDANCE_STATE;

        String selection = AttendanceEntry.ATTENDANCE_RECORD_ID + "=?";

        String[] selectionArgs = new String[]{attendRecId};

        String orderBy = StudentEntry.S_ROLL_NO_COL + " ASC";

        String query = "SELECT " + projection + " FROM " + tableName + " WHERE " + selection +
                " ORDER BY " +orderBy;

        return getDbReadOnly(context).rawQuery(query, selectionArgs);
    }

    public static Cursor getStudentForNewAttendance(Context context, String classId) {
        String tableName = StudentEntry.TABLE_NAME
                + " INNER JOIN " + ClassEntry.TABLE_NAME
                + " ON " + ClassEntry.TABLE_NAME + "." + ClassEntry.ID + " = "
                + StudentEntry.S_CLASS_ID;

        String projection = StudentEntry.TABLE_NAME + "." + StudentEntry.ID + ","
                + StudentEntry.S_NAME_COL + ","
                + StudentEntry.S_ROLL_NO_COL;

        String selection = StudentEntry.S_CLASS_ID + "=?";

        String[] selectionArgs = new String[]{classId};

        String orderBy = StudentEntry.S_ROLL_NO_COL + " ASC";

        String query = "SELECT " + projection + " FROM " + tableName + " WHERE " + selection +
                " ORDER BY " +orderBy;

        return getDbReadOnly(context).rawQuery(query, selectionArgs);
    }

    public static SQLiteDatabase getDbReadWrite(Context context) {
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
        return db;
    }

    public static SQLiteDatabase getDbReadOnly(Context context) {
        /**
         * open the database for getting the records of attendance
         */
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        SQLiteDatabase db;
        try {
            db = databaseHelper.openDataBaseReadOnly();
        } catch (SQLException sqle) {
            throw sqle;
        }
        return db;
    }


    public static int createAttendanceRecord(Context context, String lectureId, String date) {

        ContentValues values = new ContentValues();
        values.put(AttendanceRecordEntry.LECTURE_ID_COL, lectureId);
        values.put(AttendanceRecordEntry.DATE_COL, date);

        int attendRecId = (int) getDbReadWrite(context)
                .insert(AttendanceRecordEntry.TABLE_NAME, null, values);

        return attendRecId;
    }
}
