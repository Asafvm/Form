package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;

import il.co.diamed.com.form.PDFActivity;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.Tuple;

import static il.co.diamed.com.form.devices.Helper.isValidString;

public class PlasmaThawerActivity extends AppCompatActivity {
    private final double EXPECTED_TEMP = 36.4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plasma_thawer);
        Helper h = new Helper();
        h.setLayout(this, R.layout.plasma_layout);


        Bundle bundle = getIntent().getExtras().getBundle("cal");
        final String techname = bundle.getString("techName");
        final String signature = bundle.getString("signature");
        final String thermometer = bundle.getString("thermometer");
        final String speedometer = bundle.getString("speedometer");
        final String barometer = bundle.getString("barometer");
        final String timer = bundle.getString("timer");


        //get views
        final Button btn = findViewById(R.id.formSubmitButton);

        final EditText t11 = findViewById(R.id.formMainLocation);
        final EditText t12 = findViewById(R.id.formRoomLocation);
        final RadioGroup t2 = findViewById(R.id.rgModelSelect);
        final EditText t3 = findViewById(R.id.etDeviceSerial);
        final EditText t4 = findViewById(R.id.ptTemp);
        final EditText t5 = findViewById(R.id.ptTime);
        final EditText t6 = findViewById(R.id.formTechName);
        final DatePicker dp = findViewById(R.id.formDate);

        final Switch waterSwitch = findViewById(R.id.ptWaterCheck);
        final Switch oilSwitch = findViewById(R.id.ptOilCheck);
        final Switch alarmSwitch = findViewById(R.id.ptAlarmCheck);


        //default basic values
        ((EditText)findViewById(R.id.ptExpectedTemp)).setText(String.valueOf(EXPECTED_TEMP));
        t2.check(R.id.dh8);
        t5.setText("10");
        waterSwitch.setChecked(true);
        oilSwitch.setChecked(true);
        alarmSwitch.setChecked(true);
        t6.setText(techname);


        h.setListener(t11);
        h.setListener(t12);
        h.setListener(t3);
        h.setTempListener(t4,EXPECTED_TEMP-1,EXPECTED_TEMP+1);
        h.setListener(t5);
        h.setListener(t6);
        btn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {


                if (checkStatus()) {


                    ArrayList<Tuple> corCheck = new ArrayList<>();
                    //       corCheck.add(new Tuple(116,467));           //temp not ok
                    corCheck.add(new Tuple(217, 452));           //temp ok
                    //       corCheck.add(new Tuple(114,327));           //time not ok
                    corCheck.add(new Tuple(222, 311));           //time ok
                    if(!waterSwitch.isChecked())
                        corCheck.add(new Tuple(154,214));           //water not ok
                    else
                        corCheck.add(new Tuple(275, 214));           //water ok

                    corCheck.add(new Tuple(275, 198));           //oil ok

                    corCheck.add(new Tuple(275, 178));           //alarm ok



                    //       corCheck.add(new Tuple(232,95));           //overall not ok
                    corCheck.add(new Tuple(473, 85));           //overall ok

                    ArrayList<Tuple> corText = new ArrayList<>();

                    corText.add(new Tuple(305, 622));                        //Location
                    corText.add(new Tuple(310, 30));                        //Tech Name
                    corText.add(new Tuple(73, 622));                        //Date
                    corText.add(new Tuple(200, 550));                        //type
                    corText.add(new Tuple(425, 550));                        //Serial
                    corText.add(new Tuple(280, 449));                        //temp
                    //corText.add(new Tuple(425, 455));                        //temp expected
                    corText.add(new Tuple(305, 309));                        //Time
                    corText.add(new Tuple(435, 57));                        //Next Date


                    corText.add(new Tuple(350, 405));                        //thermometer
                    corText.add(new Tuple(400, 269));                        //Timer
                    //corText.add(new Tuple(380,30));                        //Signature

                    ArrayList<String> arrText = new ArrayList<>();

                    arrText.add(t11.getText().toString() + " - " + t12.getText().toString());                                       //Location
                    arrText.add(t6.getText().toString());                        //Tech Name
                    arrText.add(dp.getDayOfMonth() + "       " + dp.getMonth() + "       " + dp.getYear());                      //Date
                    arrText.add(((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString());     //type
                    arrText.add(t3.getText().toString());                        //Serial
                    arrText.add(t4.getText().toString());                        //temp
                    //arrText.add(expectedTemp.getText().toString());                        //cent expected
                    arrText.add(t5.getText().toString());                        //Time
                    arrText.add(dp.getMonth() + "   " + (dp.getYear() + 1));           //Next Date


                    arrText.add(thermometer);                       //thermometer
                    arrText.add(timer);                             //Timer


                    ArrayList<String> destArray = new ArrayList<>();
                    destArray.add(t11.getText().toString() + "_" + t12.getText().toString());
                    destArray.add(String.valueOf(dp.getYear()));
                    destArray.add(String.valueOf(dp.getMonth()));
                    destArray.add(String.valueOf(dp.getDayOfMonth()));
                    destArray.add(t3.getText().toString());


                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_plasma_biyearly.pdf");

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
                if (!isValidString(t5.getText().toString()))
                    return false;
                if (!isValidString(t6.getText().toString()))
                    return false;
                if (!alarmSwitch.isChecked())
                    return false;
                if (!oilSwitch.isChecked())
                    return false;
                if (!Helper.isTempValid(t4,EXPECTED_TEMP-1,EXPECTED_TEMP+1))
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
                et = findViewById(R.id.ptTemp);
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



}


