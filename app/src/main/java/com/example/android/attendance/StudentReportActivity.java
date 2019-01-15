package com.example.android.attendance;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.android.attendance.adapters.ReportAdapter;
import com.example.android.attendance.pojos.Report;
import com.example.android.attendance.pojos.SubReport;
import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.utilities.GsonUtils;
import com.example.android.attendance.volley.VolleyCallback;
import com.example.android.attendance.volley.VolleyTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentReportActivity extends AppCompatActivity {

    @BindView(R.id.rv_show_attendance)
    RecyclerView mRecyclerView;

    private ReportAdapter mAdapter;
    private CreatePdf mCreatePdf;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_attendance);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);

        mAdapter = new ReportAdapter(this, new ArrayList<Report>(), 0);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        final Bundle classDetails = getIntent().getExtras();
        if (classDetails != null) {

            int branchId = classDetails.getInt(ExtraUtils.EXTRA_BRANCH_ID);
            int classId = classDetails.getInt(ExtraUtils.EXTRA_CLASS_ID);
            int collId = classDetails.getInt(ExtraUtils.EXTRA_COLLEGE_ID);
            boolean isDayWise = classDetails.getBoolean(ExtraUtils.EXTRA_IS_DATE_WISE);
            String fromDate = null;
            String toDate = null;
            if (isDayWise) {
                fromDate = classDetails.getString(ExtraUtils.EXTRA_FROM_DATE);
                toDate = classDetails.getString(ExtraUtils.EXTRA_TO_DATE);
            }

            VolleyTask.showReport(this,  branchId, classId, collId,
                    isDayWise, fromDate, toDate, new VolleyCallback() {
                @Override
                public void onSuccessResponse(JSONObject jObj) {
                    List<Report> reports = GsonUtils.extractReportsFromJson(jObj);
                    List<SubReport> subReports = GsonUtils.extractSubReportsFromJson(jObj);
                    showSubReport(subReports);
                    try {
                        int attendTaken = jObj.getInt("attend_taken");
                        String collName = jObj.getString("coll_full_name");

                        mAdapter.swapList(reports, attendTaken);
                        mCreatePdf = new CreatePdf(StudentReportActivity.this, reports,
                                subReports, attendTaken, collName, classDetails);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void showSubReport(List<SubReport> subReports) {
        TableLayout totalSubLectTable = findViewById(R.id.sub_total_lect_table);
        TableRow rowHeader = new TableRow(this);
        rowHeader.setBackgroundColor(Color.parseColor("#c0c0c0"));
        rowHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        if (subReports != null && subReports.size() > 0) {

            for (SubReport subReport : subReports) {

                String subName = subReport.getSubName();
                int subTotalLect = subReport.getSubTotalLect();

                TextView subNameTv = ExtraUtils.getTextView(this, 14);
                subNameTv.setText(subName);
                rowHeader.addView(subNameTv);
                TextView tv = ExtraUtils.getTextView(this, 16);
                tv.setText(String.valueOf(subTotalLect));
                row.addView(tv);
            }
            totalSubLectTable.addView(rowHeader);
            totalSubLectTable.addView(row);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_report, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.item_create_pdf:
                if (mCreatePdf != null)
                    mCreatePdf.execute();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
