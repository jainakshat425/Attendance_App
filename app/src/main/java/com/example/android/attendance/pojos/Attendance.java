package com.example.android.attendance.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attendance {

    @SerializedName("attendance_record_id")
    @Expose
    private Integer attendanceRecordId;
    @SerializedName("attendance_state")
    @Expose
    private Integer attendanceState;
    @SerializedName("_id")
    @Expose
    private Integer id;
    @SerializedName("student_id")
    @Expose
    private Integer studentId;
    @SerializedName("std_roll_no")
    @Expose
    private String stdRollNo;
    @SerializedName("std_name")
    @Expose
    private String stdName;
    @SerializedName("class_id")
    @Expose
    private Integer classId;

    public Integer getAttendanceRecordId() {
        return attendanceRecordId;
    }

    public void setAttendanceRecordId(Integer attendanceRecordId) {
        this.attendanceRecordId = attendanceRecordId;
    }

    public Integer getAttendanceState() {
        return attendanceState;
    }

    public void setAttendanceState(Integer attendanceState) {
        this.attendanceState = attendanceState;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getStdRollNo() {
        return stdRollNo;
    }

    public void setStdRollNo(String stdRollNo) {
        this.stdRollNo = stdRollNo;
    }

    public String getStdName() {
        return stdName;
    }

    public void setStdName(String stdName) {
        this.stdName = stdName;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

}