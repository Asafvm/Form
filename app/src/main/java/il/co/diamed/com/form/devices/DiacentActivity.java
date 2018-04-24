package il.co.diamed.com.form.devices;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Objects;

import il.co.diamed.com.form.PDFActivity;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.Tuple;

import static il.co.diamed.com.form.R.string.time20;
import static il.co.diamed.com.form.devices.Helper.isValidString;

public class DiacentActivity extends AppCompatActivity {
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
        Helper h = new Helper();
        h.setLayout(this,R.layout.diacent12_layout);


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

        final EditText t6 = findViewById(R.id.formTechName);
        final DatePicker dp = findViewById(R.id.formDate);
        h.setListener(t11);
        h.setListener(t12);
        h.setListener(t3);
        //default basic values
        initDiacent12();
        t2.check(R.id.dia12);
        t6.setText(techname);
        setListener(t2);
/*
        setListener(t11);
        setListener(t12);

        setListener(t3);
        setListener(t6);

*/
        btn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    ArrayList<Tuple> corText;

                    if (t2.getCheckedRadioButtonId() == R.id.dia12) {
                        corText = getDiacent12TextCor();
                        intent.putExtra("report", "2018_diacent_yearly.pdf");
                    } else {
                        corText = getDiacentCWTextCor();
                        intent.putExtra("report", "2018_diacw_yearly.pdf");
                    }

                    ArrayList<String> destArray = new ArrayList<>();
                    destArray.add(t11.getText().toString() + "_" + t12.getText().toString());
                    destArray.add(String.valueOf(dp.getYear()));
                    destArray.add(String.valueOf(dp.getMonth()));
                    destArray.add(String.valueOf(dp.getDayOfMonth()));
                    destArray.add(t3.getText().toString());


                    Bundle pages = new Bundle();
                    Bundle page1 = new Bundle();

                    pages.putParcelableArrayList("page1",corText);
                    intent.putExtra("pages",pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", t11.getText().toString()+" "+t12.getText().toString()+"/"+dp.getYear()+""+dp.getDayOfMonth()+""+dp.getMonth()+"_"+
                            "_"+((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString()+"_"+t3.getText().toString()+".pdf");
                    startActivityForResult(intent,1);
                } else {
                    Log.e("Diacent: ", "checkStatus Failed");
                }
            }

