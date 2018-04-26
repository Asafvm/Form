package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import il.co.diamed.com.form.PDFActivity;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.Tuple;

import static il.co.diamed.com.form.devices.Helper.isValidString;

public class GelstationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gelstation);
        Helper h = new Helper();
        h.setLayout(this, R.layout.gelstation_layout);


        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        final String thermometer = bundle.getString("thermometer");
        final String speedometer = bundle.getString("speedometer");
        final String barometer = bundle.getString("barometer");


        //get views
        //          Reconstruct Branch               :
        //D/vndksupport: Loading /vendor/lib64/hw/gralloc.msm8996.so from current namespace instead of sphal namespace.
        //I/Adreno: PFP: 0x005ff087, ME: 0x005ff063
        //I/zygote64: android::hardware::configstore::V1_0::ISurfaceFlingerConfigs::hasWideColorDisplay retrieved: 0
        //I/OpenGLRenderer: Initialized EGL, version 1.4
        //D/OpenGLRenderer: Swap behavior 2
        //I/zygote64: Do full code cache collection, code=125KB, data=82KB
        final Button btn = findViewById(R.id.formSubmitButton);

        final EditText t11 = findViewById(R.id.formMainLocation);
        final EditText t12 = findViewById(R.id.formRoomLocation);
        final RadioGroup t2 = findViewById(R.id.rgModelSelect);
        final EditText t3 = findViewById(R.id.etDeviceSerial);
        final EditText t4 = findViewById(R.id.etVer);
        final EditText t5 = findViewById(R.id.etNewVer);
        final EditText t6 = findViewById(R.id.formTechName);
        final DatePicker dp = findViewById(R.id.formDate);


        //default basic values
        t6.setText(techname);

