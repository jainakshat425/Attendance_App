package com.example.android.attendance;

import android.graphics.Color;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.attendance.adapters.ReportAdapter;
import com.example.android.attendance.pojos.Report;
import com.example.android.attendance.pojos.SubReport;
import com.example.android.attendance.utilities.ExtraUtils;
import com.example.android.attendance.volley.VolleyTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentReportActivity extends AppCompatActivity {

    @BindView(R.id.rv_show_attendance)
    RecyclerView mRecyclerView;
    @BindView(R.id.save_pdf_fab)
    FloatingActionButton savePdfFab;

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

        ReportAdapter adapter = new ReportAdapter(this, new ArrayList<Report>(), 0);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        Bundle classDetails = getIntent().getExtras();
        if (classDetails != null)
            VolleyTask.showReport(this, classDetails, adapter, savePdfFab);
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

        if (subReports != null && subReports.size() > 1) {

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
}
