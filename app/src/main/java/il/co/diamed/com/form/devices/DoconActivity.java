package il.co.diamed.com.form.devices;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import il.co.diamed.com.form.PDFActivity;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.res.Tuple;

public class DoconActivity extends DevicePrototypeActivity {
    private Helper h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_device_activity);
        h = new Helper();
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
                    findViewById(R.id.pbPDF).setVisibility(View.VISIBLE);
                    DatePicker dp = findViewById(R.id.formDate);
                    String day = h.fixDay(dp.getDayOfMonth());
                    String month = h.fixMonth(dp.getMonth());
                    Date date = new Date();
                    ArrayList<Tuple> corText = new ArrayList<>();
                    corText.add(new Tuple(320, 662, ((EditText) findViewById(R.id.formMainLocation)).getText().toString() + " - " +
                            ((EditText) findViewById(R.id.formRoomLocation)).getText().toString(), true));                        //Location
                    corText.add(new Tuple(310, 117, ((EditText) findViewById(R.id.formTechName)).getText().toString(), true));                        //Tech Name
                    corText.add(new Tuple(150, 686, day + "/" + month + "/" + dp.getYear(), false));                        //Date
                    corText.add(new Tuple(90, 607, ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString(), false));                        //Serial
                    corText.add(new Tuple(390, 607, "MDA V"+((EditText) findViewById(R.id.etDoconVer)).getText().toString(), false));                        //Serial
                    corText.add(new Tuple(405, 527, ((EditText) findViewById(R.id.etDoconW200)).getText().toString(), false));                        //temp
                    corText.add(new Tuple(405, 498, ((EditText) findViewById(R.id.etDoconW500)).getText().toString(), false));                        //temp
                    corText.add(new Tuple(405, 469, ((EditText) findViewById(R.id.etDoconW700)).getText().toString(), false));                        //temp
                    corText.add(new Tuple(338, 686,
                            dp.getYear() + "-" + date.getTime()/1000, false));                        //report
                    corText.add(new Tuple(421, 158, month + " / " +
                            (dp.getYear() + 1), false));                        //Next Date

                    corText.add(new Tuple(110, 115, "!", false));                        //Signature

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
                    createPDF(intent);
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
    public void restart() {
        ((EditText) findViewById(R.id.etDeviceSerial)).setText("");
    }

    private void init() {
        Helper h = new Helper();
        h.setListener(((EditText) findViewById(R.id.formMainLocation)));
        h.setListener(((EditText) findViewById(R.id.formRoomLocation)));
        h.setListener(((EditText) findViewById(R.id.etDeviceSerial)));
        h.setListener(((EditText) findViewById(R.id.formTechName)));
    }

}

