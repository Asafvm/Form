package il.co.diamed.com.form;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import il.co.diamed.com.form.res.ResPDFHelper;

import static il.co.diamed.com.form.res.ResPDFHelper.*;

public class PDFActivity extends AppCompatActivity {

    final int defaultColor = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        Bundle data = getIntent().getExtras();

        TextView tv1 = (TextView) findViewById(R.id.temp);
        TextView tv2 = (TextView) findViewById(R.id.type);

        tv1.setText(data.getString("temp"));
        tv2.setText(data.getString("type"));



        // create a new document
        PdfDocument document = new PdfDocument();
        // crate a page info with attributes as below
        // page number, height and width
        // i have used height and width to that of pdf content view
        int pageNumber = 1;
        // create a new page from the PageInfo
        // start a page
        PdfDocument.Page page = document.startPage(getA4Page(pageNumber));
        // repaint the user's text into the page
        Paint paint = setFontType(defaultColor, 20);
        Canvas canvas = page.getCanvas();

        //draw relevant logo
        drawLogo(this, canvas,"diamed",-50);
        drawLogo(this, canvas,"",0);


        //Write stuff to page
        /*
        int xOffset = 50, yOffset = 200;
        drawText(canvas, tv2.getText().toString(), xOffset, yOffset, paint);
        xOffset += 100;
        drawText(canvas, tv1.getText().toString(), tv2.getText().toString().length()*24 + xOffset, yOffset, paint);
        */

        // do final processing of the page
        document.finishPage(page);

        // saving pdf document to sdcard
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
        String pdfName = "pdfdemo"
                + sdf.format(Calendar.getInstance().getTime()) + ".pdf";

        // all created files will be saved at path /sdcard/PDFDemo_AndroidSRC/
        File outputFile = new File("/sdcard/download/", pdfName);

        try {
            outputFile.createNewFile();
            OutputStream out = new FileOutputStream(outputFile);
            document.writeTo(out);
            document.close();
            out.close();
        } catch (IOException e) {

            Log.e("pdf", e.getMessage());
        }


        /*
        // create a new document
        PdfDocument document = new PdfDocument();
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(100, 100, 1).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        // draw something on the page


        // finish the page
        document.finishPage(page);


        // write the document content
        document.writeTo("/sdcard/file.pdf");

        // close the document
        document.close();

        */
    }
}
