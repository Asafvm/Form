package il.co.diamed.com.form.devices;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import il.co.diamed.com.form.PDFActivity;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.res.Tuple;

import static il.co.diamed.com.form.devices.Helper.isSpeedValid;
import static il.co.diamed.com.form.devices.Helper.isTimeValid;
import static il.co.diamed.com.form.devices.Helper.isValidString;

public class DiacentCWActivity extends DevicePrototypeActivity {
    private static final int EXPECTED_CW_SPEED = 2500;
    private static final int EXPECTED_CW_TIME = 60;
    private Helper h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_device_activity);
        h = new Helper();
        h.setLayout(this, R.layout.device_diacentcw_layout);


        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String signature = bundle.getString("signature");
        final String speedometer = bundle.getString("speedometer");
        final String timer = bundle.getString("timer");


        init();
        ((EditText) findViewById(R.id.formTechName)).setText(Objects.requireNonNull(bundle).getString("techName"));
        final DatePicker dp = findViewById(R.id.formDate);
        final String day = h.fixDay(dp.getDayOfMonth());
        final String month = h.fixMonth(dp.getMonth());
        (findViewById(R.id.formSubmitButton)).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {

                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    ArrayList<Tuple> corText;

                    corText = getDiacentCWTextCor();
                    intent.putExtra("report", "2018_diacw_yearly.pdf");


                    Bundle pages = new Bundle();


                    pages.putParcelableArrayList("page1", corText);
                    intent.putExtra("pages", pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "/" +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            dp.getYear() + "" +
                            month + "" + day + "_DiacentCW_" +
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");
                    createPDF(intent);
                } else {
                    Log.e("Diacent: ", "checkStatus Failed");
                }
            }

            private ArrayList<Tuple> getDiacentCWTextCor() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(204, 505, "", false));           //speed ok
                corText.add(new Tuple(190, 385, "", false));           //time ok
                corText.add(new Tuple(240, 289, "", false));           //fan ok
                corText.add(new Tuple(240, 268, "", false));           //fan ok
                corText.add(new Tuple(240, 247, "", false));           //fan ok
                corText.add(new Tuple(481, 159, "", false));           //overall ok

                corText.add(new Tuple(300, 635, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                        ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                corText.add(new Tuple(330, 95, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                corText.add(new Tuple(74, 635, day + "    " +
                        month + "    " +
                        dp.getYear(), false));                        //Date

                corText.add(new Tuple(226, 572, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                corText.add(new Tuple(310, 505, ((EditText) findViewById(R.id.centcwSpeed2500)).getText().toString(), false));                        //cent2500
                corText.add(new Tuple(315, 383, ((EditText) findViewById(R.id.centCWtime)).getText().toString(), false));                        //Time
                corText.add(new Tuple(446, 128, month + "   " +
                        (dp.getYear() + 1), false));                        //Next Date
                corText.add(new Tuple(410, 465, speedometer, false));                        //speedometer
                corText.add(new Tuple(410, 340, timer, false));                        //Timer
                corText.add(new Tuple(135, 95, "!", false));                        //Signature

                return corText;
            }


            private boolean checkStatus() {
                if (!isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()))
                    return false;
                if (!isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()))
                    return false;
                if (!isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()))
                    return false;
                if (!isSpeedValid(Integer.valueOf((((EditText) findViewById(R.id.centcwSpeed2500)).getText().toString())), EXPECTED_CW_SPEED))
                    return false;
                if (!isTimeValid(((EditText) findViewById(R.id.centCWtime)), EXPECTED_CW_TIME))
                    return false;
                if (!((Switch) findViewById(R.id.centCheckHolders)).isChecked())
                    return false;
                if (!((Switch) findViewById(R.id.centCheckRemaining)).isChecked())
                    return false;
                if (!((Switch) findViewById(R.id.centCheckFilling)).isChecked())
                    return false;
                return isValidString(((EditText) findViewById(R.id.formTechName)).getText().toString());
            }
        });

    }


    private void init() {
        Helper h = new Helper();
        h.setListener(((EditText) findViewById(R.id.formMainLocation)));
        h.setListener(((EditText) findViewById(R.id.formRoomLocation)));
        h.setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        h.setListener(((EditText) findViewById(R.id.formTechName)));
        ((EditText) findViewById(R.id.formMainLocation)).setText("");
        ((EditText) findViewById(R.id.formRoomLocation)).setText("");
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");
        initDiacentCW();
    }

    private void initDiacentCW() {

        /* Diacent CW */
        Helper h = new Helper();
        h.setTimeListener(((EditText) findViewById(R.id.centCWtime)), EXPECTED_CW_TIME);
        h.setSpeedListener(((EditText) findViewById(R.id.centcwSpeed2500)), EXPECTED_CW_SPEED);
        ((EditText) findViewById(R.id.centcwSpeed2500)).setText("");
        ((EditText) findViewById(R.id.centCWtime)).setText(R.string.time60);
        ((Switch) findViewById(R.id.centCheckHolders)).setChecked(true);
        ((Switch) findViewById(R.id.centCheckRemaining)).setChecked(true);
        ((Switch) findViewById(R.id.centCheckFilling)).setChecked(true);


    }

}
