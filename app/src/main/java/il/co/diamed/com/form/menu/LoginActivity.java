package il.co.diamed.com.form.menu;

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
import android.graphics.Point;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.providers.AnalyticsScreenItem;
import il.co.diamed.com.form.res.providers.AuthenticationProvider;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

public class LoginActivity extends AppCompatActivity implements
        UserSetupFragment.OnFragmentInteractionListener {
    private static final int MY_PERMISSIONS_REQUEST_CONTACTS = 0;
    private static final String TAG = "Login";
    private ClassApplication application;
    private FirebaseUser user;
    boolean verChecked = false;
    Dialog loginDialog = null;
    private boolean labReady = false, locReady = false, userdbReady = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setProgressInfo("Checking Latest Version", 0);
        application.getDatabaseProvider(this).getAppVer();
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
        //moveLogo(400, 0);

    }


    private void moveLogo(float distance, int duration) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(findViewById(R.id.loginLogo), "translationY", distance);
        animation.setDuration(duration);
        animation.start();

        //AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        //anim.setDuration(duration);
        //findViewById(R.id.loginLogo).startAnimation(anim);
    }

    public void signUser() {
        setProgressInfo("Starting app", 10);

        //FirebaseAuth.getInstance().getCurrentUser();
        FirebaseAuth.getInstance().signOut();    //testing forced log in
        user = FirebaseAuth.getInstance().getCurrentUser();
        application.getAuthProvider().getAuth().addAuthStateListener(listener);

        if (user == null) {
            Log.e(TAG, "No active user");
            setProgressInfo("Waiting for user and password", 30);
            //OPEN LOG IN SCREEN HERE
            loginDialog = new Dialog(LoginActivity.this, android.R.style.Theme_Black_NoTitleBar);
            Window win = loginDialog.getWindow();
            if (win != null)
                win.setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
            loginDialog.setContentView(R.layout.activity_signup);
            loginDialog.setCancelable(false);

            //load saved user
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor spedit = sharedPref.edit();
            String savedUser = sharedPref.getString("username", "");
            String savedDomain = sharedPref.getString("domain", "");
            boolean remember = sharedPref.getBoolean("remember", false);
            if (remember) {
                ((CheckBox) loginDialog.findViewById(R.id.cbRememberme)).setChecked(true);
                if (savedUser != null && !savedUser.isEmpty()) {
                    ((EditText) loginDialog.findViewById(R.id.editUserName)).setText(savedUser);
                }
                if (savedDomain != null && !savedDomain.isEmpty()) {
                    ((EditText) loginDialog.findViewById(R.id.editDomainName)).setText(savedDomain.toUpperCase());
                }
            }


            //set buttons
            Button submit = loginDialog.findViewById(R.id.btn_signup);
            Button register = loginDialog.findViewById(R.id.btn_register);
            Button forgot = loginDialog.findViewById(R.id.btn_forgetpwd);


            submit.setOnClickListener(view -> {
                //Login with user and pass
                String username = ((EditText) loginDialog.findViewById(R.id.editUserName)).getText().toString();
                String password = ((EditText) loginDialog.findViewById(R.id.editPassword)).getText().toString();
                String domain = ((EditText) loginDialog.findViewById(R.id.editDomainName)).getText().toString().toUpperCase();

                setProgressInfo("Attempting to login", 50);
                if (username.isEmpty() || password.isEmpty()) {
                    if (username.isEmpty())
                        ((TextView) loginDialog.findViewById(R.id.editUserName)).setError("שדה חובה!");
                    if (password.isEmpty())
                        ((TextView) loginDialog.findViewById(R.id.editPassword)).setError("שדה חובה!");
                    ((TextView) loginDialog.findViewById(R.id.textMessage)).setText("נא למלא שדות חובה");
                } else {
                    if (((CheckBox) loginDialog.findViewById(R.id.cbRememberme)).isChecked()) {
                        spedit.putBoolean("remember", true);
                        if (savedUser != null && (savedUser.isEmpty() || (!savedUser.equals(username)))) {
                            spedit.putString("username", username);
                        }
                        if (savedDomain != null && (savedDomain.isEmpty() || (!savedDomain.equals(domain)))) {
                            spedit.putString("domain", domain.toUpperCase());
                        }
                    } else {
                        spedit.putBoolean("remember", false);
                        spedit.putString("username", "");
                        spedit.putString("domain", "");
                    }
                    spedit.apply();

                    application.signin(username, password);

                }
            });

            register.setOnClickListener(view -> {
                //Login with user and pass
                String username = ((EditText) loginDialog.findViewById(R.id.editUserName)).getText().toString();
                String password = ((EditText) loginDialog.findViewById(R.id.editPassword)).getText().toString();
                setProgressInfo("Attempting to login", 50);
                if (username.isEmpty() || password.isEmpty()) {
                    if (username.isEmpty())
                        ((TextView) loginDialog.findViewById(R.id.editUserName)).setError("שדה חובה!");
                    if (password.isEmpty())
                        ((TextView) loginDialog.findViewById(R.id.editPassword)).setError("שדה חובה!");
                    ((TextView) loginDialog.findViewById(R.id.textMessage)).setText("נא למלא שדות חובה");
                } else
                    application.createUser(username, password);
            });


            forgot.setOnClickListener(view -> {
                //Login with user and pass
                String username = ((EditText) loginDialog.findViewById(R.id.editUserName)).getText().toString();
                setProgressInfo("Attempting to login", 50);
                if (username.isEmpty()) {
                    ((TextView) loginDialog.findViewById(R.id.editUserName)).setError("שדה חובה!");
                    ((TextView) loginDialog.findViewById(R.id.textMessage)).setText("נא למלא שדות חובה");
                } else
                    application.forgotPassword(username);
            });

            loginDialog.show();

        } else {
            Log.d(TAG, "Logged: " + user.getEmail());
            updateTechTools();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CONTACTS) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay!
                signUser();

            } else {
                // permission denied, boo!
                quitApp(getString(R.string.noContactsPermission));
            }
        }
    }


    public void quitApp(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(() -> android.os.Process.killProcess(android.os.Process.myPid()), 2000);
    }


    private void getData() {
        Log.d(TAG, "Getting databases");
        application.getDatabaseProvider(this).initialize();
    }


    public void updateUserDetails() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (loginDialog != null && loginDialog.isShowing())
                loginDialog.dismiss();
            moveLogo(0, 1000);
        }, 1000);


        setProgressInfo("Updating info", 70);
        Log.d(TAG, "Setting User Info");

        String userEmail = user.getEmail();
        //UserProfileChangeRequest profileUpdates;
        if (userEmail != null && !userEmail.isEmpty()) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(Objects.equals(user.getDisplayName(), "") ? userEmail.substring(0, userEmail.indexOf('@')) : user.getDisplayName()) //display user name or email
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");

                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor spedit = sharedPref.edit();


                            String data = sharedPref.getString("techName", "");
                            if (data == null || data.equals(""))
                                spedit.putString("techName", user.getDisplayName());

                            data = sharedPref.getString("techeMail", "");
                            if (data == null || data.equals(""))
                                spedit.putString("techeMail", user.getEmail());

                            data = sharedPref.getString("techePhone", "");
                            if (data == null || data.equals(""))
                                spedit.putString("techePhone", user.getPhoneNumber());
                            spedit.apply();

                            updateTechTools();
                        }
                    });
        }
    }


    public void updateTechTools() {  //user logged, start app
        setProgressInfo("Updating Prefernces", 90);


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();

        String fontsize = sharedPref.getString("sync_fontsize", "");
        if (fontsize != null && fontsize.equals("")) {
            editor.putString("sync_fontsize", "12");
            editor.apply();
        }


        HashMap<String, String> pInfo = application.getDatabaseProvider(this).getPersonalInfo();
        HashMap<String, String> pEquip = application.getDatabaseProvider(this).getPersonalEquipment();

        if (pInfo == null || pEquip == null) {
            displayUserUpdagePage();
        } else {
            for (String key : pInfo.keySet()) {
                String data = pInfo.get(key);
                if (data != null && !data.equals("")) {
                    editor.putString(key, data);
                    editor.apply();
                }
            }
            for (String key : pEquip.keySet()) {
                String data = pEquip.get(key);
                if (data != null && !data.equals("")) {
                    editor.putString(key, data);
                    editor.apply();
                }
            }
            final String name = sharedPref.getString("techName", "");
            final String signature = sharedPref.getString("signature", "");
            final String thermometer = sharedPref.getString("thermometer", "");
            final String barometer = sharedPref.getString("barometer", "");
            final String timer = sharedPref.getString("timer", "");
            final String speedometer = sharedPref.getString("speedometer", "");
            if (name == null || signature == null || thermometer == null ||
                    barometer == null || timer == null || speedometer == null ||
                    name.equals("") || signature.equals("") || thermometer.equals("") ||
                    barometer.equals("") || timer.equals("") || speedometer.equals("")) {
                displayUserUpdagePage();

            } else{
                HashMap<String, String> userInfo = new HashMap<>();
                HashMap<String, String> userTools = new HashMap<>();

                userInfo.put("techName", name);
                userTools.put("speedometer", speedometer);
                userTools.put("thermometer", thermometer);
                userTools.put("barometer", barometer);
                userTools.put("timer", timer);
                ClassApplication application = (ClassApplication)getApplication();
                application.getDatabaseProvider(this).uploadUserData(userInfo,"Info");
                application.getDatabaseProvider(this).uploadUserData(userTools, "Tools");
                moveToMainMenu();

            }
        }
    }

    private void displayUserUpdagePage() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        UserSetupFragment mUserSetupFragment = new UserSetupFragment();
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.END);
        slide.setDuration(600);
        mUserSetupFragment.setEnterTransition(slide);
        fragmentTransaction.replace(R.id.fragment_container, mUserSetupFragment).commit();
    }

    private void moveToMainMenu() {


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
        registerReceiver(appVerReciever, new IntentFilter(DatabaseProvider.BROADCAST_APPVER));
        registerReceiver(loginMessageReciever, new IntentFilter(AuthenticationProvider.BROADCAST_MESSAGE));
        registerReceiver(loginLoadingReciever, new IntentFilter(DatabaseProvider.BROADCAST_GLOBAL_PART_DB_READY));
        registerReceiver(loginLoadingReciever, new IntentFilter(DatabaseProvider.BROADCAST_LOCDB_READY));
        registerReceiver(loginLoadingReciever, new IntentFilter(DatabaseProvider.BROADCAST_DB_READY));
        //checkVersion();

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(appVerReciever);
        unregisterReceiver(loginMessageReciever);
        unregisterReceiver(loginLoadingReciever);
    }

    private BroadcastReceiver loginLoadingReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null)
                switch (intent.getAction()) {
                    case DatabaseProvider.BROADCAST_GLOBAL_PART_DB_READY:
                        if (loginDialog != null && loginDialog.isShowing())
                            ((ImageView) loginDialog.findViewById(R.id.loading2)).setImageDrawable(getResources().getDrawable(R.drawable.check, getApplicationContext().getTheme()));
                        labReady = true;
                        break;
                    case DatabaseProvider.BROADCAST_LOCDB_READY:
                        if (loginDialog != null && loginDialog.isShowing())
                            ((ImageView) loginDialog.findViewById(R.id.loading3)).setImageDrawable(getResources().getDrawable(R.drawable.check, getApplicationContext().getTheme()));
                        locReady = true;
                        break;
                    case DatabaseProvider.BROADCAST_DB_READY:
                        if (loginDialog != null && loginDialog.isShowing())
                            ((ImageView) loginDialog.findViewById(R.id.loading1)).setImageDrawable(getResources().getDrawable(R.drawable.check, getApplicationContext().getTheme()));
                        userdbReady = true;
                        break;

                }
            if (labReady && locReady && userdbReady) {
                updateUserDetails();
            }

        }
    };

    private BroadcastReceiver appVerReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String appVer = application.getAppVer();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String fireBaseAppVer = bundle.getString("AppVer");
                if (fireBaseAppVer != null && !appVer.equals("") && appVer.compareTo(fireBaseAppVer) >= 0)
                    if (!verChecked) {
                        Point scrSize = new Point();
                        getWindow().getWindowManager().getDefaultDisplay().getSize(scrSize);
                        moveLogo(-scrSize.y / 2 + 350, 1500);

                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            verChecked = true;
                            signUser();
                        }, 1400);

                    } else {
                        displayAlert(fireBaseAppVer);

                        Log.e(TAG, "Current ver: " + fireBaseAppVer);
                        Log.e(TAG, "App ver: " + appVer);
                    }
            }

        }
    };

    private BroadcastReceiver loginMessageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String message = bundle.getString("message");
                if (message != null) {
                    if (loginDialog != null && loginDialog.isShowing()) {
                        ((TextView) loginDialog.findViewById(R.id.textMessage)).setText(message);
                        loginDialog.findViewById(R.id.btn_register).setVisibility(View.VISIBLE);
                        loginDialog.findViewById(R.id.btn_forgetpwd).setVisibility(View.VISIBLE);
                    }
                }

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
                if (loginDialog != null) {
                    if (user.isEmailVerified()) {
                        application.getAuthProvider().getAuth().removeAuthStateListener(listener);
                        loginDialog.findViewById(R.id.login_phase1).setVisibility(View.GONE);
                        loginDialog.findViewById(R.id.login_phase2).setVisibility(View.VISIBLE);
                        //loginDialog.dismiss();
                        //updateUserDetails();

                        getData();
                    } else {
                        ((TextView) loginDialog.findViewById(R.id.textMessage)).setText(getString(R.string.verify_email_message));
                        user.sendEmailVerification();
                    }

                }

            } else {
                Log.e(TAG, "User logged out");
            }

        }
    };


}



