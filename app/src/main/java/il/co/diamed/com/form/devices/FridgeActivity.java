package il.co.diamed.com.form.devices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Objects;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.res.DevicePrototypeActivity;
import il.co.diamed.com.form.devices.res.Tuple;

public class FridgeActivity extends DevicePrototypeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_device_activity);
        setLayout(R.layout.device_fridge_layout);
        setListener(findViewById(R.id.formTechName));

        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        final String thermometer = bundle.getString("thermometer");
        //get views
        init();
        ((EditText) findViewById(R.id.formTechName)).setText(techname);

        //default basic values


        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    findViewById(R.id.pbPDF).setVisibility(View.VISIBLE);
                    DatePicker dp = findViewById(R.id.formDate);
                    String day = fixDay(dp.getDayOfMonth());
                    String month = fixMonth(dp.getMonth());
                    ArrayList<Tuple> corText = new ArrayList<>();
                    corText.add(new Tuple(205, 426, "", false));           //temp ok

                    if(((RadioGroup)findViewById(R.id.rgTypeSelect)).getCheckedRadioButtonId()==R.id.rbFridge)
                        corText.add(new Tuple(138, 508, "", false));           //fridge
                    else
                        corText.add(new Tuple(204, 508, "", false));           //sensor

                    corText.add(new Tuple(482, 269, "", false));           //overall ok

                    corText.add(new Tuple(335, 617, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " + ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));             //Location
                    corText.add(new Tuple(340, 163, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));            //Tech Name
                    corText.add(new Tuple(100, 615, day + "    " + month + "     " + dp.getYear(), false));                        //Date
                    corText.add(new Tuple(95, 525, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                    corText.add(new Tuple(365, 525, ((EditText) findViewById(R.id.etModel)).getText().toString(), false));                        //ver
                    corText.add(new Tuple(406, 213, month + "    " + (dp.getYear() + 1), false));                        //Next Date
                    //corText.add(new Tuple(72, 525, ((EditText) findViewById(R.id.etSensor)).getText().toString(), false));
                    corText.add(new Tuple(278, 382, thermometer, false));
                    corText.add(new Tuple(410, 422, ((EditText) findViewById(R.id.etSysTemp)).getText().toString(), false));
                    corText.add(new Tuple(275, 422, ((EditText) findViewById(R.id.etMesTemp)).getText().toString(), false));

                    corText.add(new Tuple(140, 163, "!", false));                        //Signature

                    Intent intent = new Intent();
                    intent.putExtra("report", "2018_fridge_yearly.pdf");

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1", corText);
                    intent.putExtra("pages", pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "/" +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            dp.getYear() + "" + month + "" + day + "_" +
                            ((EditText) findViewById(R.id.etModel)).getText().toString() + "_" +
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");
                    createPDF(intent);
                }

            }


            private boolean checkStatus() {
                if (!isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()))
                    return false;
                if (!isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()))
                    return false;
                if (!isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()))
                    return false;
                return isValidString(((EditText) findViewById(R.id.formTechName)).getText().toString());
            }
        });
    }

    private void init() {
        setListener(findViewById(R.id.formMainLocation));
        setListener(findViewById(R.id.formRoomLocation));
        setListener(findViewById(R.id.etDeviceSerial));
    }

}
