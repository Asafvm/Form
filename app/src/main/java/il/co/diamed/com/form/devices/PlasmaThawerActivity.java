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
    private final int EXPECTED_TIME = 10;

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
        final String timer = bundle.getString("timer");


        //get views

        //default basic values
        init();
        h.setListener(((EditText) findViewById(R.id.formTechName)));
        ((EditText) findViewById(R.id.formTechName)).setText(techname);

        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {


                if (checkStatus()) {
                    ArrayList<Tuple> corText = new ArrayList<>();
                    corText.add(new Tuple(218, 453, "", false));           //temp ok
                    corText.add(new Tuple(223, 312, "", false));           //time ok
                    if(!((Switch)findViewById(R.id.ptWaterCheck)).isChecked())
                        corText.add(new Tuple(154,216, "", false));           //water not ok
                    else
                        corText.add(new Tuple(276, 216, "", false));           //water ok
                    corText.add(new Tuple(276, 198, "", false));           //oil ok
                    corText.add(new Tuple(276, 180, "", false));           //alarm ok

                    corText.add(new Tuple(475, 88, "", false));           //overall ok


                    corText.add(new Tuple(310, 623, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                    corText.add(new Tuple(310, 30, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                    corText.add(new Tuple(73, 623, ((DatePicker)findViewById(R.id.formDate)).getDayOfMonth() + "       " +
                            ((DatePicker)findViewById(R.id.formDate)).getMonth() + "       " +
                            ((DatePicker)findViewById(R.id.formDate)).getYear(), false));                        //Date
                    corText.add(new Tuple(200, 552, ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString(), false));                        //type
                    corText.add(new Tuple(425, 552, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                    corText.add(new Tuple(280, 449, ((EditText) findViewById(R.id.ptTemp)).getText().toString(), false));                        //temp
                    //corText.add(new Tuple(425, 455));                        //temp expected
                    corText.add(new Tuple(305, 309, ((EditText) findViewById(R.id.ptTime)).getText().toString(), false));                        //Time
                    corText.add(new Tuple(434, 57, ((DatePicker)findViewById(R.id.formDate)).getMonth() + "   " +
                            (((DatePicker)findViewById(R.id.formDate)).getYear() + 1), false));                        //Next Date


                    corText.add(new Tuple(350, 405, thermometer, false));                        //thermometer
                    corText.add(new Tuple(400, 269, timer, false));                        //Timer
                    corText.add(new Tuple(105, 30, "!", false));                        //Signature

                    ArrayList<String> destArray = new ArrayList<>();
                    destArray.add(((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "_" + ((EditText) findViewById(R.id.formRoomLocation)).getText().toString());
                    destArray.add(String.valueOf(((DatePicker)findViewById(R.id.formDate)).getYear()));
                    destArray.add(String.valueOf(((DatePicker)findViewById(R.id.formDate)).getMonth()));
                    destArray.add(String.valueOf(((DatePicker)findViewById(R.id.formDate)).getDayOfMonth()));
                    destArray.add(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString());


                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_plasma_biyearly.pdf");

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1",corText);
                    intent.putExtra("pages",pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString()+" "+((EditText) findViewById(R.id.formRoomLocation)).getText().toString()+"/"+((DatePicker)findViewById(R.id.formDate)).getYear()+""+((DatePicker)findViewById(R.id.formDate)).getDayOfMonth()+""+((DatePicker)findViewById(R.id.formDate)).getMonth()+"_"+
                            "_"+((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString()+"_"+((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()+".pdf");
                    startActivityForResult(intent,1);
                }


            }

            private boolean checkStatus() {
                return isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.ptTime)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.formTechName)).getText().toString()) &&
                        ((Switch)findViewById(R.id.ptAlarmCheck)).isChecked() &&
                        ((Switch)findViewById(R.id.ptOilCheck)).isChecked() &&
                        Helper.isTempValid(((EditText) findViewById(R.id.ptTemp)), EXPECTED_TEMP - 1, EXPECTED_TEMP + 1);

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


    private void restart() {
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");
        ((RadioGroup) findViewById(R.id.rgModelSelect)).check(R.id.si);
        ((EditText) findViewById(R.id.temp)).setText(String.valueOf(""));
        ((EditText) findViewById(R.id.time)).setText(String.valueOf(EXPECTED_TIME));
    }

    private void init() {
        Helper h = new Helper();
        h.setListener(((EditText) findViewById(R.id.formMainLocation)));
        h.setListener(((EditText) findViewById(R.id.formRoomLocation)));
        h.setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        h.setListener(((EditText) findViewById(R.id.formTechName)));
        h.setTempListener(((EditText) findViewById(R.id.ptTemp)),EXPECTED_TEMP-1,EXPECTED_TEMP+1);
        h.setTimeListener(((EditText) findViewById(R.id.ptTime)),EXPECTED_TIME);

        ((RadioGroup) findViewById(R.id.rgModelSelect)).check(R.id.dh8);
        ((EditText) findViewById(R.id.formMainLocation)).setText("");
        ((EditText) findViewById(R.id.formRoomLocation)).setText("");
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");
        ((EditText)findViewById(R.id.ptExpectedTemp)).setText(String.valueOf(EXPECTED_TEMP));
        ((EditText) findViewById(R.id.temp)).setText(String.valueOf(""));
        ((EditText) findViewById(R.id.time)).setText(String.valueOf(R.string.time10));

        ((Switch)findViewById(R.id.ptAlarmCheck)).setChecked(true);
        ((Switch)findViewById(R.id.ptOilCheck)).setChecked(true);
        ((Switch)findViewById(R.id.ptWaterCheck)).setChecked(true);


    }
}


