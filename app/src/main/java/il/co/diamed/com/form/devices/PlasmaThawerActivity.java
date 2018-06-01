package il.co.diamed.com.form.devices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Objects;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.res.Tuple;

import static il.co.diamed.com.form.devices.Helper.isValidString;

public class PlasmaThawerActivity extends DevicePrototypeActivity {
    private final double EXPECTED_TEMP = 36.4;
    private final int EXPECTED_TIME = 10;
    private Helper h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_device_activity);
        h = new Helper();
        h.setLayout(this, R.layout.device_plasma_layout);


        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        final String thermometer = bundle.getString("thermometer");
        final String timer = bundle.getString("timer");


        //get views

        //default basic values
        init();
        h.setListener(((EditText) findViewById(R.id.formTechName)));
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

                    ArrayList<Tuple> corText = new ArrayList<>();
                    corText.add(new Tuple(218, 453, "", false));           //temp ok
                    corText.add(new Tuple(223, 312, "", false));           //time ok
                    if (!((Switch) findViewById(R.id.ptWaterCheck)).isChecked())
                        corText.add(new Tuple(154, 216, "", false));           //water not ok
                    else
                        corText.add(new Tuple(276, 216, "", false));           //water ok
                    corText.add(new Tuple(276, 198, "", false));           //oil ok
                    corText.add(new Tuple(276, 180, "", false));           //alarm ok
                    corText.add(new Tuple(475, 88, "", false));           //overall ok

                    corText.add(new Tuple(310, 623, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                    corText.add(new Tuple(310, 30, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                    corText.add(new Tuple(75, 623, day + "     " +
                            month + "      " +
                            dp.getYear(), false));                        //Date
                    corText.add(new Tuple(200, 552, ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString(), false));                        //type
                    corText.add(new Tuple(425, 552, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                    corText.add(new Tuple(280, 449, ((EditText) findViewById(R.id.ptTemp)).getText().toString(), false));                        //temp
                    //corText.add(new Tuple(425, 455));                        //temp expected
                    corText.add(new Tuple(305, 309, ((EditText) findViewById(R.id.ptTime)).getText().toString(), false));                        //Time
                    corText.add(new Tuple(430, 57, month + "   " +
                            (dp.getYear() + 1), false));                        //Next Date

                    corText.add(new Tuple(350, 405, thermometer, false));                        //thermometer
                    corText.add(new Tuple(400, 269, timer, false));                        //Timer
                    corText.add(new Tuple(105, 30, "!", false));                        //Signature


                    Intent intent = new Intent();
                    intent.putExtra("report", "2018_plasma_biyearly.pdf");

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1", corText);
                    intent.putExtra("pages", pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "/" +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            dp.getYear() + "" +
                            month+ "" + day + "_PlasmaThawer-" +
                            ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString() + "_" + ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");

                    createPDF(intent);
                }


            }

            private boolean checkStatus() {
                return isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.ptTime)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.formTechName)).getText().toString()) &&
                        ((Switch) findViewById(R.id.ptAlarmCheck)).isChecked() &&
                        ((Switch) findViewById(R.id.ptOilCheck)).isChecked() &&
                        Helper.isTempValid(((EditText) findViewById(R.id.ptTemp)), EXPECTED_TEMP - 1, EXPECTED_TEMP + 1);

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
        Helper h = new Helper();
        h.setListener(((EditText) findViewById(R.id.formMainLocation)));
        h.setListener(((EditText) findViewById(R.id.formRoomLocation)));
        h.setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        h.setListener(((EditText) findViewById(R.id.formTechName)));
        h.setTempListener(((EditText) findViewById(R.id.ptTemp)), EXPECTED_TEMP - 1, EXPECTED_TEMP + 1);
        h.setTimeListener(((EditText) findViewById(R.id.ptTime)), EXPECTED_TIME);

        ((RadioGroup) findViewById(R.id.rgModelSelect)).check(R.id.dh8);
        ((EditText) findViewById(R.id.formMainLocation)).setText("");
        ((EditText) findViewById(R.id.formRoomLocation)).setText("");
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");
        ((EditText) findViewById(R.id.ptExpectedTemp)).setText(String.valueOf(EXPECTED_TEMP));
        ((EditText) findViewById(R.id.ptTemp)).setText(String.valueOf(""));
        ((EditText) findViewById(R.id.ptTime)).setText(R.string.time10);

        ((Switch) findViewById(R.id.ptAlarmCheck)).setChecked(true);
        ((Switch) findViewById(R.id.ptOilCheck)).setChecked(true);
        ((Switch) findViewById(R.id.ptWaterCheck)).setChecked(true);
    }
}


