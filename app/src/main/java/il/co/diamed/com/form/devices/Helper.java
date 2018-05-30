package il.co.diamed.com.form.devices;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import il.co.diamed.com.form.R;

public class Helper extends AppCompatActivity {
    int fSize;

    public void setLayout(Activity activity, int resLayout) {
        View lowLayout = activity.findViewById(R.id.lowLayout);
        ViewGroup parent = (ViewGroup) lowLayout.getParent();
        int index = parent.indexOfChild(lowLayout);
        parent.removeView(lowLayout);
        lowLayout = activity.getLayoutInflater().inflate(resLayout, parent, false);
        parent.addView(lowLayout, index);

        //SET FONT SIZE
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        String fontsize = sharedPref.getString("sync_fontsize", "12");
        fSize = Integer.valueOf(fontsize);


        setButtons((ViewGroup) lowLayout.getParent());


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
                if(view instanceof TextView){

                    ((TextView) view).setMaxLines(2);
                    ((TextView) view).setTextSize(fSize);
                }
                if(view instanceof EditText){
                    ((EditText) view).setMaxLines(1);
                    view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    ((EditText)view).setHintTextColor(Color.RED);

                }
                if (view instanceof EditText) {
                    setListener((EditText) view);
                        ((EditText) view).setText("");
                }
            }
        }
    }

    public void setListener(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(editText.getText().toString().equals("")) {
                    //editText.setError("מידע דרוש");//.setHintTextColor(Color.RED);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editText.getText().toString().equals("")) {
                    //editText.setError("מידע דרוש");//.setHintTextColor(Color.RED);
                }else {
                    //editText.setError(null);
                }
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
        return (mVolt <= target + threshold && mVolt >= target - threshold);
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
                    if (!isVoltValid(Float.valueOf(editText.getText().toString()), target, volt_threshold)) {
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
                    if (!isVoltValid(Float.valueOf(editText.getText().toString()), target, volt_threshold)) {
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

    public String fixDay(int date){

        if (date<10){
            return "0"+date;
        }else{
            return ""+date;
        }
    }
    public String fixMonth(int date){
        date++;
        if (date<10){
            return "0"+date;
        }else{
            return ""+date;
        }

    }
    public void setError(EditText editText, boolean error){
        if(error){
            editText.setError("מידע דרוש");
        }else{
            editText.setError(null);
        }
    }

    public void setPDFprogress(Activity activity, String text, boolean visible){
        if(visible) {
            activity.findViewById(R.id.PDFprogressLayout).setVisibility(View.VISIBLE);
            ((TextView)activity.findViewById(R.id.tvPDF)).setText(text);

        }else{
            activity.findViewById(R.id.PDFprogressLayout).setVisibility(View.INVISIBLE);
        }

    }
}
