package com.example.android.attendance.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Schedule {

    @SerializedName("coll_id")
    @Expose
    private Integer collId;
    @SerializedName("semester")
    @Expose
    private Integer sem;
    @SerializedName("class_id")
    @Expose
    private Integer classId;
    @SerializedName("lect_id")
    @Expose
    private Integer lectId;
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
    private String lectStartTime;
    @SerializedName("lect_end_time")
    @Expose
    private String lectEndTime;

    public Integer getCollId() {
        return collId;
    }

    public void setCollId(Integer collId) {
        this.collId = collId;
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

    public String getLectStartTime() {
        return lectStartTime;
    }

    public void setLectStartTime(String lectStartTime) {
        this.lectStartTime = lectStartTime;
    }

    public String getLectEndTime() {
        return lectEndTime;
    }

    public void setLectEndTime(String lectEndTime) {
        this.lectEndTime = lectEndTime;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getLectId() {
        return lectId;
    }

    public void setLectId(Integer lectId) {
        this.lectId = lectId;
    }
}