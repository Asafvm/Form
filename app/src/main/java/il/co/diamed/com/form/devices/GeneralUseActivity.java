package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.Date;
import java.util.Objects;

import il.co.diamed.com.form.PDFActivity;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.Tuple;

import static il.co.diamed.com.form.devices.Helper.isValidString;

public class GeneralUseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_use);
        Helper h = new Helper();
        h.setLayout(this, R.layout.general_layout);
        h.setListener((EditText) findViewById(R.id.formTechName));

        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        //get views
        init();
        ((EditText) findViewById(R.id.formTechName)).setText(techname);
        ((RadioGroup) findViewById(R.id.rgModelSelect)).check(bundle.getInt("type"));

        //default basic values


        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    ArrayList<Tuple> corText = new ArrayList<>();
                    corText.add(new Tuple(355, 543, "", false));           //temp ok
                    corText.add(new Tuple(355, 525, "", false));           //time ok
                    corText.add(new Tuple(355, 488, "", false));           //fan ok
                    corText.add(new Tuple(355, 470, "", false));           //rubber ok
                    corText.add(new Tuple(355, 452, "", false));           //overall ok

                    corText.add(new Tuple(100, 663, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " + ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));             //Location
                    corText.add(new Tuple(100, 140, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));            //Tech Name
                    corText.add(new Tuple(310, 663, ((DatePicker) findViewById(R.id.formDate)).getDayOfMonth() + "    " +
                            ((DatePicker) findViewById(R.id.formDate)).getMonth() + "    " +
                            ((DatePicker) findViewById(R.id.formDate)).getYear(), false));                        //Date
                    corText.add(new Tuple(455, 633, ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString(), false));                        //type
                    corText.add(new Tuple(125, 633, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                    corText.add(new Tuple(450, 487, ((EditText) findViewById(R.id.etVer)).getText().toString(), false));                        //ver
                    corText.add(new Tuple(520, 663, ((DatePicker) findViewById(R.id.formDate)).getMonth() + "   " +
                            (((DatePicker) findViewById(R.id.formDate)).getYear() + 1), false));                        //Next Date
                    if (((Switch)findViewById(R.id.verUpdateSwitch)).isChecked()) {
                        corText.add(new Tuple(292, 470, ((EditText) findViewById(R.id.etNewVer)).getText().toString(), false));
                    }
                    corText.add(new Tuple(435, 130, "!", false));                        //Signature

                    ArrayList<String> destArray = new ArrayList<>();
                    destArray.add(((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "_" + ((EditText) findViewById(R.id.formRoomLocation)).getText().toString());
                    destArray.add(String.valueOf(((DatePicker) findViewById(R.id.formDate)).getYear()));
                    destArray.add(String.valueOf(((DatePicker) findViewById(R.id.formDate)).getMonth()));
                    destArray.add(String.valueOf(((DatePicker) findViewById(R.id.formDate)).getDayOfMonth()));
                    destArray.add(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString());

                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_general_yearly.pdf");

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1",corText);
                    intent.putExtra("pages", pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString()+" "+((EditText) findViewById(R.id.formRoomLocation)).getText().toString()+"/"+
                            ((DatePicker) findViewById(R.id.formDate)).getYear()+""+
                            ((DatePicker) findViewById(R.id.formDate)).getDayOfMonth()+""+
                            ((DatePicker) findViewById(R.id.formDate)).getMonth()+"_"+
                            ((RadioButton) findViewById(((RadioGroup) findViewById(R.id.rgModelSelect)).getCheckedRadioButtonId())).getText().toString()+"_"+
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()+".pdf");
                    startActivityForResult(intent, 1);
                }

            }


            private boolean checkStatus() {
                if (!isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()))
                    return false;
                if (!isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()))
                    return false;
                if (!isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()))
                    return false;
                if (!isValidString(((EditText) findViewById(R.id.etVer)).getText().toString()))
                    return false;
                if (((Switch)findViewById(R.id.verUpdateSwitch)).isChecked()) {
                    if (!isValidString(((EditText) findViewById(R.id.etNewVer)).getText().toString()))
                        return false;
                }
                return isValidString(((EditText)findViewById(R.id.formTechName)).getText().toString()) &&
                        ((Switch)findViewById(R.id.generalCleaningSwitch)).isChecked() &&
                        ((Switch)findViewById(R.id.selfTextSwitch)).isChecked();
            }
        });
    }

    private void init() {
        Helper h = new Helper();
        h.setListener(((EditText) findViewById(R.id.formMainLocation)));
        h.setListener(((EditText) findViewById(R.id.formRoomLocation)));
        h.setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        h.setListener(((EditText) findViewById(R.id.etVer)));
        h.setListener(((EditText) findViewById(R.id.etNewVer)));
        setListener(((Switch)findViewById(R.id.verUpdateSwitch)));

        ((Switch)findViewById(R.id.generalCleaningSwitch)).setChecked(true);
        ((Switch)findViewById(R.id.selfTextSwitch)).setChecked(true);
        ((Switch)findViewById(R.id.verUpdateSwitch)).setChecked(true);
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
                EditText et = findViewById(R.id.etNewVer);
                et.setVisibility(View.INVISIBLE);
                et.setText("");
                et = findViewById(R.id.etVer);
                et.setText("");
                et = findViewById(R.id.etDeviceSerial);
                et.setText("");
                Switch s = findViewById(R.id.verUpdateSwitch);
                s.setChecked(false);
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

    private void setListener(Switch s) {
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.getId() == R.id.verUpdateSwitch) {
                    if (compoundButton.isChecked()) {
                        findViewById(R.id.etNewVer).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.etNewVer).setVisibility(View.INVISIBLE);
                    }

                }
            }
        });
    }



}

