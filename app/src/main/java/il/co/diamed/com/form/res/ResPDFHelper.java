package il.co.diamed.com.form.res;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ResourceBundle;

import il.co.diamed.com.form.PDFActivity;
import il.co.diamed.com.form.R;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;

/**
 * Created by asafv on 3/27/2018.
 */

public class ResPDFHelper extends PdfDocument {
    private final static int pageWidth = 597;   //8.3 * 72
    private final static int pageHeight = 842;  //11.7 * 72


    public static PageInfo getA4Page(int numOfPages) {
        return new PageInfo.Builder(pageWidth, pageHeight, numOfPages).create();
    }


    public static Paint setFontType(int color, int fontSize) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(fontSize);
        return paint;
    }

    public static void drawLogo(PDFActivity pdfActivity, Canvas canvas, String logo,int yOffset) {
        Bitmap cameraBitmap = null;

        switch (logo) {
            case "medigal":
                cameraBitmap = BitmapFactory.decodeResource(pdfActivity.getResources(), R.mipmap.ic_medigallogo);
                break;
            case "diamed":
                cameraBitmap = BitmapFactory.decodeResource(pdfActivity.getResources(), R.mipmap.ic_diamedlogo);
                break;
            default:
                cameraBitmap = BitmapFactory.decodeResource(pdfActivity.getResources(), R.mipmap.ic_contactinfo);
                break;
        }
        //cameraBitmap.setDensity(250)
        //cameraBitmap.setDensity(140 * pdfActivity.getResources().getDisplayMetrics().densityDpi/160);
        Log.e("pdfhelper: c density = ",String.valueOf(canvas.getDensity()));
        Log.e("pdfhelper: b density = ",String.valueOf((cameraBitmap.getDensity())));
        //Centre the drawing
        int bitMapWidthCenter = cameraBitmap.getWidth();// / 2;
        int bitMapheightCenter = cameraBitmap.getHeight();// / 2;
        //And draw it...
        canvas.drawBitmap(cameraBitmap, bitMapWidthCenter/2 - 130, yOffset, null); //pageWidth / 2 - bitMapWidthCenter

    }


    public static void drawText(Canvas canvas, String text, int xOffset, int yOffset, Paint paint) {
        canvas.drawText(text, xOffset, yOffset, paint);
    }




}
