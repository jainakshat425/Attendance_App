package com.example.android.attendance.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.attendance.R;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PdfUtils {

    public static void generatePdf(Context context, Activity activity) {

        //First Check if the external storage is writable
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(context, "Storage cannot be written!", Toast.LENGTH_SHORT).show();
        }

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
        // step 4
        try {
            document.add(createFirstTable());
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        // step 5
        document.close();
    }

    /**
     * Creates our first table
     * @return our first table
     */
    public static PdfPTable createFirstTable() {
        // a table with three columns
        PdfPTable table = new PdfPTable(3);
        // the cell object
        PdfPCell cell;
        // we add a cell with colspan 3
        cell = new PdfPCell(new Phrase("Cell with colspan 3"));
        cell.setColspan(3);
        table.addCell(cell);
        // now we add a cell with rowspan 2
        cell = new PdfPCell(new Phrase("Cell with rowspan 2"));
        cell.setRowspan(2);
        table.addCell(cell);
        // we add the four remaining cells with addCell()
        table.addCell("row 1; cell 1");
        table.addCell("row 1; cell 2");
        table.addCell("row 2; cell 1");
        table.addCell("row 2; cell 2");
        return table;
    }

}

