package il.co.diamed.com.form.menu;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.MsalClientException;
import com.microsoft.identity.client.MsalException;
import com.microsoft.identity.client.MsalServiceException;
import com.microsoft.identity.client.MsalUiRequiredException;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.providers.AnalyticsScreenItem;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

public class LoginActivity extends AppCompatActivity implements
        UserSetupFragment.OnFragmentInteractionListener{
    private static final int MY_PERMISSIONS_REQUEST_CONTACTS = 0;
    private static final String TAG = "Login";
    private ClassApplication application;
    private FirebaseUser user;
    boolean verChecked = false;

    final static String MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me";
    final static String SCOPES[] = {"https://graph.microsoft.com/User.Read"};
    final static String CLIENT_ID = "e4768b21-a07f-4d35-be0d-8aac21757356";
    private AuthenticationResult authResult;
    private PublicClientApplication sampleApp;
    JSONObject userDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setProgressInfo("Checking Latest Version", 0);

        application = (ClassApplication) getApplication();
        application.logAnalyticsScreen(new AnalyticsScreenItem(this.getClass().getName()));
        FirebaseUser user;

        moveLogo(400, 0);
        moveLogo(0, 1100);

        application.getDatabaseProvider(this).getAppVer();
        //checkVersion();


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

        user = null;//FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "No active user");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                setProgressInfo("Logging to Microsoft", 30);
                signinToMicrosoft();
                //application.getAuthProvider().signinToMicrosoftFirebase(this);
                //new MicrosoftLoginActivity(this);
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

    }


    public void quitApp(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(() -> android.os.Process.killProcess(android.os.Process.myPid()), 2000);
    }


    public void updateUserDetails() {
        setProgressInfo("Updating info", 70);

        if(userDetails == null){
            quitApp("No user info");
        }
        else {
            Log.d(TAG, "Setting User Info");

            UserProfileChangeRequest profileUpdates = null;
            try {
                profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userDetails.getString("displayName"))
                        .build();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (profileUpdates != null) {
                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor spedit = sharedPref.edit();
                                if (sharedPref.getString("techName", "").equals(""))
                                    spedit.putString("techName", user.getDisplayName());
                                if (sharedPref.getString("techeMail", "").equals(""))
                                    spedit.putString("techeMail", user.getEmail());
                                if (sharedPref.getString("techePhone", "").equals(""))
                                    spedit.putString("techePhone", user.getPhoneNumber());
                                spedit.apply();

                                updateTechTools();
                            }
                        });
            }
        }
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



    public void signinToMicrosoft() {
        /* Configure your sample app and save state for this activity */
        sampleApp = null;

        sampleApp = new PublicClientApplication(
                getApplicationContext(),
                CLIENT_ID);


        /* Attempt to get a user and acquireTokenSilent
         * If this fails we do an interactive request
         */
        List<User> users = null;

        try {
            users = sampleApp.getUsers();
            if (users != null && users.size() == 1) {
                /* We have 1 user */
                sampleApp.acquireTokenSilentAsync(SCOPES, users.get(0), getAuthSilentCallback());
            } else {
                /* We have no user */
                /* Let's do an interactive request */
                sampleApp.acquireToken(Objects.requireNonNull(getActivity()), SCOPES, getAuthInteractiveCallback());
            }

        } catch (MsalClientException e) {
            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());

        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "User at this position does not exist: " + e.toString());
        }

    }

    //
    // Core Identity methods used by MSAL
    // ==================================
    // onActivityResult() - handles redirect from System browser
    // onCallGraphClicked() - attempts to get tokens for graph, if it succeeds calls graph & updates UI
    // onSignOutClicked() - Signs account out of the app & updates UI
    // callGraphAPI() - called on successful token acquisition which makes an HTTP request to graph
    //

    /* Handles the redirect from the System Browser */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        sampleApp.handleInteractiveRequestRedirect(requestCode, resultCode, data);
    }


    //
    // Helper methods manage UI updates
    // ================================
    // updateGraphUI() - Sets graph response in UI
    // updateSuccessUI() - Updates UI when token acquisition succeeds
    // updateSignedOutUI() - Updates UI when app sign out succeeds
    //

    /* Sets the graph response */
    private void updateGraphUI(JSONObject graphResponse) {

        userDetails = graphResponse;

        try {
            String eMail = userDetails.getString("mail");

            application.getAuthProvider().getAuth().addAuthStateListener(listener);
            application.signin(eMail, eMail);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /* Set the UI for successful token acquisition data */
    private void updateSuccessUI() {



    }

    /* Set the UI for signed out account */
    private void updateSignedOutUI() {
        Log.e(TAG, "Microsoft logout success");
    }

    //
    // App callbacks for MSAL
    // ======================
    // getActivity() - returns activity so we can acquireToken within a callback
    // getAuthSilentCallback() - callback defined to handle acquireTokenSilent() case
    // getAuthInteractiveCallback() - callback defined to handle acquireToken() case
    //

    public Activity getActivity() {
        return this;
    }

    /* Callback used in for silent acquireToken calls.
     * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
     * else errors that we need to do an interactive request.
     */
    private AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");

                /* Store the authResult */
                authResult = authenticationResult;

                /* call graph */
                callGraphAPI();

                /* update the UI to post call graph state */
                updateSuccessUI();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                } else if (exception instanceof MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                }
                quitApp("Error login to microsoft");
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
                quitApp("Error login to microsoft");
            }
        };
    }

    /* Callback used for interactive request.  If succeeds we use the access
     * token to call the Microsoft Graph. Does not check cache
     */
    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "ID Token: " + authenticationResult.getIdToken());

                /* Store the auth result */
                authResult = authenticationResult;

                /* call graph */
                callGraphAPI();

                /* update the UI to post call graph state */
                updateSuccessUI();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                }

                quitApp("Error login to microsoft");
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
                quitApp("Error login to microsoft");
            }
        };
    }

    /* Use Volley to make an HTTP request to the /me endpoint from MS Graph using an access token */
    private void callGraphAPI() {
        Log.d(TAG, "Starting volley request to graph");

        /* Make sure we have a token to send to graph */
        if (authResult.getAccessToken() == null) {return;}

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("key", "value");
        } catch (Exception e) {
            Log.d(TAG, "Failed to put parameters: " + e.toString());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, MSGRAPH_URL,
                parameters,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /* Successfully called graph, process data and send to UI */
                Log.d(TAG, "Response: " + response.toString());

                updateGraphUI(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authResult.getAccessToken());
                return headers;
            }
        };

        Log.d(TAG, "Adding HTTP GET to Queue, Request: " + request.toString());

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    final FirebaseAuth.AuthStateListener listener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            user = firebaseAuth.getCurrentUser();

            if (user != null) {
                Log.e(TAG, "User logged in");
                updateUserDetails();
            } else {
                Log.e(TAG, "User logged out");
            }

        }
    };

}



