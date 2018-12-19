package com.example.android.attendance.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.widget.Toast;

import com.example.android.attendance.StudentReport;
import com.example.android.attendance.data.DbHelperMethods;
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
import java.util.List;

public class PdfUtils {

    private static final int NO_OF_FIX_COL = 5;
    private static BaseColor colorLtGrey = new BaseColor(Color.LTGRAY);
    private static BaseColor colorWhite = new BaseColor(Color.WHITE);

    public static void generatePdf(Context context, SQLiteDatabase db, List<StudentReport> stdReportList,
                                   ArrayList<String> subNameList, int totalClasses,
                                   List<Integer> totalLectForSub, Bundle classDetails) {

        //First Check if the external storage is writable
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(context, "Storage cannot be written!", Toast.LENGTH_SHORT).show();
        } else {

            String collegeId = classDetails.getString(ExtraUtils.EXTRA_COLLEGE_ID);
            String semester = classDetails.getString(ExtraUtils.EXTRA_SEMESTER);
            String branch = classDetails.getString(ExtraUtils.EXTRA_BRANCH);
            String section = classDetails.getString(ExtraUtils.EXTRA_SECTION);

            String collegeFullName = DbHelperMethods.getCollegeFullName(db, collegeId);

            //Create a directory for your PDF
            File pdfFile = new File(context.getExternalFilesDir("pdf"), "myPdfFile.pdf");


            Document document = new Document();

            try {
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
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

                pCell.setPhrase(new Phrase(collegeFullName));
                parentTable.addCell(pCell);

                String phrase = ExtraUtils.getSemester(semester) + "  " + branch + "  " + section;
                pCell.setPhrase(new Phrase(phrase));
                parentTable.addCell(pCell);

                pCell.setPhrase(new Phrase(" "));
                parentTable.addCell(pCell);

                //setup child table
                int numColumns = NO_OF_FIX_COL + subNameList.size();
                PdfPTable table = new PdfPTable(numColumns);

                table.setTotalWidth(PageSize.A4.getWidth() - 12);
                table.setLockedWidth(true);


                //set the width of column
                List<Float> widthList = new ArrayList<>();
                widthList.add(5f);
                widthList.add(25f);
                widthList.add(20f);
                widthList.add(8f);
                widthList.add(10f);
                for (int i = 0; i < subNameList.size(); i++) widthList.add(10f);

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
                String[] subNames = subNameList.toArray(new String[0]);
                for (String subName : subNames) {
                    cell.setPhrase(new Phrase(subName));
                    table.addCell(cell);
                }
                //header representing total classes and sub wise total classes
                cell.setPhrase(new Phrase("(" + String.valueOf(totalClasses) + ")"));
                table.addCell(cell);

                cell.setPhrase(new Phrase("(" + String.valueOf(100) + ")"));
                table.addCell(cell);

                Integer[] subTotalLect = totalLectForSub.toArray(new Integer[0]);
                for (int i : subTotalLect) {
                    cell.setPhrase(new Phrase("(" + String.valueOf(i) + ")"));
                    table.addCell(cell);
                }

                //row entries for each student
                for (int i = 0; i < stdReportList.size(); i++) {

                    StudentReport currentStdReport = stdReportList.get(i);

                    String stdName = currentStdReport.getmName();
                    String stdRollNo = currentStdReport.getmRollNo();
                    String stdTotalPresent = String.valueOf(currentStdReport.getmTotalPresent());
                    float totalPercentage =
                            (Float.parseFloat(stdTotalPresent) / (float) totalClasses) * 100;

                    Integer[] subWisePresents =
                            currentStdReport.getmSubAttendance().toArray(new Integer[0]);

                    cell.setPhrase(new Phrase(String.valueOf(i + 1)));
                    table.addCell(cell);
                    cell.setPhrase(new Phrase(stdName));
                    table.addCell(cell);
                    cell.setPhrase(new Phrase(stdRollNo));
                    table.addCell(cell);
                    cell.setPhrase(new Phrase(stdTotalPresent));
                    table.addCell(cell);
                    if (totalPercentage < 75) cell.setBackgroundColor(colorLtGrey);
                    String percentage = String.format("%.1f", totalPercentage);
                    cell.setPhrase(new Phrase(percentage));
                    table.addCell(cell);

                    //set bgColor back to white after setting bgColor of % cell
                    cell.setBackgroundColor(colorWhite);

                    int k = 0;
                    int subTotalLecture;

                    for (int noOfPresent : subWisePresents) {

                        subTotalLecture = totalLectForSub.get(k);
                        float subWisePercent = ((float) noOfPresent / (float) subTotalLecture) * 100;

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

                document.add(parentTable);
                Toast.makeText(context, "created PDF", Toast.LENGTH_LONG).show();
            } catch (DocumentException de) {
                Log.e("PDFCreator", "DocumentException:" + de);
            } finally {
                document.close();
            }
        }
    }
}


