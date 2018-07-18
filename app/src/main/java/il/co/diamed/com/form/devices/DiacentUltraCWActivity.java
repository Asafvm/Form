package il.co.diamed.com.form.devices;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Objects;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.res.DevicePrototypeActivity;
import il.co.diamed.com.form.devices.res.Tuple;

public class DiacentUltraCWActivity extends DevicePrototypeActivity {

    private static final int EXPECTED_CW_SPEED = 2500;
    private static final int EXPECTED_CW_TIME = 60;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_device_activity);
        setLayout(R.layout.device_diacentcw_layout);


        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techName = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        final String speedometer = bundle.getString("speedometer");
        final String timer = bundle.getString("timer");

        initDiacentCW();
        ((EditText) findViewById(R.id.formTechName)).setText(techName);


        (findViewById(R.id.formSubmitButton)).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    final DatePicker dp = findViewById(R.id.formDate);
                    final String day = fixDay(dp.getDayOfMonth());
                    final String month = fixMonth(dp.getMonth());

                    findViewById(R.id.pbPDF).setVisibility(View.VISIBLE);
                    Intent intent = new Intent();
                    ArrayList<Tuple> corText;


                    corText = getDiacentCWTextCor();
                    intent.putExtra("report", "2018_ultracw_yearly.pdf");

                    Bundle pages = new Bundle();

                    pages.putParcelableArrayList("page1", corText);
                    intent.putExtra("pages", pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "/" +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            dp.getYear() + "" +
                            month + "" + day + "_UltraCW_" +
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");

                    createPDF(intent);
                } else {
                    Log.e("Diacent: ", "checkStatus Failed");
                }
            }

            private ArrayList<Tuple> getDiacentCWTextCor() {
                final DatePicker dp = findViewById(R.id.formDate);
                final String day = fixDay(dp.getDayOfMonth());
                final String month = fixMonth(dp.getMonth());

                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(190, 481, "", false));           //speed ok
                corText.add(new Tuple(190, 345, "", false));           //time ok
                corText.add(new Tuple(226, 250, "", false));           //fan ok
                corText.add(new Tuple(226, 233, "", false));           //fan ok
                corText.add(new Tuple(226, 214, "", false));           //fan ok
                corText.add(new Tuple(484, 108, "", false));           //overall ok

                corText.add(new Tuple(303, 650, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                        ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                corText.add(new Tuple(330, 30, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                corText.add(new Tuple(76, 650, day + "     " + month + "     " + dp.getYear(), false));                        //Date

                corText.add(new Tuple(380, 575, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                corText.add(new Tuple(300, 477, ((EditText) findViewById(R.id.centcwSpeed2500)).getText().toString(), false));                        //cent2500
                corText.add(new Tuple(315, 343, ((EditText) findViewById(R.id.centCWtime)).getText().toString(), false));                        //Time
                corText.add(new Tuple(445, 77, month + "    " +
                        (dp.getYear() + 1), false));                        //Next Date
                corText.add(new Tuple(378, 436, speedometer, false));                        //speedometer
                corText.add(new Tuple(400, 302, timer, false));                        //Timer
                corText.add(new Tuple(135, 33, "!", false));                        //Signature

                return corText;
            }


            private boolean checkStatus() {
                return isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()) &&
                        isSpeedValid(Integer.valueOf((((EditText) findViewById(R.id.centcwSpeed2500)).getText().toString())), EXPECTED_CW_SPEED) &&
                        isTimeValid(findViewById(R.id.centCWtime), EXPECTED_CW_TIME) &&
                        ((Switch) findViewById(R.id.centCheckHolders)).isChecked() &&
                        ((Switch) findViewById(R.id.centCheckRemaining)).isChecked() &&
                        ((Switch) findViewById(R.id.centCheckFilling)).isChecked() &&
                        isValidString(((EditText) findViewById(R.id.formTechName)).getText().toString());

            }
        });

    }



    private void initDiacentCW() {

        /* Diacent CW */
        setListener(findViewById(R.id.formMainLocation));
        setListener(findViewById(R.id.formRoomLocation));
        setListener(findViewById(R.id.etDeviceSerial));
        setListener(findViewById(R.id.formTechName));
        ((EditText) findViewById(R.id.formMainLocation)).setText("");
        ((EditText) findViewById(R.id.formRoomLocation)).setText("");
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");


        ((EditText) findViewById(R.id.centcwSpeed2500)).setText("");
        ((EditText) findViewById(R.id.centCWtime)).setText(R.string.time60);
        ((Switch) findViewById(R.id.centCheckHolders)).setChecked(true);
        ((Switch) findViewById(R.id.centCheckRemaining)).setChecked(true);
        ((Switch) findViewById(R.id.centCheckFilling)).setChecked(true);

    }


}
