package il.co.diamed.com.form.devices.res;

import android.Manifest;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import il.co.diamed.com.form.BuildConfig;
import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;

public class PDFBuilderFragment extends Fragment {
    private static final String TAG = "PDFFragment: ";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 0;
    private File file;   //iText var
    public static final String DEST = Environment.getExternalStorageDirectory() + "/Documents/MediForms/";

    private BaseFont bf = null;
    private PdfReader reader = null;
    private PdfStamper stamper = null;
    private Image checkPNG = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private void buildPDF() {
        Bundle bundle = null;
        if (getArguments() != null) // Use the current directory as title
            bundle = getArguments();//getIntent().getExtras();
        else {
            activityFailed();
        }
        String src = "assets/" + Objects.requireNonNull(bundle).getString("report");

        String signature = bundle.getString("signature");
        String destArray = bundle.getString("destArray");
        String dest = DEST + destArray;

        checkPNG = getImageFromPNG();
        checkPNG.scalePercent(1);
        try {
            file = new File(dest);
            if(file.getParentFile().mkdirs()){
                bf = BaseFont.createFont("assets/font/arialuni.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                reader = new PdfReader(src);
                stamper = new PdfStamper(reader, new FileOutputStream(dest));
            }
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

        //Upload to firebase
        ClassApplication application = (ClassApplication) getActivity().getApplication();
        application.uploadFile(dest, destArray);

        //show preview
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                Intent.FLAG_ACTIVITY_NO_HISTORY);
        Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, file);
        target.setDataAndType(uri, "application/pdf");
        try {
            startActivity(target);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
        closeFragment();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!

                    buildPDF();
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(getContext(), getString(R.string.noWritePermission), Toast.LENGTH_SHORT).show();
                    activityFailed();
                }
            }
        }
    }

    private void activityFailed() {
        closeFragment();
    }

    public void closeFragment() {

        getActivity().onBackPressed();
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

        try {

            checkPNG.setAbsolutePosition(x, y);
            //try {
            cb.addImage(checkPNG);
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

    private Image getImageFromPNG() {//}, int width, int height) {

        InputStream ims = null;
        try {
            ims = getActivity().getAssets().open("checkmark.png");
        } catch (IOException e) {
            Log.e(TAG, "ims = null");
            activityFailed();
        }

        Bitmap bmpOrigin = BitmapFactory.decodeStream(ims);
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        bmpOrigin.compress(Bitmap.CompressFormat.PNG, 100, stream2);
        Image image = null;
        try {
            image = Image.getInstance(stream2.toByteArray());
        } catch (BadElementException | IOException e) {
            Log.e(TAG, "checkmark.png" + " ---" + e.getMessage());
        }
        return image;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            buildPDF();
        } else {
            // Permission is not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
        }
    }


}

