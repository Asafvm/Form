package il.co.diamed.com.form;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

import il.co.diamed.com.form.res.Tuple;

public class DiacentActivity extends AppCompatActivity {
    private View lowLayout;
    /* Diacent 12 */
    private EditText t41;// = findViewById(R.id.centSpeed1000);
    private EditText t51;// = findViewById(R.id.centTime1);
    private EditText t42;// = findViewById(R.id.centSpeed2000);
    private EditText t52;// = findViewById(R.id.centTime2);
    private EditText t43;// = findViewById(R.id.centSpeed3000);
    private EditText t53;// = findViewById(R.id.centTime3);
    private Switch check12Holders;// = findViewById(R.id.cent12checkHolders);

    /* Diacent CW */
    private EditText t4;// = findViewById(R.id.centcwSpeed2500);
    private EditText t5;// = findViewById(R.id.centCWtime);
    private Switch checkcwHolders;// = findViewById(R.id.centCheckHolders);
    private Switch checkRemaining;// = findViewById(R.id.centCheckRemaining);
    private Switch checkFilling;// = findViewById(R.id.centCheckFilling);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diacent);
        lowLayout = findViewById(R.id.lowLayout);
        ViewGroup parent = (ViewGroup) lowLayout.getParent();
        int index = parent.indexOfChild(lowLayout);
        parent.removeView(lowLayout);
        lowLayout = getLayoutInflater().inflate(R.layout.diacent12_layout, parent, false);
        parent.addView(lowLayout, index);

        //lowLayout.inflate();


        //Get preferrences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String speedometer = sharedPref.getString("speedometer", "");
        final String timer = sharedPref.getString("timer", "");
        final String techname = sharedPref.getString("techName", "");
        final String signature = sharedPref.getString("signature", "");

        //get views
        final Button btn = findViewById(R.id.formSubmitButton);

        final EditText t11 = findViewById(R.id.formMainLocation);
        final EditText t12 = findViewById(R.id.formRoomLocation);
        final RadioGroup t2 = findViewById(R.id.rgModelSelect);
        final EditText t3 = findViewById(R.id.etDeviceSerial);

        final EditText t6 = findViewById(R.id.formTechName);
        final DatePicker dp = findViewById(R.id.formDate);


        //default basic values
        initDiacent12();
        t2.check(R.id.dia12);
        t6.setText(techname);


        setListener(t11);
        setListener(t12);
        setListener(t2);
        setListener(t3);
        setListener(t6);


        btn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {


                if (checkStatus()) {
                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    ArrayList<Tuple> corCheck;
                    ArrayList<Tuple> corText;
                    ArrayList<String> arrText;

                    if (t2.getCheckedRadioButtonId() == R.id.dia12) {
                        corCheck = getDiacent12Checkmarks();
                        corText = getDiacent12TextCor();
                        arrText = getDiacent12TextString();
                        intent.putExtra("report", "2018_diacent_yearly.pdf");


                    } else {
                        corCheck = getDiacentCWCheckmarks();
                        corText = getDiacentCWTextCor();
                        arrText = getDiacentCWTextString();
                        intent.putExtra("report", "2018_diacw_yearly.pdf");
                    }


                    ArrayList<String> destArray = new ArrayList<>();
                    destArray.add(t11.getText().toString() + "_" + t12.getText().toString());
                    destArray.add(String.valueOf(dp.getYear()));
                    destArray.add(String.valueOf(dp.getMonth()));
                    destArray.add(String.valueOf(dp.getDayOfMonth()));
                    destArray.add(t3.getText().toString());


                    ;
                    intent.putExtra("checkmarks", corCheck);
                    intent.putExtra("arrText", arrText);
                    intent.putExtra("corText", corText);
                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", destArray);
                    startActivity(intent);
                } else {
                    Log.e("Diacent: ", "checkStatus Failed");
                }


            }

