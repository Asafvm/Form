package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

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
        h.setLayout(this, R.layout.activity_sepax);


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
                corText.add(new Tuple(470, 628, month + "    " +
                        (dp.getYear() + 1), false));                        //Next Date
                corText.add(new Tuple(90, 658, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                        ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                corText.add(new Tuple(463, 658, day + " " +
                        month + "  " +
                        dp.getYear(), false));                        //Date
                //corText.add(new Tuple(380,30));                        //Signature
                corText.add(new Tuple(422, 570, "", false));           //temp ok
                corText.add(new Tuple(422, 545, "", false));           //time ok
                corText.add(new Tuple(422, 521, "", false));           //fan ok

                corText.add(new Tuple(253, 467, "", false));           //rubber ok
                corText.add(new Tuple(253, 449, "", false));           //overall ok
                corText.add(new Tuple(253, 538, "", false));           //rubber ok
                corText.add(new Tuple(253, 413, "", false));           //overall ok
                corText.add(new Tuple(253, 394, "", false));           //rubber ok
                corText.add(new Tuple(253, 375, "", false));           //rubber ok
                corText.add(new Tuple(253, 358, "", false));           //overall ok

                corText.add(new Tuple(538, 282, "", false));           //overall ok
                corText.add(new Tuple(538, 264, "", false));           //overall ok
                //corText.add(new Tuple(430, 242, "", false));           //rubber ok

                corText.add(new Tuple(538, 162, "", false));           //overall ok
                corText.add(new Tuple(538, 144, "", false));           //rubber ok
                corText.add(new Tuple(538, 126, "", false));           //rubber ok

                return corText;
            }


            private ArrayList<Tuple> getPage2corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(538, 710, "", false));           //overall ok
                corText.add(new Tuple(538, 692, "", false));           //rubber ok
                corText.add(new Tuple(538, 673, "", false));           //overall ok
                corText.add(new Tuple(538, 655, "", false));           //rubber ok
                corText.add(new Tuple(538, 637, "", false));           //overall ok


                corText.add(new Tuple(538, 567, "", false));           //rubber ok
                corText.add(new Tuple(538, 549, "", false));           //temp ok
                corText.add(new Tuple(538, 531, "", false));           //time ok
                corText.add(new Tuple(538, 512, "", false));           //fan ok

                corText.add(new Tuple(538, 443, "", false));           //rubber ok
                corText.add(new Tuple(538, 425, "", false));           //overall ok
                corText.add(new Tuple(538, 407, "", false));           //overall ok
                corText.add(new Tuple(538, 388, "", false));           //rubber ok
                corText.add(new Tuple(538, 367, "", false));           //overall ok
                //corText.add(new Tuple(430, 344, "", false));           //rubber ok
                //corText.add(new Tuple(430, 319, "", false));           //overall ok
                //corText.add(new Tuple(430, 295, "", false));           //overall ok

                corText.add(new Tuple(538, 239, "", false));           //overall ok
                corText.add(new Tuple(538, 220, "", false));           //rubber ok
                corText.add(new Tuple(538, 202, "", false));           //overall ok
                corText.add(new Tuple(538, 184, "", false));           //rubber ok
                corText.add(new Tuple(538, 163, "", false));           //overall ok
                corText.add(new Tuple(538, 142, "", false));           //rubber ok
                //corText.add(new Tuple(430, 121, "", false));           //rubber ok
                //corText.add(new Tuple(430, 94, "", false));           //rubber ok

                return corText;
            }


            private ArrayList<Tuple> getPage3corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(542, 684, "", false));           //rubber ok
                corText.add(new Tuple(542, 671, "", false));           //rubber ok
                corText.add(new Tuple(542, 658, "", false));           //rubber ok
                corText.add(new Tuple(542, 645, "", false));           //rubber ok
                corText.add(new Tuple(542, 633, "", false));           //rubber ok
                corText.add(new Tuple(542, 620, "", false));           //rubber ok

                corText.add(new Tuple(541, 585, "", false));           //rubber ok
                corText.add(new Tuple(541, 572, "", false));           //rubber ok

                corText.add(new Tuple(540, 480, "", false));           //rubber ok
                corText.add(new Tuple(540, 469, "", false));           //rubber ok
                corText.add(new Tuple(540, 458, "", false));           //rubber ok
                corText.add(new Tuple(540, 447, "", false));           //rubber ok

                corText.add(new Tuple(539, 386, "", false));           //rubber ok

                corText.add(new Tuple(539, 325, "", false));           //rubber ok
                corText.add(new Tuple(539, 312, "", false));           //rubber ok

                corText.add(new Tuple(539, 251, "", false));           //rubber ok

                corText.add(new Tuple(539, 196, "", false));           //rubber ok

                corText.add(new Tuple(539, 150, "", false));           //rubber ok

                return corText;
            }

            public ArrayList<? extends Parcelable> getPage4corText() {
                ArrayList<Tuple> corText = new ArrayList<>();

                //YES
                corText.add(new Tuple(492, 685, "", false));           //rubber ok
                corText.add(new Tuple(492, 672, "", false));           //rubber ok
                corText.add(new Tuple(492, 659, "", false));           //rubber ok
                corText.add(new Tuple(492, 646, "", false));           //rubber ok
                corText.add(new Tuple(492, 634, "", false));           //rubber ok
                //NO
                corText.add(new Tuple(540, 685, "", false));           //overall ok
                corText.add(new Tuple(540, 672, "", false));           //overall ok
                corText.add(new Tuple(540, 659, "", false));           //overall ok
                corText.add(new Tuple(540, 646, "", false));           //overall ok
                corText.add(new Tuple(540, 634, "", false));           //overall ok

                corText.add(new Tuple(539, 118, "", false));           //overall ok

                corText.add(new Tuple(100, 61, day +"/"+ month + "/"+ dp.getYear(), true));                        //Tech Name
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
