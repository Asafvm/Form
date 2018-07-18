package il.co.diamed.com.form.devices;

import android.content.Intent;
import android.os.Parcelable;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Objects;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.res.DevicePrototypeActivity;
import il.co.diamed.com.form.devices.res.Tuple;


public class SepaxActivity extends DevicePrototypeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.generic_device_activity);
        setLayout(R.layout.device_sepax_layout);


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

        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    final DatePicker dp = findViewById(R.id.formDate);
                    final String day = fixDay(dp.getDayOfMonth());
                    final String month = fixMonth(dp.getMonth());

                    findViewById(R.id.pbPDF).setVisibility(View.VISIBLE);

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1", getPage1corText());
                    pages.putParcelableArrayList("page2", getPage2corText());
                    pages.putParcelableArrayList("page3", getPage3corText());
                    pages.putParcelableArrayList("page4", getPage4corText());


                    Intent intent = new Intent();
                    intent.putExtra("report", "2018_sepax_yearly.pdf");
                    intent.putExtra("pages", pages);
                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "/" +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            dp.getYear() + "" +
                            month + "" + day + "_Sepax_" +
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");
                    createPDF(intent);
                }

            }

            private ArrayList<Tuple> getPage1corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(400, 625, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(),false));        //serial
                corText.add(new Tuple(380, 680, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                        ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location

                corText.add(new Tuple(56, 632, "", false));
                corText.add(new Tuple(100, 583,"6110017250",false));        //Multimeter


                corText.add(new Tuple(543, 504, "", false));
                corText.add(new Tuple(200, 497, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(),false));        //serial

                corText.add(new Tuple(200, 470, ((EditText) findViewById(R.id.etSepaxParam1)).getText().toString(),false));

                corText.add(new Tuple(543, 491, "", false));
                //corText.add(new Tuple(543, 478, "", false)); no printer
                corText.add(new Tuple(543, 466, "", false));

                corText.add(new Tuple(525, 440, "", false)); //N/A
                //corText.add(new Tuple(525, 425, "", false));
                //corText.add(new Tuple(525, 412, "", false));
                //corText.add(new Tuple(525, 399, "", false));

                //corText.add(new Tuple(269, 435, "", false)); no printer
                corText.add(new Tuple(269, 422, "", false));
                corText.add(new Tuple(237, 408, "English", false));

                corText.add(new Tuple(543, 274, "", false));
                corText.add(new Tuple(170, 274, ((EditText) findViewById(R.id.etSepaxAir1)).getText().toString(),false));
                corText.add(new Tuple(543, 261, "", false));
                corText.add(new Tuple(170, 259, ((EditText) findViewById(R.id.etSepaxAir2)).getText().toString(),false));

                corText.add(new Tuple(543, 221, "", false));
                corText.add(new Tuple(160, 221, ((EditText) findViewById(R.id.etSepaxAir3_1)).getText().toString(),false));
                corText.add(new Tuple(280, 221, ((EditText) findViewById(R.id.etSepaxAir3_2)).getText().toString(),false));
                corText.add(new Tuple(543, 208, "", false));
                corText.add(new Tuple(160, 208, ((EditText) findViewById(R.id.etSepaxAir4_1)).getText().toString(),false));
                corText.add(new Tuple(280, 208, ((EditText) findViewById(R.id.etSepaxAir4_2)).getText().toString(),false));
                corText.add(new Tuple(543, 195, "", false));
                corText.add(new Tuple(160, 195, ((EditText) findViewById(R.id.etSepaxAir5_1)).getText().toString(),false));
                corText.add(new Tuple(280, 195, ((EditText) findViewById(R.id.etSepaxAir5_2)).getText().toString(),false));

                corText.add(new Tuple(543, 167, "", false));
                corText.add(new Tuple(160, 167, ((EditText) findViewById(R.id.etSepaxAir6_1)).getText().toString(),false));
                corText.add(new Tuple(320, 167, ((EditText) findViewById(R.id.etSepaxAir6_2)).getText().toString(),false));

                corText.add(new Tuple(543, 140, "", false));
                corText.add(new Tuple(260, 138, ((EditText) findViewById(R.id.etSepaxAir7_1)).getText().toString(),false));
                corText.add(new Tuple(543, 127, "", false));
                corText.add(new Tuple(260, 125, ((EditText) findViewById(R.id.etSepaxAir8_1)).getText().toString(),false));
                corText.add(new Tuple(543, 113, "", false));
                corText.add(new Tuple(260, 111, ((EditText) findViewById(R.id.etSepaxAir9_1)).getText().toString(),false));

                //check for sealing system test
                //N/A
                corText.add(new Tuple(543, 50, "", false)); //N/A

                //checked
                corText.add(new Tuple(490, 50, "", false)); //N/A
                corText.add(new Tuple(150, 50, ((EditText) findViewById(R.id.etSepaxAir10_1)).getText().toString(),false));
                corText.add(new Tuple(300, 50, ((EditText) findViewById(R.id.etSepaxAir10_2)).getText().toString(),false));

                return corText;
            }


            private ArrayList<Tuple> getPage2corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                //check for sealing system test
                corText.add(new Tuple(542, 671, "", false));
                corText.add(new Tuple(150, 670, ((EditText) findViewById(R.id.etSepaxAir11_1)).getText().toString(),false));
                corText.add(new Tuple(320, 670, ((EditText) findViewById(R.id.etSepaxAir11_2)).getText().toString(),false));

                corText.add(new Tuple(542, 624, "", false));
                corText.add(new Tuple(160, 624, ((EditText) findViewById(R.id.etSepaxVolume1)).getText().toString(),false));

                corText.add(new Tuple(542, 603, "", false));
                corText.add(new Tuple(160, 601, ((EditText) findViewById(R.id.etSepaxVolume2)).getText().toString(),false));
                corText.add(new Tuple(542, 590, "", false));
                corText.add(new Tuple(155, 589, ((EditText) findViewById(R.id.etSepaxVolume3_1)).getText().toString(),false));
                corText.add(new Tuple(420, 589, ((EditText) findViewById(R.id.etSepaxVolume3_2)).getText().toString(),false));
                corText.add(new Tuple(542, 579, "", false));
                corText.add(new Tuple(155, 578, ((EditText) findViewById(R.id.etSepaxVolume4_1)).getText().toString(),false));
                corText.add(new Tuple(420, 578, ((EditText) findViewById(R.id.etSepaxVolume4_3)).getText().toString(),false));
                corText.add(new Tuple(542, 568, "", false));
                corText.add(new Tuple(542, 557, "", false));

                corText.add(new Tuple(542, 498, "", false));
                corText.add(new Tuple(110, 498, ((EditText) findViewById(R.id.etSepaxSensor1_1)).getText().toString(),false));
                corText.add(new Tuple(542, 487, "", false));
                corText.add(new Tuple(110, 486, ((EditText) findViewById(R.id.etSepaxSensor1_2)).getText().toString(),false));

                corText.add(new Tuple(542, 464, "", false));
                corText.add(new Tuple(110, 464, ((EditText) findViewById(R.id.etSepaxSensor2_1)).getText().toString(),false));
                corText.add(new Tuple(542, 452, "", false));
                corText.add(new Tuple(110, 452, ((EditText) findViewById(R.id.etSepaxSensor2_2)).getText().toString(),false));

                corText.add(new Tuple(542, 418, "", false));
                corText.add(new Tuple(542, 407, "", false));
                corText.add(new Tuple(542, 395, "", false));
                corText.add(new Tuple(235, 395, ((EditText) findViewById(R.id.etSepaxSecurity1_1)).getText().toString(),false));
                corText.add(new Tuple(342, 395, ((EditText) findViewById(R.id.etSepaxSecurity1_2)).getText().toString(),false));
                corText.add(new Tuple(542, 384, "", false));

                corText.add(new Tuple(542, 326, "", false));
                corText.add(new Tuple(220, 326, ((EditText) findViewById(R.id.etSepaxElectric1_1)).getText().toString(),false));
                corText.add(new Tuple(542, 315, "", false));
                corText.add(new Tuple(220, 314, ((EditText) findViewById(R.id.etSepaxElectric2_2)).getText().toString(),false));
                corText.add(new Tuple(220, 280, ((EditText) findViewById(R.id.etSepaxElectric3_3)).getText().toString(),false));

                corText.add(new Tuple(542, 269, "", false));
                corText.add(new Tuple(220, 268, ((EditText) findViewById(R.id.etSepaxElectric4_4)).getText().toString(),false));
                corText.add(new Tuple(542, 246, "", false));
                corText.add(new Tuple(220, 246, ((EditText) findViewById(R.id.etSepaxElectric5_5)).getText().toString(),false));
                corText.add(new Tuple(542, 235, "", false));
                corText.add(new Tuple(220, 235, ((EditText) findViewById(R.id.etSepaxElectric6_6)).getText().toString(),false));

                corText.add(new Tuple(542, 199, "", false));
                corText.add(new Tuple(542, 187, "", false));
                corText.add(new Tuple(450, 172, ((EditText) findViewById(R.id.etSepaxUser1_1)).getText().toString(),false));
                corText.add(new Tuple(542, 161, "", false));
                corText.add(new Tuple(542, 148, "", false));
                corText.add(new Tuple(542, 135, "", false));


                if(((Switch)findViewById(R.id.switchSepaxSW2)).isChecked())
                    corText.add(new Tuple(507, 92, "", false));
                else
                    corText.add(new Tuple(478, 92, "", false));

                if(((Switch)findViewById(R.id.switchSepaxSW3)).isChecked())
                    corText.add(new Tuple(507, 79, "", false));
                else
                    corText.add(new Tuple(478, 79, "", false));

                if(((Switch)findViewById(R.id.switchSepaxSW4)).isChecked())
                    corText.add(new Tuple(507, 66, "", false));
                else
                    corText.add(new Tuple(478, 66, "", false));

                return corText;
            }


            private ArrayList<Tuple> getPage3corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(542, 689, "", false));
                corText.add(new Tuple(542, 677, "", false));
                corText.add(new Tuple(542, 664, "", false));
                corText.add(new Tuple(542, 651, "", false));
                corText.add(new Tuple(542, 639, "", false));
                corText.add(new Tuple(542, 626, "", false));

                corText.add(new Tuple(542, 589, "", false));
                corText.add(new Tuple(542, 576, "", false));



                corText.add(new Tuple(500, 537, "N/A", false));
                corText.add(new Tuple(542, 539, "", false));
                /* N/A
                corText.add(new Tuple(542, 485, "", false));
                corText.add(new Tuple(542, 474, "", false));
                corText.add(new Tuple(542, 462, "", false));
                corText.add(new Tuple(542, 450, "", false));

                corText.add(new Tuple(542, 390, "", false));

                corText.add(new Tuple(542, 329, "", false));
                corText.add(new Tuple(542, 316, "", false));

                corText.add(new Tuple(542, 255, "", false));
                */

                corText.add(new Tuple(500, 215, "N/A", false));
                corText.add(new Tuple(542, 217, "", false));
                //corText.add(new Tuple(542, 199, "", false)); N/A

                corText.add(new Tuple(542, 152, "", false));

                return corText;
            }

            public ArrayList<? extends Parcelable> getPage4corText() {
                final DatePicker dp = findViewById(R.id.formDate);
                final String day = fixDay(dp.getDayOfMonth());
                final String month = fixMonth(dp.getMonth());

                ArrayList<Tuple> corText = new ArrayList<>();

                //YES
                //corText.add(new Tuple(492, 689, "", false));
                //corText.add(new Tuple(492, 676, "", false));
                //corText.add(new Tuple(492, 663, "", false));
                //corText.add(new Tuple(492, 650, "", false));
                //corText.add(new Tuple(492, 638, "", false));
                //NO
                corText.add(new Tuple(541, 689, "", false));
                corText.add(new Tuple(541, 676, "", false));
                corText.add(new Tuple(541, 663, "", false));
                corText.add(new Tuple(541, 650, "", false));
                corText.add(new Tuple(541, 638, "", false));



                corText.add(new Tuple(150, 267, "X-Ring", false));           //Part


                corText.add(new Tuple(542, 119, "", false));           //PASS

                corText.add(new Tuple(100, 61, day +" / "+ month + " / "+ dp.getYear(), true));                        //Tech Name
                corText.add(new Tuple(380, 60, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                corText.add(new Tuple(440, 19, "!", false));                        //Signature

                return corText;            }

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

    }

}
