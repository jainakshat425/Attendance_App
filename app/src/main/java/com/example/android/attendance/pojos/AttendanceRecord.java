package com.example.android.attendance.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AttendanceRecord {

    @SerializedName("_id")
    @Expose
    private Integer id;
    @SerializedName("lecture_id")
    @Expose
    private Integer lectureId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("students_present")
    @Expose
    private Integer studentsPresent;
    @SerializedName("total_students")
    @Expose
    private Integer totalStudents;
    @SerializedName("fac_user_id")
    @Expose
    private String facUserId;
    @SerializedName("class_id")
    @Expose
    private Integer classId;
    @SerializedName("subject_id")
    @Expose
    private Integer subjectId;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("lect_no")
    @Expose
    private Integer lectNo;
    @SerializedName("college_id")
    @Expose
    private Integer collegeId;
    @SerializedName("sem")
    @Expose
    private Integer sem;
    @SerializedName("branch_id")
    @Expose
    private Integer branchId;
    @SerializedName("section")
    @Expose
    private String section;
    @SerializedName("coll_name")
    @Expose
    private String collName;
    @SerializedName("b_name")
    @Expose
    private String bName;
    @SerializedName("sub_name")
    @Expose
    private String subName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLectureId() {
        return lectureId;
    }

    public void setLectureId(Integer lectureId) {
        this.lectureId = lectureId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getStudentsPresent() {
        return studentsPresent;
    }

    public void setStudentsPresent(Integer studentsPresent) {
        this.studentsPresent = studentsPresent;
    }

    public Integer getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Integer totalStudents) {
        this.totalStudents = totalStudents;
    }

    public String getFacUserId() {
        return facUserId;
    }

    public void setFacUserId(String facUserId) {
        this.facUserId = facUserId;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Integer getLectNo() {
        return lectNo;
    }

    public void setLectNo(Integer lectNo) {
        this.lectNo = lectNo;
    }

    public Integer getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Integer collegeId) {
        this.collegeId = collegeId;
    }

    public Integer getSem() {
        return sem;
    }

    public void setSem(Integer sem) {
        this.sem = sem;
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getCollName() {
        return collName;
    }

    public void setCollName(String collName) {
        this.collName = collName;
    }

    public String getBName() {
        return bName;
    }

    public void setBName(String bName) {
        this.bName = bName;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

}