            private ArrayList<Tuple> getDiacentCWTextCor() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(190, 481, "", false));           //speed ok
                corText.add(new Tuple(190, 345, "", false));           //time ok
                corText.add(new Tuple(226, 250, "", false));           //fan ok
                corText.add(new Tuple(226, 233, "", false));           //fan ok
                corText.add(new Tuple(226, 214, "", false));           //fan ok
                corText.add(new Tuple(484, 108, "", false));           //overall ok

                corText.add(new Tuple(303, 650, t11.getText().toString() + " - " + t12.getText().toString(), true));                        //Location
                corText.add(new Tuple(330, 30, t6.getText().toString(), true));                        //Tech Name
                corText.add(new Tuple(74, 650, dp.getDayOfMonth() + "     " + dp.getMonth() + "     " + dp.getYear(), false));                        //Date
                corText.add(new Tuple(100, 575, ((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString(), false));                        //type
                corText.add(new Tuple(380, 575, t3.getText().toString(), false));                        //Serial
                corText.add(new Tuple(315, 475, t4.getText().toString(), false));                        //cent2500
                corText.add(new Tuple(315, 343, t5.getText().toString(), false));                        //Time
                corText.add(new Tuple(450, 77, dp.getMonth() + "   " + (dp.getYear() + 1), false));                        //Next Date

                corText.add(new Tuple(378, 436, speedometer, false));                        //speedometer
                corText.add(new Tuple(400, 302, timer, false));                        //Timer
                corText.add(new Tuple(135, 33, "!", false));                        //Signature

                return corText;
            }


            private ArrayList<Tuple> getDiacent12TextCor() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(205, 452, "", false));           //speed1 ok
                corText.add(new Tuple(205, 480, "", false));           //speed2 ok
                corText.add(new Tuple(205, 506, "", false));           //speed3 ok
                corText.add(new Tuple(190, 331, "", false));           //time ok
                corText.add(new Tuple(190, 304, "", false));           //time ok
                corText.add(new Tuple(190, 276, "", false));           //time ok
                corText.add(new Tuple(241, 182, "", false));           //fan ok
                corText.add(new Tuple(480, 95, "", false));           //overall ok

                corText.add(new Tuple(300, 635, t11.getText().toString() + " - " + t12.getText().toString(), true));                        //Location
                corText.add(new Tuple(330, 28, t6.getText().toString(), true));                        //Tech Name
                corText.add(new Tuple(74, 635, dp.getDayOfMonth() + "     " + dp.getMonth() + "     " + dp.getYear(), false));                        //Date

                corText.add(new Tuple(300, 605, t3.getText().toString(), false));                        //Serial
                corText.add(new Tuple(315, 502, t41.getText().toString(), false));                        //cent1000
                corText.add(new Tuple(315, 478, t42.getText().toString(), false));                        //cent2000
                corText.add(new Tuple(315, 448, t43.getText().toString(), false));                        //cent3000
                corText.add(new Tuple(305, 326, t51.getText().toString(), false));                        //Time1
                corText.add(new Tuple(305, 300, t52.getText().toString(), false));                        //Time2
                corText.add(new Tuple(305, 275, t53.getText().toString(), false));                        //Time3
                corText.add(new Tuple(446, 62, dp.getMonth() + "    " + (dp.getYear() + 1), false));                        //Next Date

                corText.add(new Tuple(405, 407, speedometer, false));                        //speedometer
                corText.add(new Tuple(413, 232, timer, false));                        //Timer
                //corText.add(new Tuple(380,30));                        //Signature

                return corText;

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
                return isValidString(t6.getText().toString());
            }
        });

    }

    /*** Swtich 12 and CW views ***/
    private void setListener(RadioGroup rg) {
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Helper h = new Helper();
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.dia12:
                        h.setLayout(DiacentActivity.this,R.layout.diacent12_layout);
                        initDiacent12();
                        break;
                    case R.id.diaCW:
                        h.setLayout(DiacentActivity.this,R.layout.diacentcw_layout);
                        initDiacentCW();
                        break;
                    default:
                        break;
                }

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
                RadioGroup rg = findViewById(R.id.rgModelSelect);
                if(rg.getCheckedRadioButtonId() == R.id.dia12) {
                    et = findViewById(R.id.centSpeed1000);
                    et.setText("");
                    et = findViewById(R.id.centSpeed2000);
                    et.setText("");
                    et = findViewById(R.id.centSpeed3000);
                    et.setText("");
                    initDiacent12();
                }else {
                    et = findViewById(R.id.centcwSpeed2500);
                    et.setText("");
                    initDiacentCW();
                }
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

    private void initDiacentCW() {

        /* Diacent CW */
        t4 = findViewById(R.id.centcwSpeed2500);
        t5 = findViewById(R.id.centCWtime);
        checkcwHolders = findViewById(R.id.centCheckHolders);
        checkRemaining = findViewById(R.id.centCheckRemaining);
        checkFilling = findViewById(R.id.centCheckFilling);

        t5.setText(R.string.time60);
        checkFilling.setChecked(true);
        checkcwHolders.setChecked(true);
        checkRemaining.setChecked(true);

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

        t51.setText(R.string.time15);
        t52.setText(R.string.time20);
        t53.setText(R.string.time30);
        check12Holders.setChecked(true);
/*
        setListener(t41);
        setListener(t42);
        setListener(t43);
        setListener(t51);
        setListener(t52);
        setListener(t53);
*/
    }

}
