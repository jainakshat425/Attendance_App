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
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class PdfUtils {

    public static void generatePdf(Context context, Activity activity) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        DrawerLayout root = (DrawerLayout) inflater.inflate(R.layout.activity_main, null);
        root.setDrawingCacheEnabled(true);
        Bitmap screen = getBitmapFromView(activity.getWindow().findViewById(R.id.main_list_view));

        createPdf(context, screen);
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    public static void createPdf(Context context, Bitmap bmp) {
        //First Check if the external storage is writable
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(context, "Storage cannot be written!", Toast.LENGTH_SHORT).show();
        }

        //Create a directory for your PDF
        File pdfDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Attendance");
        if (!pdfDir.exists()) {
            pdfDir.mkdir();
        }

        //Now create the name of your PDF file that you will generate
        File pdfFile = new File(pdfDir, "myPdfFile.pdf");
        convertImageToPdf(context, pdfFile, bmp);
    }

    public static void convertImageToPdf(Context context, File file, Bitmap bmp) {

        try {
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            addImage(document, byteArray);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addImage(Document document,byte[] byteArray)
    {
        Image image = null;
        try
        {
            image = Image.getInstance(byteArray);
        }
        catch (BadElementException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // image.scaleAbsolute(150f, 150f);
        try
        {
            document.add(image);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

