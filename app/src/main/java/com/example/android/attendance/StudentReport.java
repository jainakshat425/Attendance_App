package com.example.android.attendance;

import java.util.List;

public class StudentReport {

    private String mName;
    private String mRollNo;
    private int mTotalPresent;
    private List<Integer> mSubAttendance;

    public StudentReport(String name, String rollNo, int totalPresent,
                         List<Integer> subAttendance) {
        this.mName = name;
        this.mRollNo = rollNo;
        this.mTotalPresent = totalPresent;
        this.mSubAttendance = subAttendance;
    }

    public List<Integer> getmSubAttendance() {
        return mSubAttendance;
    }

    public String getmName() {
        return mName;
    }

    public String getmRollNo() {
        return mRollNo;
    }

    public int getmTotalPresent() {
        return mTotalPresent;
    }

    public void setmTotalPresent(int mTotalPresent) {
        this.mTotalPresent = mTotalPresent;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmRollNo(String mRollNo) {
        this.mRollNo = mRollNo;
    }

    public void setmSubAttendance(List<Integer> mSubAttendance) {
        this.mSubAttendance = mSubAttendance;
    }
}
