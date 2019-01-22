package com.example.android.attendance.utilities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.android.attendance.CheckAttendanceActivity;
import com.example.android.attendance.adapters.SpinnerArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExtraUtils {

    public static final  SimpleDateFormat dateDisplayFormat =
            new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

    public static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static final SimpleDateFormat dayFormat =
            new SimpleDateFormat("EEEE", Locale.US);

    public static final SimpleDateFormat timeFormat =
            new SimpleDateFormat("HH:mm:ss", Locale.US);

    public static final SimpleDateFormat timeDisplayFormat =
            new SimpleDateFormat("hh:mm a", Locale.US);

    public static final String EXTRA_COLLEGE_ID = "extra_college_id";
    public static final String EXTRA_CLASS_ID = "extra_class_id";
    public static final String EXTRA_BRANCH_ID = "extra_branch_id";
    public static final String EXTRA_SUBJECT_ID = "extra_subject_id";
    public static final String EXTRA_ATTEND_REC_ID = "extra_attend_rec_id";

    public static final String EXTRA_COLLEGE = "extra_college";
    public static final String EXTRA_SEMESTER = "extra_semester";
    public static final String EXTRA_BRANCH = "extra_branch";
    public static final String EXTRA_SECTION = "extra_section";

    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_DAY = "extra_day";
    public static final String EXTRA_DISPLAY_DATE = "extra_display_date" ;
    public static final String EXTRA_FROM_DATE = "extra_from_date" ;
    public static final String EXTRA_TO_DATE = "extra_to_date" ;
    public static final String EXTRA_IS_DATE_WISE = "extra_is_date_wise" ;

    public static final String EXTRA_SUBJECT = "extra_subject";
    public static final String EXTRA_LECTURE_NO = "extra_lecture";

    public static final String EXTRA_FAC_EMAIL = "extra_fac_email";
    public static final String EXTRA_FAC_NAME = "extra_fac_name";
    public static final String EXTRA_FAC_DEPT = "extra_fac_dept";



    private static final String DB_URL = "http://10.0.11.249/attendancephp/v1/";
    public static final String FAC_LOGIN_URL = DB_URL + "facLogin.php";
    public static final String CHANGE_FACULTY_PASS_URL = DB_URL + "changeFacultyPassword.php";

    public static final String GET_ATT_REC_URL = DB_URL + "getAttendanceRecords.php";
    public static final String CHECK_ATTEND_ALREADY_EXIST = DB_URL + "checkAttendAlreadyExist.php";
    public static final String SETUP_NEW_ATTEND_URL = DB_URL + "setupNewAttendance.php";
    public static final String SETUP_UPDATE_ATTEND_URL = DB_URL + "setupUpdateAttendance.php";
    public static final String SAVE_NEW_ATTEND_URL = DB_URL + "saveNewAttendance.php";
    public static final String UPDATE_ATTEND_URL = DB_URL + "updateAttendance.php";
    public static final String DELETE_RECORD_URL = DB_URL + "deleteAttendRecord.php";
    public static final String GET_BRANCH_NAMES_URL = DB_URL + "getBranchNames.php";
    public static final String GET_SECS_URL = DB_URL + "getSections.php";
    public static final String GET_LECTURE_NUMBERS_URL = DB_URL + "getLectureNumbers.php";
    public static final String GET_FAC_SCH_URL = DB_URL + "getFacSchedules.php";
    public static final String CHECK_VALID_CLASS_URL = DB_URL + "checkValidClass.php";
    public static final String GET_STD_REPORT_URL = DB_URL + "getStudentReport.php";


    public static String getLecture(String lecture) {
        if (Integer.parseInt(lecture) == 1) {
            lecture = lecture + "st Lecture";
        } else if (Integer.parseInt(lecture) == 2) {
            lecture = lecture + "nd Lecture";
        } else if (Integer.parseInt(lecture) == 3) {
            lecture = lecture + "rd Lecture";
        } else {
            lecture = lecture + "th Lecture";
        }
        return lecture;
    }

    public static String getSemester(String semester) {
        if (Integer.parseInt(semester) == 1) {
            semester = semester + "st Sem";
        } else if (Integer.parseInt(semester) == 2) {
            semester = semester + "nd Sem";
        } else if (Integer.parseInt(semester) == 3) {
            semester = semester + "rd Sem";
        } else {
            semester = semester + "th Sem";
        }
        return semester;
    }

    public static String getCurrentDay() {
        String day = dayFormat.format(Calendar.getInstance().getTime());
        return day.toUpperCase();
    }

    public static String getCurrentDate() {
        String date = dateFormat.format(Calendar.getInstance().getTime());
        return date;
    }

    public static String getCurrentDateDisplay() {
        String date = dateDisplayFormat.format(Calendar.getInstance().getTime());
        return date;
    }

    public static String getCurrentTime() {
        String time = timeFormat.format(Calendar.getInstance().getTime());
        return time;
    }

    public static String getCurrentTimeDisplay() {
        String time = timeFormat.format(Calendar.getInstance().getTime());
        return time;
    }

    public static TextView getTextView(Context context, int textSize) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(textSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextColor(context.getColor(android.R.color.black));
        }
        tv.setPadding(4, 4, 4, 4);
        return tv;
    }

    /**
     * empties the subject spinner
     */
    public static void emptySubjectSpinner(Context context, Spinner subSpinner) {
        String[] subject = {"Subject"};
        SpinnerArrayAdapter subjectAdapter = new SpinnerArrayAdapter(context,
                android.R.layout.simple_spinner_dropdown_item, subject);
        subSpinner.setAdapter(subjectAdapter);
    }

    /**
     * empties the section spinner
     */
    public static void emptySectionSpinner(Context context, Spinner secSpinner) {
        String[] section = {"Section"};
        SpinnerArrayAdapter sectionAdapter = new SpinnerArrayAdapter(context,
                android.R.layout.simple_spinner_dropdown_item, section);
        secSpinner.setAdapter(sectionAdapter);
    }

}

