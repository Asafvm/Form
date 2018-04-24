package il.co.diamed.com.form;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

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

        String src = "assets/" + Objects.requireNonNull(bundle).get("report");
        String signature = bundle.getString("signature");
        String destArray = bundle.getString("destArray");
        String dest = DEST + destArray;

        BaseFont bf = null;
        PdfReader reader = null;
        PdfStamper stamper = null;
        try {
            file = new File(dest);
            //file.getParentFile().mkdirs();
            bf = BaseFont.createFont("assets/font/arialuni.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            reader = new PdfReader(src);
            stamper = new PdfStamper(reader, new FileOutputStream(dest));
        } catch (Exception e) {
            activityFailed();
        }

        //createPdf();
        Bundle pages = bundle.getBundle("pages");
        if (pages != null) {
            for (int i = 0; i < pages.size(); i++) {
                ArrayList<Tuple> corText = pages.getParcelableArrayList("page" + (i + 1));
                if (corText != null) {
                    pdfText(stamper, bf, corText, signature, (i + 1));
                } else {
                    activityFailed();
                }
            }
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);

        try {
            if (stamper != null) {
                stamper.close();
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        if (reader != null) {
            reader.close();
        }
        finish();

    }


    private void activityFailed() {
        //if (file != null)
        //    file.delete();
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }


    //ITEXT

    private void pdfSignHere(PdfContentByte cb, float x, float y, String signature) {
        Image image;
        try {
            image = Image.getInstance(signature);
            image.scalePercent(9);
            image.setAbsolutePosition(x, y);
            cb.addImage(image);
        } catch (IOException | DocumentException e) {
            Log.e(TAG, "failed to get signature");
            activityFailed();
        }
    }


    public void pdfCheck(PdfContentByte cb, float x, float y) {
        Image image = getImageFromPNG("checkmark.png");
        image.scalePercent(1);
        image.setAbsolutePosition(x, y);
        try {
            cb.addImage(image);
        } catch (DocumentException e) {
            Log.e(TAG, "failed to add checkmarks");
            activityFailed();
        }
    }

    public void pdfText(PdfStamper stamper, BaseFont bf, ArrayList<Tuple> corText, String signature, int page) {
        PdfContentByte cb = stamper.getOverContent(page);

        //add text
        for (int i = 0; i < corText.size(); i++) {
            switch (corText.get(i).getText()) {
                case "":
                    pdfCheck(cb, corText.get(i).getX(), corText.get(i).getY());
                    break;
                case "!":
                    pdfSignHere(cb, corText.get(i).getX(), corText.get(i).getY(), signature);
                    break;
                default: {
                    ColumnText ct = new ColumnText(cb);
                    if (corText.get(i).getRtl())
                        ct.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    else
                        ct.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);

                    ct.setSimpleColumn(new Rectangle(corText.get(i).getX(), corText.get(i).getY(), corText.get(i).getX() + 160, corText.get(i).getY() + 20));
                    Font f = new Font(bf);
                    Paragraph pz = new Paragraph(new Phrase(20, corText.get(i).getText(), f));
                    ct.addElement(pz);

                    try {
                        ct.go();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                        Log.e(TAG, "failed to write text to document");
                        activityFailed();
                    }
                }
                break;
            }
        }
    }

        private Image getImageFromPNG (String url){//}, int width, int height) {

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
            } catch (BadElementException | IOException e) {
                Log.e(TAG, url + " ---" + e.getMessage());
            }
            return image;
        }

}

