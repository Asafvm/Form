package il.co.diamed.com.form.devices;

import android.content.Intent;
import android.os.Parcelable;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Objects;

import il.co.diamed.com.form.PDFActivity;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.res.Tuple;

import static il.co.diamed.com.form.devices.Helper.isValidString;

public class SepaxActivity extends DevicePrototypeActivity {
    private Helper h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.generic_device_activity);
        h = new Helper();
        h.setLayout(this, R.layout.device_sepax_layout);


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
                    findViewById(R.id.pbPDF).setVisibility(View.VISIBLE);

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1", getPage1corText());
                    pages.putParcelableArrayList("page2", getPage2corText());
                    pages.putParcelableArrayList("page3", getPage3corText());
                    pages.putParcelableArrayList("page4", getPage4corText());


                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
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

                //add piston position

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
                corText.add(new Tuple(543, 261, "", false));

                corText.add(new Tuple(543, 221, "", false));
                corText.add(new Tuple(543, 208, "", false));
                corText.add(new Tuple(543, 195, "", false));

                corText.add(new Tuple(543, 167, "", false));

                corText.add(new Tuple(543, 140, "", false));
                corText.add(new Tuple(543, 127, "", false));
                corText.add(new Tuple(543, 113, "", false));

                //check for sealing system test
                corText.add(new Tuple(543, 50, "", false)); //N/A

                return corText;
            }


            private ArrayList<Tuple> getPage2corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(542, 671, "", false));

                corText.add(new Tuple(542, 624, "", false));

                corText.add(new Tuple(542, 602, "", false));
                corText.add(new Tuple(542, 590, "", false));
                corText.add(new Tuple(542, 579, "", false));
                corText.add(new Tuple(542, 568, "", false));
                corText.add(new Tuple(542, 557, "", false));

                corText.add(new Tuple(542, 498, "", false));
                corText.add(new Tuple(542, 487, "", false));

                corText.add(new Tuple(542, 464, "", false));
                corText.add(new Tuple(542, 452, "", false));

                corText.add(new Tuple(542, 418, "", false));
                corText.add(new Tuple(542, 407, "", false));
                corText.add(new Tuple(542, 395, "", false));
                corText.add(new Tuple(542, 384, "", false));

                corText.add(new Tuple(542, 326, "", false));
                corText.add(new Tuple(542, 315, "", false));

                corText.add(new Tuple(542, 269, "", false));

                corText.add(new Tuple(542, 246, "", false));
                corText.add(new Tuple(542, 235, "", false));

                corText.add(new Tuple(542, 199, "", false));
                corText.add(new Tuple(542, 187, "", false));

                corText.add(new Tuple(542, 161, "", false));
                corText.add(new Tuple(542, 148, "", false));
                corText.add(new Tuple(542, 135, "", false));

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
                ArrayList<Tuple> corText = new ArrayList<>();

                //YES
                //corText.add(new Tuple(492, 689, "", false));           //rubber ok
                //corText.add(new Tuple(492, 676, "", false));           //rubber ok
                //corText.add(new Tuple(492, 663, "", false));           //rubber ok
                //corText.add(new Tuple(492, 650, "", false));           //rubber ok
                //corText.add(new Tuple(492, 638, "", false));           //rubber ok
                //NO
                corText.add(new Tuple(541, 689, "", false));           //overall ok
                corText.add(new Tuple(541, 676, "", false));           //overall ok
                corText.add(new Tuple(541, 663, "", false));           //overall ok
                corText.add(new Tuple(541, 650, "", false));           //overall ok
                corText.add(new Tuple(541, 638, "", false));           //overall ok



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

        Helper h = new Helper();
        h.setListener(((EditText) findViewById(R.id.formMainLocation)));
        h.setListener(((EditText) findViewById(R.id.formRoomLocation)));
        h.setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        h.setListener(((EditText) findViewById(R.id.formTechName)));

    }

}