            private ArrayList<String> getDiacentCWTextString() {
                ArrayList<String> arrText = new ArrayList<>();

                arrText.add(t11.getText().toString() + " - " + t12.getText().toString());                                       //Location
                arrText.add(t6.getText().toString());                        //Tech Name
                arrText.add(dp.getDayOfMonth() + "      " + dp.getMonth() + "      " + dp.getYear());                      //Date
                arrText.add(((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString());     //type
                arrText.add(t3.getText().toString());                        //Serial
                arrText.add(t4.getText().toString());                        //cent
                arrText.add(t5.getText().toString());                        //Time
                arrText.add(dp.getMonth() + "   " + (dp.getYear() + 1));           //Next Date

                arrText.add(speedometer);                       //speedometer
                arrText.add(timer);                             //Timer

                return arrText;
            }

            private ArrayList<Tuple> getDiacentCWTextCor() {
                ArrayList<Tuple> corText = new ArrayList<>();

                corText.add(new Tuple(300, 650));                        //Location
                corText.add(new Tuple(330, 30));                        //Tech Name
                corText.add(new Tuple(74, 650));                        //Date
                corText.add(new Tuple(100, 575));                        //type
                corText.add(new Tuple(390, 575));                        //Serial
                corText.add(new Tuple(315, 475));                        //cent2500
                corText.add(new Tuple(315, 343));                        //Time
                corText.add(new Tuple(450, 77));                        //Next Date

                corText.add(new Tuple(370, 435));                        //speedometer
                corText.add(new Tuple(400, 302));                        //Timer
                //corText.add(new Tuple(380,30));                        //Signature

                return corText;
            }

            private ArrayList<Tuple> getDiacentCWCheckmarks() {
                ArrayList<Tuple> corCheck = new ArrayList<>();
                //       corCheck.add(new Tuple(116,467));           //speed not ok
                corCheck.add(new Tuple(190, 480));           //speed ok
                //       corCheck.add(new Tuple(114,327));           //time not ok
                corCheck.add(new Tuple(190, 345));           //time ok
                //       corCheck.add(new Tuple(155,228));           //fan not ok
                corCheck.add(new Tuple(220, 250));           //fan ok
                corCheck.add(new Tuple(220, 235));           //fan ok
                corCheck.add(new Tuple(220, 210));           //fan ok
                //       corCheck.add(new Tuple(232,95));           //overall not ok
                corCheck.add(new Tuple(484, 108));           //overall ok
                return corCheck;
            }

            private ArrayList<String> getDiacent12TextString() {
                ArrayList<String> arrText = new ArrayList<>();

                arrText.add(t11.getText().toString() + " - " + t12.getText().toString());                                       //Location
                arrText.add(t6.getText().toString());                        //Tech Name
                arrText.add(dp.getDayOfMonth() + "     " + dp.getMonth() + "     " + dp.getYear());                      //Date
                //arrText.add(((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString());     //type
                arrText.add(t3.getText().toString());                        //Serial
                arrText.add(t41.getText().toString());                        //cent
                arrText.add(t42.getText().toString());                        //cent
                arrText.add(t43.getText().toString());                        //cent
                arrText.add(t51.getText().toString());                        //Time
                arrText.add(t52.getText().toString());                        //Time
                arrText.add(t53.getText().toString());                        //Time
                arrText.add(dp.getMonth() + "    " + (dp.getYear() + 1));           //Next Date
                arrText.add(speedometer);                       //speedometer
                arrText.add(timer);                             //Timer

                return arrText;
            }

            private ArrayList<Tuple> getDiacent12TextCor() {
                ArrayList<Tuple> corText = new ArrayList<>();

                corText.add(new Tuple(300, 635));                        //Location
                corText.add(new Tuple(330, 27));                        //Tech Name
                corText.add(new Tuple(72, 635));                        //Date
                //corText.add(new Tuple(200, 548));                        //type
                corText.add(new Tuple(225, 570));                        //Serial
                corText.add(new Tuple(315, 502));                        //cent1000
                corText.add(new Tuple(315, 478));                        //cent2000
                corText.add(new Tuple(315, 448));                        //cent3000
                corText.add(new Tuple(305, 326));                        //Time1
                corText.add(new Tuple(305, 300));                        //Time2
                corText.add(new Tuple(305, 275));                        //Time3
                corText.add(new Tuple(446, 62));                        //Next Date

                corText.add(new Tuple(405, 405));                        //speedometer
                corText.add(new Tuple(413, 230));                        //Timer
                //corText.add(new Tuple(380,30));                        //Signature

                return corText;

            }

            private ArrayList<Tuple> getDiacent12Checkmarks() {
                ArrayList<Tuple> corCheck = new ArrayList<>();
                //       corCheck.add(new Tuple(116,467));           //speed not ok
                corCheck.add(new Tuple(204, 452));           //speed1 ok
                corCheck.add(new Tuple(204, 480));           //speed2 ok
                corCheck.add(new Tuple(204, 512));           //speed3 ok
                //       corCheck.add(new Tuple(114,327));           //time not ok
                corCheck.add(new Tuple(192, 333));           //time ok
                corCheck.add(new Tuple(192, 309));           //time ok
                corCheck.add(new Tuple(192, 282));           //time ok
                //       corCheck.add(new Tuple(155,228));           //fan not ok
                corCheck.add(new Tuple(241, 180));           //fan ok
                //       corCheck.add(new Tuple(232,95));           //overall not ok
                corCheck.add(new Tuple(480, 95));           //overall ok
                return corCheck;
            }

            private boolean checkStatus() {
                if (!isValidString(t11.getText().toString()))
                    return false;
                if (!isValidString(t12.getText().toString()))
                    return false;
                if (!isValidString(t3.getText().toString()))
                    return false;
                if (t2.getCheckedRadioButtonId() == R.id.dia12) {

                    if (!isValidString(t41.getText().toString()))
                        return false;
                    if (!isValidString(t51.getText().toString()))
                        return false;
                    if (!isValidString(t42.getText().toString()))
                        return false;
                    if (!isValidString(t52.getText().toString()))
                        return false;
                    if (!isValidString(t43.getText().toString()))
                        return false;
                    if (!isValidString(t53.getText().toString()))
                        return false;
                    if (!check12Holders.isChecked())
                        return false;
                } else {
                    if (!isValidString(t4.getText().toString()))
                        return false;
                    if (!isValidString(t5.getText().toString()))
                        return false;
                    if (!checkFilling.isChecked())
                        return false;
                    if (!checkRemaining.isChecked())
                        return false;
                    if (!checkcwHolders.isChecked())
                        return false;
                }
                if (!isValidString(t6.getText().toString()))
                    return false;

                return true;

            }
        });
    }

    private void initDiacentCW() {

        /* Diacent CW */
        t4 = findViewById(R.id.centcwSpeed2500);
        t5 = findViewById(R.id.centCWtime);
        checkcwHolders = findViewById(R.id.centCheckHolders);
        checkRemaining = findViewById(R.id.centCheckRemaining);
        checkFilling = findViewById(R.id.centCheckFilling);

        t5.setText("60");
        checkFilling.setChecked(true);
        checkcwHolders.setChecked(true);
        checkRemaining.setChecked(true);

        setListener(t4);
        setListener(t5);
        setListener(checkFilling);
        setListener(checkRemaining);
        setListener(checkcwHolders);
    }

    private void initDiacent12() {
        /* Diacent 12 */
        t41 = findViewById(R.id.centSpeed1000);
        t51 = findViewById(R.id.centTime1);
        t42 = findViewById(R.id.centSpeed2000);
        t52 = findViewById(R.id.centTime2);
        t43 = findViewById(R.id.centSpeed3000);
        t53 = findViewById(R.id.centTime3);
        check12Holders = findViewById(R.id.cent12checkHolders);

        t51.setText("15");
        t52.setText("20");
        t53.setText("30");
        check12Holders.setChecked(true);

        setListener(t41);
        setListener(t42);
        setListener(t43);
        setListener(t51);
        setListener(t52);
        setListener(t53);

    }

    private boolean isSpeedValid(EditText exSpeed, EditText meSpeed) {
        try {
            int mSpeed = Integer.valueOf(meSpeed.getText().toString()); //mesured
            int eSpeed = Integer.valueOf(exSpeed.getText().toString()); //expected

            if (mSpeed <= eSpeed + 10 && mSpeed >= eSpeed - 10) {
                meSpeed.setBackgroundColor(Color.TRANSPARENT);
                return true;
            } else {
                meSpeed.setBackgroundColor(Color.RED);
                return false;
            }
        } catch (Exception e) {
            meSpeed.setBackgroundColor(Color.RED);
            return false;
        }

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
            return true;

        } catch (Exception e) {
            return false;
        }

    }


    /*** Swtich 12 and CW views ***/
    private void setListener(RadioGroup rg) {
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                View C = findViewById(R.id.lowLayout);
                ViewGroup parent = (ViewGroup) C.getParent();
                int index = parent.indexOfChild(C);
                parent.removeView(C);

                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.dia12:
                        C = getLayoutInflater().inflate(R.layout.diacent12_layout, parent, false);
                        //C.setLayoutResource(R.layout.diacent12_layout);
                        parent.addView(C, index);
                        initDiacent12();
                        break;
                    case R.id.diaCW:
                        C = getLayoutInflater().inflate(R.layout.diacentcw_layout, parent, false);
                        //C.setLayoutResource(R.layout.diacentcw_layout);
                        parent.addView(C, index);
                        initDiacentCW();
                        break;
                    default:
                        break;
                }

            }
        });
    }

    void setListener(final EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et.getId() == R.id.centCWtime || et.getId() == R.id.centTime1 || et.getId() == R.id.centTime2 || et.getId() == R.id.centTime3) {
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
                if (et.getId() == R.id.centTime) {
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
                if (et.getId() == R.id.centTime) {
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

        return (!(s == null || s.equals("")));
    }

}
