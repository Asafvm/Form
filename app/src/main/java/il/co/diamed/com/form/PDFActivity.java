package il.co.diamed.com.form;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import il.co.diamed.com.form.res.ResPDFHelper;

import static android.content.ContentValues.TAG;
import static il.co.diamed.com.form.res.ResPDFHelper.*;

public class PDFActivity extends AppCompatActivity {
    private File pdfFile;   //iText var

    final int defaultColor = Color.BLACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        try {
            createPdf();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        /*
        Bundle data = getIntent().getExtras();

        TextView tv1 = (TextView) findViewById(R.id.temp);
        TextView tv2 = (TextView) findViewById(R.id.type);

        tv1.setText(data.getString("temp"));
        tv2.setText(data.getString("type"));

*/

        // create a new document
            //PdfDocument document = new PdfDocument();
        // crate a page info with attributes as below
        // page number, height and width
        // i have used height and width to that of pdf content view
            //int pageNumber = 1;
        // create a new page from the PageInfo
        // start a page
            //PdfDocument.Page page = document.startPage(getA4Page(pageNumber));
        // repaint the user's text into the page
            //Paint paint = setFontType(defaultColor, 20);
            //Canvas canvas = page.getCanvas();

        //draw relevant logo
            //drawLogo(this, canvas,"diamed",-50);
            //drawLogo(this, canvas,"",0);


        //Write stuff to page
        /*
        int xOffset = 50, yOffset = 200;
        drawText(canvas, tv2.getText().toString(), xOffset, yOffset, paint);
        xOffset += 100;
        drawText(canvas, tv1.getText().toString(), tv2.getText().toString().length()*24 + xOffset, yOffset, paint);
        */

        // do final processing of the page
            //document.finishPage(page);

        // saving pdf document to sdcard
            //SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
            //String pdfName = "pdfdemo" + sdf.format(Calendar.getInstance().getTime()) + ".pdf";

        // all created files will be saved at path /sdcard/PDFDemo_AndroidSRC/
            //File outputFile = new File("/sdcard/download/", pdfName);
/*
        try {
            outputFile.createNewFile();
            OutputStream out = new FileOutputStream(outputFile);
            document.writeTo(out);
            document.close();
            out.close();
        } catch (IOException e) {

            Log.e("pdf", e.getMessage());
        }

*/
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

    //ITEXT
    private void createPdf() throws FileNotFoundException, DocumentException {

        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }

        pdfFile = new File(docsFolder.getAbsolutePath(),"HelloWorld.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();
        document.add(new Paragraph("Hello World!"));

        document.close();
        previewPdf();

    }

    private void previewPdf() {

        PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(pdfFile);
            intent.setDataAndType(uri, "application/pdf");

            startActivity(intent);
        }else{
            Toast.makeText(this,"Download a PDF Viewer to see the generated PDF",Toast.LENGTH_SHORT).show();
        }
    }
}