/*
        setListener(t11);
        setListener(t12);
        setListener(t3);
        setListener(t4);
        setListener(t5);
        setListener(t6);
        setListener(t2);
        setListener(cleanSwitch);
        setListener(selfTestSwitch);
        setListener(verSwitch);
*/


        btn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1", getPage1corText());
                    pages.putParcelableArrayList("page2", getPage2corText());
                    pages.putParcelableArrayList("page3", getPage3corText());

                    ArrayList<String> destArray = new ArrayList<>();
                    destArray.add(t11.getText().toString() + "_" + t12.getText().toString());
                    destArray.add(String.valueOf(dp.getYear()));
                    destArray.add(String.valueOf(dp.getMonth()));
                    destArray.add(String.valueOf(dp.getDayOfMonth()));
                    destArray.add(t3.getText().toString());

                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_gelstation_yearly.pdf");
                    intent.putExtra("pages", pages);
                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", t11.getText().toString()+" "+t12.getText().toString()+"/"+dp.getYear()+""+dp.getDayOfMonth()+""+dp.getMonth()+"_"+
                            "_Gelstation_"+t3.getText().toString()+".pdf");
                    startActivityForResult(intent, 1);
                }

            }

            private ArrayList<Tuple> getPage1corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(460, 632, dp.getMonth() + "   " + (dp.getYear() + 1), false));                        //Next Date
                corText.add(new Tuple(90, 661, t11.getText().toString() + " - " + t12.getText().toString(), true));                        //Location
                corText.add(new Tuple(390, 661, dp.getDayOfMonth() + "    " + dp.getMonth() + "    " + dp.getYear(), false));                        //Date
                corText.add(new Tuple(135, 632, t3.getText().toString(), false));                        //Serial
                //corText.add(new Tuple(380,30));                        //Signature
                corText.add(new Tuple(344, 543, "", false));           //temp ok
                corText.add(new Tuple(344, 525, "", false));           //time ok
                corText.add(new Tuple(344, 507, "", false));           //fan ok

                corText.add(new Tuple(292, 468, "3.18", false));           //rubber ok
                corText.add(new Tuple(344, 470, "", false));           //rubber ok
                corText.add(new Tuple(394, 452, "XP SP3", false));           //overall ok
                corText.add(new Tuple(344, 452, "", false));           //overall ok
                corText.add(new Tuple(394, 432, "C:\\300GB\tD:\\100GB", false));           //rubber ok
                corText.add(new Tuple(344, 434, "", false));           //rubber ok
                corText.add(new Tuple(344, 416, "", false));           //overall ok

                corText.add(new Tuple(344, 379, "", false));           //rubber ok
                corText.add(new Tuple(292, 359, "24v", false));           //overall ok
                corText.add(new Tuple(344, 361, "", false));           //overall ok
                corText.add(new Tuple(292, 341, "8v", false));           //rubber ok
                corText.add(new Tuple(344, 343, "", false));           //rubber ok
                corText.add(new Tuple(292, 323, "12v", false));           //overall ok
                corText.add(new Tuple(344, 325, "", false));           //overall ok
                corText.add(new Tuple(344, 307, "", false));           //rubber ok

                corText.add(new Tuple(344, 265, "", false));           //overall ok
                corText.add(new Tuple(344, 235, "", false));           //overall ok
                corText.add(new Tuple(344, 214, "", false));           //rubber ok
                corText.add(new Tuple(344, 198, "", false));           //overall ok
                corText.add(new Tuple(344, 180, "", false));           //rubber ok
                corText.add(new Tuple(344, 162, "", false));           //overall ok
                corText.add(new Tuple(344, 140, "", false));           //rubber ok
                corText.add(new Tuple(344, 117, "", false));           //rubber ok
                corText.add(new Tuple(344, 88, "", false));           //rubber ok
                return corText;
            }


            private ArrayList<Tuple> getPage2corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(420, 706, thermometer, false));           //overall ok
                corText.add(new Tuple(320, 706, "", false));           //overall ok
                corText.add(new Tuple(320, 688, "", false));           //rubber ok
                corText.add(new Tuple(320, 670, "", false));           //overall ok
                corText.add(new Tuple(320, 652, "", false));           //rubber ok
                corText.add(new Tuple(320, 634, "", false));           //overall ok


                corText.add(new Tuple(320, 598, "", false));           //overall ok
                corText.add(new Tuple(320, 580, "", false));           //rubber ok
                corText.add(new Tuple(320, 562, "", false));           //rubber ok
                corText.add(new Tuple(320, 543, "", false));           //temp ok
                corText.add(new Tuple(320, 525, "", false));           //time ok
                corText.add(new Tuple(272, 525, "991", false));           //time ok
                corText.add(new Tuple(420, 521, speedometer, false));           //overall ok
                corText.add(new Tuple(320, 507, "", false));           //fan ok

                corText.add(new Tuple(320, 470, "", false));           //rubber ok
                corText.add(new Tuple(320, 452, "", false));           //overall ok
                corText.add(new Tuple(320, 434, "", false));           //rubber ok
                corText.add(new Tuple(320, 416, "", false));           //overall ok
                corText.add(new Tuple(320, 398, "", false));           //overall ok
                corText.add(new Tuple(320, 378, "", false));           //rubber ok
                corText.add(new Tuple(320, 361, "", false));           //overall ok
                corText.add(new Tuple(320, 343, "", false));           //rubber ok
                corText.add(new Tuple(320, 324, "", false));           //overall ok
                corText.add(new Tuple(320, 306, "", false));           //rubber ok
                corText.add(new Tuple(320, 288, "", false));           //overall ok
                corText.add(new Tuple(320, 266, "", false));           //overall ok

                corText.add(new Tuple(320, 218, "", false));           //rubber ok
                corText.add(new Tuple(320, 200, "", false));           //overall ok
                corText.add(new Tuple(320, 182, "", false));           //rubber ok
                corText.add(new Tuple(320, 161, "", false));           //overall ok
                corText.add(new Tuple(420, 161, barometer, false));           //overall ok
                corText.add(new Tuple(272, 161, "700", false));           //overall ok
                corText.add(new Tuple(272, 129, "180", false));           //rubber ok
                corText.add(new Tuple(320, 129, "", false));           //rubber ok
                corText.add(new Tuple(320, 99, "", false));           //rubber ok

                return corText;
            }


            private ArrayList<Tuple> getPage3corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(320, 686, "", false));           //rubber ok
                corText.add(new Tuple(320, 668, "", false));           //overall ok
                corText.add(new Tuple(320, 650, "", false));           //rubber ok
                corText.add(new Tuple(320, 632, "", false));           //overall ok

                corText.add(new Tuple(320, 595, "", false));           //overall ok
                corText.add(new Tuple(320, 577, "", false));           //rubber ok
                corText.add(new Tuple(320, 559, "", false));           //rubber ok

                corText.add(new Tuple(320, 522, "", false));           //time ok

                corText.add(new Tuple(320, 452, "", false));           //temp ok
                corText.add(new Tuple(320, 480, "", false));           //fan ok

                corText.add(new Tuple(100, 95, t6.getText().toString(), true));                        //Tech Name
                corText.add(new Tuple(465, 95, "!", false));                        //Signature

                return corText;
            }

            private boolean checkStatus() {
                return isValidString(t11.getText().toString()) && isValidString(t12.getText().toString()) &&
                        isValidString(t3.getText().toString()) && isValidString(t6.getText().toString());

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.pdfSuccess, Toast.LENGTH_SHORT).show();
                doAnother();
                setResult(RESULT_OK);
            } else {
                setResult(RESULT_CANCELED);
            }
        }
    }

    private void doAnother() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.doAnother);
        alertBuilder.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText et = findViewById(R.id.etNewVer);
                et.setVisibility(View.INVISIBLE);
                et.setText("");
                et = findViewById(R.id.etVer);
                et.setText("");
                et = findViewById(R.id.etDeviceSerial);
                et.setText("");
                Switch s = findViewById(R.id.verUpdateSwitch);
                s.setChecked(false);
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
        alertBuilder.create().show();
    }


}
