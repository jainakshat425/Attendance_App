package com.example.android.attendance.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Schedule {

    @SerializedName("coll_name")
    @Expose
    private String collName;
    @SerializedName("sem")
    @Expose
    private Integer sem;
    @SerializedName("b_name")
    @Expose
    private String bName;
    @SerializedName("section")
    @Expose
    private String section;
    @SerializedName("lect_no")
    @Expose
    private Integer lectNo;
    @SerializedName("sub_name")
    @Expose
    private String subName;
    @SerializedName("lect_start_time")
    @Expose
    private Integer lectStartTime;
    @SerializedName("lect_end_time")
    @Expose
    private Integer lectEndTime;

    public String getCollName() {
        return collName;
    }

    public void setCollName(String collName) {
        this.collName = collName;
    }

    public Integer getSem() {
        return sem;
    }

    public void setSem(Integer sem) {
        this.sem = sem;
    }

    public String getBName() {
        return bName;
    }

    public void setBName(String bName) {
        this.bName = bName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getLectNo() {
        return lectNo;
    }

    public void setLectNo(Integer lectNo) {
        this.lectNo = lectNo;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public Integer getLectStartTime() {
        return lectStartTime;
    }

    public void setLectStartTime(Integer lectStartTime) {
        this.lectStartTime = lectStartTime;
    }

    public Integer getLectEndTime() {
        return lectEndTime;
    }

    public void setLectEndTime(Integer lectEndTime) {
        this.lectEndTime = lectEndTime;
    }

}