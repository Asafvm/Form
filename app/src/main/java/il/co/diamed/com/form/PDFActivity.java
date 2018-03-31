package il.co.diamed.com.form;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import il.co.diamed.com.form.res.Tuple;

import static android.content.ContentValues.TAG;

public class PDFActivity extends AppCompatActivity {
    private File pdfFile;   //iText var
    final int defaultColor = Color.BLACK;
    public static final String DEST = Environment.getExternalStorageDirectory() + "/Documents";
    public static final String IMG = "assets/checkmark.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        Bundle bundle = getIntent().getExtras();
        String src = "assets/"+bundle.get("report");
        ArrayList<Tuple> checkmarks = bundle.getParcelableArrayList("checkmarks");

        ArrayList<Tuple> corText = bundle.getParcelableArrayList("corText");
        ArrayList<String> arrText = bundle.getStringArrayList("arrText");

        try {
            //createPdf();
            manipulatePdf(src,checkmarks,corText,arrText);
        } catch (FileNotFoundException e) {
            Log.e("addImage: ",e.getMessage());
        } catch (DocumentException e) {
            Log.e("addImage: ",e.getMessage()+" - "+e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e("addImage: ",e.getMessage());
        }
    }



    //ITEXT



    public void manipulatePdf(String src, ArrayList<Tuple> checkmarks, ArrayList<Tuple> corText, ArrayList<String> arrText) throws IOException, DocumentException {
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

        PdfContentByte over = stamper.getOverContent(1);
        //add checkmarks
        for(int i=0;i<checkmarks.size();i++) {
            image.setAbsolutePosition(checkmarks.get(i).getX(), checkmarks.get(i).getY());
            over.addImage(image);
        }
        //add text


        PdfContentByte cb = stamper.getOverContent(1);
        ColumnText ct = new ColumnText(cb);
        PdfWriter writer = null;

        Font f = new Font();
        for(int i=0;i<corText.size();i++) {

            //ct.setSimpleColumn(corText.get(i).getX(),corText.get(i).getY(), corText.get(i).getX()+500,corText.get(i).getY()+500);

            BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

            cb.saveState();
            cb.beginText();
            cb.moveText(corText.get(i).getX(),corText.get(i).getY());
            cb.setFontAndSize(bf, 16);
            cb.showText(arrText.get(i));
            cb.endText();
            cb.restoreState();
        }

        stamper.close();
        reader.close();
    }
}
