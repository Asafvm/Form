package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

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


        Bundle bundle = getIntent().getExtras().getBundle("cal");
        final String techname = bundle.getString("techName");
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

                    Bundle page1 = new Bundle();
                    ArrayList<Tuple> corCheck = getPage1corCheck();
                    ArrayList<Tuple> corText = getPage1corText();
                    ArrayList<String> arrText = getPage1arrText();
                    page1.putParcelableArrayList("checkmarks", corCheck);
                    page1.putStringArrayList("arrText", arrText);
                    page1.putParcelableArrayList("corText", corText);



                    Bundle page2 = new Bundle();
                    corCheck = getPage2corCheck();
                    page2.putParcelableArrayList("checkmarks", corCheck);
                    page2.putStringArrayList("arrText", arrText);
                    page2.putParcelableArrayList("corText", corText);

                    Bundle page3 = new Bundle();
                    page3.putParcelableArrayList("checkmarks", corCheck);
                    page3.putStringArrayList("arrText", arrText);
                    page3.putParcelableArrayList("corText", corText);


                    pages.putBundle("page1", page1);
                    pages.putBundle("page2", page2);
                    pages.putBundle("page3", page3);

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
                    intent.putExtra("destArray", destArray);
                    startActivityForResult(intent, 1);
                }

            }

            private ArrayList<String> getPage1arrText() {
                ArrayList<String> arrText = new ArrayList<>();


                arrText.add(t11.getText().toString() + " - " + t12.getText().toString());                                       //Location
                arrText.add(t6.getText().toString());                        //Tech Name
                arrText.add(dp.getDayOfMonth() + "    " + dp.getMonth() + "    " + dp.getYear());                      //Date
                arrText.add(t3.getText().toString());                        //Serial

                arrText.add(dp.getMonth() + "   " + (dp.getYear() + 1));           //Next Date




                return arrText;
            }

            private ArrayList<Tuple> getPage1corText() {
                ArrayList<Tuple> corText = new ArrayList<>();

                corText.add(new Tuple(90, 661));                        //Location
                corText.add(new Tuple(100, 140));                        //Tech Name
                corText.add(new Tuple(390, 661));                        //Date
                corText.add(new Tuple(135, 632));                        //Serial
                corText.add(new Tuple(455, 632));                        //Next Date
                //corText.add(new Tuple(380,30));                        //Signature

                return corText;
            }

            private ArrayList<Tuple> getPage1corCheck() {
                ArrayList<Tuple> corCheck = new ArrayList<>();
                corCheck.add(new Tuple(344, 543));           //temp ok
                corCheck.add(new Tuple(344, 525));           //time ok
                corCheck.add(new Tuple(344, 507));           //fan ok

                corCheck.add(new Tuple(344, 470));           //rubber ok
                corCheck.add(new Tuple(344, 452));           //overall ok
                corCheck.add(new Tuple(344, 434));           //rubber ok
                corCheck.add(new Tuple(344, 416));           //overall ok

                corCheck.add(new Tuple(344, 379));           //rubber ok
                corCheck.add(new Tuple(344, 361));           //overall ok
                corCheck.add(new Tuple(344, 343));           //rubber ok
                corCheck.add(new Tuple(344, 325));           //overall ok
                corCheck.add(new Tuple(344, 307));           //rubber ok

                corCheck.add(new Tuple(344, 270));           //overall ok
                corCheck.add(new Tuple(344, 234));           //overall ok
                corCheck.add(new Tuple(344, 214));           //rubber ok
                corCheck.add(new Tuple(344, 198));           //overall ok
                corCheck.add(new Tuple(344, 180));           //rubber ok
                corCheck.add(new Tuple(344, 162));           //overall ok
                corCheck.add(new Tuple(344, 140));           //rubber ok
                corCheck.add(new Tuple(344, 118));           //rubber ok
                corCheck.add(new Tuple(344, 90));           //rubber ok

                return corCheck;
            }


            private ArrayList<Tuple> getPage2corCheck() {
                ArrayList<Tuple> corCheck = new ArrayList<>();
                corCheck.add(new Tuple(320, 543));           //temp ok
                corCheck.add(new Tuple(320, 525));           //time ok
                corCheck.add(new Tuple(320, 507));           //fan ok

                corCheck.add(new Tuple(320, 470));           //rubber ok
                corCheck.add(new Tuple(320, 452));           //overall ok
                corCheck.add(new Tuple(320, 434));           //rubber ok
                corCheck.add(new Tuple(320, 416));           //overall ok

                corCheck.add(new Tuple(320, 379));           //rubber ok
                corCheck.add(new Tuple(320, 361));           //overall ok
                corCheck.add(new Tuple(320, 343));           //rubber ok
                corCheck.add(new Tuple(320, 325));           //overall ok
                corCheck.add(new Tuple(320, 307));           //rubber ok

                corCheck.add(new Tuple(320, 270));           //overall ok
                corCheck.add(new Tuple(320, 234));           //overall ok
                corCheck.add(new Tuple(320, 214));           //rubber ok
                corCheck.add(new Tuple(320, 198));           //overall ok
                corCheck.add(new Tuple(320, 180));           //rubber ok
                corCheck.add(new Tuple(320, 162));           //overall ok
                corCheck.add(new Tuple(320, 140));           //rubber ok
                corCheck.add(new Tuple(320, 118));           //rubber ok
                corCheck.add(new Tuple(320, 90));           //rubber ok

                return corCheck;
            }


            private boolean checkStatus() {
                if (!isValidString(t11.getText().toString()))
                    return false;
                if (!isValidString(t12.getText().toString()))
                    return false;
                if (!isValidString(t3.getText().toString()))
                    return false;
                if (!isValidString(t6.getText().toString()))
                    return false;

                return true;

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
