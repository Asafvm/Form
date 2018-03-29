package il.co.diamed.com.form;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfIndirectObject;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import il.co.diamed.com.form.res.ResPDFHelper;

import static android.content.ContentValues.TAG;
import static android.graphics.PorterDuff.Mode.SRC;
import static com.itextpdf.text.pdf.PdfName.DEST;
import static il.co.diamed.com.form.res.ResPDFHelper.*;

public class PDFActivity extends AppCompatActivity {
    private File pdfFile;   //iText var
    final int defaultColor = Color.BLACK;
    public static final String SRC = "assets/general.pdf";
    public static final String DEST = Environment.getExternalStorageDirectory() + "/Documents";
    public static final String IMG = "assets/checkmark.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);


        try {
            //createPdf();
            addImage();
        } catch (FileNotFoundException e) {
            Log.e("addImage: ",e.getMessage());
        } catch (DocumentException e) {
            Log.e("addImage: ",e.getMessage()+" - "+e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e("addImage: ",e.getMessage());
        }
    }

    //ITEXT
    private void createPdf() throws FileNotFoundException, DocumentException {

        File docsFolder = new File(DEST);
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
            Log.i(TAG, "Created a new directory for PDF");
        }

        pdfFile = new File(docsFolder.getAbsolutePath(),"HelloWorld.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, output);
        document.open();
        document.add(new Paragraph("Hello World!"));
        PdfContentByte canvas = writer.getDirectContentUnder();

        try {
            InputStream ims = getAssets().open("general-1.bmp");
            Bitmap bmp = BitmapFactory.decodeStream(ims);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = Image.getInstance(stream.toByteArray());

            image.setAbsolutePosition(0, 0);
            canvas.addImage(image);
        } catch (IOException e) {
            Log.e("PDFActivity: ",e.getMessage());
        }



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

    public void addImage() throws IOException, DocumentException {
        File file = new File(DEST+"/test/test.pdf");
        file.getParentFile().mkdirs();
        manipulatePdf(SRC, DEST+"/test/test.pdf");
    }

    public void manipulatePdf(String src, String dest) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));

        //Image image = Image.getInstance(getClass().getClassLoader().getResource("assets/checkmark.png"));

        InputStream ims = getAssets().open("checkmark.png");
        Bitmap bmpOrigin = BitmapFactory.decodeStream(ims);
        Bitmap bmp = Bitmap.createScaledBitmap(bmpOrigin, 10, 10, true);
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream2);
        Image image = Image.getInstance(stream2.toByteArray());

        //PdfImage stream = new PdfImage(image, "", null);
        //stream.put(new PdfName("ITXT_SpecialId"), new PdfName("123456789"));
        //PdfIndirectObject ref = stamper.getWriter().addToBody(stream);
        //image.setDirectReference(ref.getIndirectReference());

        PdfContentByte over = stamper.getOverContent(1);
        image.setAbsolutePosition(360, 542);
        over.addImage(image);
        image.setAbsolutePosition(360, 522);
        over.addImage(image);
        image.setAbsolutePosition(360, 487);
        over.addImage(image);
        image.setAbsolutePosition(360, 467);
        over.addImage(image);
        image.setAbsolutePosition(360, 448);
        over.addImage(image);
        stamper.close();
        reader.close();
    }
}
