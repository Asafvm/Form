package il.co.diamed.com.form.menu;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Objects;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.providers.AnalyticsScreenItem;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

public class LoginActivity extends AppCompatActivity implements
        UserSetupFragment.OnFragmentInteractionListener {
    private static final int MY_PERMISSIONS_REQUEST_CONTACTS = 0;
    private static final String TAG = "Login";
    private ClassApplication application;
    private FirebaseUser user;
    boolean verChecked = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setProgressInfo("Checking Latest Version", 0);
        signinUser();
        //application.getDatabaseProvider(this).getAppVer();
    }


    private void moveLogo(float distance, int duration) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(findViewById(R.id.loginLogo), "translationY", distance);
        animation.setDuration(duration);
        animation.start();

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(duration);
        findViewById(R.id.loginLogo).startAnimation(anim);
    }

    public void signinUser() {
        setProgressInfo("Starting app", 10);

        //FirebaseAuth.getInstance().getCurrentUser();
        user = null;    //testing forced log in
        if (user == null) {
            Log.e(TAG, "No active user");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                setProgressInfo("Waiting for user and password", 30);
                //signinToMicrosoft();

                //OPEN LOG IN SCREEN HERE
                final Dialog loginDialog = new Dialog(LoginActivity.this, android.R.style.Theme_Black_NoTitleBar);
                Window win = loginDialog.getWindow();
                if(win!=null)
                    win.setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                //loginDialog.getWindow().setEnterTransition();

                loginDialog.setContentView(R.layout.activity_signup);
                loginDialog.setCancelable(true);

                Button submit = loginDialog.findViewById(R.id.btn_signup);
                submit.setOnClickListener(view -> {
                    //Login with user and pass
                    String username = ((EditText) loginDialog.findViewById(R.id.editUserName)).getText().toString();
                    String password = ((EditText) loginDialog.findViewById(R.id.editPassword)).getText().toString();
                    Log.e(TAG, "Logging with " + username + " : " + password);
                    setProgressInfo("Attempting to login", 50);
                    application.signin(username, username);
                    application.getAuthProvider().getAuth().addAuthStateListener(listener);

                    loginDialog.dismiss();

                });

                loginDialog.show();
                //application.getAuthProvider().signinToMicrosoftFirebase(this);
            } else {
                // Permission is not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_CONTACTS);
            }

        } else {
            Log.d(TAG, "Logged: " + user.getEmail());
            updateTechTools();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    signinUser();

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


        application = (ClassApplication) getApplication();
        application.logAnalyticsScreen(new AnalyticsScreenItem(this.getClass().getName()));
        moveLogo(400, 0);
        moveLogo(-650 , 1400);
    }


    public void quitApp(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(() -> android.os.Process.killProcess(android.os.Process.myPid()), 2000);
    }


    public void updateUserDetails() {
        setProgressInfo("Updating info", 70);

            Log.d(TAG, "Setting User Info");

            UserProfileChangeRequest profileUpdates = null;
                profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(Objects.equals(user.getDisplayName(), "") ? user.getEmail() : user.getDisplayName()) //display user name or email
                        .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User profile updated.");
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor spedit = sharedPref.edit();
                        if (Objects.equals(sharedPref.getString("techName", ""), ""))
                            spedit.putString("techName", user.getDisplayName());
                        if (Objects.equals(sharedPref.getString("techeMail", ""), ""))
                            spedit.putString("techeMail", user.getEmail());
                        if (Objects.equals(sharedPref.getString("techePhone", ""), ""))
                            spedit.putString("techePhone", user.getPhoneNumber());
                        spedit.apply();

                        updateTechTools();
                    }
                });
    }


    public void updateTechTools() {  //user logged, start app
        setProgressInfo("Updating Prefernces", 90);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String fontsize = sharedPref.getString("sync_fontsize", "");
        if (fontsize.equals("")) {
            SharedPreferences.Editor spedit = sharedPref.edit();
            spedit.putString("sync_fontsize", "12");
            spedit.apply();
        }
        final String name = sharedPref.getString("techName", "");
        final String signature = sharedPref.getString("signature", "");
        final String thermometer = sharedPref.getString("thermometer", "");
        final String barometer = sharedPref.getString("barometer", "");
        final String timer = sharedPref.getString("timer", "");
        final String speedometer = sharedPref.getString("speedometer", "");

        if (name.equals("") || signature.equals("") || thermometer.equals("") ||
                barometer.equals("") || timer.equals("") || speedometer.equals("")) {

            UserSetupFragment mUserSetupFragment = new UserSetupFragment();
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.END);
            slide.setDuration(600);
            mUserSetupFragment.setEnterTransition(slide);
            fragmentTransaction.replace(R.id.fragment_container, mUserSetupFragment).commit();

        } else {
            HashMap<String, String> userInfo = new HashMap<>();
            userInfo.put("techName", name);
            userInfo.put("speedometer", speedometer);
            userInfo.put("thermometer", thermometer);
            userInfo.put("barometer", barometer);
            userInfo.put("timer", timer);
            userInfo.put("AppVer", application.getAppVer());

            ClassApplication application = (ClassApplication) getApplication();
            application.getDatabaseProvider(this).uploadUserData(userInfo);

            moveToMainMenu();
        }
    }

    private void moveToMainMenu() {
        application.getDatabaseProvider(this).initialize();

        setProgressInfo("Ready... Set.... GO!", 100);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            finish();
            Intent intent = new Intent(getApplicationContext(), MainMenuAcitivity.class);
            startActivity(intent);

        }, 2000);

    }

    public void setProgressInfo(String text, int percent) {

        ((TextView) findViewById(R.id.tvProgress)).setText(text);
        ((ProgressBar) findViewById(R.id.pbLogin)).setProgress(percent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(databaseLocDBReceiver, new IntentFilter(DatabaseProvider.BROADCAST_APPVER));
        //checkVersion();

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(databaseLocDBReceiver);
    }

    private BroadcastReceiver databaseLocDBReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String appVer = application.getAppVer();
            String fireBaseAppVer = intent.getExtras().getString("AppVer");
            if (!appVer.equals("") && appVer.compareTo(fireBaseAppVer) >= 0)
                if (!verChecked) {
                    verChecked = true;
                    signinUser();
                } else {
                    displayAlert(fireBaseAppVer);

                    Log.e(TAG, "Current ver: " + fireBaseAppVer);
                    Log.e(TAG, "App ver: " + appVer);
                }
        }
    };

    private void displayAlert(String databaseVer) {
        new AlertDialog.Builder(this)
                .setTitle("גרסה לא מעודכנת")
                .setMessage("קיימת גרסה חדשה יותר (" + databaseVer + ")")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.confirm, (dialog, whichButton) -> quitApp("גרסה לא עדכנית"))
                .show();


    }


    final FirebaseAuth.AuthStateListener listener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            user = firebaseAuth.getCurrentUser();

            if (user != null) {
                Log.e(TAG, "User logged in");
                application.getAuthProvider().getAuth().removeAuthStateListener(listener);
                updateUserDetails();
            } else {
                Log.e(TAG, "User logged out");
            }

        }
    };

}



