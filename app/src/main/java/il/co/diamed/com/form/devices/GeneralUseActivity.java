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


        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        //get views
        final Button btn = findViewById(R.id.formSubmitButton);

        final EditText t11 = findViewById(R.id.formMainLocation);
        final EditText t12 = findViewById(R.id.formRoomLocation);
        final RadioGroup t2 = findViewById(R.id.rgModelSelect);
        final EditText t3 = findViewById(R.id.etDeviceSerial);
        final EditText t4 = findViewById(R.id.etVer);
        final EditText t5 = findViewById(R.id.etNewVer);
        final EditText t6 = findViewById(R.id.formTechName);
        final DatePicker dp = findViewById(R.id.formDate);

        final Switch cleanSwitch = findViewById(R.id.generalCleaningSwitch);
        final Switch verSwitch = findViewById(R.id.verUpdateSwitch);
        final Switch selfTestSwitch = findViewById(R.id.selfTextSwitch);
        //default basic values
        t2.check(bundle.getInt("type"));
        cleanSwitch.setChecked(true);
        selfTestSwitch.setChecked(true);
        verSwitch.setChecked(false);
        t6.setText(techname);

        h.setListener(t11);
        h.setListener(t12);
        h.setListener(t3);
        h.setListener(t4);
        h.setListener(t5);
        h.setListener(t6);
        setListener(verSwitch);


        btn.setOnClickListener(new View.OnClickListener()

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

                    corText.add(new Tuple(100, 663, t11.getText().toString() + " - " + t12.getText().toString(), true));                        //Location
                    corText.add(new Tuple(100, 140, t6.getText().toString(), true));                        //Tech Name
                    corText.add(new Tuple(310, 663, dp.getDayOfMonth() + "    " + dp.getMonth() + "    " + dp.getYear(), false));                        //Date
                    corText.add(new Tuple(455, 633, ((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString(), false));                        //type
                    corText.add(new Tuple(125, 633, t3.getText().toString(), false));                        //Serial
                    corText.add(new Tuple(450, 487, t4.getText().toString(), false));                        //ver
                    corText.add(new Tuple(520, 663, dp.getMonth() + "   " + (dp.getYear() + 1), false));                        //Next Date
                    if (verSwitch.isChecked()) {
                        corText.add(new Tuple(292, 470, t5.getText().toString(), false));
                    }
                    corText.add(new Tuple(435, 130, "!", false));                        //Signature

                    ArrayList<String> destArray = new ArrayList<>();
                    destArray.add(t11.getText().toString() + "_" + t12.getText().toString());
                    destArray.add(String.valueOf(dp.getYear()));
                    destArray.add(String.valueOf(dp.getMonth()));
                    destArray.add(String.valueOf(dp.getDayOfMonth()));
                    destArray.add(t3.getText().toString());

                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_general_yearly.pdf");

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1",corText);
                    intent.putExtra("pages", pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", t11.getText().toString()+" "+t12.getText().toString()+"/"+dp.getYear()+""+dp.getDayOfMonth()+""+dp.getMonth()+"_"+
                            "_"+((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString()+"_"+t3.getText().toString()+".pdf");
                    startActivityForResult(intent, 1);
                }

            }


            private boolean checkStatus() {
                if (!isValidString(t11.getText().toString()))
                    return false;
                if (!isValidString(t12.getText().toString()))
                    return false;
                if (!isValidString(t3.getText().toString()))
                    return false;
                if (!isValidString(t4.getText().toString()))
                    return false;
                if (verSwitch.isChecked()) {
                    if (!isValidString(t5.getText().toString()))
                        return false;
                }
                return isValidString(t6.getText().toString()) && cleanSwitch.isChecked() && selfTestSwitch.isChecked();

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

