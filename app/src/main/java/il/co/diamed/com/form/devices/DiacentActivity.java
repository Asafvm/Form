package il.co.diamed.com.form.devices;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import static il.co.diamed.com.form.devices.Helper.isSpeedValid;
import static il.co.diamed.com.form.devices.Helper.isTimeValid;
import static il.co.diamed.com.form.devices.Helper.isValidString;

public class DiacentActivity extends AppCompatActivity {


    private static final int EXPECTED_CW_SPEED = 2500;
    private static final int EXPECTED_CW_TIME = 60;
    private static final int EXPECTED_12_TIME1 = 15;
    private static final int EXPECTED_12_TIME2 = 20;
    private static final int EXPECTED_12_TIME3 = 30;
    private static final int EXPECTED_12_SPEED1 = 1000;
    private static final int EXPECTED_12_SPEED2 = 2000;
    private static final int EXPECTED_12_SPEED3 = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diacent);
        Helper h = new Helper();
        h.setLayout(this, R.layout.diacent12_layout);


        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String signature = bundle.getString("signature");
        final String speedometer = bundle.getString("speedometer");
        final String timer = bundle.getString("timer");



        init();
        ((EditText) findViewById(R.id.formTechName)).setText(Objects.requireNonNull(bundle).getString("techName"));

        (findViewById(R.id.formSubmitButton)).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    ArrayList<Tuple> corText;

                    if (((RadioGroup)findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId() == R.id.dia12) {
                        corText = getDiacent12TextCor();
                        intent.putExtra("report", "2018_diacent_yearly.pdf");
                    } else {
                        corText = getDiacentCWTextCor();
                        intent.putExtra("report", "2018_diacw_yearly.pdf");
                    }


                    Bundle pages = new Bundle();


                    pages.putParcelableArrayList("page1", corText);
                    intent.putExtra("pages", pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " " +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            ((DatePicker)findViewById(R.id.formDate)).getYear() + "" +
                            ((DatePicker)findViewById(R.id.formDate)).getDayOfMonth() + "" +
                            ((DatePicker)findViewById(R.id.formDate)).getMonth() + "_" +
                            ((RadioButton) findViewById(((RadioGroup)findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString() + "_" +
                            ((EditText)findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");
                    startActivityForResult(intent, 1);
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

                corText.add(new Tuple(300, 635, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                        ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                corText.add(new Tuple(330, 28, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                corText.add(new Tuple(74, 635, ((DatePicker) findViewById(R.id.formDate)).getDayOfMonth() + "     " +
                        ((DatePicker) findViewById(R.id.formDate)).getMonth() + "     " +
                        ((DatePicker) findViewById(R.id.formDate)).getYear(), false));                        //Date

                corText.add(new Tuple(100, 575, ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString(), false));                        //type
                corText.add(new Tuple(380, 575, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                corText.add(new Tuple(315, 475, ((EditText) findViewById(R.id.centcwSpeed2500)).getText().toString(), false));                        //cent2500
                corText.add(new Tuple(315, 343, ((EditText) findViewById(R.id.centCWtime)).getText().toString(), false));                        //Time
                corText.add(new Tuple(450, 77, ((DatePicker) findViewById(R.id.formDate)).getMonth() + "    " +
                        (((DatePicker) findViewById(R.id.formDate)).getYear() + 1), false));                        //Next Date
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

                corText.add(new Tuple(300, 635, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                        ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                corText.add(new Tuple(330, 28, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                corText.add(new Tuple(74, 635, ((DatePicker) findViewById(R.id.formDate)).getDayOfMonth() + "     " +
                        ((DatePicker) findViewById(R.id.formDate)).getMonth() + "     " +
                        ((DatePicker) findViewById(R.id.formDate)).getYear(), false));                        //Date

                corText.add(new Tuple(300, 605, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                corText.add(new Tuple(315, 502, ((EditText) findViewById(R.id.centSpeed1000)).getText().toString(), false));                        //cent1000
                corText.add(new Tuple(315, 478, ((EditText) findViewById(R.id.centSpeed2000)).getText().toString(), false));                        //cent2000
                corText.add(new Tuple(315, 448, ((EditText) findViewById(R.id.centSpeed3000)).getText().toString(), false));                        //cent3000
                corText.add(new Tuple(305, 326, ((EditText) findViewById(R.id.centTime1)).getText().toString(), false));                        //Time1
                corText.add(new Tuple(305, 300, ((EditText) findViewById(R.id.centTime2)).getText().toString(), false));                        //Time2
                corText.add(new Tuple(305, 275, ((EditText) findViewById(R.id.centTime3)).getText().toString(), false));                        //Time3
                corText.add(new Tuple(446, 62, ((DatePicker) findViewById(R.id.formDate)).getMonth() + "    " +
                        (((DatePicker) findViewById(R.id.formDate)).getYear() + 1), false));                        //Next Date

                corText.add(new Tuple(405, 407, speedometer, false));                        //speedometer
                corText.add(new Tuple(413, 232, timer, false));                        //Timer
                //corText.add(new Tuple(380,30));                        //Signature

                return corText;

            }

            private boolean checkStatus() {
                if (!isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()))
                    return false;
                if (!isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()))
                    return false;
                if (!isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()))
                    return false;
                if (((RadioGroup)findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId() == R.id.dia12) {

                    if (!isSpeedValid(Integer.valueOf(((EditText) findViewById(R.id.centSpeed1000)).getText().toString()), EXPECTED_12_SPEED1))
                        return false;
                    if (!isTimeValid(((EditText) findViewById(R.id.centTime1)), EXPECTED_12_TIME1))
                        return false;
                    if (!isSpeedValid(Integer.valueOf((((EditText) findViewById(R.id.centSpeed2000)).getText().toString())), EXPECTED_12_SPEED2))
                        return false;
                    if (!isTimeValid(((EditText) findViewById(R.id.centTime2)), EXPECTED_12_TIME2))
                        return false;
                    if (!isSpeedValid(Integer.valueOf((((EditText) findViewById(R.id.centSpeed3000)).getText().toString())), EXPECTED_12_SPEED3))
                        return false;
                    if (!isTimeValid(((EditText) findViewById(R.id.centTime3)), EXPECTED_12_TIME3))
                        return false;
                    if (!((Switch) findViewById(R.id.cent12checkHolders)).isChecked())
                        return false;
                } else {
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
                }
                return isValidString(((EditText)findViewById(R.id.formTechName)).getText().toString());
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
                        h.setLayout(DiacentActivity.this, R.layout.diacent12_layout);
                        initDiacent12();
                        break;
                    case R.id.diaCW:
                        h.setLayout(DiacentActivity.this, R.layout.diacentcw_layout);
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
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.pdfSuccess, Toast.LENGTH_SHORT).show();
                doAnother();
                setResult(RESULT_OK);
            } else {
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
                if (((RadioGroup)findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId() == R.id.dia12) {
                    initDiacent12();
                } else {
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

    private void init() {
        Helper h = new Helper();
        h.setListener(((EditText) findViewById(R.id.formMainLocation)));
        h.setListener(((EditText) findViewById(R.id.formRoomLocation)));
        h.setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        h.setListener(((EditText) findViewById(R.id.formTechName)));
        ((EditText) findViewById(R.id.formMainLocation)).setText("");
        ((EditText) findViewById(R.id.formRoomLocation)).setText("");
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");

        setListener(((RadioGroup)findViewById(R.id.rgModelSelect)));
        ((RadioGroup)findViewById(R.id.rgModelSelect)).check(R.id.dia12);
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

    private void initDiacent12() {
        /* Diacent 12 */
        Helper h = new Helper();
        h.setSpeedListener(((EditText) findViewById(R.id.centSpeed1000)), EXPECTED_12_SPEED1);
        h.setTimeListener(((EditText) findViewById(R.id.centTime1)), EXPECTED_12_TIME1);
        h.setSpeedListener(((EditText) findViewById(R.id.centSpeed2000)), EXPECTED_12_SPEED2);
        h.setTimeListener(((EditText) findViewById(R.id.centTime2)), EXPECTED_12_TIME2);
        h.setSpeedListener(((EditText) findViewById(R.id.centSpeed3000)), EXPECTED_12_SPEED3);
        h.setTimeListener(((EditText) findViewById(R.id.centTime3)), EXPECTED_12_TIME3);
        ((EditText) findViewById(R.id.centSpeed1000)).setText("");
        ((EditText) findViewById(R.id.centTime1)).setText(R.string.time15);
        ((EditText) findViewById(R.id.centSpeed2000)).setText("");
        ((EditText) findViewById(R.id.centTime2)).setText(R.string.time20);
        ((EditText) findViewById(R.id.centSpeed3000)).setText("");
        ((EditText) findViewById(R.id.centTime3)).setText(R.string.time30);
        ((Switch) findViewById(R.id.cent12checkHolders)).setChecked(true);


    }

}
