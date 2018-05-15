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
import java.util.Date;
import java.util.Objects;

import il.co.diamed.com.form.PDFActivity;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.Tuple;

public class DoconActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_device_activity);
        final Helper h = new Helper();
        h.setLayout(this, R.layout.device_docon_layout);

        Bundle bundle = Objects.requireNonNull(getIntent().getExtras()).getBundle("cal");
        final String techname = Objects.requireNonNull(bundle).getString("techName");
        final String signature = bundle.getString("signature");
        //get views
        init();
        h.setListener(((EditText) findViewById(R.id.formTechName)));
        ((EditText) findViewById(R.id.formTechName)).setText(techname);


        findViewById(R.id.formSubmitButton).setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (checkStatus()) {
                    String day = h.fixDate(((DatePicker)findViewById(R.id.formDate)).getDayOfMonth());
                    String month = h.fixDate(((DatePicker)findViewById(R.id.formDate)).getMonth());
                    ArrayList<Tuple> corText = new ArrayList<>();
                    corText.add(new Tuple(310, 662, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                    //corText.add(new Tuple(330, 30, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                    corText.add(new Tuple(144, 686, ((DatePicker) findViewById(R.id.formDate)).getDayOfMonth() + " / " +
                            ((DatePicker) findViewById(R.id.formDate)).getMonth() + " / " +
                            ((DatePicker) findViewById(R.id.formDate)).getYear(), false));                        //Date
                    corText.add(new Tuple(120, 607, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                    corText.add(new Tuple(390, 525, ((EditText) findViewById(R.id.etDoconW200)).getText().toString(), false));                        //temp
                    corText.add(new Tuple(390, 495, ((EditText) findViewById(R.id.etDoconW500)).getText().toString(), false));                        //temp
                    corText.add(new Tuple(390, 465, ((EditText) findViewById(R.id.etDoconW700)).getText().toString(), false));                        //temp
                    corText.add(new Tuple(338, 686, ((EditText) findViewById(R.id.etDoconReport)).getText().toString(), false));                        //Time
                    corText.add(new Tuple(421, 158, ((DatePicker) findViewById(R.id.formDate)).getMonth() + " / " +
                            (((DatePicker) findViewById(R.id.formDate)).getYear() + 1), false));                        //Next Date

                    //corText.add(new Tuple(135, 33, "!", false));                        //Signature

                    Intent intent = new Intent(getBaseContext(), PDFActivity.class);

                    Bundle pages = new Bundle();
                    pages.putParcelableArrayList("page1", corText);
                    intent.putExtra("pages", pages);

                    intent.putExtra("report", "2018_docon_yearly.pdf");

                    intent.putExtra("signature", signature);
                    intent.putExtra("destArray", ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + "/" +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString() + "/" +
                            ((DatePicker) findViewById(R.id.formDate)).getYear() + "" +
                            month + "" + day + "_Docon_" +
                            ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString() + ".pdf");
                    startActivityForResult(intent, 1);
                }
            }

            private boolean checkStatus() {
                return Helper.isValidString(((EditText) findViewById(R.id.formMainLocation)).getText().toString()) &&
                        Helper.isValidString(((EditText) findViewById(R.id.formRoomLocation)).getText().toString()) &&
                        Helper.isValidString(((EditText) findViewById(R.id.etDeviceSerial)).getText().toString()) &&
                        Helper.isValidString(((EditText) findViewById(R.id.formTechName)).getText().toString());

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
    }

    private void init() {
        Helper h = new Helper();
        h.setListener(((EditText) findViewById(R.id.formMainLocation)));
        h.setListener(((EditText) findViewById(R.id.formRoomLocation)));
        h.setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        h.setListener(((EditText) findViewById(R.id.formTechName)));

        ((EditText) findViewById(R.id.formMainLocation)).setText("");
        ((EditText) findViewById(R.id.formRoomLocation)).setText("");
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");

        DatePicker d = new DatePicker(this);
        ((EditText) findViewById(R.id.etDoconReport)).setText(d.getYear()+""+d.getMonth()+""+d.getDayOfMonth()+"_"+
                ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString());

    }
}

