package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
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

public class HC10Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_device_activity);
        final Helper h = new Helper();
        h.setLayout(this, R.layout.device_hc10_layout);
        h.setListener((EditText) findViewById(R.id.formTechName));

        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        //get views
        init();
        ((EditText) findViewById(R.id.formTechName)).setText(techname);
        //default basic values


        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    String day = h.fixDate(((DatePicker)findViewById(R.id.formDate)).getDayOfMonth());
                    String month = h.fixDate(((DatePicker)findViewById(R.id.formDate)).getMonth());
                    Bundle pages = new Bundle();

                    pages.putParcelableArrayList("page1", getPage1corText());
                    pages.putParcelableArrayList("page2", getPage2corText());

                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_hc10_yearly.pdf");

                    intent.putExtra("pages", pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString()+"/"+
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString()+"/"+
                            ((DatePicker) findViewById(R.id.formDate)).getYear()+""+
                            month+""+day+"_HC10_"+
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()+".pdf");
                    startActivityForResult(intent, 1);
                }

            }
            private ArrayList<Tuple> getPage1corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(450, 653, ((DatePicker) findViewById(R.id.formDate)).getMonth() + "    " +
                        (((DatePicker) findViewById(R.id.formDate)).getYear() + 1), false));                        //Next Date
                corText.add(new Tuple(70, 683, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                        ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                corText.add(new Tuple(438, 681, ((DatePicker) findViewById(R.id.formDate)).getDayOfMonth() + "   " +
                        ((DatePicker) findViewById(R.id.formDate)).getMonth() + "    " +
                        ((DatePicker) findViewById(R.id.formDate)).getYear(), false));                        //Date
                corText.add(new Tuple(135, 653, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                //corText.add(new Tuple(380,30));                        //Signature
                corText.add(new Tuple(360, 565, "", false));           //temp ok
                corText.add(new Tuple(360, 545, "", false));           //time ok
                corText.add(new Tuple(360, 526, "", false));           //fan ok
                corText.add(new Tuple(360, 507, "", false));           //fan ok
                corText.add(new Tuple(360, 490, "", false));           //fan ok

                corText.add(new Tuple(475, 454, ((EditText) findViewById(R.id.etHC10ver)).getText().toString(), false));           //overall ok
                corText.add(new Tuple(360, 455, "", false));           //overall ok
                corText.add(new Tuple(360, 435, "", false));           //overall ok
                corText.add(new Tuple(360, 408, "", false));           //overall ok
                corText.add(new Tuple(360, 375, "", false));           //overall ok
                corText.add(new Tuple(360, 343, "", false));           //overall ok

                //corText.add(new Tuple(360, 315, "", false));           //overall ok
                corText.add(new Tuple(360, 295, "", false));           //overall ok
                corText.add(new Tuple(360, 278, "", false));           //overall ok
                corText.add(new Tuple(360, 259, "", false));           //overall ok
                corText.add(new Tuple(360, 230, "", false));           //overall ok
                corText.add(new Tuple(360, 200, "", false));           //overall ok

                corText.add(new Tuple(360, 156, "", false));           //rubber ok

                corText.add(new Tuple(165, 101, techname, false));           //rubber ok
                corText.add(new Tuple(430, 100, "!", false));           //rubber ok
                return corText;
            }


            private ArrayList<Tuple> getPage2corText() {
                ArrayList<Tuple> corText = new ArrayList<>();

                corText.add(new Tuple(165, 256, techname, false));           //rubber ok
                corText.add(new Tuple(410, 258, "!", false));           //rubber ok

                return corText;
            }



            private boolean checkStatus() {
                if (!isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()))
                    return false;
                if (!isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()))
                    return false;
                if (!isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()))
                    return false;
                if (!isValidString(((EditText) findViewById(R.id.etHC10ver)).getText().toString()))
                    return false;

                return isValidString(((EditText)findViewById(R.id.formTechName)).getText().toString());
            }
        });
    }

    private void init() {
        Helper h = new Helper();
        h.setListener(((EditText) findViewById(R.id.formMainLocation)));
        h.setListener(((EditText) findViewById(R.id.formRoomLocation)));
        h.setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        h.setListener(((EditText) findViewById(R.id.etHC10ver)));
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
                EditText et = findViewById(R.id.etHC10ver);
                et.setText("");

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
