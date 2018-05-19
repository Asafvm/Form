package il.co.diamed.com.form;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import io.fabric.sdk.android.services.concurrency.Task;

public class LoginActivity extends FragmentActivity implements MainMenuFragment.OnFragmentInteractionListener,
                                                                UserSetupFragment.OnFragmentInteractionListener{
    private ProgressBar progressBar;
    private static final String TAG = "Login";
    private FragmentManager mFragmentManager;
    private MainMenuFragment mainMenuFragment;
    private final String PREFS_NAME = "USER_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        //hide action bar
        //Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.pbLogin);
        progressBar.setProgress(0);

        ClassApplication application = (ClassApplication) getApplication();
        application.logAnalyticsScreen(new AnalyticsScreenItem(this.getClass().getName()));
        //application.signout();
        if (application.getCurrentUser() == null) {
            progressBar.setProgress(10);
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
                progressBar.setProgress(80);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String user_name = user.getDisplayName();
                String user_email = user.getEmail();
                Log.d(TAG, "Got user from microsoft: " + user_name + " " + user_email);
                Toast.makeText(this, getString(R.string.loggedin) + " " + user_email, Toast.LENGTH_LONG).show();

                signin();

            }
        }
    }
    /*
    @Override
    public void onBackPressed() {

//        if (stackCounter == 0)
//            super.onBackPressed();


    }*/

    public void signin() {  //user logged, start app
        progressBar.setProgress(90);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(100);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                //SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                final String techname = sharedPref.getString("techName", "");
                final String signature = sharedPref.getString("signature", "");
                final String thermometer = sharedPref.getString("thermometer", "");
                final String barometer = sharedPref.getString("barometer", "");
                final String timer = sharedPref.getString("timer", "");
                final String speedometer = sharedPref.getString("speedometer", "");

                if (techname.equals("") || signature.equals("") || thermometer.equals("") ||
                        barometer.equals("") || timer.equals("") || speedometer.equals("")) {

                    UserSetupFragment fragment = new UserSetupFragment();
                    fragmentTransaction.add(R.id.fragment_container, fragment);
                    fragmentTransaction.commit();

                } else {
                    MainMenuFragment fragment = new MainMenuFragment();
                    fragmentTransaction.add(R.id.fragment_container, fragment);
                    fragmentTransaction.commit();
                }


            }
        }, 3000);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO: what is this?
    }

    public void moveToMainMenu() {

        mFragmentManager = getSupportFragmentManager();
        if (mainMenuFragment == null) {
            mainMenuFragment = new MainMenuFragment();
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        //ft.setCustomAnimations(R.animator, R.animator.fade_in);
        ft.replace(R.id.fragment_container, mainMenuFragment);
        ft.commit();
    }


}



