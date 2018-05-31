package il.co.diamed.com.form.menu;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.json.JSONException;
import org.json.JSONObject;

import il.co.diamed.com.form.res.providers.AnalyticsScreenItem;
import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;

public class LoginActivity extends FragmentActivity implements
        UserSetupFragment.OnFragmentInteractionListener,
        MicrosoftSigninFragment.OnFragmentInteractionListener {
    private static final int MY_PERMISSIONS_REQUEST_CONTACTS = 0;

    private static final String TAG = "Login";
    private ClassApplication application;
    private UserSetupFragment mUserSetupFragment;
    JSONObject userDetails;
    private FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setProgressInfo("Starting",0);

        application = (ClassApplication) getApplication();
        application.logAnalyticsScreen(new AnalyticsScreenItem(this.getClass().getName()));

        signinUser();

    }

    public void signinUser() {
        setProgressInfo("Starting app",10);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "No active user");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                setProgressInfo("Logging to Microsoft",30);
                signinToMicrosoft();

            } else {
                // Permission is not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_CONTACTS);
            }

        } else {
            Log.d(TAG, "Logged: " + user.getEmail());
            updatePrefernces();
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
                    signinToMicrosoft();

                } else {
                    // permission denied, boo!
                    quitApp(getString(R.string.noContactsPermission));
                }
            }
        }
    }
    private void init() {

        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_login);

        View decorView = getWindow().getDecorView();
        Slide slide = new Slide();
        slide.setDuration(500);
        slide.setSlideEdge(Gravity.END);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(new Fade());
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

    }
    private void signinToMicrosoft() {
        FragmentManager mFragmentManager;
        MicrosoftSigninFragment mMicrosoftSigninFragment = new MicrosoftSigninFragment();
        mFragmentManager = getSupportFragmentManager();
        if (mMicrosoftSigninFragment == null) {
            mMicrosoftSigninFragment = new MicrosoftSigninFragment();
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        //ft.setCustomAnimations(R.animator, R.animator.fade_in);
        ft.replace(R.id.fragment_container, mMicrosoftSigninFragment).commit();
    }

    public void quitApp(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }, 2000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "got result login");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        fragment.onActivityResult(requestCode, resultCode, data);

    }

    public void verifyUser(JSONObject response) {
        setProgressInfo("Got user from Microsoft",50);
        userDetails = response;

        try {
            String eMail = userDetails.getString("mail");
            application.getAuthProvider().addAuthStateListener(listener);
            application.signin(eMail, eMail);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    final FirebaseAuth.AuthStateListener listener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            user = firebaseAuth.getCurrentUser();

            if (user != null) {
                setProgressInfo("Logged with: "+user.getEmail(),70);
                Log.e(TAG, "User logged in");
                updateUserDetails();
            }else{
                Log.e(TAG, "User logged out");
            }
        }
    };

    private void updateUserDetails() {
        setProgressInfo("Updating info",70);
        application.getAuthProvider().removeAuthStateListener(listener);
        Log.d(TAG, "Setting User Info");

        UserProfileChangeRequest profileUpdates = null;
        try {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(userDetails.getString("displayName"))
                    .build();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor spedit = sharedPref.edit();
                            if(sharedPref.getString("techName", "").equals(""))
                                spedit.putString("techName", user.getDisplayName());
                            spedit.putString("techeMail", user.getEmail());
                            spedit.putString("techePhone", user.getPhoneNumber());
                            spedit.apply();
                            updatePrefernces();
                        }
                    }
                });
    }

    public void updatePrefernces() {  //user logged, start app
        setProgressInfo("Updating Prefernces",90);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String fontsize = sharedPref.getString("sync_fontsize", "");
        if(fontsize.equals("")) {
            SharedPreferences.Editor spedit = sharedPref.edit();
            spedit.putString("sync_fontsize", "12");
        }
        final String techname = sharedPref.getString("techName", "");
        final String signature = sharedPref.getString("signature", "");
        final String thermometer = sharedPref.getString("thermometer", "");
        final String barometer = sharedPref.getString("barometer", "");
        final String timer = sharedPref.getString("timer", "");
        final String speedometer = sharedPref.getString("speedometer", "");

        if (techname.equals("") || signature.equals("") || thermometer.equals("") ||
                barometer.equals("") || timer.equals("") || speedometer.equals("")) {

            mUserSetupFragment = new UserSetupFragment();
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.RIGHT);
            slide.setDuration(600);
            mUserSetupFragment.setEnterTransition(slide);
            fragmentTransaction.replace(R.id.fragment_container, mUserSetupFragment).commit();

        } else {
            moveToMainMenu();
        }
    }
    private void moveToMainMenu() {
        setProgressInfo("Ready... Set.... GO!",100);
        //String user_email = user.getEmail();
        //Toast.makeText(getApplicationContext(), getString(R.string.loggedin) + " " + user_email, Toast.LENGTH_LONG).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent intent = new Intent(getApplicationContext(), MainMenuAcitivity.class);
                startActivity(intent);

            }
        }, 2000);

    }
    public void setProgressInfo(String text, int percent){

        ((TextView)findViewById(R.id.tvProgress)).setText(text);
        ((ProgressBar)findViewById(R.id.pbLogin)).setProgress(percent);
    }
    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}



