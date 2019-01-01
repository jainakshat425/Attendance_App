package com.example.android.attendance.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.attendance.contracts.AttendanceRecordContract.AttendanceRecordEntry;
import com.example.android.attendance.contracts.BranchContract.BranchEntry;
import com.example.android.attendance.contracts.ClassContract.ClassEntry;
import com.example.android.attendance.contracts.CollegeContract.CollegeEntry;
import com.example.android.attendance.contracts.LectureContract.LectureEntry;
import com.example.android.attendance.contracts.StudentContract.StudentEntry;
import com.example.android.attendance.contracts.SubjectContract.SubjectEntry;
import com.example.android.attendance.utilities.ExtraUtils;

import static com.example.android.attendance.utilities.ExtraUtils.getCurrentDay;


public class DbHelperMethods {
    /************************************************
     ************************************************
     *  All DB related helper methods ***************
     * **********************************************
     ************************************************/

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
        String query = "select " + projection + " from " + table +
                " where lectures.class_id =? group by lectures.subject_id";

        return db.rawQuery(query, new String[]{String.valueOf(classId)});
    }

    public static String getCollegeFullName(SQLiteDatabase db, String collegeId) {

        Cursor cursor = db.query(CollegeEntry.TABLE_NAME, new String[]{CollegeEntry.FULL_NAME},
                CollegeEntry.ID + "=?", new String[]{collegeId}, null,
                null, null);

        String collegeName = "";
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            collegeName = cursor.getString(cursor.getColumnIndex(CollegeEntry.FULL_NAME));
        }
        cursor.close();
        return collegeName;
    }
}
