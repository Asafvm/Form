package il.co.diamed.com.form;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import il.co.diamed.com.form.res.Tuple;

public class PDFActivity extends AppCompatActivity {
    private static final String TAG = "PDFActivity: ";
    private File pdfFile;   //iText var
    final int defaultColor = Color.BLACK;
    public static final String DEST = Environment.getExternalStorageDirectory() + "/Documents/MediForms/";
    public static final String IMG = "assets/checkmark.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        Toast.makeText(getApplicationContext(),"test",Toast.LENGTH_SHORT);


        Bundle bundle = getIntent().getExtras();
        String src = "assets/" + bundle.get("report");
        String signature = bundle.getString("signature");


        ArrayList<Tuple> checkmarks = bundle.getParcelableArrayList("checkmarks");

        ArrayList<Tuple> corText = bundle.getParcelableArrayList("corText");
        ArrayList<String> arrText = bundle.getStringArrayList("arrText");

        if(corText.size() != arrText.size()){
            Log.e(TAG, "corText="+corText.size()+" and arrText="+arrText.size());
            Toast.makeText(getBaseContext(),R.string.pdfFailed,Toast.LENGTH_SHORT);
            finish();
        }

        ArrayList<String> destArray = bundle.getStringArrayList("destArray");

        String dest = DEST + destArray.get(0) + "/" + destArray.get(1) + "/" + destArray.get(1) + destArray.get(2) + destArray.get(3) + "_" + destArray.get(4) + ".pdf";
        Log.e(TAG+" dest=", dest);
        Log.e(TAG+" src=", src);
        try {
            //createPdf();
            manipulatePdf(src, dest, checkmarks, corText, arrText, signature);
        } catch (FileNotFoundException e) {
            Log.e(TAG +" addimage-", e.getMessage());
        } catch (DocumentException e) {
            Log.e(TAG +" addimage-", e.getMessage() + " - " + e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e(TAG +" addimage-", e.getStackTrace().toString());
        }
        Toast.makeText(getBaseContext(),R.string.pdfSuccess,Toast.LENGTH_SHORT);
        finish();
    }


    //ITEXT


    public void manipulatePdf(String src, String dest, ArrayList<Tuple> checkmarks, ArrayList<Tuple> corText, ArrayList<String> arrText, String signature) throws IOException, DocumentException {

        File file = new File(dest);
        file.getParentFile().mkdirs();

        BaseFont bf = BaseFont.createFont("assets/font/arialuni.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        PdfReader reader = new PdfReader(src);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        PdfContentByte cb = stamper.getOverContent(1);


        Image image = getImageFromPNG("checkmark.png");
        image.scalePercent(1);
        //add checkmarks
        for (int i = 0; i < checkmarks.size(); i++) {
            image.setAbsolutePosition(checkmarks.get(i).getX(), checkmarks.get(i).getY());
            try {
                cb.addImage(image);
            } catch (DocumentException e) {
                Log.e(TAG,"failed to add checkmarks");
            }
        }
        try {
            image = Image.getInstance(signature);
            image.scalePercent(9);
            image.setAbsolutePosition(135, 33);
            cb.addImage(image);
        }catch (IOException e){
            Log.e(TAG,"failed to get signature");
        }catch (DocumentException e){
            Log.e(TAG,"failed to get signature");
        }
        //add text

        //stamper.getWriter().setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

        //cb.getPdfWriter().setRunDirection(PdfWriter.RUN_DIRECTION_RTL);

        //test
        ColumnText ct;// = new ColumnText(stamper.getUnderContent(0));


        for (int i = 0; i < corText.size(); i++) {

            cb = stamper.getOverContent(1);
            ct = new ColumnText(cb);
            if(i<=1)
                ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            else
                ct.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
            //ct.setAlignment(Element.ALIGN_LEFT);
            ct.setSimpleColumn(new Rectangle(corText.get(i).getX(), corText.get(i).getY(), corText.get(i).getX()+160, corText.get(i).getY() + 20));

            //ct.setSimpleColumn(120f, 48f, 200f, 600f);
            Font f = new Font(bf);
            Paragraph pz = new Paragraph(new Phrase(20, arrText.get(i).toString(), f));
            ct.addElement(pz);

            try {
                ct.go();
            } catch (DocumentException e) {
                e.printStackTrace();Log.e(TAG,"failed to write text to document");
            }
            // f = new Font(bf, 13);
            //ct = new ColumnText(cb);
            //ct.setSimpleColumn(120f, 48f, 200f, 700f);
            //pz = new Paragraph ("Hello World!", f);
            //ct.addElement(pz);
            //ct.go();

            /*ColumnText
            ct.setSimpleColumn(new Rectangle(corText.get(i).getX(), corText.get(i).getY(),corText.get(i).getX()+50, corText.get(i).getY()+50));

            //ct.setSimpleColumn(new Rectangle(writer.getBoxSize((arrText.get(i))));
            Chunk c = new Chunk(arrText.get(i));

            ct.addText(c);
            ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            ct.go();
            */
            /*  ContentByte
            cb.saveState();
            cb.beginText();
            cb.moveText(corText.get(i).getX(), corText.get(i).getY());
            cb.setColorStroke(BaseColor.BLUE);
            cb.setFontAndSize(bf, 16);
            cb.showText(arrText.get(i));
            cb.endText();
            cb.restoreState();
            */
        }

        stamper.close();
        reader.close();
    }

    private Image getImageFromPNG(String url){//}, int width, int height) {

        InputStream ims = null;
        try {
            ims = getAssets().open(url);
        } catch (IOException e) {
            Log.e(TAG, "ims = null");
        }
        if (ims == null) {
            try {
                ims = new FileInputStream(url);
            } catch (IOException e) {
                Log.e(TAG, "ims = null. again");
            }
        }
        Bitmap bmpOrigin = BitmapFactory.decodeStream(ims);
        Bitmap bmp = Bitmap.createScaledBitmap(bmpOrigin, bmpOrigin.getWidth(), bmpOrigin.getHeight(), true);
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream2);
        Image image = null;
        try {
            image = Image.getInstance(stream2.toByteArray());
        } catch (BadElementException e) {
            Log.e(TAG, url + " ---" + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, url + " ---" + e.getMessage());
        }
        return image;
    }
}
