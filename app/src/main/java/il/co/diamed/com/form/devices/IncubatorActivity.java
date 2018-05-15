package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Objects;

import il.co.diamed.com.form.res.providers.AnalyticsEventItem;
import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.PDFActivity;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.Tuple;

public class IncubatorActivity extends AppCompatActivity {
    private static final String TAG = "IncubatorActivity";
    private final int MAX_TEMP = 39;
    private final int MIN_TEMP = 35;
    private final int EXPECTED_TIME = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_device_activity);
        final Helper h = new Helper();
        h.setLayout(this, R.layout.device_incubator_layout);

        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        final String thermometer = bundle.getString("thermometer");
        final String timer = bundle.getString("timer");
        //get views
        init();
        h.setListener(((EditText)findViewById(R.id.formTechName)));
        ((EditText)findViewById(R.id.formTechName)).setText(techname);


        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    String day = h.fixDate(((DatePicker)findViewById(R.id.formDate)).getDayOfMonth());
                    String month = h.fixDate(((DatePicker)findViewById(R.id.formDate)).getMonth());

                    ArrayList<Tuple> corText = new ArrayList<>();
                    corText.add(new Tuple(205, 469, "", false));           //temp ok
                    corText.add(new Tuple(203, 329, "", false));           //time ok
                    corText.add(new Tuple(251, 230, "", false));           //fan ok
                    corText.add(new Tuple(251, 212, "", false));           //rubber ok
                    corText.add(new Tuple(482, 97, "", false));           //overall ok

                    corText.add(new Tuple(300, 636, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " + 
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                    corText.add(new Tuple(330, 30, ((EditText)findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                    corText.add(new Tuple(72, 636, day + "       " +month + "         " +
                            ((DatePicker)findViewById(R.id.formDate)).getYear(), false));                        //Date
                    corText.add(new Tuple(290, 568, ((RadioButton) findViewById(((RadioGroup)findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString(), false));                        //type
                    corText.add(new Tuple(425, 568, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                    corText.add(new Tuple(275, 465, ((EditText) findViewById(R.id.temp)).getText().toString(), false));                        //temp
                    corText.add(new Tuple(305, 325, ((EditText) findViewById(R.id.time)).getText().toString(), false));                        //Time
                    corText.add(new Tuple(451, 65, ((DatePicker)findViewById(R.id.formDate)).getMonth() + "   " + 
                            (((DatePicker)findViewById(R.id.formDate)).getYear() + 1), false));                        //Next Date

                    corText.add(new Tuple(330, 425, thermometer, false));                        //Thermometer
                    corText.add(new Tuple(400, 285, timer, false));                        //Timer

                    corText.add(new Tuple(135, 33, "!", false));                        //Signature

                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);

                    Bundle pages = new Bundle();
                    Bundle page1 = new Bundle();
                    pages.putParcelableArrayList("page1",corText);
                    intent.putExtra("pages", pages);

                    intent.putExtra("report", "2018_id37_yearly.pdf");

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString()+"/"+
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString()+"/"+
                            ((DatePicker)findViewById(R.id.formDate)).getYear()+""+
                            month+""+ day+"_"+"Incubator-"+
                            ((RadioButton) findViewById(((RadioGroup)findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString()+"_"+
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()+".pdf");
                    startActivityForResult(intent, 1);
                }
            }

            private boolean checkStatus() {
                return Helper.isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()) && 
                        Helper.isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()) &&
                        Helper.isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()) &&
                        Helper.isTempValid(((EditText) findViewById(R.id.temp)), MIN_TEMP, MAX_TEMP) &&
                        Helper.isTimeValid(((EditText) findViewById(R.id.time)), EXPECTED_TIME) && 
                        Helper.isValidString(((EditText)findViewById(R.id.formTechName)).getText().toString()) &&
                        ((Switch)findViewById(R.id.incRubberSwitch)).isChecked() &&
                        ((Switch)findViewById(R.id.incFanSwitch)).isChecked();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ClassApplication  application = (ClassApplication) getApplication();
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                application.logAnalyticsEvent(new AnalyticsEventItem(
                        "Creating Incubator Report",
                        ((EditText)findViewById(R.id.formTechName)).getText().toString(),
                        this.getCallingActivity().getShortClassName(),
                        AnalyticsEventItem.PDF_CREATED,
                        "none"));
                Toast.makeText(this, R.string.pdfSuccess, Toast.LENGTH_SHORT).show();
                doAnother();
                setResult(RESULT_OK);
            } else {
                application.logAnalyticsEvent(new AnalyticsEventItem(
                        "Creating Incubator Report",
                        ((EditText)findViewById(R.id.formTechName)).getText().toString(),
                        this.getCallingActivity().getShortClassName(),
                        AnalyticsEventItem.PDF_FAILED,
                        "none"));
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
        h.setTempListener(((EditText) findViewById(R.id.temp)),MIN_TEMP,MAX_TEMP);
        h.setTimeListener(((EditText) findViewById(R.id.time)),EXPECTED_TIME);

        ((RadioGroup) findViewById(R.id.rgModelSelect)).check(R.id.si);
        ((EditText) findViewById(R.id.formMainLocation)).setText("");
        ((EditText) findViewById(R.id.formRoomLocation)).setText("");
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");
        ((EditText) findViewById(R.id.temp)).setText(String.valueOf(""));
        ((EditText) findViewById(R.id.time)).setText(String.valueOf(EXPECTED_TIME));

        ((Switch)findViewById(R.id.incRubberSwitch)).setChecked(true);
        ((Switch)findViewById(R.id.incFanSwitch)).setChecked(true);

    }
}
