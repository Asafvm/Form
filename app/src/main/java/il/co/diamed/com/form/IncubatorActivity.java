package il.co.diamed.com.form;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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


        ArrayList<Tuple> cor = new ArrayList<>();
        cor.add(new Tuple(100,466));
        Intent intent = new Intent(getBaseContext(),PDFActivity.class);
        intent.putExtra("type","ID-Incubator 37SI");
        intent.putExtra("report","2018_id37_yearly.pdf");
        intent.putExtra("temp","36.6");
        intent.putExtra("cor",cor);


        startActivity(intent);

    }

}
