package il.co.diamed.com.form.calibration;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Objects;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.calibration.res.DevicePrototypeActivity;
import il.co.diamed.com.form.calibration.res.Tuple;


public class CentrifugeActivity extends DevicePrototypeActivity {
    private final int EXPECTED_TIME = 10;
    private int EXPECTED_SPEED = 1030;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout(R.layout.device_centrifuge_layout);

        //default basic values
        init();
        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        final String speedometer = bundle.getString("speedometer");
        final String timer = bundle.getString("timer");

        ((EditText) findViewById(R.id.formTechName)).setText(techname);
        ((EditText) findViewById(R.id.centExpectedSpeed)).setText(String.valueOf(EXPECTED_SPEED));

        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    DatePicker dp = findViewById(R.id.formDate);
                    String day = fixDay(dp.getDayOfMonth());
                    String month = fixMonth(dp.getMonth());
                    ArrayList<Tuple> corText = new ArrayList<>();
                    corText.add(new Tuple(219, 448, "", false));           //speed ok
                    corText.add(new Tuple(214, 313, "", false));           //time ok
                    corText.add(new Tuple(245, 208, "", false));           //fan ok
                    corText.add(new Tuple(479, 102, "", false));           //overall ok
                    corText.add(new Tuple(305, 618, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                    corText.add(new Tuple(330, 37, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                    corText.add(new Tuple(92, 618, day + "     " + month + "      " +
                            dp.getYear(), false));                        //Date
                    corText.add(new Tuple(200, 548, ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString(), false));                        //type
                    corText.add(new Tuple(425, 548, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                    corText.add(new Tuple(315, 445, ((EditText) findViewById(R.id.centSpeed)).getText().toString(), false));                        //cent
                    corText.add(new Tuple(445, 445, String.valueOf(EXPECTED_SPEED), false));                        //cent expected
                    corText.add(new Tuple(305, 309, ((EditText) findViewById(R.id.centTime)).getText().toString(), false));                        //Time
                    corText.add(new Tuple(444, 70, month + "    " +
                            (dp.getYear() + 1), false));                        //Next Date


                    corText.add(new Tuple(350, 402, speedometer, false));                        //speedometer
                    corText.add(new Tuple(400, 267, timer, false));                        //Timer
                    corText.add(new Tuple(135, 33, "!", false));                        //Signature


                    Intent intent = new Intent();
                    intent.putExtra("report", "2018_idcent_yearly.pdf");

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1", corText);
                    intent.putExtra("pages", pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "/" +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            dp.getYear() + "" +
                            month + "" + day + "_" + "Centrifuge-" +
                            ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString() + "_" +
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");

                    intent.putExtra("type", "Centrifuge");
                    intent.putExtra("model",((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString());
                    createPDF(intent);
                }
            }


            private boolean checkStatus() {
                if (isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()))
                    if (isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()))
                        if (isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()))
                            if (isSpeedValid(Integer.valueOf(((EditText) findViewById(R.id.centSpeed)).getText().toString()), EXPECTED_SPEED))
                                if (isTimeValid(findViewById(R.id.centTime), EXPECTED_TIME))
                                    if (isValidString(((EditText) findViewById(R.id.formTechName)).getText().toString()))
                                        if (((Switch) findViewById(R.id.centFanSwitch)).isChecked())
                                            return true;
                return false;

            }
        });
    }

    private void setListener(RadioGroup rg) {
        rg.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.c6S:
                    EXPECTED_SPEED = 1175;
                    break;
                case R.id.c12S:
                    EXPECTED_SPEED = 1175;
                    break;
                case R.id.c12SII:
                    EXPECTED_SPEED = 1030;
                    break;
                case R.id.c24S:
                    EXPECTED_SPEED = 910;
                    break;
                case R.id.l:
                    EXPECTED_SPEED = 1030;
                    break;
                default:
                    EXPECTED_SPEED = 0;
                    break;
            }
            //setSpeedListener(findViewById(R.id.centSpeed), EXPECTED_SPEED);
            ((EditText) findViewById(R.id.centExpectedSpeed)).setText(String.valueOf(EXPECTED_SPEED));
        });
    }
    @Override
    public void restart() {
        super.restart();

        ((RadioGroup) findViewById(R.id.rgModelSelect)).check(R.id.c12SII);
        ((EditText) findViewById(R.id.centSpeed)).setText(String.valueOf(EXPECTED_SPEED));
        ((EditText) findViewById(R.id.centTime)).setText(String.valueOf(EXPECTED_TIME));
    }

    private void init() {

        setListener(((EditText) findViewById(R.id.formMainLocation)));
        setListener(((EditText) findViewById(R.id.formRoomLocation)));
        setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        setListener(((EditText) findViewById(R.id.formTechName)));
        setSpeedListener(findViewById(R.id.centSpeed), EXPECTED_SPEED);
        setTimeListener(findViewById(R.id.centTime), EXPECTED_TIME);
        setListener(((RadioGroup) findViewById(R.id.rgModelSelect)));

        ((RadioGroup) findViewById(R.id.rgModelSelect)).check(R.id.c12SII);

        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");
        ((EditText) findViewById(R.id.centSpeed)).setText(String.valueOf(EXPECTED_SPEED));
        ((EditText) findViewById(R.id.centTime)).setText(String.valueOf(EXPECTED_TIME));

        ((EditText) findViewById(R.id.centExpectedSpeed)).addTextChangedListener(speedListener);
        ((Switch) findViewById(R.id.centFanSwitch)).setChecked(true);
    }

    TextWatcher speedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            EXPECTED_SPEED = Integer.valueOf(((EditText) findViewById(R.id.centExpectedSpeed)).getText().toString());
            setSpeedListener(findViewById(R.id.centSpeed), EXPECTED_SPEED);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
