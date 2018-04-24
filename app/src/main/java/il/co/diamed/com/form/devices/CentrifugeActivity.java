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

import static il.co.diamed.com.form.devices.Helper.isTimeValid;
import static il.co.diamed.com.form.devices.Helper.isValidString;

public class CentrifugeActivity extends AppCompatActivity {
    private final int EXPECTED_TIME = 10;
    private int EXPECTED_SPEED = 1030;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centrifuge);
        Helper h = new Helper();
        h.setLayout(this,R.layout.centrifuge_layout);


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
        final EditText t4 = findViewById(R.id.centSpeed);
        final EditText t5 = findViewById(R.id.centTime);
        final EditText t6 = findViewById(R.id.formTechName);
        final DatePicker dp = findViewById(R.id.formDate);

        final Switch fanSwitch = findViewById(R.id.centFanSwitch);


        //default basic values
        EditText expectedSpeed = findViewById(R.id.centExpectedTime);
        expectedSpeed.setText(String.valueOf(EXPECTED_SPEED));

        t2.check(R.id.c12SII);
        t5.setText(R.string.time10);
        fanSwitch.setChecked(true);
        t6.setText(techname);


        h.setListener(t11);
        h.setListener(t12);
        h.setListener(t3);
        h.setSpeedListener(t4,EXPECTED_SPEED);
        h.setTimeListener(t5,EXPECTED_TIME);
        h.setListener(t6);
        setListener(t2);

        btn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {


                if (checkStatus()) {

                    ArrayList<Tuple> corText = new ArrayList<>();
                    corText.add(new Tuple(219, 448, "", false));           //speed ok
                    corText.add(new Tuple(214, 313, "", false));           //time ok
                    corText.add(new Tuple(245, 208, "", false));           //fan ok
                    corText.add(new Tuple(479, 102, "", false));           //overall ok
                    corText.add(new Tuple(305, 618, t11.getText().toString() + " - " + t12.getText().toString(), true));                        //Location
                    corText.add(new Tuple(330, 37, t6.getText().toString(), true));                        //Tech Name
                    corText.add(new Tuple(92, 618, dp.getDayOfMonth() + "       " + dp.getMonth() + "       " + dp.getYear(), false));                        //Date
                    corText.add(new Tuple(200, 548, ((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString(), false));                        //type
                    corText.add(new Tuple(425, 548, t3.getText().toString(), false));                        //Serial
                    corText.add(new Tuple(315, 445, t4.getText().toString(), false));                        //cent
                    corText.add(new Tuple(445, 445, String.valueOf(EXPECTED_SPEED), false));                        //cent expected
                    corText.add(new Tuple(305, 309, t5.getText().toString(), false));                        //Time
                    corText.add(new Tuple(450, 70, dp.getMonth() + "   " + (dp.getYear() + 1), false));                        //Next Date


                    corText.add(new Tuple(350, 400, speedometer, false));                        //speedometer
                    corText.add(new Tuple(400, 265, timer, false));                        //Timer
                    corText.add(new Tuple(135, 33, "!", false));                        //Signature


                    ArrayList<String> destArray = new ArrayList<>();
                    destArray.add(t11.getText().toString() + "_" + t12.getText().toString());
                    destArray.add(String.valueOf(dp.getYear()));
                    destArray.add(String.valueOf(dp.getMonth()));
                    destArray.add(String.valueOf(dp.getDayOfMonth()));
                    destArray.add(t3.getText().toString());


                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_idcent_yearly.pdf");

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
                        isValidString(t3.getText().toString()) &&
                        Helper.isSpeedValid(Integer.valueOf(t4.getText().toString()), EXPECTED_SPEED) &&
                        isTimeValid(t5, EXPECTED_TIME) && isValidString(t6.getText().toString()) && fanSwitch.isChecked();

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
                EditText t4 = findViewById(R.id.centSpeed);
                Helper h = new Helper();
                h.setSpeedListener(t4,EXPECTED_SPEED);
                EditText expectedSpeed = findViewById(R.id.centExpectedTime);
                expectedSpeed.setText(String.valueOf(EXPECTED_SPEED));            }
        });
    }

}
