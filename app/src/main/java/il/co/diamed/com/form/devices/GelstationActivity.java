package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import il.co.diamed.com.form.res.helper;

public class GelstationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_gelstation);
        helper h = new helper();
        h.setLayout(this,R.layout.gelstation_layout);


        Bundle bundle = getIntent().getExtras();
        //Get preferrences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String techname = sharedPref.getString("techName", "");
        final String signature = sharedPref.getString("signature", "");

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

        /*        t2.check(bundle.getInt("type"));
        cleanSwitch.setChecked(true);
        selfTestSwitch.setChecked(true);
        verSwitch.setChecked(false);
        t6.setText(techname);

        setListener(t11);
        setListener(t12);
        setListener(t3);
        setListener(t4);
        setListener(t5);
        setListener(t6);
        setListener(t2);
        setListener(cleanSwitch);
        setListener(selfTestSwitch);
        setListener(verSwitch);
*/


        btn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {


                if (checkStatus()) {


                    ArrayList<Tuple> corCheck = new ArrayList<>();
                    //       corCheck.add(new Tuple(116,467));           //temp not ok
                    corCheck.add(new Tuple(355, 543));           //temp ok
                    //       corCheck.add(new Tuple(114,327));           //time not ok
                    corCheck.add(new Tuple(355, 525));           //time ok
                    //       corCheck.add(new Tuple(155,228));           //fan not ok
                    corCheck.add(new Tuple(355, 488));           //fan ok
                    //       corCheck.add(new Tuple(155,210));           //rubber not ok
                    corCheck.add(new Tuple(355, 470));           //rubber ok
                    //       corCheck.add(new Tuple(232,95));           //overall not ok
                    corCheck.add(new Tuple(355, 452));           //overall ok

                    ArrayList<Tuple> corText = new ArrayList<>();

                    corText.add(new Tuple(100, 663));                        //Location
                    corText.add(new Tuple(100, 140));                        //Tech Name
                    corText.add(new Tuple(310, 663));                        //Date
                    corText.add(new Tuple(455, 633));                        //type
                    corText.add(new Tuple(125, 633));                        //Serial
                    corText.add(new Tuple(450, 487));                        //ver

                    corText.add(new Tuple(510, 663));                        //Next Date


                    //corText.add(new Tuple(380,30));                        //Signature

                    ArrayList<String> arrText = new ArrayList<>();


                    arrText.add(t11.getText().toString() + " - " + t12.getText().toString());                                       //Location
                    arrText.add(t6.getText().toString());                        //Tech Name
                    arrText.add(dp.getDayOfMonth() + "    " + dp.getMonth() + "    " + dp.getYear());                      //Date
                    arrText.add(((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString());     //type
                    arrText.add(t3.getText().toString());                        //Serial
                    arrText.add(t4.getText().toString());                        //ver

                    arrText.add(dp.getMonth() + "   " + (dp.getYear() + 1));           //Next Date


                    ArrayList<String> destArray = new ArrayList<>();
                    destArray.add(t11.getText().toString() + "_" + t12.getText().toString());
                    destArray.add(String.valueOf(dp.getYear()));
                    destArray.add(String.valueOf(dp.getMonth()));
                    destArray.add(String.valueOf(dp.getDayOfMonth()));
                    destArray.add(t3.getText().toString());


                    if (verSwitch.isChecked()) {
                        corText.add(new Tuple(295, 470));
                        arrText.add(t5.getText().toString());
                    }

                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                    intent.putExtra("report", "2018_general_yearly.pdf");

                    Bundle pages = new Bundle();
                    Bundle page1 = new Bundle();
                    page1.putParcelableArrayList("checkmarks", corCheck);
                    page1.putStringArrayList("arrText", arrText);
                    page1.putParcelableArrayList("corText", corText);
                    pages.putBundle("page1", page1);
                    intent.putExtra("pages", pages);

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", destArray);
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
                if (!isValidString(t6.getText().toString()))
                    return false;
                if (!cleanSwitch.isChecked())
                    return false;
                if (!selfTestSwitch.isChecked())
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

    private void setListener(RadioGroup rg) {
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

            }
        });
    }

    void setListener(final EditText et) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (isValidString(et.getText().toString())) {
                    et.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    et.setBackgroundColor(Color.RED);
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (isValidString(et.getText().toString())) {
                    et.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    et.setBackgroundColor(Color.RED);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (isValidString(et.getText().toString())) {
                    et.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    et.setBackgroundColor(Color.RED);
                }
            }
        });
    }

    private boolean isValidString(String s) {
        if (s.equals("") || s == null)
            return false;
        else
            return true;
    }



    public void setLayout(int resLayout) {

        View lowLayout = findViewById(R.id.lowLayout);
        ViewGroup parent = (ViewGroup) lowLayout.getParent();
        int index = parent.indexOfChild(lowLayout);
        parent.removeView(lowLayout);
        lowLayout = getLayoutInflater().inflate(resLayout, parent, false);
        parent.addView(lowLayout, index);

        helper.setButtons((ViewGroup)lowLayout);

    }

}
