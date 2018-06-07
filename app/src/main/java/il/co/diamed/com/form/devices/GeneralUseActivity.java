package il.co.diamed.com.form.devices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Objects;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.res.DevicePrototypeActivity;
import il.co.diamed.com.form.devices.res.Tuple;

import static il.co.diamed.com.form.devices.Helper.isValidString;

public class GeneralUseActivity extends DevicePrototypeActivity {
    private Helper h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_device_activity);
        h = new Helper();
        h.setLayout(this, R.layout.device_general_layout);
        h.setListener((EditText) findViewById(R.id.formTechName));

        Bundle bundle = Objects.requireNonNull(getIntent().getExtras());
        Bundle cal_data = bundle.getBundle("cal");
        final String techname = Objects.requireNonNull(cal_data).getString("techName");
        final String signature = cal_data.getString("signature");
        //get views
        init();
        ((EditText) findViewById(R.id.formTechName)).setText(techname);
        ((RadioGroup) findViewById(R.id.rgModelSelect)).check(bundle.getInt("type"));

        //default basic values
        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    findViewById(R.id.pbPDF).setVisibility(View.VISIBLE);

                    DatePicker dp = findViewById(R.id.formDate);
                    String day = h.fixDay(dp.getDayOfMonth());
                    String month = h.fixMonth(dp.getMonth());
                    ArrayList<Tuple> corText = new ArrayList<>();
                    corText.add(new Tuple(355, 543, "", false));           //temp ok
                    corText.add(new Tuple(355, 525, "", false));           //time ok
                    corText.add(new Tuple(355, 488, "", false));           //fan ok
                    corText.add(new Tuple(355, 470, "", false));           //rubber ok
                    corText.add(new Tuple(355, 452, "", false));           //overall ok

                    corText.add(new Tuple(100, 663, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " + ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));             //Location
                    corText.add(new Tuple(100, 140, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));            //Tech Name
                    corText.add(new Tuple(313, 663, day + "  " +
                            month + "    " +
                            dp.getYear(), false));                        //Date
                    corText.add(new Tuple(455, 633, ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString(), false));                        //type
                    corText.add(new Tuple(125, 633, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                    corText.add(new Tuple(450, 487, ((EditText) findViewById(R.id.etVer)).getText().toString(), false));                        //ver
                    corText.add(new Tuple(514, 662, month + "  " +
                            (dp.getYear() + 1), false));                        //Next Date
                    if (((Switch) findViewById(R.id.verUpdateSwitch)).isChecked()) {
                        corText.add(new Tuple(292, 470, ((EditText) findViewById(R.id.etNewVer)).getText().toString(), false));
                    }
                    corText.add(new Tuple(435, 139, "!", false));                        //Signature

                    Intent intent = new Intent();
                    intent.putExtra("report", "2018_general_yearly.pdf");

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1", corText);
                    intent.putExtra("pages", pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "/" +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            dp.getYear() + "" +
                            month + "" + day + "_" +
                            ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString() + "_" +
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
                if (!isValidString(((EditText) findViewById(R.id.etVer)).getText().toString()))
                    return false;
                if (((Switch) findViewById(R.id.verUpdateSwitch)).isChecked()) {
                    if (!isValidString(((EditText) findViewById(R.id.etNewVer)).getText().toString()))
                        return false;
                }
                return isValidString(((EditText) findViewById(R.id.formTechName)).getText().toString()) &&
                        ((Switch) findViewById(R.id.generalCleaningSwitch)).isChecked() &&
                        ((Switch) findViewById(R.id.selfTextSwitch)).isChecked();
            }
        });
    }

    private void init() {
        Helper h = new Helper();
        h.setListener(((EditText) findViewById(R.id.formMainLocation)));
        h.setListener(((EditText) findViewById(R.id.formRoomLocation)));
        h.setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        h.setListener(((EditText) findViewById(R.id.etVer)));
        h.setListener(((EditText) findViewById(R.id.etNewVer)));
        setListener(((Switch) findViewById(R.id.verUpdateSwitch)));

        ((Switch) findViewById(R.id.verUpdateSwitch)).setChecked(false);
    }


    private void setListener(Switch s) {
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.getId() == R.id.verUpdateSwitch) {
                    if (compoundButton.isChecked()) {
                        findViewById(R.id.etNewVer).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.etNewVer).setVisibility(View.INVISIBLE);
                    }

                }
            }
        });
    }

}

