package com.example.android.attendance.utilities;

import com.example.android.attendance.pojos.AttendanceRecord;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Akshat Jain on 29-Dec-18.
 */
public class GsonUtils {

    public static List<AttendanceRecord> extractRecordsFromJSON(JSONObject jObj) {

        try {
            String recordsArray = jObj.getString("attendanceRecord");

            Gson gson = new Gson();
            AttendanceRecord[] targetArray = gson.fromJson(recordsArray, AttendanceRecord[].class);

            return Arrays.asList(targetArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
