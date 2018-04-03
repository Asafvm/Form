package il.co.diamed.com.form;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import il.co.diamed.com.form.res.Tuple;

public class IncubatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incubator);
        final Button btn = (Button) findViewById(R.id.submit);

        //Get preferrences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        final String thermometer = sharedPref.getString("thermometer", "");
        final String timer = sharedPref.getString("timer", "");
        final String techname = sharedPref.getString("techName", "");
        ;
        final String signature = sharedPref.getString("signature", "");

        final EditText t1 = (EditText) findViewById(R.id.location);
        final RadioGroup t2 = (RadioGroup) findViewById(R.id.type);
        final EditText t3 = (EditText) findViewById(R.id.serial);
        final EditText t4 = (EditText) findViewById(R.id.temp);
        final EditText t5 = (EditText) findViewById(R.id.time);
        final EditText t6 = (EditText) findViewById(R.id.techname);
        final DatePicker dp = (DatePicker) findViewById(R.id.date);
        //default basic values
        t5.setText("15");
        t6.setText(techname);


        t4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(t4.getText().toString().equals("")){

                    btn.setClickable(false);
                    t4.setBackgroundColor(Color.RED);
                }else {

                    if (tempValid(Float.parseFloat(t4.getText().toString()))) {
                        t4.setBackgroundColor(Color.TRANSPARENT);
                        btn.setClickable(true);
                    } else {
                        btn.setClickable(false);
                        t4.setBackgroundColor(Color.RED);
                    }
                }
            }
        });


        //btn.setActivated(false);
        //check stats


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Tuple> corCheck = new ArrayList<>();
                //       corCheck.add(new Tuple(116,467));           //temp not ok
                corCheck.add(new Tuple(205, 467));           //temp ok

                //       corCheck.add(new Tuple(114,327));           //time not ok
                corCheck.add(new Tuple(203, 327));           //time ok

                //       corCheck.add(new Tuple(155,228));           //fan not ok
                corCheck.add(new Tuple(250, 228));           //fan ok

                //       corCheck.add(new Tuple(155,210));           //rubber not ok
                corCheck.add(new Tuple(250, 210));           //rubber ok

                //       corCheck.add(new Tuple(232,95));           //overall not ok
                corCheck.add(new Tuple(480, 95));           //overall ok

                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(78, 636));                        //Date
                corText.add(new Tuple(300, 635));                        //Location
                corText.add(new Tuple(290, 568));                        //type
                corText.add(new Tuple(425, 568));                        //Serial
                corText.add(new Tuple(275, 465));                        //temp
                corText.add(new Tuple(305, 325));                        //Time
                corText.add(new Tuple(451, 65));                        //Next Date
                corText.add(new Tuple(330, 30));                        //Tech Name

                corText.add(new Tuple(330, 425));                        //Thermometer
                corText.add(new Tuple(400, 285));                        //Timer
                //corText.add(new Tuple(380,30));                        //Signature

                ArrayList<String> arrText = new ArrayList<>();
                arrText.add(dp.getDayOfMonth() + "       " + dp.getMonth() + "       " + dp.getYear());                      //Date
                arrText.add(t1.getText().toString());                                                           //Location
                arrText.add(((RadioButton) findViewById(t2.getCheckedRadioButtonId())).getText().toString());     //type
                arrText.add(t3.getText().toString());                        //Serial
                arrText.add(t4.getText().toString());                        //temp
                arrText.add(t5.getText().toString());                        //Time
                arrText.add(dp.getMonth() + "   " + (dp.getYear() + 1));           //Next Date

                arrText.add(t6.getText().toString());                        //Tech Name
                arrText.add(thermometer);                       //Thermometer
                arrText.add(timer);                             //Timer


                ArrayList<String> destArray = new ArrayList<>();
                destArray.add(t1.getText().toString());
                destArray.add(String.valueOf(dp.getYear()));
                destArray.add(String.valueOf(dp.getMonth()));
                destArray.add(String.valueOf(dp.getDayOfMonth()));
                destArray.add(t3.getText().toString());


                Intent intent = new Intent(getBaseContext(), PDFActivity.class);
                intent.putExtra("report", "2018_id37_yearly.pdf");
                intent.putExtra("checkmarks", corCheck);
                intent.putExtra("arrText", arrText);
                intent.putExtra("corText", corText);
                intent.putExtra("signature", signature);
                intent.putExtra("destArray", destArray);
                startActivity(intent);
            }
        });


    }

    private boolean tempValid(float temp) {

            if (temp >= 35 && temp <= 39)
                return true;
            else
                return false;
        }


}
