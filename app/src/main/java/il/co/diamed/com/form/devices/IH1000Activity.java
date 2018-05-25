package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import il.co.diamed.com.form.PDFActivity;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.res.Tuple;

import static il.co.diamed.com.form.devices.Helper.isValidString;

public class IH1000Activity extends AppCompatActivity {
    private int EXPECTED_SPEED = 1008;
    private final double EXPECTED_INCUBATOR_TEMP = 38;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.generic_device_activity);
        final Helper h = new Helper();
        h.setLayout(this, R.layout.device_ih1000_layout_short);


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
        final DatePicker dp = findViewById(R.id.formDate);
        final String day = h.fixDay(dp.getDayOfMonth());
        final String month = h.fixMonth(dp.getMonth());

        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1", getPage1corText());
                    pages.putParcelableArrayList("page2", getPage2corText());
                    pages.putParcelableArrayList("page3", getPage3corText());


                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_ih1000_yearly.pdf");
                    intent.putExtra("pages", pages);
                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "/" +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            dp.getYear() + "" +
                            month + "" +day + "_IH1000_" +
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");
                    startActivityForResult(intent, 1);
                }
            }

            private ArrayList<Tuple> getPage1corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(489, 630, month + "   " +
                        (dp.getYear() + 1), false));                        //Next Date
                corText.add(new Tuple(90, 660, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                        ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                corText.add(new Tuple(456, 658, day + " " +
                        month + " " +
                        dp.getYear(), false));                        //Date
                corText.add(new Tuple(132, 630, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                //corText.add(new Tuple(380,30));                        //Signature
                corText.add(new Tuple(360, 550, "", false));           //temp ok
                corText.add(new Tuple(360, 531, "", false));           //time ok
                corText.add(new Tuple(360, 512, "", false));           //fan ok
                corText.add(new Tuple(360, 493, "", false));           //fan ok

                corText.add(new Tuple(360, 458, "", false));           //rubber ok
                corText.add(new Tuple(500, 458, ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgIH1000software1)).getCheckedRadioButtonId())).getText().toString(), false));           //overall ok
                corText.add(new Tuple(360, 441, "", false));           //overall ok
                corText.add(new Tuple(470, 440, ((EditText) findViewById(R.id.etIH1000softwareVer)).getText().toString(), false));           //rubber ok
                corText.add(new Tuple(470, 421, ((EditText) findViewById(R.id.etIH1000softwareVer2)).getText().toString(), false));           //rubber ok
                corText.add(new Tuple(360, 421, "", false));           //rubber ok
                corText.add(new Tuple(360, 401, "", false));           //overall ok
                corText.add(new Tuple(467, 386, ((EditText) findViewById(R.id.etIH1000softwareMaster)).getText().toString(), false));           //rubber ok
                corText.add(new Tuple(467, 356, ((EditText) findViewById(R.id.etIH1000softwareAutomate)).getText().toString(), false));           //rubber ok
                corText.add(new Tuple(360, 380, "", false));           //rubber ok
                corText.add(new Tuple(360, 354, "", false));           //overall ok

                corText.add(new Tuple(360, 305, "", false));           //overall ok
                corText.add(new Tuple(360, 285, "", false));           //overall ok
                corText.add(new Tuple(360, 265, "", false));           //overall ok
                corText.add(new Tuple(360, 246, "", false));           //rubber ok
                corText.add(new Tuple(440, 230, speedometer, false));           //overall ok
                corText.add(new Tuple(300, 230, ((EditText) findViewById(R.id.etIH1000CentrifugeLeftSpeed)).getText().toString(), false));           //overall ok
                corText.add(new Tuple(300, 211, ((EditText) findViewById(R.id.etIH1000CentrifugeCenterSpeed)).getText().toString(), false));           //overall ok
                corText.add(new Tuple(300, 192, ((EditText) findViewById(R.id.etIH1000CentrifugeRightSpeed)).getText().toString(), false));           //overall ok
                corText.add(new Tuple(360, 230, "", false));           //overall ok
                corText.add(new Tuple(360, 211, "", false));           //overall ok
                corText.add(new Tuple(360, 193, "", false));           //rubber ok


                corText.add(new Tuple(360, 160, "", false));           //overall ok
                corText.add(new Tuple(360, 142, "", false));           //rubber ok
                corText.add(new Tuple(440, 122, thermometer, false));           //overall ok
                corText.add(new Tuple(300, 122, ((EditText) findViewById(R.id.etIH1000IncubatorFront)).getText().toString(), false));           //rubber ok
                corText.add(new Tuple(300, 103, ((EditText) findViewById(R.id.etIH1000IncubatorMiddle)).getText().toString(), false));           //rubber ok
                corText.add(new Tuple(300, 84, ((EditText) findViewById(R.id.etIH1000IncubatorRear)).getText().toString(), false));           //time ok
                corText.add(new Tuple(360, 122, "", false));           //overall ok
                corText.add(new Tuple(360, 103, "", false));           //rubber ok
                corText.add(new Tuple(360, 84, "", false));           //rubber ok
                return corText;
            }


            private ArrayList<Tuple> getPage2corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(360, 684, "", false));           //rubber ok
                corText.add(new Tuple(360, 666, "", false));           //overall ok
                corText.add(new Tuple(300, 644, ((EditText) findViewById(R.id.etIH1000CardSensor)).getText().toString(), false));           //overall ok
                corText.add(new Tuple(360, 646, "", false));           //rubber ok
                corText.add(new Tuple(360, 617, "", false));           //overall ok
                corText.add(new Tuple(360, 598, "", false));           //overall ok
                corText.add(new Tuple(360, 571, "", false));           //rubber ok
                corText.add(new Tuple(360, 549, "", false));           //temp ok
                corText.add(new Tuple(360, 531, "", false));           //time ok
                corText.add(new Tuple(360, 512, "", false));           //fan ok

                corText.add(new Tuple(360, 473, "", false));           //rubber ok
                corText.add(new Tuple(360, 443, "", false));           //rubber ok
                corText.add(new Tuple(360, 415, "", false));           //overall ok
                corText.add(new Tuple(360, 390, "", false));           //rubber ok
                corText.add(new Tuple(360, 373, "", false));           //overall ok
                corText.add(new Tuple(360, 355, "", false));           //overall ok

                corText.add(new Tuple(360, 321, "", false));           //overall ok
                corText.add(new Tuple(360, 302, "", false));           //rubber ok
                corText.add(new Tuple(360, 284, "", false));           //overall ok

                corText.add(new Tuple(360, 244, "", false));           //overall ok
                corText.add(new Tuple(360, 225, "", false));           //rubber ok
                corText.add(new Tuple(360, 207, "", false));           //overall ok
                corText.add(new Tuple(360, 190, "", false));           //rubber ok

                corText.add(new Tuple(360, 146, "", false));           //rubber ok
                corText.add(new Tuple(360, 113, "", false));           //rubber ok

                return corText;
            }


            private ArrayList<Tuple> getPage3corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(360, 685, "", false));           //rubber ok
                corText.add(new Tuple(360, 659, "", false));           //rubber ok
                corText.add(new Tuple(360, 634, "", false));           //rubber ok
                corText.add(new Tuple(360, 617, "", false));           //overall ok
                corText.add(new Tuple(360, 599, "", false));           //overall ok
                corText.add(new Tuple(360, 580, "", false));           //overall ok
                corText.add(new Tuple(360, 561, "", false));           //overall ok

                corText.add(new Tuple(360, 524, "", false));           //overall ok
                corText.add(new Tuple(360, 505, "", false));           //overall ok
                corText.add(new Tuple(360, 486, "", false));           //overall ok

                corText.add(new Tuple(360, 450, "", false));           //overall ok

                corText.add(new Tuple(100, 112, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                corText.add(new Tuple(461, 106, "!", false));                        //Signature

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
        h.setSpeedListener(((EditText) findViewById(R.id.etIH1000CentrifugeRightSpeed)), EXPECTED_SPEED);
        h.setSpeedListener(((EditText) findViewById(R.id.etIH1000CentrifugeCenterSpeed)), EXPECTED_SPEED);
        h.setSpeedListener(((EditText) findViewById(R.id.etIH1000CentrifugeLeftSpeed)), EXPECTED_SPEED);
        h.setTempListener(((EditText) findViewById(R.id.etIH1000IncubatorFront)), EXPECTED_INCUBATOR_TEMP - 1, EXPECTED_INCUBATOR_TEMP + 1);
        h.setTempListener(((EditText) findViewById(R.id.etIH1000IncubatorMiddle)), EXPECTED_INCUBATOR_TEMP - 1, EXPECTED_INCUBATOR_TEMP + 1);
        h.setTempListener(((EditText) findViewById(R.id.etIH1000IncubatorRear)), EXPECTED_INCUBATOR_TEMP - 1, EXPECTED_INCUBATOR_TEMP + 1);

        h.setListener(((EditText) findViewById(R.id.etIH1000softwareVer2)));
        h.setListener(((EditText) findViewById(R.id.etIH1000softwareVer)));

        ((EditText) findViewById(R.id.formMainLocation)).setText("");
        ((EditText) findViewById(R.id.formRoomLocation)).setText("");
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");
        ((EditText) findViewById(R.id.etIH1000CentrifugeRightSpeed)).setText("");
        ((EditText) findViewById(R.id.etIH1000CentrifugeCenterSpeed)).setText("");
        ((EditText) findViewById(R.id.etIH1000CentrifugeLeftSpeed)).setText("");
        ((EditText) findViewById(R.id.etIH1000IncubatorFront)).setText("");
        ((EditText) findViewById(R.id.etIH1000IncubatorMiddle)).setText("");
        ((EditText) findViewById(R.id.etIH1000IncubatorRear)).setText("");
        ((EditText) findViewById(R.id.etIH1000softwareVer2)).setText("");
        ((EditText) findViewById(R.id.etIH1000softwareVer)).setText("");
        ((EditText) findViewById(R.id.etIH1000CardSensor)).setText("");
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
