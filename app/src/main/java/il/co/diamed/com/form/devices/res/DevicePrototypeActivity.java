package il.co.diamed.com.form.devices.res;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import il.co.diamed.com.form.devices.Helper;
import il.co.diamed.com.form.devices.res.PDFBuilderFragment;
import il.co.diamed.com.form.R;

public class DevicePrototypeActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL = 0;
    private android.app.FragmentTransaction mFragmentTransaction;
    private android.app.FragmentManager mFragmentManager;
    private PDFBuilderFragment mPDFBuilderFragment;
    private Helper h = new Helper();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getFragmentManager();
    }

    public void createPDF(Intent intent) {

        h.setPDFprogress(this, "בונה טופס", true);
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mPDFBuilderFragment = new PDFBuilderFragment();
        mPDFBuilderFragment.setArguments(intent.getExtras());
        mFragmentTransaction.replace(R.id.pdffragment_container, mPDFBuilderFragment).addToBackStack(null).commit();

    }

    @Override
    public void onBackPressed() {
        h.setPDFprogress(this, "", false);

        if (mPDFBuilderFragment != null) {
            android.app.FragmentManager manager = getFragmentManager();
            android.app.FragmentTransaction trans = manager.beginTransaction();
            trans.remove(mPDFBuilderFragment);
            trans.commit();
            manager.popBackStack();

            doAnother();
        } else {
            finish();
        }

    }


    public void doAnother() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.doAnother);
        alertBuilder.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restart();
            }
        });
        alertBuilder.setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        alertBuilder.setCancelable(false);
        alertBuilder.create().show();
    }

    public void restart() { //meant to be overridden
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (mPDFBuilderFragment != null) {
            mPDFBuilderFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}