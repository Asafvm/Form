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

public class GelstationActivity extends AppCompatActivity {
    private final double VOLT_THRESHOLD = 0.3;
    private final int VACCUM_MIN = 600;
    private final int PRESSURE_MIN = 180;
    private int EXPECTED_SPEED = 1000;
    private final int EXPECTED_TEMP_HIGH = 37;
    private final int EXPECTED_TEMP_LOW = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.generic_device_activity);
        Helper h = new Helper();
        h.setLayout(this, R.layout.gelstation_layout);


        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        final String thermometer = bundle.getString("thermometer");
        final String speedometer = bundle.getString("speedometer");
        final String barometer = bundle.getString("barometer");


        Helper helper = new Helper();
        init();
        helper.setListener((EditText) findViewById(R.id.formTechName));
        ((EditText) findViewById(R.id.formTechName)).setText(techname);


        //default basic values
        ((EditText) findViewById(R.id.formTechName)).setText(techname);


        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1", getPage1corText());
                    pages.putParcelableArrayList("page2", getPage2corText());
                    pages.putParcelableArrayList("page3", getPage3corText());


                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_gelstation_yearly.pdf");
                    intent.putExtra("pages", pages);
                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " " +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            ((DatePicker) findViewById(R.id.formDate)).getYear() + "" +
                            ((DatePicker) findViewById(R.id.formDate)).getDayOfMonth() + "" +
                            ((DatePicker) findViewById(R.id.formDate)).getMonth() +
                            "_Gelstation_" + ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");
                    startActivityForResult(intent, 1);
                }

            }

            private ArrayList<Tuple> getPage1corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(460, 632, ((DatePicker) findViewById(R.id.formDate)).getMonth() + "   " + (((DatePicker) findViewById(R.id.formDate)).getYear() + 1), false));                        //Next Date
                corText.add(new Tuple(90, 661, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " + ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                corText.add(new Tuple(390, 661, ((DatePicker) findViewById(R.id.formDate)).getDayOfMonth() + "    " + ((DatePicker) findViewById(R.id.formDate)).getMonth() + "    " + ((DatePicker) findViewById(R.id.formDate)).getYear(), false));                        //Date
                corText.add(new Tuple(135, 632, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                //corText.add(new Tuple(380,30));                        //Signature
                corText.add(new Tuple(344, 543, "", false));           //temp ok
                corText.add(new Tuple(344, 525, "", false));           //time ok
                corText.add(new Tuple(344, 507, "", false));           //fan ok

                corText.add(new Tuple(292, 468, "3.18", false));           //rubber ok
                corText.add(new Tuple(344, 470, "", false));           //rubber ok
                corText.add(new Tuple(394, 452, "XP SP3", false));           //overall ok
                corText.add(new Tuple(344, 452, "", false));           //overall ok
                corText.add(new Tuple(394, 432, "C:\\300GB\tD:\\100GB", false));           //rubber ok
                corText.add(new Tuple(344, 434, "", false));           //rubber ok
                corText.add(new Tuple(344, 416, "", false));           //overall ok

                corText.add(new Tuple(344, 379, "", false));           //rubber ok
                corText.add(new Tuple(292, 359, "24v", false));           //overall ok
                corText.add(new Tuple(344, 361, "", false));           //overall ok
                corText.add(new Tuple(292, 341, "8v", false));           //rubber ok
                corText.add(new Tuple(344, 343, "", false));           //rubber ok
                corText.add(new Tuple(292, 323, "12v", false));           //overall ok
                corText.add(new Tuple(344, 325, "", false));           //overall ok
                corText.add(new Tuple(344, 307, "", false));           //rubber ok

                corText.add(new Tuple(344, 265, "", false));           //overall ok
                corText.add(new Tuple(344, 235, "", false));           //overall ok
                corText.add(new Tuple(344, 214, "", false));           //rubber ok
                corText.add(new Tuple(344, 198, "", false));           //overall ok
                corText.add(new Tuple(344, 180, "", false));           //rubber ok
                corText.add(new Tuple(344, 162, "", false));           //overall ok
                corText.add(new Tuple(344, 140, "", false));           //rubber ok
                corText.add(new Tuple(344, 117, "", false));           //rubber ok
                corText.add(new Tuple(344, 88, "", false));           //rubber ok
                return corText;
            }


            private ArrayList<Tuple> getPage2corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(420, 706, thermometer, false));           //overall ok
                corText.add(new Tuple(320, 706, "", false));           //overall ok
                corText.add(new Tuple(320, 688, "", false));           //rubber ok
                corText.add(new Tuple(320, 670, "", false));           //overall ok
                corText.add(new Tuple(320, 652, "", false));           //rubber ok
                corText.add(new Tuple(320, 634, "", false));           //overall ok


                corText.add(new Tuple(320, 598, "", false));           //overall ok
                corText.add(new Tuple(320, 580, "", false));           //rubber ok
                corText.add(new Tuple(320, 562, "", false));           //rubber ok
                corText.add(new Tuple(320, 543, "", false));           //temp ok
                corText.add(new Tuple(320, 525, "", false));           //time ok
                corText.add(new Tuple(272, 525, "991", false));           //time ok
                corText.add(new Tuple(420, 521, speedometer, false));           //overall ok
                corText.add(new Tuple(320, 507, "", false));           //fan ok

                corText.add(new Tuple(320, 470, "", false));           //rubber ok
                corText.add(new Tuple(320, 452, "", false));           //overall ok
                corText.add(new Tuple(320, 434, "", false));           //rubber ok
                corText.add(new Tuple(320, 416, "", false));           //overall ok
                corText.add(new Tuple(320, 398, "", false));           //overall ok
                corText.add(new Tuple(320, 378, "", false));           //rubber ok
                corText.add(new Tuple(320, 361, "", false));           //overall ok
                corText.add(new Tuple(320, 343, "", false));           //rubber ok
                corText.add(new Tuple(320, 324, "", false));           //overall ok
                corText.add(new Tuple(320, 306, "", false));           //rubber ok
                corText.add(new Tuple(320, 288, "", false));           //overall ok
                corText.add(new Tuple(320, 266, "", false));           //overall ok

                corText.add(new Tuple(320, 218, "", false));           //rubber ok
                corText.add(new Tuple(320, 200, "", false));           //overall ok
                corText.add(new Tuple(320, 182, "", false));           //rubber ok
                corText.add(new Tuple(320, 161, "", false));           //overall ok
                corText.add(new Tuple(420, 161, barometer, false));           //overall ok
                corText.add(new Tuple(272, 161, "700", false));           //overall ok
                corText.add(new Tuple(272, 129, "180", false));           //rubber ok
                corText.add(new Tuple(320, 129, "", false));           //rubber ok
                corText.add(new Tuple(320, 99, "", false));           //rubber ok

                return corText;
            }


            private ArrayList<Tuple> getPage3corText() {
                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(320, 686, "", false));           //rubber ok
                corText.add(new Tuple(320, 668, "", false));           //overall ok
                corText.add(new Tuple(320, 650, "", false));           //rubber ok
                corText.add(new Tuple(320, 632, "", false));           //overall ok

                corText.add(new Tuple(320, 595, "", false));           //overall ok
                corText.add(new Tuple(320, 577, "", false));           //rubber ok
                corText.add(new Tuple(320, 559, "", false));           //rubber ok

                corText.add(new Tuple(320, 522, "", false));           //time ok

                corText.add(new Tuple(320, 452, "", false));           //temp ok
                corText.add(new Tuple(320, 480, "", false));           //fan ok

                corText.add(new Tuple(100, 95, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                corText.add(new Tuple(465, 95, "!", false));                        //Signature

                return corText;
            }

            private boolean checkStatus() {
                return isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()) && isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()) && isValidString(((EditText) findViewById(R.id.formTechName)).getText().toString());

            }
        });
    }

    private void init() {
        Helper h = new Helper();
        h.setListener(((EditText) findViewById(R.id.formMainLocation)));
        h.setListener(((EditText) findViewById(R.id.formRoomLocation)));
        h.setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        h.setListener(((EditText) findViewById(R.id.formTechName)));
        h.setSpeedListener(((EditText) findViewById(R.id.etGScentrifugation)), EXPECTED_SPEED);
        h.setTempListener(((EditText) findViewById(R.id.etGSincubator1_37)), EXPECTED_TEMP_HIGH - 2, EXPECTED_TEMP_HIGH + 2);
        h.setTempListener(((EditText) findViewById(R.id.etGSincubator2_37)), EXPECTED_TEMP_HIGH - 2, EXPECTED_TEMP_HIGH + 2);
        h.setTempListener(((EditText) findViewById(R.id.etGSincubator1_25)), EXPECTED_TEMP_LOW - 2, EXPECTED_TEMP_LOW + 2);
        h.setTempListener(((EditText) findViewById(R.id.etGSincubator2_25)), EXPECTED_TEMP_LOW - 2, EXPECTED_TEMP_LOW + 2);
        h.setVoltListener(((EditText) findViewById(R.id.etGScommon24)), 24, VOLT_THRESHOLD);
        h.setVoltListener(((EditText) findViewById(R.id.etGScommon8)), 8, VOLT_THRESHOLD);
        h.setVoltListener(((EditText) findViewById(R.id.etGScommon12)), 12, VOLT_THRESHOLD);
        h.setBarListener(((EditText) findViewById(R.id.etGSfluidVaccum)), VACCUM_MIN);
        h.setBarListener(((EditText) findViewById(R.id.etGSfluidPressure)), PRESSURE_MIN);
        h.setListener(((EditText) findViewById(R.id.etGSsoftwareD)));
        h.setListener(((EditText) findViewById(R.id.etGSsoftwareC)));
        h.setListener(((EditText) findViewById(R.id.etGSsoftware1)));


        ((EditText) findViewById(R.id.formMainLocation)).setText("");
        ((EditText) findViewById(R.id.formRoomLocation)).setText("");
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");

        ((EditText) findViewById(R.id.etGSsoftware1)).setText("3.18");
        ((RadioGroup) findViewById(R.id.rgGSsoftware1)).check(R.id.rbGSxpsp3);

        ((EditText) findViewById(R.id.etGScommon24)).setText("");
        ((EditText) findViewById(R.id.etGScommon8)).setText("");
        ((EditText) findViewById(R.id.etGScommon12)).setText("");

        ((EditText) findViewById(R.id.etGScentrifugation)).setText(String.valueOf(EXPECTED_SPEED));

        ((EditText) findViewById(R.id.etGSincubator1_37)).setText("");
        ((EditText) findViewById(R.id.etGSincubator2_37)).setText("");
        ((EditText) findViewById(R.id.etGSincubator1_25)).setText("");
        ((EditText) findViewById(R.id.etGSincubator2_25)).setText("");

        ((EditText) findViewById(R.id.etGSsoftwareD)).setText("");
        ((EditText) findViewById(R.id.etGSsoftwareC)).setText("");
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
