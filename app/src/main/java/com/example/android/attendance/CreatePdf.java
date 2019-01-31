package com.example.android.attendance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.attendance.pojos.Report;
import com.example.android.attendance.pojos.SubReport;
import com.example.android.attendance.utilities.ExtraUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;

import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreatePdf extends AsyncTask<String, Void, File> {

    private static final int NO_OF_FIX_COL = 5;
    private static BaseColor colorLtGrey = new BaseColor(Color.LTGRAY);
    private static BaseColor colorWhite = new BaseColor(Color.WHITE);
    private ProgressDialog writingDialog;

    private WeakReference<Context> mContextRef;
    private List<Report> mReports;
    private List<SubReport> mSubReports;
    private int mAttendTaken;
    private String mCollegeName;
    private Bundle mClassDetails;

    public CreatePdf(Context context, List<Report> mReports, List<SubReport> mSubReports,
                     int mAttendTaken, String mCollegeName, Bundle mClassDetails) {
        this.mContextRef = new WeakReference<>(context);
        this.mReports = mReports;
        this.mSubReports = mSubReports;
        this.mAttendTaken = mAttendTaken;
        this.mCollegeName = mCollegeName;
        this.mClassDetails = mClassDetails;
    }

    @Override
    protected File doInBackground(String... params) {
        return generatePdf();
    }

    @Override
    protected void onPreExecute() {
        Context context = mContextRef.get();
        writingDialog = new ProgressDialog(context);
        writingDialog.setMessage("Writing...");
        writingDialog.show();
    }

    protected void onPostExecute(File pdfFile) {
        Context context = mContextRef.get();
        writingDialog.dismiss();
        if (pdfFile != null)
            renameFileDialog(context, pdfFile);
        else
            Toast.makeText(context, "Error generating Pdf.",Toast.LENGTH_SHORT).show();

    }

    private File generatePdf() {
        Context context = mContextRef.get();
        //First Check if the external storage is writable
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(context, "Storage cannot be written!", Toast.LENGTH_SHORT).show();
        } else {
            String semester = mClassDetails.getString(ExtraUtils.EXTRA_SEMESTER);
            String branch = mClassDetails.getString(ExtraUtils.EXTRA_BRANCH);
            String section = mClassDetails.getString(ExtraUtils.EXTRA_SECTION);
            boolean isDateWise = mClassDetails.getBoolean(ExtraUtils.EXTRA_IS_DATE_WISE);

            //Create a directory for your PDF
            String pdfName = semester + branch + section + "_" +
                    String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".pdf";
            File pdfFile = new File(context.getExternalFilesDir("pdf"), pdfName);

            Document document = new Document();

            try {
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            } catch (DocumentException | FileNotFoundException e) {
                e.printStackTrace();
            }

            document.open();

            try {

                //parent table holding header and the report
                PdfPTable parentTable = new PdfPTable(1);
                parentTable.setTotalWidth(PageSize.A4.getWidth() - 10);
                parentTable.setLockedWidth(true);

                //Header of the table
                PdfPCell pCell = new PdfPCell();
                pCell.setPadding(4);
                pCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pCell.setVerticalAlignment(Element.ALIGN_MIDDLE);


                pCell.setPhrase(new Phrase("Attendance Report"));
                parentTable.addCell(pCell);

                pCell.setPhrase(new Phrase(mCollegeName));
                parentTable.addCell(pCell);

                String phrase = ExtraUtils.getSemester(semester) + "  " + branch + "  " + section;
                pCell.setPhrase(new Phrase(phrase));
                parentTable.addCell(pCell);

                if (isDateWise) {
                    String fromDate = mClassDetails.getString(ExtraUtils.EXTRA_FROM_DATE);
                    String toDate = mClassDetails.getString(ExtraUtils.EXTRA_TO_DATE);

                    String dateString = "From "+fromDate +" To " +toDate;
                    pCell.setPhrase(new Phrase(dateString));
                    parentTable.addCell(pCell);
                }

                pCell.setPhrase(new Phrase(" "));
                parentTable.addCell(pCell);

                //setup child table
                int numColumns = NO_OF_FIX_COL + mSubReports.size();
                PdfPTable table = new PdfPTable(numColumns);

                table.setTotalWidth(PageSize.A4.getWidth() - 12);
                table.setLockedWidth(true);


                //set the width of column
                List<Float> widthList = new ArrayList<>();
                widthList.add(5f);
                widthList.add(25f);
                widthList.add(20f);
                widthList.add(8f);
                widthList.add(8f);
                for (int i = 0; i < mSubReports.size(); i++) widthList.add(10f);

                float[] columnWidth = new float[widthList.size()];
                int j = 0;

                for (Float f : widthList) columnWidth[j++] = (f != null ? f : Float.NaN);

                table.setWidths(columnWidth);

                PdfPCell cell = new PdfPCell();
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setRowspan(2);
                cell.setPadding(2);

                cell.setPhrase(new Phrase("#"));
                table.addCell(cell);

                cell.setPhrase(new Phrase("Name"));
                table.addCell(cell);

                cell.setPhrase(new Phrase("Roll No."));
                table.addCell(cell);


                //set rowspan=1 for remaining columns
                cell.setRowspan(1);


                cell.setPhrase(new Phrase("Total"));
                table.addCell(cell);

                cell.setPhrase(new Phrase("%"));
                table.addCell(cell);

                //header for each sub name
                for (SubReport subReport : mSubReports) {
                    cell.setPhrase(new Phrase(subReport.getSubName()));
                    table.addCell(cell);
                }
                //header representing total classes and sub wise total classes
                cell.setPhrase(new Phrase("(" + String.valueOf(mAttendTaken) + ")"));
                table.addCell(cell);

                cell.setPhrase(new Phrase("(" + String.valueOf(100) + ")"));
                table.addCell(cell);

                for (SubReport subReport : mSubReports) {
                    cell.setPhrase(new Phrase("(" + subReport.getSubTotalLect() + ")"));
                    table.addCell(cell);
                }

                //row entries for each student
                for (int i = 0; i < mReports.size(); i++) {
                    cell.setBackgroundColor(colorWhite);

                    Report currentStdReport = mReports.get(i);

                    String stdName = currentStdReport.getStdName();
                    String stdRollNo = currentStdReport.getStdRollNo();
                    String stdTotalPresent = currentStdReport.getTotalPresent();
                    float totalPercentage = 0;

                    if (mAttendTaken > 0) {
                        totalPercentage = (Float.parseFloat(stdTotalPresent) / (float) mAttendTaken) * 100;
                    }

                    String[] subWisePresents =
                            currentStdReport.getSubWiseAttend().toArray(new String[0]);

                    cell.setPhrase(new Phrase(String.valueOf(i + 1)));
                    table.addCell(cell);
                    cell.setPhrase(new Phrase(stdName));
                    table.addCell(cell);
                    cell.setPhrase(new Phrase(stdRollNo));
                    table.addCell(cell);
                    cell.setPhrase(new Phrase(stdTotalPresent));
                    table.addCell(cell);
                    if (totalPercentage < 75) cell.setBackgroundColor(colorLtGrey);
                    String percentage = String.format(Locale.US, "%.1f", totalPercentage);
                    cell.setPhrase(new Phrase(percentage));
                    table.addCell(cell);

                    int k = 0;
                    int subTotalLecture;

                    for (String noOfPresent : subWisePresents) {

                        subTotalLecture = mSubReports.get(k).getSubTotalLect();
                        float subWisePercent = 0;
                        if (subTotalLecture > 0) {
                            subWisePercent = (Float.valueOf(noOfPresent) / (float) subTotalLecture) * 100;
                        }

                        if (subWisePercent < 75) cell.setBackgroundColor(colorLtGrey);
                        else cell.setBackgroundColor(colorWhite);

                        cell.setPhrase(new Phrase(String.valueOf(noOfPresent)));
                        table.addCell(cell);
                        k++;
                    }
                }
                parentTable.addCell(table);

                //moment at which report is generated
                String moment = ExtraUtils.getCurrentTime()
                        + " "
                        + ExtraUtils.getCurrentDateDisplay()
                        + ", "
                        + ExtraUtils.getCurrentDay();
                pCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                pCell.setPhrase(new Phrase(moment));
                parentTable.addCell(pCell);

                if (document.add(parentTable)) {
                    return pdfFile;
                }
            } catch (DocumentException de) {
                Log.e("CreatePdf", "DocumentException:" + de);
                writingDialog.dismiss();
                return null;
            } finally {
                document.close();
            }
        }
        return null;
    }

    private void renameFileDialog(final Context context, final File pdfFile) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("PDF Created.");
        View dialogView = View.inflate(context, R.layout.input_dialog_layout, null);
        // Set up the input
        final EditText input = dialogView.findViewById(R.id.new_pdf_name_et);
        input.setText(pdfFile.getName());
        builder.setView(dialogView);

        // Set up the buttons
        builder.setPositiveButton("Save & Open", (dialog, which) -> {

            String newName = input.getText().toString().trim();

            if (newName.equals("") || newName.startsWith(" ")) {

                builder.show();
                input.setError("Enter valid Filename.");
            } else {
                if (!newName.endsWith(".pdf")) {
                    newName.concat(".pdf");
                }
                File newFile = new File(context.getExternalFilesDir("pdf"), newName);

                boolean renamed = pdfFile.renameTo(newFile);
                dialog.cancel();
                if (renamed) {
                    Toast.makeText(context, "saved at " + newFile.getAbsolutePath(),
                            Toast.LENGTH_LONG)
                            .show();
                    openPdf(context, newFile);
                    showShareDialog(context, newFile);
                } else {
                    Toast.makeText(context, "saved at " + pdfFile.getAbsolutePath(),
                            Toast.LENGTH_LONG)
                            .show();
                    openPdf(context, pdfFile);
                    showShareDialog(context, pdfFile);
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            Toast.makeText(context, "saved at " + pdfFile.getAbsolutePath(),
                    Toast.LENGTH_LONG)
                    .show();
            showShareDialog(context, pdfFile);
        });
        builder.show();
    }

    private void showShareDialog(final Context context, final File pdfFile) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Share file?");

        // Set up the buttons
        builder.setPositiveButton("Yes", (dialog, which) -> {
            Uri uri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    pdfFile);

            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(share);
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void openPdf(final Context context, File pdfFile) {

        // Get URI and MIME type of file
        Uri uri = FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                pdfFile);
        String mime = context.getContentResolver().getType(uri);

        // Open file with user selected app
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    public static TextView getTextView(Context context, int textSize) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(textSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextColor(context.getColor(android.R.color.black));
        }
        tv.setPadding(4, 4, 4, 4);
        return tv;
    }


}



