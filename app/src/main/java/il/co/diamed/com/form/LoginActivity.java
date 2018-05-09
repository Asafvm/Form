package il.co.diamed.com.form;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import io.fabric.sdk.android.services.concurrency.Task;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        //hide action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);


        ClassApplication application = (ClassApplication) getApplication();
        application.logAnalyticsScreen(new AnalyticsScreenItem(this.getClass().getName()));
        //application.signout();
        if (application.getCurrentUser() == null) {
            Intent intent = new Intent(getBaseContext(), MicrosoftSignIn.class);
            startActivityForResult(intent, 1);
            Toast.makeText(this, "Signed Out!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Logged: " + application.getCurrentUser().getEmail());
            signin();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String user_name = user.getDisplayName();
                String user_email = user.getEmail();
                Log.d(TAG, "Got user from microsoft: " + user_name + " " + user_email);
                Toast.makeText(this,getString(R.string.loggedin)+" "+user_email,Toast.LENGTH_LONG).show();

                signin();

            }
        }
    }


    public void signin() {

        new CountDownTimer(3000, 1500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                Intent intent = new Intent(getBaseContext(), DeviceActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }

}



