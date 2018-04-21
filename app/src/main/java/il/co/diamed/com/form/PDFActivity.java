package il.co.diamed.com.form;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import il.co.diamed.com.form.res.Tuple;

import static android.content.ContentValues.TAG;

public class PDFActivity extends AppCompatActivity {
    private File pdfFile;   //iText var
    final int defaultColor = Color.BLACK;
    public static final String SRC = "assets/.pdf";
    public static final String DEST = Environment.getExternalStorageDirectory() + "/Documents";
    public static final String IMG = "assets/checkmark.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        Bundle bundle = getIntent().getExtras();
        String src = "assets/"+bundle.get("report");
        ArrayList<Tuple> cor = (ArrayList<Tuple>) bundle.getParcelable("cor");

        try {
            //createPdf();
            manipulatePdf(src,cor);
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
    }

    public void manipulatePdf(String src, ArrayList<Tuple> cor) throws IOException, DocumentException {
        String dest = DEST+"/test/test.pdf";
        File file = new File(dest);
        file.getParentFile().mkdirs();

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

        image.setAbsolutePosition((Float) cor.get(0).x, (Float) cor.get(0).y);
        over.addImage(image);

        /*
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
        */
        stamper.close();
        reader.close();
    }
}
