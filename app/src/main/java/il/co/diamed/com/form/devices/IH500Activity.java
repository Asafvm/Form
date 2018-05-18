package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import il.co.diamed.com.form.PDFActivity;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.Tuple;

import static il.co.diamed.com.form.devices.Helper.isValidString;

public class IH500Activity extends AppCompatActivity {
    private int EXPECTED_SPEED = 1008;
    private final double EXPECTED_INCUBATOR_TEMP = 38.5;
    private final int EXPECTED_REAGENT_TEMP = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.generic_device_activity);
        final Helper h = new Helper();
        h.setLayout(this, R.layout.device_ih500_layout_short);


        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        final String thermometer = bundle.getString("thermometer");
        final String speedometer = bundle.getString("speedometer");
        final String barometer = bundle.getString("barometer");


        Helper helper = new Helper();
        init();
        helper.setListener((EditText) findViewById(R.id.formTechName));
        ((EditText) findViewById(R.id.formTechName)).setText(techname);


        //default basic values
        ((EditText) findViewById(R.id.formTechName)).setText(techname);


        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    DatePicker dp = findViewById(R.id.formDate);
                    String day = h.fixDay(dp.getDayOfMonth());
                    String month = h.fixMonth(dp.getMonth());
                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1", getPage1corText());
                    pages.putParcelableArrayList("page2", getPage2corText());
                    pages.putParcelableArrayList("page3", getPage3corText());


                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_ih500_yearly.pdf");
                    intent.putExtra("pages", pages);
                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "/" +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            dp.getYear() + "" +
                            month + "" + day + "_IH500_" +
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");
                    startActivityForResult(intent, 1);
                }

            }

            private ArrayList<Tuple> getPage1corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(474, 628, ((DatePicker) findViewById(R.id.formDate)).getMonth() + "   " +
                        (((DatePicker) findViewById(R.id.formDate)).getYear() + 1), false));                        //Next Date
                corText.add(new Tuple(90, 658, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                        ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                corText.add(new Tuple(464, 658, ((DatePicker) findViewById(R.id.formDate)).getDayOfMonth() + "   " +
                        ((DatePicker) findViewById(R.id.formDate)).getMonth() + "    " +
                        ((DatePicker) findViewById(R.id.formDate)).getYear(), false));                        //Date
                corText.add(new Tuple(135, 628, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                //corText.add(new Tuple(380,30));                        //Signature
                corText.add(new Tuple(422, 570, "", false));           //temp ok
                corText.add(new Tuple(422, 545, "", false));           //time ok
                corText.add(new Tuple(422, 521, "", false));           //fan ok

                corText.add(new Tuple(253, 467, "", false));           //rubber ok
                corText.add(new Tuple(489, 450, "Win7 SP1", false));           //overall ok
                corText.add(new Tuple(253, 449, "", false));           //overall ok
                corText.add(new Tuple(480, 432, ((EditText) findViewById(R.id.etIH500softwareVer)).getText().toString(), false));           //rubber ok
                corText.add(new Tuple(253, 431, "", false));           //rubber ok
                corText.add(new Tuple(253, 413, "", false));           //overall ok
                corText.add(new Tuple(410, 396, "C:\\"+((EditText) findViewById(R.id.etIH500softwareC)).getText().toString(), false));           //rubber ok
                corText.add(new Tuple(253, 394, "", false));           //rubber ok
                corText.add(new Tuple(253, 375, "", false));           //rubber ok
                corText.add(new Tuple(253, 358, "", false));           //overall ok

                corText.add(new Tuple(431, 282, "", false));           //overall ok
                corText.add(new Tuple(431, 264, "", false));           //overall ok
                //corText.add(new Tuple(430, 242, "", false));           //rubber ok

                corText.add(new Tuple(431, 162, "", false));           //overall ok
                corText.add(new Tuple(431, 144, "", false));           //rubber ok
                corText.add(new Tuple(431, 126, "", false));           //rubber ok

                return corText;
            }


            private ArrayList<Tuple> getPage2corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(431, 710, "", false));           //overall ok
                corText.add(new Tuple(431, 692, "", false));           //rubber ok
                corText.add(new Tuple(431, 673, "", false));           //overall ok
                corText.add(new Tuple(431, 655, "", false));           //rubber ok
                corText.add(new Tuple(431, 637, "", false));           //overall ok


                corText.add(new Tuple(431, 567, "", false));           //rubber ok
                corText.add(new Tuple(431, 549, "", false));           //temp ok
                corText.add(new Tuple(431, 531, "", false));           //time ok
                corText.add(new Tuple(431, 512, "", false));           //fan ok

                corText.add(new Tuple(431, 443, "", false));           //rubber ok
                corText.add(new Tuple(431, 425, "", false));           //overall ok
                corText.add(new Tuple(431, 407, "", false));           //overall ok
                corText.add(new Tuple(431, 388, "", false));           //rubber ok
                corText.add(new Tuple(431, 367, "", false));           //overall ok
                //corText.add(new Tuple(430, 344, "", false));           //rubber ok
                //corText.add(new Tuple(430, 319, "", false));           //overall ok
                //corText.add(new Tuple(430, 295, "", false));           //overall ok

                corText.add(new Tuple(431, 239, "", false));           //overall ok
                corText.add(new Tuple(431, 220, "", false));           //rubber ok
                corText.add(new Tuple(431, 202, "", false));           //overall ok
                corText.add(new Tuple(431, 184, "", false));           //rubber ok
                corText.add(new Tuple(431, 163, "", false));           //overall ok
                corText.add(new Tuple(431, 142, "", false));           //rubber ok
                //corText.add(new Tuple(430, 121, "", false));           //rubber ok
                //corText.add(new Tuple(430, 94, "", false));           //rubber ok

                return corText;
            }


            private ArrayList<Tuple> getPage3corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(502, 722, "", false));           //rubber ok
                corText.add(new Tuple(502, 703, "", false));           //overall ok
                corText.add(new Tuple(502, 685, "", false));           //rubber ok
                corText.add(new Tuple(440, 650, speedometer, false));           //overall ok
                corText.add(new Tuple(380, 647, ((EditText) findViewById(R.id.etIH500CentrifugeFrontSpeed)).getText().toString(), false));           //overall ok
                corText.add(new Tuple(380, 627, ((EditText) findViewById(R.id.etIH500CentrifugeRearSpeed)).getText().toString(), false));           //overall ok
                corText.add(new Tuple(502, 612, "", false));           //overall ok
                corText.add(new Tuple(502, 593, "", false));           //overall ok

                corText.add(new Tuple(440, 510, thermometer, false));           //overall ok
                corText.add(new Tuple(380, 510, ((EditText) findViewById(R.id.etIH500ReagentFront)).getText().toString(), false));           //overall ok
                corText.add(new Tuple(380, 490, ((EditText) findViewById(R.id.etIH500ReagentRear)).getText().toString(), false));           //rubber ok

                //corText.add(new Tuple(440, 418, thermometer, false));           //overall ok
                corText.add(new Tuple(380, 418, ((EditText) findViewById(R.id.etIH500IncubatorFront)).getText().toString(), false));           //rubber ok
                corText.add(new Tuple(380, 398, ((EditText) findViewById(R.id.etIH500IncubatorRear)).getText().toString(), false));           //time ok

                corText.add(new Tuple(431, 330, "", false));           //temp ok
                corText.add(new Tuple(431, 312, "", false));           //fan ok

                corText.add(new Tuple(100, 132, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                corText.add(new Tuple(465, 131, "!", false));                        //Signature

                return corText;
            }

            private boolean checkStatus() {
                return isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()) && isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()) && isValidString(((EditText) findViewById(R.id.formTechName)).getText().toString());

            }
        });
    }

    private void init() {

        Helper h = new Helper();
        h.setListener(((EditText) findViewById(R.id.formMainLocation)));
        h.setListener(((EditText) findViewById(R.id.formRoomLocation)));
        h.setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        h.setListener(((EditText) findViewById(R.id.formTechName)));
        h.setSpeedListener(((EditText) findViewById(R.id.etIH500CentrifugeFrontSpeed)), EXPECTED_SPEED);
        h.setSpeedListener(((EditText) findViewById(R.id.etIH500CentrifugeRearSpeed)), EXPECTED_SPEED);
        h.setTempListener(((EditText) findViewById(R.id.etIH500IncubatorFront)), EXPECTED_INCUBATOR_TEMP - 1, EXPECTED_INCUBATOR_TEMP + 1);
        h.setTempListener(((EditText) findViewById(R.id.etIH500IncubatorRear)), EXPECTED_INCUBATOR_TEMP - 1, EXPECTED_INCUBATOR_TEMP + 1);
        h.setTempListener(((EditText) findViewById(R.id.etIH500ReagentFront)), EXPECTED_REAGENT_TEMP - 1, EXPECTED_REAGENT_TEMP + 1);
        h.setTempListener(((EditText) findViewById(R.id.etIH500ReagentRear)), EXPECTED_REAGENT_TEMP - 1, EXPECTED_REAGENT_TEMP + 1);
        h.setListener(((EditText) findViewById(R.id.etIH500softwareC)));
        h.setListener(((EditText) findViewById(R.id.etIH500softwareVer)));

        ((EditText) findViewById(R.id.formMainLocation)).setText("");
        ((EditText) findViewById(R.id.formRoomLocation)).setText("");
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");
        ((EditText) findViewById(R.id.etIH500CentrifugeFrontSpeed)).setText("");
        ((EditText) findViewById(R.id.etIH500CentrifugeRearSpeed)).setText("");
        ((EditText) findViewById(R.id.etIH500IncubatorFront)).setText("");
        ((EditText) findViewById(R.id.etIH500IncubatorRear)).setText("");
        ((EditText) findViewById(R.id.etIH500ReagentFront)).setText("");
        ((EditText) findViewById(R.id.etIH500ReagentRear)).setText("");
        ((EditText) findViewById(R.id.etIH500softwareC)).setText("");
        ((EditText) findViewById(R.id.etIH500softwareVer)).setText("");
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
