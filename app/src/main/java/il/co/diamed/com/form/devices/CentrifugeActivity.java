package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import static il.co.diamed.com.form.devices.Helper.isTimeValid;
import static il.co.diamed.com.form.devices.Helper.isValidString;

public class CentrifugeActivity extends AppCompatActivity {
    private final int EXPECTED_TIME = 10;
    private int EXPECTED_SPEED = 1030;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_device_activity);
        final Helper h = new Helper();
        h.setLayout(this,R.layout.device_centrifuge_layout);

        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        final String speedometer = bundle.getString("speedometer");
        final String timer = bundle.getString("timer");
        //default basic values
        init();
        ((EditText)findViewById(R.id.formTechName)).setText(techname);
        ((EditText) findViewById(R.id.centExpectedSpeed)).setText(String.valueOf(EXPECTED_SPEED));

        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    String day = h.fixDate(((DatePicker)findViewById(R.id.formDate)).getDayOfMonth());
                    String month = h.fixDate(((DatePicker)findViewById(R.id.formDate)).getMonth());
                    ArrayList<Tuple> corText = new ArrayList<>();
                    corText.add(new Tuple(219, 448, "", false));           //speed ok
                    corText.add(new Tuple(214, 313, "", false));           //time ok
                    corText.add(new Tuple(245, 208, "", false));           //fan ok
                    corText.add(new Tuple(479, 102, "", false));           //overall ok
                    corText.add(new Tuple(305, 618, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                    corText.add(new Tuple(330, 37, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                    corText.add(new Tuple(92, 618, day + "      " + month + "      " +
                            ((DatePicker)findViewById(R.id.formDate)).getYear(), false));                        //Date
                    corText.add(new Tuple(200, 548, ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString(), false));                        //type
                    corText.add(new Tuple(425, 548, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                    corText.add(new Tuple(315, 445, ((EditText) findViewById(R.id.centSpeed)).getText().toString(), false));                        //cent
                    corText.add(new Tuple(445, 445, String.valueOf(EXPECTED_SPEED), false));                        //cent expected
                    corText.add(new Tuple(305, 309, ((EditText) findViewById(R.id.centTime)).getText().toString(), false));                        //Time
                    corText.add(new Tuple(450, 70, ((DatePicker)findViewById(R.id.formDate)).getMonth() + "   " +
                            (((DatePicker)findViewById(R.id.formDate)).getYear() + 1), false));                        //Next Date


                    corText.add(new Tuple(350, 402, speedometer, false));                        //speedometer
                    corText.add(new Tuple(400, 267, timer, false));                        //Timer
                    corText.add(new Tuple(135, 33, "!", false));                        //Signature


                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_idcent_yearly.pdf");

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1",corText);
                    intent.putExtra("pages",pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString()+"/"+
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString()+"/"+
                            ((DatePicker)findViewById(R.id.formDate)).getYear()+""+
                            month+""+day+"_"+"Centrifuge-"+
                            ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString()+"_"+
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()+".pdf");
                    startActivityForResult(intent,1);
                }
            }

            private boolean checkStatus() {
                return isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()) &&
                        Helper.isSpeedValid(Integer.valueOf(((EditText) findViewById(R.id.centSpeed)).getText().toString()), EXPECTED_SPEED) &&
                        isTimeValid(((EditText) findViewById(R.id.centTime)), EXPECTED_TIME) &&
                        isValidString(((EditText) findViewById(R.id.formTechName)).getText().toString()) &&
                        ((Switch)findViewById(R.id.centFanSwitch)).isChecked();

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
                restart();
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



    private void setListener(RadioGroup rg) {
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.c6S:
                        EXPECTED_SPEED = 1175;
                        break;
                    case R.id.c12S:
                        EXPECTED_SPEED = 1030;
                        break;
                    case R.id.c12SII:
                        EXPECTED_SPEED = 1030;
                        break;
                    case R.id.c24S:
                        EXPECTED_SPEED = 910;
                        break;
                    case R.id.l:
                        EXPECTED_SPEED = 1030;
                        break;
                    default:
                        EXPECTED_SPEED = 0;
                        break;
                }
                Helper h = new Helper();
                h.setSpeedListener(((EditText) findViewById(R.id.centSpeed)),EXPECTED_SPEED);
                ((EditText) findViewById(R.id.centExpectedSpeed)).setText(String.valueOf(EXPECTED_SPEED));            }
        });
    }
    private void restart() {
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");
        ((RadioGroup) findViewById(R.id.rgModelSelect)).check(R.id.c12SII);
        ((EditText) findViewById(R.id.centSpeed)).setText(String.valueOf(EXPECTED_SPEED));
        ((EditText) findViewById(R.id.centTime)).setText(String.valueOf(EXPECTED_TIME));
    }
    private void init() {
        Helper h = new Helper();
        h.setListener(((EditText) findViewById(R.id.formMainLocation)));
        h.setListener(((EditText) findViewById(R.id.formRoomLocation)));
        h.setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        h.setListener(((EditText) findViewById(R.id.formTechName)));
        h.setSpeedListener(((EditText) findViewById(R.id.centSpeed)),EXPECTED_SPEED);
        h.setTimeListener(((EditText) findViewById(R.id.centTime)),EXPECTED_TIME);
        setListener(((RadioGroup) findViewById(R.id.rgModelSelect)));

        ((RadioGroup) findViewById(R.id.rgModelSelect)).check(R.id.c12SII);
        ((EditText) findViewById(R.id.formMainLocation)).setText("");
        ((EditText) findViewById(R.id.formRoomLocation)).setText("");
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");
        ((EditText) findViewById(R.id.centSpeed)).setText(String.valueOf(EXPECTED_SPEED));
        ((EditText) findViewById(R.id.centTime)).setText(String.valueOf(EXPECTED_TIME));

        ((Switch)findViewById(R.id.centFanSwitch)).setChecked(true);
    }


}
