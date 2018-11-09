package com.example.android.attendance.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.contracts.AttendanceContract.AttendanceEntry;
import com.example.android.attendance.contracts.AttendanceRecordContract.AttendanceRecordEntry;
import com.example.android.attendance.contracts.BranchContract.BranchEntry;
import com.example.android.attendance.contracts.ClassContract.ClassEntry;
import com.example.android.attendance.contracts.CollegeContract.CollegeEntry;
import com.example.android.attendance.contracts.LectureContract.LectureEntry;
import com.example.android.attendance.contracts.StudentContract.StudentEntry;
import com.example.android.attendance.contracts.SubjectContract.SubjectEntry;

import java.util.List;

import static com.example.android.attendance.utilities.ExtraUtils.getCurrentDay;


public class DbHelperMethods {
    /************************************************
     ************************************************
     *  All DB related helper methods ***************
     * **********************************************
     ************************************************/

    public static int getClassId(SQLiteDatabase db, int collegeId, String semester,
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

        Cursor cursor = db.query(ClassEntry.TABLE_NAME, projection, selections, selectionArgs,
                null, null, null);
        int classIdIndex = cursor.getColumnIndex(ClassEntry.ID);

        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            //if class is found
            int classId = cursor.getInt(classIdIndex);
            cursor.close();
            return classId;
        } else {
            //if class is not found insert new class
            ContentValues values = new ContentValues();
            values.put(ClassEntry.COLLEGE_ID, collegeId);
            values.put(ClassEntry.SEMESTER, semester);
            values.put(ClassEntry.BRANCH_ID, branchId);
            values.put(ClassEntry.SECTION, section);
            long newRowId = db.insert(ClassEntry.TABLE_NAME, null, values);
            return (int) newRowId;
        }
    }

    public static int getBranchId(SQLiteDatabase db, String branchName) {
        int branchId = -1;

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

        cursor.close();
        return branchId;
    }

    public static int getSubjectId(SQLiteDatabase db, String subject, String branchId,
                                   String semester) {
        int subjectId = -1;

        String selection = SubjectEntry.SUB_NAME_COL + "=?"
                + SubjectEntry.BRANCH_ID_COL + "=?"
                + SubjectEntry.SUB_SEMESTER_COL + "=?";

        String[] selectionArgs = new String[]{subject, branchId, semester};

        Cursor cursor = db.query(SubjectEntry.TABLE_NAME,
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
        cursor.close();
        return subjectId;
    }

    public static int getLectureId(SQLiteDatabase db, String classId, String lectureNo,
                                   String daySelected) {
        int lectureId = -1;

        String[] projection = new String[]{LectureEntry.ID};
        String selection = LectureEntry.CLASS_ID + "=?" + " and "
                + LectureEntry.LECTURE_NUMBER + "=?" + " and "
                + LectureEntry.LECTURE_DAY + "=?";
        String[] selectionArgs = new String[]{classId, lectureNo, daySelected};

        Cursor cursor = db.query(LectureEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs, null, null, null);

        if (cursor.getCount() != 0 && cursor.moveToFirst()) {
            cursor.moveToFirst();
            lectureId = cursor.getInt(cursor.getColumnIndex(LectureEntry.ID));
        }
        cursor.close();
        return lectureId;
    }

    public static Cursor getAttendanceRecordsCursor(SQLiteDatabase db, String facUserId) {

        String projection = AttendanceRecordEntry.TABLE_NAME + "." + AttendanceRecordEntry.ID + ","
                + AttendanceRecordEntry.LECTURE_ID_COL + ","
                + AttendanceRecordEntry.DATE_COL + ","
                + AttendanceRecordEntry.STUDENTS_PRESENT_COL + ","
                + AttendanceRecordEntry.TOTAL_STUDENTS_COL + ","
                + LectureEntry.FAC_USER_ID + ","
                + LectureEntry.TABLE_NAME + "." + LectureEntry.CLASS_ID + ","
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
                + LectureEntry.TABLE_NAME + "." + LectureEntry.CLASS_ID
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

        return db.rawQuery(query, new String[]{facUserId});
    }

    public static Cursor getLectureCursor(SQLiteDatabase db, String facUserId) {

        String time = ExtraUtils.getCurrentTime();

        String projection = LectureEntry.TABLE_NAME + "." + LectureEntry.ID + ","
                + LectureEntry.TABLE_NAME + "." + LectureEntry.CLASS_ID + ","
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
                + LectureEntry.TABLE_NAME + "." + LectureEntry.CLASS_ID
                + " INNER JOIN " + CollegeEntry.TABLE_NAME
                + " ON " + CollegeEntry.TABLE_NAME + "." + CollegeEntry.ID + " = "
                + ClassEntry.COLLEGE_ID
                + " INNER JOIN " + BranchEntry.TABLE_NAME
                + " ON " + BranchEntry.TABLE_NAME + "." + BranchEntry.ID + " = "
                + ClassEntry.TABLE_NAME + "." + ClassEntry.BRANCH_ID
                + " INNER JOIN " + SubjectEntry.TABLE_NAME
                + " ON " + SubjectEntry.TABLE_NAME + "." + SubjectEntry._ID + " = "
                + LectureEntry.SUBJECT_ID;

        String selection = LectureEntry.FAC_USER_ID + "=?" + " and "
                + LectureEntry.LECTURE_DAY + "=?" + " and "
                + LectureEntry.LECTURE_START_TIME + "<=?" + " and "
                + LectureEntry.LECTURE_END_TIME + ">?";

        String[] selectionArgs = new String[]{facUserId, getCurrentDay(), time, time};


        String query = "SELECT " + projection + " FROM " + tableName + " WHERE "
                + selection;

        return db.rawQuery(query, selectionArgs);
    }


    public static Cursor getStudentForUpdateAttendance(SQLiteDatabase db, String attendRecId) {
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
                " ORDER BY " + orderBy;

        return db.rawQuery(query, selectionArgs);
    }

    public static Cursor getStudentForNewAttendance(SQLiteDatabase db, String classId) {
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
                " ORDER BY " + orderBy;

        return db.rawQuery(query, selectionArgs);
    }


    public static int createAttendanceRecord(SQLiteDatabase db, String lectureId,
                                             String date, String classId) {

        ContentValues values = new ContentValues();
        values.put(AttendanceRecordEntry.LECTURE_ID_COL, lectureId);
        values.put(AttendanceRecordEntry.DATE_COL, date);
        values.put(AttendanceRecordEntry.CLASS_ID_COL, classId);

        return (int) db.insert(AttendanceRecordEntry.TABLE_NAME, null, values);
    }

    public static boolean isAttendanceAlreadyExists(SQLiteDatabase db,
                                                    int lectureId, String date) {

        String selection = AttendanceRecordEntry.LECTURE_ID_COL + "=?" + " and " +
                AttendanceRecordEntry.DATE_COL + "=?";

        String[] selectionArgs = new String[]{String.valueOf(lectureId),
                date};

        Cursor cursor = db.query(AttendanceRecordEntry.TABLE_NAME, null, selection,
                        selectionArgs, null, null, null);

        return (cursor != null && cursor.getCount() > 0);
    }

    public static Cursor getClassAttendanceCursor(SQLiteDatabase db, int classId) {

        String projectionString = "students.std_roll_no, students.std_name, " +
                "sum(attendance.attendance_state) as total_present";

        String tableString = " attendance inner join students on students._id = attendance.std_id ";

        String query = "select " + projectionString + " from " + tableString +
                "where class_id =? " +
                "group by std_roll_no;";

        String[] selection = new String[]{String.valueOf(classId)};

        return db.rawQuery(query, selection);
    }

    public static int getTotalClasses(SQLiteDatabase db, int classId) {

        Cursor cursor = db.query(AttendanceRecordEntry.TABLE_NAME,
                null,
                AttendanceRecordEntry.CLASS_ID_COL + "=?",
                new String[]{String.valueOf(classId)},
                null, null, null);

        int totalClasses = cursor.getCount();
        cursor.close();
        return totalClasses;
    }

    public static Cursor getSubjectCursor(SQLiteDatabase db,
                                          String semester, int branchId) {

        String[] projection = new String[]{SubjectEntry._ID, SubjectEntry.SUB_NAME_COL};
        String selection = SubjectEntry.SUB_SEMESTER_COL + "=? and "
                + SubjectEntry.BRANCH_ID_COL + "=?";
        String[] selectionArgs = new String[]{semester, String.valueOf(branchId)};

        return db.query(SubjectEntry.TABLE_NAME, projection,
                selection, selectionArgs, null, null, null);
    }

    /**
     * ##sub_attendance should be used to get attendance of that subject
     *
     * @param currentSubId current subject id for attendance in that particular subject
     * @param classId      current class id
     * @return cursor containing attendance of class id corresponding to given subject id
     */
    public static Cursor getCurrentSubAttendCursor(SQLiteDatabase db,
                                                   int currentSubId, int classId) {

        String innerQuery = "select attendance_records._id" +
                " from attendance_records" +
                " inner join lectures" +
                " on lectures._id = attendance_records.lecture_id" +
                " where attendance_records.class_id =? and lectures.subject_id =?";

        String query = "select std_name,std_roll_no, sum(attendance_state) as sub_attendance" +
                " from attendance inner join students" +
                " on students._id = attendance.std_id" +
                " where attendance.attendance_record_id in"
                + " (" + innerQuery + ") " + "group by std_roll_no";

        String[] selection = new String[]{String.valueOf(classId),
                String.valueOf(currentSubId)};


        return db.rawQuery(query, selection);
    }

    public static Cursor getTotalLecturesForEachSub(SQLiteDatabase db, int classId) {

        String projection = "subjects.sub_name,count(attendance_records._id) as sub_total_lect";
        String table = "lectures left join attendance_records" +
                " on lectures._id = attendance_records.lecture_id inner join subjects" +
                " on lectures.subject_id = subjects._id";
        String query = "select " +projection+ " from " +table+
                " where lectures.class_id =? group by lectures.subject_id";

        return db.rawQuery(query, new String[]{String.valueOf(classId)});
    }
}
