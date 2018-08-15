package il.co.diamed.com.form.calibration.res;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.field.Device;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

public class DevicePrototypeActivity extends AppCompatActivity {


    private PDFBuilderFragment mPDFBuilderFragment;
    private String location = "";
    private String sublocation = "";
    DatabaseProvider provider;

    protected static String dev_name;
    protected static String dev_model;
    protected static String dev_serial;
    protected static Date install_date;
    protected static String comments;
    protected static Date next_maintenance;
    protected static boolean under_warranty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_device_activity);
        Bundle data = getIntent().getExtras();
        location = data.getString("location");
        sublocation = data.getString("sublocation");
        //location upload test
        ClassApplication application = (ClassApplication) getApplication();
        provider = application.getDatabaseProvider(this);
        findViewById(R.id.formSubmitButton).setActivated(false);

        if (!(location.equals("") && sublocation.equals("")))
            provider.createLocation(location, sublocation);
    }


    public void createPDF(Intent intent) {
        DatePicker dp = findViewById(R.id.formDate);
        Calendar calendar = new GregorianCalendar(dp.getYear(),
                dp.getMonth(),
                dp.getDayOfMonth());

        if (intent.hasExtra("type")) {
            dev_name = intent.getExtras().getString("type");
            if(dev_name.equals("Plasma Thawer")){
                calendar.add(Calendar.MONTH, 6);
                next_maintenance = calendar.getTime();
            }else{
                calendar.add(Calendar.YEAR, 1);
                next_maintenance = calendar.getTime();
            }
        }
        if (intent.hasExtra("model")) {
            dev_model = intent.getExtras().getString("model");
        }

        dev_serial = ((EditText) findViewById(R.id.etDeviceSerial)).getText().toString();

        //install_date = dp.getDayOfMonth()+"/"+(dp.getMonth()+1)+"/"+dp.getYear();
        comments = "";
        under_warranty = false;

        Device device = new Device(dev_name, dev_model, dev_serial, next_maintenance);
        provider.updateLocation(location, sublocation, device);

        setPDFprogress(this, "בונה טופס", true);

        FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
        mPDFBuilderFragment = new PDFBuilderFragment();
        mPDFBuilderFragment.setArguments(intent.getExtras());
        mFragmentTransaction.replace(R.id.pdffragment_container, mPDFBuilderFragment).addToBackStack(null).commit();

    }




    @Override
    public void onBackPressed() {
        setPDFprogress(this, "", false);

        if (mPDFBuilderFragment != null) {
            android.app.FragmentManager manager = getFragmentManager();
            android.app.FragmentTransaction trans = manager.beginTransaction();
            trans.remove(mPDFBuilderFragment);
            trans.commit();
            manager.popBackStack();

            doAnother();
        } else {
            finish();
        }

    }


    public void doAnother() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(R.string.doAnother);
        alertBuilder.setPositiveButton(R.string.okButton, (dialog, which) -> restart());
        alertBuilder.setNegativeButton(R.string.cancelButton, (dialog, which) -> finish());

        alertBuilder.setCancelable(false);
        alertBuilder.create().show();
    }

    public void restart() { //meant to be overridden
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        if (mPDFBuilderFragment != null) {
            mPDFBuilderFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    int fSize;

    public void setLayout(int resLayout) {
        View lowLayout = findViewById(R.id.lowLayout);
        ViewGroup parent = (ViewGroup) lowLayout.getParent();
        int index = parent.indexOfChild(lowLayout);
        parent.removeView(lowLayout);
        lowLayout = getLayoutInflater().inflate(resLayout, parent, false);
        parent.addView(lowLayout, index);

        //SET FONT SIZE
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String fontsize = sharedPref.getString("sync_fontsize", "");
        if (fontsize.equals("")) {
            fSize = 12;
        } else {
            fSize = Integer.valueOf(fontsize);
        }

        setButtons((ViewGroup) lowLayout.getParent());


        ((TextView) findViewById(R.id.formMainLocation)).setText(location);
        ((TextView) findViewById(R.id.formRoomLocation)).setText(sublocation);
    }

    public void setButtons(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (view instanceof ViewGroup)
                setButtons((ViewGroup) view);
            else {
                if (view instanceof CheckBox) {
                    ((CheckBox) view).setChecked(true);
                }
                if (view instanceof Switch) {
                    try {
                        if (view.getTag().equals("opt")) {
                            ((Switch) view).setChecked(false);
                        }
                    } catch (Exception e) {
                        ((Switch) view).setChecked(true);
                    }
                }
                if (view instanceof TextView) {

                    ((TextView) view).setMaxLines(2);
                    ((TextView) view).setTextSize(fSize);

                }
                if (view instanceof EditText) {
                    ((EditText) view).setMaxLines(1);
                    view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    Double tSize = (float)fSize/1.3;

                    ((EditText) view).setTextSize(tSize.floatValue());

                    ((EditText) view).setHintTextColor(Color.RED);
                    ((EditText) view).setText("");
                }
                if (view instanceof RadioButton) {
                    ((RadioButton) view).setMaxLines(1);
                    view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    Double tSize = (float)fSize/1.5;

                    ((RadioButton) view).setTextSize(tSize.floatValue());

                }
            }

        }
    }


    public void setListener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
    }

    public void setTempListener(final EditText editText, final double min, final double max) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //editText.setHintTextColor(Color.RED);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isTempValid(editText, min, max)) {
                    editText.setTextColor(Color.RED);
                } else {
                    editText.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
    }

    public void setTimeListener(final EditText editText, final int expTemp) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //editText.setHintTextColor(Color.RED);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!isTimeValid(editText, expTemp)) {
                        editText.setTextColor(Color.RED);
                    } else {
                        editText.setTextColor(Color.BLACK);
                    }
                } catch (Exception e) {
                    //editText.setHintTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
    }

    protected static boolean isValidString(String s) {
        return (!(s == null || s.equals("")));
    }

    protected static boolean isTempValid(EditText s, double min, double max) {
        try {
            float temp = Float.parseFloat(s.getText().toString());
            return (temp >= min && temp <= max);
        } catch (Exception e) {
            return false;
        }
    }

    protected static boolean isTimeValid(EditText s, int expTime) {
        try {
            float time = Float.parseFloat(s.getText().toString());
            return (time == expTime);
        } catch (Exception e) {
            return false;
        }
    }

    protected void setSpeedListener(final EditText editText, final int expected_speed) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //editText.setHintTextColor(Color.RED);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!isSpeedValid(Integer.valueOf(editText.getText().toString()), expected_speed)) {
                        editText.setTextColor(Color.RED);
                    } else {
                        editText.setTextColor(Color.BLACK);
                    }
                } catch (Exception e) {
                    //editText.setHintTextColor(Color.RED);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });
    }

    protected static boolean isBarValid(int mBar, int bar_min) {
        return mBar >= bar_min;
    }

    protected static boolean isSpeedValid(int mSpeed, int eSpeed) {
        return (mSpeed <= eSpeed + 10 && mSpeed >= eSpeed - 10);
    }

    protected static boolean isVoltValid(double mVolt, double target, double threshold) {
        return (mVolt > target + threshold || mVolt < target - threshold);
    }


    public void setVoltListener(final EditText editText, final double target, final double volt_threshold) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //editText.setHintTextColor(Color.RED);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (isVoltValid(Float.valueOf(editText.getText().toString()), target, volt_threshold)) {
                        editText.setTextColor(Color.RED);
                    } else {
                        editText.setTextColor(Color.BLACK);
                    }
                } catch (Exception e) {
                    //editText.setHintTextColor(Color.RED);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (isVoltValid(Float.valueOf(editText.getText().toString()), target, volt_threshold)) {
                        editText.setTextColor(Color.RED);
                    } else {
                        editText.setTextColor(Color.BLACK);
                    }
                } catch (Exception e) {
                    //editText.setHintTextColor(Color.RED);
                }
            }
        });
    }

    public void setBarListener(final EditText editText, final int bar_min) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //editText.setHintTextColor(Color.RED);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!isBarValid(Integer.valueOf(editText.getText().toString()), bar_min)) {
                        editText.setTextColor(Color.RED);
                    } else {
                        editText.setTextColor(Color.BLACK);
                    }
                } catch (Exception e) {
                    //editText.setHintTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public String fixDay(int date) {

        if (date < 10) {
            return "0" + date;
        } else {
            return "" + date;
        }
    }

    public String fixMonth(int date) {
        date = (date % 12) + 1;
        if (date < 10) {
            return "0" + date;
        } else {
            return "" + date;
        }

    }

    public void setPDFprogress(Activity activity, String text, boolean visible) {
        if (visible) {
            activity.findViewById(R.id.PDFprogressLayout).setVisibility(View.VISIBLE);
            ((TextView) activity.findViewById(R.id.tvPDF)).setText(text);

        } else {
            activity.findViewById(R.id.PDFprogressLayout).setVisibility(View.INVISIBLE);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(databaseReceiver, new IntentFilter(DatabaseProvider.BROADCAST_LOCATION_READY));
        registerReceiver(databaseLocDBReceiver, new IntentFilter(DatabaseProvider.BROADCAST_LOCDB_READY));

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(databaseReceiver);
        unregisterReceiver(databaseLocDBReceiver);
    }

    private BroadcastReceiver databaseLocDBReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!(location.equals("") && sublocation.equals("")))
                provider.createLocation(location, sublocation);
        }

    };

    private BroadcastReceiver databaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Toast.makeText(getApplicationContext(), "Location Ready", Toast.LENGTH_SHORT).show();
            findViewById(R.id.formSubmitButton).setActivated(true);
        }

    };
}