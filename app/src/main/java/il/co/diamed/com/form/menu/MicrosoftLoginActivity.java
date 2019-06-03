package il.co.diamed.com.form.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

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


public class MicrosoftLoginActivity extends Activity {

    private static final String TAG = "MicrosoftLogin: ";

    //private String MSGRAPH_URL = "https://mediforms-04052018.firebaseapp.com/__/auth/handler";
    final static String MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me";
    final static String SCOPES[] = {"https://graph.microsoft.com/User.Read"};
    final static String CLIENT_ID = "074d69f8-eed5-46ed-b577-13a834d0a716";
    private AuthenticationResult authResult;
    private PublicClientApplication sampleApp;
    JSONObject userDetails;
    LoginActivity _caller;
    ClassApplication application ;


    public MicrosoftLoginActivity(){}

    public MicrosoftLoginActivity(Activity callingActivity){
        application = (ClassApplication)callingActivity.getApplication();
        _caller = (LoginActivity)callingActivity;
        signinToMicrosoft();
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
                EndAcitivity(false);
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
                EndAcitivity(false);
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

                EndAcitivity(false);
            }

            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
                EndAcitivity(false);
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
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                Log.e(TAG, "User logged in");
            } else {
                Log.e(TAG, "User logged out");
            }
            EndAcitivity(true);
        }
    };

    private void EndAcitivity(boolean code) {
        if(code){

            //_caller.updateUserDetails(userDetails);
        }else{
           // _caller.updateUserDetails(null);
        }
        finish();
    }

}
