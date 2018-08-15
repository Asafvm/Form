package il.co.diamed.com.form.calibration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.calibration.res.DevicePrototypeActivity;
import il.co.diamed.com.form.calibration.res.Tuple;

public class IncubatorActivity extends DevicePrototypeActivity {
    private static final String TAG = "IncubatorActivity";
    private final int MAX_TEMP = 39;
    private final int MIN_TEMP = 35;
    private final int EXPECTED_TIME = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_device_activity);
        setLayout(R.layout.device_incubator_layout);
        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        final String thermometer = bundle.getString("thermometer");
        final String timer = bundle.getString("timer");
        //get views
        init();
        setListener(findViewById(R.id.formTechName));
        ((EditText) findViewById(R.id.formTechName)).setText(techname);

        //Device class


        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    DatePicker dp = findViewById(R.id.formDate);
                    String day = fixDay(dp.getDayOfMonth());
                    String month = fixMonth(dp.getMonth());

                    ArrayList<Tuple> corText = new ArrayList<>();
                    corText.add(new Tuple(205, 469, "", false));           //temp ok
                    corText.add(new Tuple(203, 329, "", false));           //time ok
                    corText.add(new Tuple(251, 230, "", false));           //fan ok
                    corText.add(new Tuple(251, 212, "", false));           //rubber ok
                    corText.add(new Tuple(482, 97, "", false));           //overall ok

                    corText.add(new Tuple(300, 636, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                    corText.add(new Tuple(330, 30, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                    corText.add(new Tuple(71, 636, day + "      " + month + "        " +
                            dp.getYear(), false));                        //Date
                    corText.add(new Tuple(290, 568, ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString(), false));                        //type
                    corText.add(new Tuple(425, 568, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                    corText.add(new Tuple(278, 466, ((EditText) findViewById(R.id.temp)).getText().toString(), false));                        //temp
                    corText.add(new Tuple(305, 327, ((EditText) findViewById(R.id.time)).getText().toString(), false));                        //Time
                    corText.add(new Tuple(442, 65, month + "    " + (dp.getYear() + 1), false));                        //Next Date

                    corText.add(new Tuple(330, 425, thermometer, false));                        //Thermometer
                    corText.add(new Tuple(400, 285, timer, false));                        //Timer

                    corText.add(new Tuple(135, 33, "!", false));                        //Signature

                    Intent intent = new Intent();

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1", corText);
                    intent.putExtra("pages", pages);

                    intent.putExtra("report", "2018_id37_yearly.pdf");

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "/" +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            dp.getYear() + "" +
                            month + "" + day + "_" + "Incubator-" +
                            ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString() + "_" +
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");

                    intent.putExtra("type", "Incubator");
                    intent.putExtra("model", ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString());



                    createPDF(intent);
                }
            }

            private boolean checkStatus() {
                return isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()) &&
                        isTempValid(findViewById(R.id.temp), MIN_TEMP, MAX_TEMP) &&
                        isTimeValid(findViewById(R.id.time), EXPECTED_TIME) &&
                        isValidString(((EditText) findViewById(R.id.formTechName)).getText().toString()) &&
                        ((Switch) findViewById(R.id.incRubberSwitch)).isChecked() &&
                        ((Switch) findViewById(R.id.incFanSwitch)).isChecked();

            }
        });
    }


    @Override
    public void restart() {
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");
        ((RadioGroup) findViewById(R.id.rgModelSelect)).check(R.id.si);
        ((EditText) findViewById(R.id.temp)).setText(String.valueOf(""));
        ((EditText) findViewById(R.id.time)).setText(String.valueOf(EXPECTED_TIME));
    }

    private void init() {
        setListener(findViewById(R.id.formMainLocation));
        setListener(findViewById(R.id.formRoomLocation));
        setListener(findViewById(R.id.etDeviceSerial));
        setListener(findViewById(R.id.formTechName));
        setTempListener(findViewById(R.id.temp), MIN_TEMP, MAX_TEMP);
        setTimeListener(findViewById(R.id.time), EXPECTED_TIME);

        ((RadioGroup) findViewById(R.id.rgModelSelect)).check(R.id.si);
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");
        ((EditText) findViewById(R.id.temp)).setText(String.valueOf(""));
        ((EditText) findViewById(R.id.time)).setText(String.valueOf(EXPECTED_TIME));

        ((Switch) findViewById(R.id.incRubberSwitch)).setChecked(true);
        ((Switch) findViewById(R.id.incFanSwitch)).setChecked(true);

        findViewById(R.id.formSubmitButton).setActivated(false);
    }

}
