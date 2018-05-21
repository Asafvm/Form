package il.co.diamed.com.form.menu;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import il.co.diamed.com.form.res.providers.AnalyticsScreenItem;
import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;

public class LoginActivity extends FragmentActivity implements
        UserSetupFragment.OnFragmentInteractionListener,
        MicrosoftSigninFragment.OnFragmentInteractionListener {
    private static final int MY_PERMISSIONS_REQUEST_CONTACTS = 0;

    private ProgressBar progressBar;
    private static final String TAG = "Login";
    private FragmentManager mFragmentManager;
    private MicrosoftSigninFragment mMicrosoftSigninFragment;
    private UserSetupFragment mUserSetupFragment;
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
            Log.e(TAG,"No active user");
            progressBar.setProgress(10);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                mFragmentManager = getSupportFragmentManager();
                if (mMicrosoftSigninFragment == null) {
                    mMicrosoftSigninFragment = new MicrosoftSigninFragment();
                }
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                //ft.setCustomAnimations(R.animator, R.animator.fade_in);
                ft.replace(R.id.fragment_container, mMicrosoftSigninFragment).commit();
            } else {
                // Permission is not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_CONTACTS);
            }

        } else {
            Log.d(TAG, "Logged: " + application.getCurrentUser().getEmail());
            signin();
        }
    }

    public void signin() {  //user logged, start app
        progressBar.setProgress(80);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String user_name = user.getDisplayName();
        String user_email = user.getEmail();
        Log.d(TAG, "Got user from microsoft: " + user_name + " " + user_email);
        Toast.makeText(this, getString(R.string.loggedin) + " " + user_email, Toast.LENGTH_LONG).show();
        progressBar.setProgress(90);


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

            mUserSetupFragment = new UserSetupFragment();
            fragmentTransaction.add(R.id.fragment_container, mUserSetupFragment).commit();

        } else {
            progressBar.setProgress(100);
            Intent intent = new Intent(this,MainMenuAcitivity.class);
            startActivity(intent);
            finish();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!

                    mFragmentManager = getSupportFragmentManager();
                    if (mMicrosoftSigninFragment == null) {
                        mMicrosoftSigninFragment = new MicrosoftSigninFragment();
                    }
                    FragmentTransaction ft = mFragmentManager.beginTransaction();
                    //ft.setCustomAnimations(R.animator, R.animator.fade_in);
                    ft.replace(R.id.fragment_container, mMicrosoftSigninFragment).commit();

                } else {
                    // permission denied, boo!
                    quitApp(getString(R.string.noContactsPermission));
                }
            }
        }
    }
    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO: what is this?
    }

    public void moveToMainMenu() {

        Intent intent = new Intent(this,MainMenuAcitivity.class);
        startActivity(intent);
        finish();
    }


    public void quitApp(String message) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if(mMicrosoftSigninFragment!=null){
            fragmentTransaction.remove(mMicrosoftSigninFragment);
        }
        if(mUserSetupFragment!=null){
            fragmentTransaction.remove(mUserSetupFragment);
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }, 2000);
    }

    private int getAuthSilentCallback() {
        Log.e(TAG,"got silent token");
        return 0;
    }
    private void getAuthInteractiveCallback() {
        Log.e(TAG,"got interactive token token");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"got result login");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.onActivityResult(requestCode, resultCode, data);

    }
}



