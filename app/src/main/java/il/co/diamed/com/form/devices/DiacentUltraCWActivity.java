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

public class DiacentUltraCWActivity extends AppCompatActivity {

    private static final int EXPECTED_CW_SPEED = 2500;
    private static final int EXPECTED_CW_TIME = 60;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_device_activity);
        Helper h = new Helper();
        h.setLayout(this, R.layout.device_diacentcw_layout);


        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techName = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        final String speedometer = bundle.getString("speedometer");
        final String timer = bundle.getString("timer");

        initDiacentCW();
        ((EditText) findViewById(R.id.formTechName)).setText(techName);

        (findViewById(R.id.formSubmitButton)).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    ArrayList<Tuple> corText;


                    corText = getDiacentCWTextCor();
                    intent.putExtra("report", "2018_ultracw_yearly.pdf");

                    Bundle pages = new Bundle();

                    pages.putParcelableArrayList("page1", corText);
                    intent.putExtra("pages", pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " " +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            ((DatePicker) findViewById(R.id.formDate)).getYear() + "" +
                            ((DatePicker) findViewById(R.id.formDate)).getDayOfMonth() + "" +
                            ((DatePicker) findViewById(R.id.formDate)).getMonth() + "_UltraCW_" +
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");
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

                corText.add(new Tuple(303, 650, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                        ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                corText.add(new Tuple(330, 30, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                corText.add(new Tuple(74, 650, ((DatePicker) findViewById(R.id.formDate)).getDayOfMonth() + "     " +
                        ((DatePicker) findViewById(R.id.formDate)).getMonth() + "     " +
                        ((DatePicker) findViewById(R.id.formDate)).getYear(), false));                        //Date

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


            private boolean checkStatus() {
                return isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()) &&
                        isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()) &&
                        isSpeedValid(Integer.valueOf((((EditText) findViewById(R.id.centcwSpeed2500)).getText().toString())), EXPECTED_CW_SPEED) &&
                        isTimeValid(((EditText) findViewById(R.id.centCWtime)), EXPECTED_CW_TIME) &&
                        ((Switch) findViewById(R.id.centCheckHolders)).isChecked() &&
                        ((Switch) findViewById(R.id.centCheckRemaining)).isChecked() &&
                        ((Switch) findViewById(R.id.centCheckFilling)).isChecked() &&
                        isValidString(((EditText) findViewById(R.id.formTechName)).getText().toString());

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
                initDiacentCW();
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


        Helper h = new Helper();
        h.setListener(((EditText) findViewById(R.id.formMainLocation)));
        h.setListener(((EditText) findViewById(R.id.formRoomLocation)));
        h.setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        h.setListener(((EditText) findViewById(R.id.formTechName)));
        ((EditText) findViewById(R.id.formMainLocation)).setText("");
        ((EditText) findViewById(R.id.formRoomLocation)).setText("");
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");


        ((EditText) findViewById(R.id.centcwSpeed2500)).setText("");
        ((EditText) findViewById(R.id.centCWtime)).setText(R.string.time60);
        ((Switch) findViewById(R.id.centCheckHolders)).setChecked(true);
        ((Switch) findViewById(R.id.centCheckRemaining)).setChecked(true);
        ((Switch) findViewById(R.id.centCheckFilling)).setChecked(true);

    }


}
