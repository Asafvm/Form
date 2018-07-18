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

public class HC10Activity extends DevicePrototypeActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_device_activity);
        setLayout(R.layout.device_hc10_layout);
        setListener(findViewById(R.id.formTechName));

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

                    final DatePicker dp = findViewById(R.id.formDate);
                    final String day = fixDay(dp.getDayOfMonth());
                    final String month = fixMonth(dp.getMonth());

                    findViewById(R.id.pbPDF).setVisibility(View.VISIBLE);

                    Bundle pages = new Bundle();

                    pages.putParcelableArrayList("page1", getPage1corText());
                    pages.putParcelableArrayList("page2", getPage2corText());

                    Intent intent = new Intent();
                    intent.putExtra("report", "2018_hc10_yearly.pdf");

                    intent.putExtra("pages", pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "/" +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            dp.getYear() + "" +
                            month + "" + day + "_HC10_" +
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");
                    createPDF(intent);
                }

            }

            private ArrayList<Tuple> getPage1corText() {
                final DatePicker dp = findViewById(R.id.formDate);
                final String day = fixDay(dp.getDayOfMonth());
                final String month = fixMonth(dp.getMonth());
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(447, 653, month + "    " +
                        (dp.getYear() + 1), false));                        //Next Date
                corText.add(new Tuple(70, 683, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                        ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                corText.add(new Tuple(439, 681, day + "  " +
                        month + "   " +
                        dp.getYear(), false));                        //Date
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

                return isValidString(((EditText) findViewById(R.id.formTechName)).getText().toString());
            }
        });
    }

    private void init() {
        setListener(findViewById(R.id.formMainLocation));
        setListener(findViewById(R.id.formRoomLocation));
        setListener(findViewById(R.id.etDeviceSerial));
        setListener(findViewById(R.id.etHC10ver));
    }

}
