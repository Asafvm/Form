package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

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


        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
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
        t5.setText(R.string.time10);
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
                    ArrayList<Tuple> corText = new ArrayList<>();
                    corText.add(new Tuple(218, 453, "", false));           //temp ok
                    corText.add(new Tuple(223, 312, "", false));           //time ok
                    if(!waterSwitch.isChecked())
                        corText.add(new Tuple(154,216, "", false));           //water not ok
                    else
                        corText.add(new Tuple(276, 216, "", false));           //water ok
                    corText.add(new Tuple(276, 198, "", false));           //oil ok
                    corText.add(new Tuple(276, 180, "", false));           //alarm ok

                    corText.add(new Tuple(475, 88, "", false));           //overall ok


                    corText.add(new Tuple(310, 623, t11.getText().toString() + " - " + t12.getText().toString(), true));                        //Location
                    corText.add(new Tuple(310, 30, t6.getText().toString(), true));                        //Tech Name
                    corText.add(new Tuple(73, 623, dp.getDayOfMonth() + "       " + dp.getMonth() + "       " + dp.getYear(), false));                        //Date
                    corText.add(new Tuple(200, 552, ((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString(), false));                        //type
                    corText.add(new Tuple(425, 552, t3.getText().toString(), false));                        //Serial
                    corText.add(new Tuple(280, 449, t4.getText().toString(), false));                        //temp
                    //corText.add(new Tuple(425, 455));                        //temp expected
                    corText.add(new Tuple(305, 309, t5.getText().toString(), false));                        //Time
                    corText.add(new Tuple(434, 57, dp.getMonth() + "   " + (dp.getYear() + 1), false));                        //Next Date


                    corText.add(new Tuple(350, 405, thermometer, false));                        //thermometer
                    corText.add(new Tuple(400, 269, timer, false));                        //Timer
                    corText.add(new Tuple(105, 30, "!", false));                        //Signature

                    ArrayList<String> destArray = new ArrayList<>();
                    destArray.add(t11.getText().toString() + "_" + t12.getText().toString());
                    destArray.add(String.valueOf(dp.getYear()));
                    destArray.add(String.valueOf(dp.getMonth()));
                    destArray.add(String.valueOf(dp.getDayOfMonth()));
                    destArray.add(t3.getText().toString());


                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_plasma_biyearly.pdf");

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1",corText);
                    intent.putExtra("pages",pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", t11.getText().toString()+" "+t12.getText().toString()+"/"+dp.getYear()+""+dp.getDayOfMonth()+""+dp.getMonth()+"_"+
                            "_"+((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString()+"_"+t3.getText().toString()+".pdf");
                    startActivityForResult(intent,1);
                }


            }

            private boolean checkStatus() {
                return isValidString(t11.getText().toString()) && isValidString(t12.getText().toString()) &&
                        isValidString(t3.getText().toString()) && isValidString(t5.getText().toString()) &&
                        isValidString(t6.getText().toString()) && alarmSwitch.isChecked() && oilSwitch.isChecked() &&
                        Helper.isTempValid(t4, EXPECTED_TEMP - 1, EXPECTED_TEMP + 1);

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


