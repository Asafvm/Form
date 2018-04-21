package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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


        Bundle bundle = getIntent().getExtras().getBundle("cal");
        final String techname = bundle.getString("techName");
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
        t5.setText("15");
        t6.setText(techname);

        btn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {


                if (checkStatus()) {


                    ArrayList<Tuple> corCheck = new ArrayList<>();
                    //       corCheck.add(new Tuple(116,467));           //temp not ok
                    corCheck.add(new Tuple(205, 469));           //temp ok
                    //       corCheck.add(new Tuple(114,327));           //time not ok
                    corCheck.add(new Tuple(203, 329));           //time ok
                    //       corCheck.add(new Tuple(155,228));           //fan not ok
                    corCheck.add(new Tuple(251, 230));           //fan ok
                    //       corCheck.add(new Tuple(155,210));           //rubber not ok
                    corCheck.add(new Tuple(251, 212));           //rubber ok
                    //       corCheck.add(new Tuple(232,95));           //overall not ok
                    corCheck.add(new Tuple(482, 97));           //overall ok

                    ArrayList<Tuple> corText = new ArrayList<>();

                    corText.add(new Tuple(300, 636));                        //Location
                    corText.add(new Tuple(330, 30));                        //Tech Name
                    corText.add(new Tuple(72, 636));                        //Date
                    corText.add(new Tuple(290, 568));                        //type
                    corText.add(new Tuple(425, 568));                        //Serial
                    corText.add(new Tuple(275, 465));                        //temp
                    corText.add(new Tuple(305, 325));                        //Time
                    corText.add(new Tuple(451, 65));                        //Next Date


                    corText.add(new Tuple(330, 425));                        //Thermometer
                    corText.add(new Tuple(400, 285));                        //Timer
                    //corText.add(new Tuple(380,30));                        //Signature

                    ArrayList<String> arrText = new ArrayList<>();


                    arrText.add(t11.getText().toString() + " - " + t12.getText().toString());                                       //Location
                    arrText.add(t6.getText().toString());                        //Tech Name
                    arrText.add(dp.getDayOfMonth() + "       " + dp.getMonth() + "         " + dp.getYear());                      //Date
                    arrText.add(((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString());     //type
                    arrText.add(t3.getText().toString());                        //Serial
                    arrText.add(t4.getText().toString());                        //temp
                    arrText.add(t5.getText().toString());                        //Time
                    arrText.add(dp.getMonth() + "   " + (dp.getYear() + 1));           //Next Date


                    arrText.add(thermometer);                       //Thermometer
                    arrText.add(timer);                             //Timer


                    ArrayList<String> destArray = new ArrayList<>();
                    destArray.add(t11.getText().toString() + "_" + t12.getText().toString());
                    destArray.add(String.valueOf(dp.getYear()));
                    destArray.add(String.valueOf(dp.getMonth()));
                    destArray.add(String.valueOf(dp.getDayOfMonth()));
                    destArray.add(t3.getText().toString());


                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);

                    Bundle pages = new Bundle();
                    Bundle page1 = new Bundle();
                    page1.putParcelableArrayList("checkmarks", corCheck);
                    page1.putStringArrayList("arrText", arrText);
                    page1.putParcelableArrayList("corText", corText);
                    pages.putBundle("page1", page1);
                    intent.putExtra("pages", pages);

                    intent.putExtra("report", "2018_id37_yearly.pdf");

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", destArray);
                    startActivityForResult(intent, 1);
                }
            }

            private boolean checkStatus() {
                if (!Helper.isValidString(t11.getText().toString()))
                    return false;
                if (!Helper.isValidString(t12.getText().toString()))
                    return false;
                if (!Helper.isValidString(t3.getText().toString()))
                    return false;
                if (!Helper.isTempValid(t4, MIN_TEMP, MAX_TEMP))
                    return false;
                if (!Helper.isTimeValid(t5, EXPECTED_TIME))
                    return false;
                if (!Helper.isValidString(t6.getText().toString()))
                    return false;
                if (!fanSwitch.isChecked())
                    return false;
                if (!rubberSwitch.isChecked())
                    return false;
                return true;

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
