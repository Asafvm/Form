package il.co.diamed.com.form;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class IncubatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incubator);

        Intent intent = new Intent(getBaseContext(),PDFActivity.class);
        intent.putExtra("type","ID-Incubator 37SI");
        intent.putExtra("temp","36.6");
        startActivity(intent);

    }
}
