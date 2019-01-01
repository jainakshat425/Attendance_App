package com.example.android.attendance.pojos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Report {

    @SerializedName("student_id")
    @Expose
    private Integer studentId;
    @SerializedName("std_roll_no")
    @Expose
    private String stdRollNo;
    @SerializedName("std_name")
    @Expose
    private String stdName;
    @SerializedName("total_present")
    @Expose
    private String totalPresent;
    @SerializedName("sub_wise_attend")
    @Expose
    private List<String> subWiseAttend = null;

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

    public String getTotalPresent() {
        return totalPresent;
    }

    public void setTotalPresent(String totalPresent) {
        this.totalPresent = totalPresent;
    }

    public List<String> getSubWiseAttend() {
        return subWiseAttend;
    }

    public void setSubWiseAttend(List<String> subWiseAttend) {
        this.subWiseAttend = subWiseAttend;
    }

}