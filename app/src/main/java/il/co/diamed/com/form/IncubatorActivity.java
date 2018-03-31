package il.co.diamed.com.form;

import android.content.Intent;
import android.os.Parcelable;
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


        final ArrayList<Tuple> corCheck = new ArrayList<>();
        corCheck.add(new Tuple(116,467));
        corCheck.add(new Tuple(205,467));

        Button btn = (Button)findViewById(R.id.submit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText t1 = (EditText)findViewById(R.id.location);
                RadioGroup t2 = (RadioGroup)findViewById(R.id.type);
                EditText t3 = (EditText)findViewById(R.id.serial);
                EditText t4 = (EditText)findViewById(R.id.temp);
                EditText t5 = (EditText)findViewById(R.id.time);
                EditText t6 = (EditText)findViewById(R.id.techname);
                DatePicker dp = (DatePicker)findViewById(R.id.date);



                ArrayList<Tuple> corText = new ArrayList<>();
                corText.add(new Tuple(70,635));                        //Date
                corText.add(new Tuple(380,635));                        //Location
                corText.add(new Tuple(290,570));                        //type
                corText.add(new Tuple(455,570));                        //Serial
                corText.add(new Tuple(270,465));                        //temp
                corText.add(new Tuple(305,325));                        //Time
                corText.add(new Tuple(440,65));                        //Date
                corText.add(new Tuple(380,30));                        //Tech Name

                ArrayList<String> arrText = new ArrayList<>();
                arrText.add(dp.getDayOfMonth()+"    "+dp.getMonth()+"     "+dp.getYear());                      //Date
                arrText.add(t1.getText().toString());                                                           //Location
                arrText.add(((RadioButton)findViewById(t2.getCheckedRadioButtonId())).getText().toString());     //type
                arrText.add(t3.getText().toString());                        //Serial
                arrText.add(t4.getText().toString());                        //temp
                arrText.add(t5.getText().toString());                        //Time
                arrText.add(dp.getMonth()+"   "+(dp.getYear()+1));                      //Next Date
                arrText.add(t6.getText().toString());                        //Tech Name

                Intent intent = new Intent(getBaseContext(),PDFActivity.class);
                intent.putExtra("report","2018_id37_yearly.pdf");
                intent.putExtra("checkmarks",corCheck);
                intent.putExtra("arrText",arrText);
                intent.putExtra("corText",corText);

                startActivity(intent);
            }
        });


    }

}
