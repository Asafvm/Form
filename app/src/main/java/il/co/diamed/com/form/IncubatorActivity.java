package il.co.diamed.com.form;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.util.ArrayList;

import il.co.diamed.com.form.res.Tuple;

public class IncubatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incubator);

        //Get preferrences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String thermometer = sharedPref.getString("thermometer", "");
        final String timer = sharedPref.getString("timer", "");
        final String techname = sharedPref.getString("techName", "");
        final String signature = sharedPref.getString("signature", "");

        //get views
        final Button btn = findViewById(R.id.formSubmitButton);

        final EditText t11 = findViewById(R.id.formMainLocation);
        final EditText t12 = findViewById(R.id.formRoomLocation);
        final RadioGroup t2 = findViewById(R.id.rgModelSelect);
        final EditText t3 = findViewById(R.id.etDeviceSerial);
        final EditText t4 = findViewById(R.id.temp);
        final EditText t5 = findViewById(R.id.time);
        final EditText t6 = findViewById(R.id.formTechName);
        final DatePicker dp = findViewById(R.id.formDate);

        final Switch fanSwitch = findViewById(R.id.incFanSwitch);
        final Switch rubberSwitch = findViewById(R.id.incRubberSwitch);

        //default basic values
        t2.check(R.id.si);
        t5.setText("15");
        fanSwitch.setChecked(true);
        rubberSwitch.setChecked(true);
        t6.setText(techname);


        setListener(t11);
        setListener(t12);
        setListener(t3);
        setListener(t4);
        setListener(t5);
        setListener(t6);
        setListener(t2);
        setListener(rubberSwitch);
        setListener(fanSwitch);

        btn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {


                if (checkStatus()) {


                    ArrayList<Tuple> corCheck = new ArrayList<>();
                    //       corCheck.add(new Tuple(116,467));           //temp not ok
                    corCheck.add(new Tuple(205, 467));           //temp ok
                    //       corCheck.add(new Tuple(114,327));           //time not ok
                    corCheck.add(new Tuple(203, 327));           //time ok
                    //       corCheck.add(new Tuple(155,228));           //fan not ok
                    corCheck.add(new Tuple(250, 228));           //fan ok
                    //       corCheck.add(new Tuple(155,210));           //rubber not ok
                    corCheck.add(new Tuple(250, 210));           //rubber ok
                    //       corCheck.add(new Tuple(232,95));           //overall not ok
                    corCheck.add(new Tuple(480, 95));           //overall ok

                    ArrayList<Tuple> corText = new ArrayList<>();

                    corText.add(new Tuple(300, 635));                        //Location
                    corText.add(new Tuple(330, 30));                        //Tech Name
                    corText.add(new Tuple(72, 636));                        //Date
                    corText.add(new Tuple(290, 568));                        //type
                    corText.add(new Tuple(425, 568));                        //Serial
                    corText.add(new Tuple(275, 465));                        //temp
                    corText.add(new Tuple(305, 325));                        //Time
                    corText.add(new Tuple(451, 65));                        //Next Date


                    corText.add(new Tuple(330, 425));                        //Thermometer
                    corText.add(new Tuple(400, 285));                        //Timer
                    //corText.add(new Tuple(380,30));                        //Signature

                    ArrayList<String> arrText = new ArrayList<>();


                    arrText.add(t11.getText().toString() + " - " + t12.getText().toString());                                       //Location
                    arrText.add(t6.getText().toString());                        //Tech Name
                    arrText.add(dp.getDayOfMonth() + "       " + dp.getMonth() + "         " + dp.getYear());                      //Date
                    arrText.add(((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString());     //type
                    arrText.add(t3.getText().toString());                        //Serial
                    arrText.add(t4.getText().toString());                        //temp
                    arrText.add(t5.getText().toString());                        //Time
                    arrText.add(dp.getMonth() + "   " + (dp.getYear() + 1));           //Next Date


                    arrText.add(thermometer);                       //Thermometer
                    arrText.add(timer);                             //Timer


                    ArrayList<String> destArray = new ArrayList<>();
                    destArray.add(t11.getText().toString() + "_" + t12.getText().toString());
                    destArray.add(String.valueOf(dp.getYear()));
                    destArray.add(String.valueOf(dp.getMonth()));
                    destArray.add(String.valueOf(dp.getDayOfMonth()));
                    destArray.add(t3.getText().toString());


                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_id37_yearly.pdf");
                    intent.putExtra("checkmarks", corCheck);
                    intent.putExtra("arrText", arrText);
                    intent.putExtra("corText", corText);
                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", destArray);
                    startActivity(intent);
                }
                ;


            }

            private boolean checkStatus() {
                if (!isValidString(t11.getText().toString()))
                    return false;
                if (!isValidString(t12.getText().toString()))
                    return false;
                if (!isValidString(t3.getText().toString()))
                    return false;
                if (!isValidString(t4.getText().toString()))
                    return false;
                if (!isValidString(t5.getText().toString()))
                    return false;
                if (!isValidString(t6.getText().toString()))
                    return false;
                if (!fanSwitch.isChecked())
                    return false;
                if (!rubberSwitch.isChecked())
                    return false;
                return true;

            }
        });
    }


    private void setListener(Switch s) {
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
    }


    private boolean isTimeValid(String s) {

        try {
            float time = Float.parseFloat(s);
            if (time == 15)
                return true;
            else
                return false;

        } catch (Exception e) {
            return false;
        }

    }


    private boolean isTempValid(String s) {

        try {
            float temp = Float.parseFloat(s);
            if (temp >= 35 && temp <= 39)
                return true;
            else
                return false;

        } catch (Exception e) {
            return false;
        }
    }


    private void setListener(RadioGroup rg) {
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

            }
        });
    }

    void setListener(final EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et.getId() == R.id.temp) {
                    if (isTempValid(et.getText().toString())) {
                        et.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        et.setBackgroundColor(Color.RED);
                    }
                } else if (et.getId() == R.id.time) {
                    if (isTimeValid(et.getText().toString())) {
                        et.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        et.setBackgroundColor(Color.RED);
                    }
                } else {
                    if (isValidString(et.getText().toString())) {
                        et.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        et.setBackgroundColor(Color.RED);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et.getId() == R.id.temp) {
                    if (isTempValid(et.getText().toString())) {
                        et.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        et.setBackgroundColor(Color.RED);
                    }
                } else if (et.getId() == R.id.time) {
                    if (isTimeValid(et.getText().toString())) {
                        et.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        et.setBackgroundColor(Color.RED);
                    }
                } else {
                    if (isValidString(et.getText().toString())) {
                        et.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        et.setBackgroundColor(Color.RED);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et.getId() == R.id.temp) {
                    if (isTempValid(et.getText().toString())) {
                        et.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        et.setBackgroundColor(Color.RED);
                    }
                } else if (et.getId() == R.id.time) {
                    if (isTimeValid(et.getText().toString())) {
                        et.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        et.setBackgroundColor(Color.RED);
                    }
                } else {
                    if (isValidString(et.getText().toString())) {
                        et.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        et.setBackgroundColor(Color.RED);
                    }
                }
            }
        });
    }

    private boolean isValidString(String s) {
        if (s.equals("") || s == null)
            return false;
        else
            return true;
    }

}
