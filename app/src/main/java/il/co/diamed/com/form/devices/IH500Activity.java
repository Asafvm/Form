package il.co.diamed.com.form.devices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Objects;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.res.DevicePrototypeActivity;
import il.co.diamed.com.form.devices.res.Tuple;


public class IH500Activity extends DevicePrototypeActivity {
    private int EXPECTED_SPEED = 1008;
    private final double EXPECTED_INCUBATOR_TEMP = 38.5;
    private final int EXPECTED_REAGENT_TEMP = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.generic_device_activity);
        setLayout(R.layout.device_ih500_layout_short);


        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        final String thermometer = bundle.getString("thermometer");
        final String speedometer = bundle.getString("speedometer");
        final String barometer = bundle.getString("barometer");


        init();
        setListener(findViewById(R.id.formTechName));
        ((EditText) findViewById(R.id.formTechName)).setText(techname);


        //default basic values
        ((EditText) findViewById(R.id.formTechName)).setText(techname);
        final DatePicker dp = findViewById(R.id.formDate);
        final String day = fixDay(dp.getDayOfMonth());
        final String month = fixMonth(dp.getMonth());

        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    findViewById(R.id.pbPDF).setVisibility(View.VISIBLE);

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1", getPage1corText());
                    pages.putParcelableArrayList("page2", getPage2corText());
                    pages.putParcelableArrayList("page3", getPage3corText());


                    Intent intent = new Intent();
                    intent.putExtra("report", "2018_ih500_yearly.pdf");
                    intent.putExtra("pages", pages);
                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "/" +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            dp.getYear() + "" +
                            month + "" + day + "_IH500_" +
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");
                    createPDF(intent);
                }

            }

            private ArrayList<Tuple> getPage1corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(470, 628, month + "    " +
                        (dp.getYear() + 1), false));                        //Next Date
                corText.add(new Tuple(90, 658, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                        ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                corText.add(new Tuple(463, 658, day + " " +
                        month + "  " +
                        dp.getYear(), false));                        //Date
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
                corText.add(new Tuple(410, 396, "C:\\" + ((EditText) findViewById(R.id.etIH500softwareC)).getText().toString(), false));           //rubber ok
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

        setListener(findViewById(R.id.formMainLocation));
        setListener(findViewById(R.id.formRoomLocation));
        setListener(findViewById(R.id.etDeviceSerial));
        setListener(findViewById(R.id.formTechName));
        setSpeedListener(findViewById(R.id.etIH500CentrifugeFrontSpeed), EXPECTED_SPEED);
        setSpeedListener(findViewById(R.id.etIH500CentrifugeRearSpeed), EXPECTED_SPEED);
        setTempListener(findViewById(R.id.etIH500IncubatorFront), EXPECTED_INCUBATOR_TEMP - 1, EXPECTED_INCUBATOR_TEMP + 1);
        setTempListener(findViewById(R.id.etIH500IncubatorRear), EXPECTED_INCUBATOR_TEMP - 1, EXPECTED_INCUBATOR_TEMP + 1);
        setTempListener(findViewById(R.id.etIH500ReagentFront), EXPECTED_REAGENT_TEMP - 1, EXPECTED_REAGENT_TEMP + 1);
        setTempListener(findViewById(R.id.etIH500ReagentRear), EXPECTED_REAGENT_TEMP - 1, EXPECTED_REAGENT_TEMP + 1);
        setListener(findViewById(R.id.etIH500softwareC));
        setListener(findViewById(R.id.etIH500softwareVer));

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

}
