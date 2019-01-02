package com.example.android.attendance.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.android.attendance.BuildConfig;
import com.example.android.attendance.R;
import com.example.android.attendance.StudentReportActivity;
import com.example.android.attendance.pojos.Report;
import com.example.android.attendance.pojos.SubReport;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PdfUtils {

    private static final int NO_OF_FIX_COL = 5;
    private static BaseColor colorLtGrey = new BaseColor(Color.LTGRAY);
    private static BaseColor colorWhite = new BaseColor(Color.WHITE);

    public static void generatePdf(Context context, List<Report> reports,
                                   List<SubReport> subReports, int attendTaken,
                                   String collName, Bundle classDetails, ProgressDialog writingDialog) {
        //First Check if the external storage is writable
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(context, "Storage cannot be written!", Toast.LENGTH_SHORT).show();
        } else {
            String semester = classDetails.getString(ExtraUtils.EXTRA_SEMESTER);
            String branch = classDetails.getString(ExtraUtils.EXTRA_BRANCH);
            String section = classDetails.getString(ExtraUtils.EXTRA_SECTION);

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

            /** For making image compatible for pdf **/
            /*   Image bgImage;
            //set drawable in cell
            Drawable myImage = context.getResources().getDrawable(R.drawable.trinity);
            Bitmap bitmap = ((BitmapDrawable) myImage).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bitmapdata = stream.toByteArray();*/

            /** for adding heading and image to the pdf **/
            /*  bgImage = Image.getInstance(bitmapdata);
            bgImage.setAbsolutePosition(330f, 642f);
            cell.addElement(bgImage);
            pt.addCell(cell);    */

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

                pCell.setPhrase(new Phrase(collName));
                parentTable.addCell(pCell);

                String phrase = ExtraUtils.getSemester(semester) + "  " + branch + "  " + section;
                pCell.setPhrase(new Phrase(phrase));
                parentTable.addCell(pCell);

                pCell.setPhrase(new Phrase(" "));
                parentTable.addCell(pCell);

                //setup child table
                int numColumns = NO_OF_FIX_COL + subReports.size();
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
                for (int i = 0; i < subReports.size(); i++) widthList.add(10f);

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
                for (SubReport subReport : subReports) {
                    cell.setPhrase(new Phrase(subReport.getSubName()));
                    table.addCell(cell);
                }
                //header representing total classes and sub wise total classes
                cell.setPhrase(new Phrase("(" + String.valueOf(attendTaken) + ")"));
                table.addCell(cell);

                cell.setPhrase(new Phrase("(" + String.valueOf(100) + ")"));
                table.addCell(cell);

                for (SubReport subReport : subReports) {
                    cell.setPhrase(new Phrase("(" + subReport.getSubTotalLect() + ")"));
                    table.addCell(cell);
                }

                //row entries for each student
                for (int i = 0; i < reports.size(); i++) {

                    Report currentStdReport = reports.get(i);

                    String stdName = currentStdReport.getStdName();
                    String stdRollNo = currentStdReport.getStdRollNo();
                    String stdTotalPresent = currentStdReport.getTotalPresent();
                    float totalPercentage =
                            (Float.parseFloat(stdTotalPresent) / (float) attendTaken) * 100;

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

                    //set bgColor back to white after setting bgColor of % cell
                    cell.setBackgroundColor(colorWhite);

                    int k = 0;
                    int subTotalLecture;

                    for (String noOfPresent : subWisePresents) {

                        subTotalLecture = subReports.get(k).getSubTotalLect();
                        float subWisePercent = (Float.valueOf(noOfPresent) / (float) subTotalLecture) * 100;

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
                        + ExtraUtils.getCurrentDate()
                        + ", "
                        + ExtraUtils.getCurrentDay();
                pCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                pCell.setPhrase(new Phrase(moment));
                parentTable.addCell(pCell);

                if (document.add(parentTable)) {
                    writingDialog.dismiss();
                    renameFileDialog(context, pdfFile);
                }
            } catch (DocumentException de) {
                Log.e("PDFCreator", "DocumentException:" + de);
            } finally {
                document.close();
                writingDialog.dismiss();
            }
        }
    }

    private static void renameFileDialog(final Context context, final File pdfFile) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("PDF Created.");
        View dialogView = View.inflate(context, R.layout.input_dialog_layout, null);
        // Set up the input
        final EditText input = dialogView.findViewById(R.id.new_pdf_name_et);
        input.setText(pdfFile.getName());
        builder.setView(dialogView);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String newName = input.getText().toString().trim();

                if (newName.equals("") || newName.startsWith(" ")) {

                    builder.show();
                    input.setError("Enter valid Filename.");
                } else {
                    File newFile = new File(context.getExternalFilesDir("pdf"),
                            newName + ".pdf");

                    boolean renamed = pdfFile.renameTo(newFile);
                    dialog.cancel();
                    if (renamed) {
                        Toast.makeText(context, "saved at " + newFile.getAbsolutePath(),
                                Toast.LENGTH_LONG)
                                .show();
                        showShareDialog(context, newFile);
                    }
                    else {
                        Toast.makeText(context, "saved at " + pdfFile.getAbsolutePath(),
                                Toast.LENGTH_LONG)
                                .show();
                        showShareDialog(context, pdfFile);
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "saved at " + pdfFile.getAbsolutePath(),
                        Toast.LENGTH_LONG)
                        .show();
                showShareDialog(context, pdfFile);
            }
        });
        builder.show();
    }

    private static void showShareDialog(final Context context, final File pdfFile) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Share file?");

        // Set up the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = FileProvider.getUriForFile(context,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        pdfFile);

                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/pdf");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(share);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}


