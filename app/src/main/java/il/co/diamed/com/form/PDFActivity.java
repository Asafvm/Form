package il.co.diamed.com.form;

import android.content.Intent;
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
    private File file;   //iText var
    public static final String DEST = Environment.getExternalStorageDirectory() + "/Documents/MediForms/";
    public static final String IMG = "assets/checkmark.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        String src = "assets/" + bundle.get("report");
        String signature = bundle.getString("signature");


        ArrayList<String> destArray = bundle.getStringArrayList("destArray");

        String dest = DEST + destArray.get(0) + "/" + destArray.get(1) + "/" + destArray.get(1) + destArray.get(2) + destArray.get(3) + "_" + destArray.get(4) + ".pdf";
        Log.e(TAG + " dest=", dest);
        Log.e(TAG + " src=", src);

        BaseFont bf = null;
        PdfReader reader = null;
        PdfStamper stamper = null;
        try {
            file = new File(dest);
            file.getParentFile().mkdirs();
            bf = BaseFont.createFont("assets/font/arialuni.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            reader = new PdfReader(src);
            stamper = new PdfStamper(reader, new FileOutputStream(dest));
        } catch (Exception e) {
            activityFailed();
        }

        //createPdf();
        Bundle pages = bundle.getBundle("pages");
        for (int i = 0; i < pages.size(); i++) {
            Bundle page = pages.getBundle("page" + (i + 1));
            if (page != null) {
                ArrayList<Tuple> checkmarks = page.getParcelableArrayList("checkmarks");
                ArrayList<Tuple> corText = page.getParcelableArrayList("corText");
                ArrayList<String> arrText = page.getStringArrayList("arrText");

                if (checkmarks != null)
                    pdfCheck(stamper, checkmarks, (i + 1));

                if (corText != null && arrText != null) {
                    if (corText.size() != arrText.size()) {
                        Log.e(TAG, "corText=" + corText.size() + " and arrText=" + arrText.size());
                        activityFailed();
                    } else {
                        pdfText(stamper, bf, corText, arrText, signature, (i + 1));
                    }
                }
            } else {
                activityFailed();
            }
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        try {
            stamper.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
        reader.close();
        finish();

    }


    private void activityFailed() {
        if (file != null)
            file.delete();
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }


    //ITEXT


    public void pdfCheck(PdfStamper stamper, ArrayList<Tuple> checkmarks, int page) {
        PdfContentByte cb = stamper.getOverContent(page);
        Image image = getImageFromPNG("checkmark.png");
        image.scalePercent(1);
        //add checkmarks
        for (int i = 0; i < checkmarks.size(); i++) {
            image.setAbsolutePosition(checkmarks.get(i).getX(), checkmarks.get(i).getY());
            try {
                cb.addImage(image);
            } catch (DocumentException e) {
                Log.e(TAG, "failed to add checkmarks");
                activityFailed();
            }
        }
    }

    public void pdfText(PdfStamper stamper, BaseFont bf, ArrayList<Tuple> corText, ArrayList<String> arrText, String signature, int page) {
        PdfContentByte cb = stamper.getOverContent(page);
        Image image;
        try {
            image = Image.getInstance(signature);
            image.scalePercent(9);
            image.setAbsolutePosition(135, 33);
            cb.addImage(image);
        } catch (IOException e) {
            Log.e(TAG, "failed to get signature");
            activityFailed();
        } catch (DocumentException e) {
            Log.e(TAG, "failed to get signature");
            activityFailed();
        }
        //add text
        for (int i = 0; i < corText.size(); i++) {
            ColumnText ct = new ColumnText(cb);
            if (i <= 1)
                ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
            else
                ct.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
            ct.setSimpleColumn(new Rectangle(corText.get(i).getX(), corText.get(i).getY(), corText.get(i).getX() + 160, corText.get(i).getY() + 20));

            Font f = new Font(bf);
            Paragraph pz = new Paragraph(new Phrase(20, arrText.get(i).toString(), f));
            ct.addElement(pz);

            try {
                ct.go();
            } catch (DocumentException e) {
                e.printStackTrace();
                Log.e(TAG, "failed to write text to document");
                activityFailed();
            }
        }
    }

    private Image getImageFromPNG(String url) {//}, int width, int height) {

        InputStream ims = null;
        try {
            ims = getAssets().open(url);
        } catch (IOException e) {
            Log.e(TAG, "ims = null");
            activityFailed();
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
