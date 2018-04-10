package il.co.diamed.com.form;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

public class LoginActivity2 extends AppCompatActivity {final static String CLIENT_ID = "b3131887-d338-4d8d-a0fb-89c5db805612";
final static String SCOPES [] = {"https://graph.microsoft.com/User.Read"};
final static String MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me";
/* UI &amp; Debugging Variables */
private static final String TAG = "LoginActivity2: ";

/* Azure AD Variables */
private PublicClientApplication sampleApp;
private AuthenticationResult authResult;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //setContentView(R.layout.activity_login2);
    if(authResult==null){
        Log.e(TAG, "no access token");
    }else {
        if (authResult.getAccessToken() == null || authResult.getAccessToken().equals(""))
            Log.e(TAG, "no access token");
        else {
            Log.e(TAG, authResult.getAccessToken());

            callGraphAPI();
        }
    }
    Bundle bundle = getIntent().getExtras();    //get log command

/* Configure your sample app and save state for this activity */
        sampleApp = null;
        if (sampleApp == null) {
            sampleApp = new PublicClientApplication(
                    this.getApplicationContext(),
                    CLIENT_ID);
        }

/* Attempt to get a user and acquireTokenSilent
* If this fails we do an interactive request
*/

        List<User> users;    try {
        users = sampleApp.getUsers();

        if (users != null && users.size() == 1) {
    /* We have 1 user */

            sampleApp.acquireTokenSilentAsync(SCOPES, users.get(0), getAuthSilentCallback());
        } else {
    /* We have no user */

    /* Let's do an interactive request */
            sampleApp.acquireToken(this, SCOPES, getAuthInteractiveCallback());
        }
    } catch (MsalClientException e) {
        Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());

    } catch (IndexOutOfBoundsException e) {
        Log.d(TAG, "User at this position does not exist: " + e.toString());
    }

    switch(bundle.getInt("log")){
        case 0:                         //login
            Log.e(TAG,"signin");
            onCallGraphClicked();
            break;
        case 1:                         //logout
            Log.e(TAG,"signout");
            onSignOutClicked();
            break;
        default:
            Log.e(TAG,"Not a valid command "+String.valueOf(bundle.getInt("log")));
            break;
    }

}
// App callbacks for MSAL
// ======================
// getActivity() - returns activity so we can acquireToken within a callback
// getAuthSilentCallback() - callback defined to handle acquireTokenSilent() case
// getAuthInteractiveCallback() - callback defined to handle acquireToken() case
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
            Log.e(TAG, "onSilentSuccess");
        /* Store the authResult */
            authResult = authenticationResult;

        /* call graph */
            callGraphAPI();

        /* update the UI to post call Graph state */
            //updateSuccessUI();
        }

        @Override
        public void onError(MsalException exception) {
        /* Failed to acquireToken */
            Log.d(TAG, "Authentication failed: " + exception.toString());
            Log.e(TAG, "onSilentError");
            if (exception instanceof MsalClientException) {
                Log.e(TAG, "got MsalClientException");

                /* Exception inside MSAL, more info inside MsalError.java */
            } else if (exception instanceof MsalServiceException) {
                Log.e(TAG, "got MsalServiceException");

                /* Exception when communicating with the STS, likely config issue */
            } else if (exception instanceof MsalUiRequiredException) {
                Log.e(TAG, "got MsalUiRequiredException");

                /* Tokens expired or no session, retry with interactive */
            }
            setResult(RESULT_CANCELED);
            finish();
        }

        @Override
        public void onCancel() {
        /* User cancelled the authentication */
            Log.d(TAG, "User cancelled login.");
            setResult(RESULT_CANCELED);
            finish();
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
            Log.e(TAG, "onInteractiveSuccess");
        /* Store the auth result */
            authResult = authenticationResult;

        /* call Graph */
            callGraphAPI();

        /* update the UI to post call Graph state */
            //updateSuccessUI();
        }

        @Override
        public void onError(MsalException exception) {
        /* Failed to acquireToken */
            Log.d(TAG, "Authentication failed: " + exception.toString());
            Log.e(TAG, "onInteractiveError");
            if (exception instanceof MsalClientException) {
                Log.e(TAG, "got MsalClientException");
            /* Exception inside MSAL, more info inside MsalError.java */
            } else if (exception instanceof MsalServiceException) {
                Log.e(TAG, "got MsalServiceException");
            /* Exception when communicating with the STS, likely config issue */
            }
            setResult(RESULT_CANCELED);
            finish();
        }

        @Override
        public void onCancel() {
        /* User cancelled the authentication */
            Log.d(TAG, "User cancelled login.");
            setResult(RESULT_CANCELED);
            finish();
        }
    };
}

/* Set the UI for successful token acquisition data */
private void updateSuccessUI(JSONObject response) {

    Intent intent = new Intent();

    Log.e("MicroLog: ","got here");
    intent.putExtra("fullName",authResult.getUser().getName());
    try {
        intent.putExtra("eMail",response.getString("mail"));
    } catch (JSONException e) {
        Log.e("LOGIN: ","could not get mail");
    }
    setResult(RESULT_OK, intent);
    finish();

}

/* Use MSAL to acquireToken for the end-user
* Callback will call Graph api w/ access token &amp; update UI
*/
private void onCallGraphClicked() {
    Log.e(TAG, "Trying to acquire token");
    sampleApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
}

/* Handles the redirect from the System Browser */
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    sampleApp.handleInteractiveRequestRedirect(requestCode, resultCode, data);
}
//}

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

            updateSuccessUI(response);
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d(TAG, "Error: " + error.toString());
            setResult(RESULT_CANCELED);
            finish();
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

/* Clears a user's tokens from the cache.
 * Logically similar to "sign out" but only signs out of this app.
 */
private void onSignOutClicked() {

    /* Attempt to get a user and remove their cookies from cache */
    List<User> users = null;

    try {
        users = sampleApp.getUsers();

        if (users == null) {
            /* We have no users */

        } else if (users.size() == 1) {
            /* We have 1 user */
            /* Remove from token cache */
            sampleApp.remove(users.get(0));

        }
        else {
            /* We have multiple users */
            for (int i = 0; i < users.size(); i++) {
                sampleApp.remove(users.get(i));
            }
        }

        Toast.makeText(getBaseContext(), "Signed Out!", Toast.LENGTH_SHORT)
                .show();

    } catch (MsalClientException e) {
        Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());

    } catch (IndexOutOfBoundsException e) {
        Log.d(TAG, "User at this position does not exist: " + e.toString());
    }
}


}

