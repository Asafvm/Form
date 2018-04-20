package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import il.co.diamed.com.form.PDFActivity;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.Tuple;

public class CentrifugeActivity extends AppCompatActivity {
    private EditText expectedSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centrifuge);
        setLayout(R.layout.centrifuge_layout);

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
        final EditText t4 = findViewById(R.id.centSpeed);
        final EditText t5 = findViewById(R.id.centTime);
        final EditText t6 = findViewById(R.id.formTechName);
        final DatePicker dp = findViewById(R.id.formDate);

        final Switch fanSwitch = findViewById(R.id.centFanSwitch);


        //default basic values
        expectedSpeed = findViewById(R.id.centExpectedTime);
        expectedSpeed.setText("1030");
        t2.check(R.id.c12SII);
        t5.setText("10");
        fanSwitch.setChecked(true);
        t6.setText(techname);


        setListener(t11);
        setListener(t12);
        setListener(t3);
        setListener(t4);
        setListener(t5);
        setListener(t6);
        setListener(t2);
        setListener(fanSwitch);

        btn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {


                if (checkStatus()) {


                    ArrayList<Tuple> corCheck = new ArrayList<>();
                    //       corCheck.add(new Tuple(116,467));           //speed not ok
                    corCheck.add(new Tuple(217, 447));           //speed ok
                    //       corCheck.add(new Tuple(114,327));           //time not ok
                    corCheck.add(new Tuple(213, 313));           //time ok
                    //       corCheck.add(new Tuple(155,228));           //fan not ok
                    corCheck.add(new Tuple(244, 207));           //fan ok
                    //       corCheck.add(new Tuple(232,95));           //overall not ok
                    corCheck.add(new Tuple(478, 102));           //overall ok

                    ArrayList<Tuple> corText = new ArrayList<>();

                    corText.add(new Tuple(305, 618));                        //Location
                    corText.add(new Tuple(330, 37));                        //Tech Name
                    corText.add(new Tuple(92, 618));                        //Date
                    corText.add(new Tuple(200, 548));                        //type
                    corText.add(new Tuple(425, 548));                        //Serial
                    corText.add(new Tuple(315, 445));                        //cent
                    corText.add(new Tuple(445, 445));                        //cent expected
                    corText.add(new Tuple(305, 309));                        //Time
                    corText.add(new Tuple(450, 70));                        //Next Date


                    corText.add(new Tuple(350, 400));                        //speedometer
                    corText.add(new Tuple(400, 265));                        //Timer
                    //corText.add(new Tuple(380,30));                        //Signature

                    ArrayList<String> arrText = new ArrayList<>();

                    arrText.add(t11.getText().toString() + " - " + t12.getText().toString());                                       //Location
                    arrText.add(t6.getText().toString());                        //Tech Name
                    arrText.add(dp.getDayOfMonth() + "       " + dp.getMonth() + "       " + dp.getYear());                      //Date
                    arrText.add(((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString());     //type
                    arrText.add(t3.getText().toString());                        //Serial
                    arrText.add(t4.getText().toString());                        //cent
                    arrText.add(expectedSpeed.getText().toString());                        //cent expected
                    arrText.add(t5.getText().toString());                        //Time
                    arrText.add(dp.getMonth() + "   " + (dp.getYear() + 1));           //Next Date


                    arrText.add(speedometer);                       //speedometer
                    arrText.add(timer);                             //Timer


                    ArrayList<String> destArray = new ArrayList<>();
                    destArray.add(t11.getText().toString() + "_" + t12.getText().toString());
                    destArray.add(String.valueOf(dp.getYear()));
                    destArray.add(String.valueOf(dp.getMonth()));
                    destArray.add(String.valueOf(dp.getDayOfMonth()));
                    destArray.add(t3.getText().toString());


                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_idcent_yearly.pdf");

                    Bundle pages = new Bundle();
                    Bundle page1 = new Bundle();
                    page1.putParcelableArrayList("checkmarks",corCheck);
                    page1.putStringArrayList("arrText",arrText);
                    page1.putParcelableArrayList("corText",corText);
                    pages.putBundle("page1",page1);
                    intent.putExtra("pages",pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", destArray);
                    startActivityForResult(intent,1);
                }
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
                if (!isSpeedValid(expectedSpeed, t4))
                    return false;
                return true;

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode==RESULT_OK){
                Toast.makeText(this,R.string.pdfSuccess,Toast.LENGTH_SHORT).show();
                doAnother();
                setResult(RESULT_OK);
            }else{
                setResult(RESULT_CANCELED);
            }
        }
    }

    private void doAnother() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.doAnother);
        alertBuilder.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText et = findViewById(R.id.etDeviceSerial);
                et.setText("");
                et = findViewById(R.id.centSpeed);
                et.setText("");
            }
        });
        alertBuilder.setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        alertBuilder.create().show();
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
            if (time == 10)
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
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.c6S:
                        expectedSpeed.setText("1175");
                        break;
                    case R.id.c12S:
                        expectedSpeed.setText("1030");
                        break;
                    case R.id.c12SII:
                        expectedSpeed.setText("1030");
                        break;
                    case R.id.c24S:
                        expectedSpeed.setText("910");
                        break;
                    case R.id.l:
                        expectedSpeed.setText("1030");
                        break;
                    default:
                        expectedSpeed.setText("0");
                        break;
                }

            }
        });
    }

    void setListener(final EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et.getId() == R.id.centSpeed) {
                    if (isSpeedValid(expectedSpeed,et)) {
                        et.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        et.setBackgroundColor(Color.RED);
                    }
                } else if (et.getId() == R.id.centTime) {
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
                if (et.getId() == R.id.centSpeed) {
                    if (isSpeedValid(expectedSpeed,et)) {
                        et.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        et.setBackgroundColor(Color.RED);
                    }
                } else if (et.getId() == R.id.centTime) {
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
                if (et.getId() == R.id.centSpeed) {
                    if (isSpeedValid(expectedSpeed,et)) {
                        et.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        et.setBackgroundColor(Color.RED);
                    }
                } else if (et.getId() == R.id.centTime) {
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


    private void setLayout(int resLayout) {

        View lowLayout = findViewById(R.id.lowLayout);
        ViewGroup parent = (ViewGroup) lowLayout.getParent();
        int index = parent.indexOfChild(lowLayout);
        parent.removeView(lowLayout);
        lowLayout = getLayoutInflater().inflate(resLayout, parent, false);
        parent.addView(lowLayout, index);
    }
}
