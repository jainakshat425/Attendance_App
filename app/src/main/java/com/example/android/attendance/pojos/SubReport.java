
package com.example.android.attendance.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubReport {

    @SerializedName("subject_id")
    @Expose
    private Integer subjectId;
    @SerializedName("sub_name")
    @Expose
    private String subName;
    @SerializedName("sub_total_lect")
    @Expose
    private Integer subTotalLect;

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public Integer getSubTotalLect() {
        return subTotalLect;
    }

    public void setSubTotalLect(Integer subTotalLect) {
        this.subTotalLect = subTotalLect;
    }

}