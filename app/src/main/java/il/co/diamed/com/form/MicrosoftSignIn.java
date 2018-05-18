package il.co.diamed.com.form;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
//import com.microsoft.aad.adal.AuthenticationCallback;
//import com.microsoft.aad.adal.AuthenticationContext;
//import com.microsoft.aad.adal.AuthenticationResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.microsoft.aad.adal.AuthenticationContext;
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
import java.util.concurrent.Delayed;

public class MicrosoftSignIn extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CONTACTS = 0;
    private ProgressBar progressBar;

    final static String CLIENT_ID = "b3131887-d338-4d8d-a0fb-89c5db805612";
    public final static String AUTHORITY_URL = "https://login.microsoftonline.com/common";  //COMMON OR YOUR TENANT ID
    public final static String REDIRECT_URI = "http://localhost"; //REPLACE WITH YOUR REDIRECT URL

    private static final String TAG = "MicrosoftSignIn: ";
    private AuthenticationContext mAuthContext;

    //TEST
    private PublicClientApplication loggingApp;
    private AuthenticationResult authResult;
    final static String SCOPES[] = {"https://graph.microsoft.com/User.Read"};
    final static String MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        Log.d(TAG, "Logging Activity Started");

        progressBar = findViewById(R.id.pbLogin);
        progressBar.setProgress(20);

        super.onCreate(savedInstanceState);
        loggingApp = new PublicClientApplication(
                this.getApplicationContext(),
                CLIENT_ID);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            signout();
            
        }else
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            signIn();
        } else {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_CONTACTS);

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

                    signIn();
                } else {
                    // permission denied, boo!
                    Toast.makeText(getApplicationContext(), getString(R.string.noContactsPermission), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void signIn() {
        /* TEST */
        progressBar.setProgress(30);
        List<User> users = null;

        try {
            users = loggingApp.getUsers();

            if (users != null && users.size() == 1) {
                /* We have 1 user */

                loggingApp.acquireTokenSilentAsync(SCOPES, users.get(0), getAuthSilentCallback());
            } else {
                /* We have no user */

                /* Let's do an interactive request */
                loggingApp.acquireToken(this, SCOPES, getAuthInteractiveCallback());
            }
        } catch (MsalClientException e) {
            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());

        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "User at this position does not exist: " + e.toString());
        }
    }

    /**
     * TESTING BELOW
     **/
    public Activity getActivity() {
        return this;
    }

    /* Callback method for acquireTokenSilent calls
     * Looks if tokens are in the cache (refreshes if necessary and if we don't forceRefresh)
     * else errors that we need to do an interactive request.
     */
    private AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call Graph now */
                Log.d(TAG, "Successfully authenticated");

                /* Store the authResult */
                authResult = authenticationResult;

                /* call graph */
                callGraphAPI();

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
            }

            @Override
            public void onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.");
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

                /* call Graph */
                callGraphAPI();
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
            }

            @Override
            public void onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    /* Handles the redirect from the System Browser */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loggingApp.handleInteractiveRequestRedirect(requestCode, resultCode, data);
    }

    /* Use Volley to make an HTTP request to the /me endpoint from MS Graph using an access token */
    private void callGraphAPI() {
        Log.d(TAG, "Starting volley request to graph");
        progressBar.setProgress(50);
        /* Make sure we have a token to send to graph */
        if (authResult.getAccessToken() == null) {
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject parameters = new JSONObject();

        try {
            parameters.put("key", "value");
        } catch (Exception e) {
            Log.d(TAG, "Failed to put parameters: " + e.toString());
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, MSGRAPH_URL,
                parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                /* Successfully called graph, process data and send to UI */
                Log.d(TAG, "Response: " + response.toString());

                //CONNECTED TO MICROSOFT
                final ClassApplication application = (ClassApplication) getApplication();
                try {
                    application.signin(response.getString("mail"), response.getString("mail"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(60);
                        if (application.getCurrentUser() != null)
                            setUser(application.getCurrentUser(), response);

                    }
                },3000);


                //Intent intent = new Intent();
                //intent.putExtra("user_email",result.getUserInfo().getDisplayableId());
                //intent.putExtra("user_name",result.getUserInfo().getGivenName()+" "+result.getUserInfo().getFamilyName());


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


/*  backup
 mAuthContext = new AuthenticationContext(MicrosoftSignIn.this, AUTHORITY_URL, true);

        mAuthContext.acquireToken(
                MicrosoftSignIn.this,
                CLIENT_ID,
                CLIENT_ID,
                REDIRECT_URI,
                PromptBehavior.Auto,
                new AuthenticationCallback<AuthenticationResult>() {

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Error getting token: " + e.toString());

                        setResult(RESULT_CANCELED);
                        finish();
                    }

                    @Override
                    public void onSuccess(final AuthenticationResult result) {
                        Log.v(TAG, "Successfully obtained token, still need to validate");
                        if (result != null && !result.getAccessToken().isEmpty()) {

                            Log.e(TAG, result.getUserInfo().getGivenName()+" "+result.getUserInfo().getFamilyName());
                            final ClassApplication application = (ClassApplication)getApplication();
                            application.signin(result.getUserInfo().getDisplayableId(),result.getUserInfo().getDisplayableId());
                            new CountDownTimer(3000,3000){

                                @Override
                                public void onTick(long millisUntilFinished) {
                                    //do nothing
                                }

                                @Override
                                public void onFinish() {
                                    if(application.getCurrentUser()!=null)
                                        setUser(application.getCurrentUser(),result);
                                }
                            }.start();

                            //Intent intent = new Intent();
                            //intent.putExtra("user_email",result.getUserInfo().getDisplayableId());
                            //intent.putExtra("user_name",result.getUserInfo().getGivenName()+" "+result.getUserInfo().getFamilyName());


                        } else {
                            Log.e(TAG, "Error: token came back empty");

                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    }
                });
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Got Result");
        super.onActivityResult(requestCode, resultCode, data);
        mAuthContext.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,data.toString());

    }

    private void setUser(final FirebaseUser user, AuthenticationResult result) {

        Log.d(TAG, "Setting User Info");

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(result.getUserInfo().getGivenName()+" "+result.getUserInfo().getFamilyName())
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            updatePrefernces(user);
                        }
                    }
                });
    }

    public void updatePrefernces(FirebaseUser user){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor spedit = sharedPref.edit();
        spedit.putString("techName", user.getDisplayName());
        spedit.putString("techeMail", user.getEmail());
        spedit.putString("techePhone", user.getPhoneNumber());
        spedit.apply();

        setResult(RESULT_OK);
        finish();
    }



 */

    private void setUser(final FirebaseUser user, JSONObject response) {
        progressBar.setProgress(70);
        Log.d(TAG, "Setting User Info");

        UserProfileChangeRequest profileUpdates = null;
        try {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(response.getString("displayName"))
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
                            updatePrefernces(user);
                        }
                    }
                });
    }

    public void updatePrefernces(FirebaseUser user) {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor spedit = sharedPref.edit();
        spedit.putString("techName", user.getDisplayName());
        spedit.putString("techeMail", user.getEmail());
        spedit.putString("techePhone", user.getPhoneNumber());
        spedit.apply();

        setResult(RESULT_OK);
        finish();
    }

    private void signout() {
        FirebaseAuth.getInstance().signOut();
        /* Attempt to get a user and remove their cookies from cache */
        List<User> users = null;

        try {
            users = loggingApp.getUsers();

            if (users == null) {
                /* We have no users */

            } else if (users.size() == 1) {
                /* We have 1 user */
                /* Remove from token cache */
                loggingApp.remove(users.get(0));
            }
            else {
                /* We have multiple users */
                for (int i = 0; i < users.size(); i++) {
                    loggingApp.remove(users.get(i));
                }
            }



        } catch (MsalClientException e) {
            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());

        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "User at this position does not exist: " + e.toString());
        }

        setResult(RESULT_OK);
        finish();
    }
    
}