package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class IncubatorActivity extends AppCompatActivity {
    private final int MAX_TEMP = 39;
    private final int MIN_TEMP = 35;
    private final int EXPECTED_TIME = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incubator);
        Helper h = new Helper();
        h.setLayout(this, R.layout.incubator_layout);

        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        final String thermometer = bundle.getString("thermometer");
        final String timer = bundle.getString("timer");
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

        h.setListener(t11);
        h.setListener(t12);
        h.setListener(t3);
        h.setTempListener(t4,MIN_TEMP,MAX_TEMP);
        h.setTimeListener(t5,EXPECTED_TIME);
        //default basic values
        t11.setText("");
        t12.setText("");
        t2.check(R.id.si);
        t3.setText("");
        t5.setText(R.string.time15);
        t6.setText(techname);

        btn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    ArrayList<Tuple> corText = new ArrayList<>();
                    corText.add(new Tuple(205, 469, "", false));           //temp ok
                    corText.add(new Tuple(203, 329, "", false));           //time ok
                    corText.add(new Tuple(251, 230, "", false));           //fan ok
                    corText.add(new Tuple(251, 212, "", false));           //rubber ok
                    corText.add(new Tuple(482, 97, "", false));           //overall ok

                    corText.add(new Tuple(300, 636, t11.getText().toString() + " - " + t12.getText().toString(), true));                        //Location
                    corText.add(new Tuple(330, 30, t6.getText().toString(), true));                        //Tech Name
                    corText.add(new Tuple(72, 636, dp.getDayOfMonth() + "       " + dp.getMonth() + "         " + dp.getYear(), false));                        //Date
                    corText.add(new Tuple(290, 568, ((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString(), false));                        //type
                    corText.add(new Tuple(425, 568, t3.getText().toString(), false));                        //Serial
                    corText.add(new Tuple(275, 465, t4.getText().toString(), false));                        //temp
                    corText.add(new Tuple(305, 325, t5.getText().toString(), false));                        //Time
                    corText.add(new Tuple(451, 65, dp.getMonth() + "   " + (dp.getYear() + 1), false));                        //Next Date

                    corText.add(new Tuple(330, 425, thermometer, false));                        //Thermometer
                    corText.add(new Tuple(400, 285, timer, false));                        //Timer

                    corText.add(new Tuple(135, 33, "!", false));                        //Signature
                    ArrayList<String> destArray = new ArrayList<>();

                    destArray.add(t11.getText().toString() + "_" + t12.getText().toString());
                    destArray.add(String.valueOf(dp.getYear()));
                    destArray.add(String.valueOf(dp.getMonth()));
                    destArray.add(String.valueOf(dp.getDayOfMonth()));
                    destArray.add(t3.getText().toString());

                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);

                    Bundle pages = new Bundle();
                    Bundle page1 = new Bundle();
                    pages.putParcelableArrayList("page1",corText);
                    intent.putExtra("pages", pages);

                    intent.putExtra("report", "2018_id37_yearly.pdf");

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", t11.getText().toString()+" "+t12.getText().toString()+"/"+dp.getYear()+""+dp.getDayOfMonth()+""+dp.getMonth()+"_"+
                            "_"+((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString()+"_"+t3.getText().toString()+".pdf");
                    startActivityForResult(intent, 1);
                }
            }

            private boolean checkStatus() {
                return Helper.isValidString(t11.getText().toString()) && Helper.isValidString(t12.getText().toString()) &&
                        Helper.isValidString(t3.getText().toString()) && Helper.isTempValid(t4, MIN_TEMP, MAX_TEMP) &&
                        Helper.isTimeValid(t5, EXPECTED_TIME) && Helper.isValidString(t6.getText().toString()) &&
                        fanSwitch.isChecked() && rubberSwitch.isChecked();

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
                EditText et = findViewById(R.id.etDeviceSerial);
                et.setText("");
                et = findViewById(R.id.temp);
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
