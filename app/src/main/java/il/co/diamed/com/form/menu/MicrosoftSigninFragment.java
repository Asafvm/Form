package il.co.diamed.com.form.menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MicrosoftSigninFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MicrosoftSigninFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MicrosoftSigninFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_CONTACTS = 0;
    private ProgressBar progressBar;
    final static String CLIENT_ID = "b3131887-d338-4d8d-a0fb-89c5db805612";
    private static final String TAG = "MicrosoftSignInF: ";
    private PublicClientApplication loggingApp;
    private AuthenticationResult authResult;
    final static String SCOPES[] = {"https://graph.microsoft.com/User.Read"};
    final static String MSGRAPH_URL = "https://graph.microsoft.com/v1.0/me";

    private OnFragmentInteractionListener mListener;

    public MicrosoftSigninFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MicrosoftSigninFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MicrosoftSigninFragment newInstance(String param1, String param2) {
        MicrosoftSigninFragment fragment = new MicrosoftSigninFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_login, container, false);
        Log.d(TAG, "Logging Activity Started");
        super.onCreate(savedInstanceState);

        progressBar = view.findViewById(R.id.pbLogin);
        progressBar.setProgress(20);
        loggingApp = new PublicClientApplication(
                getContext(),
                CLIENT_ID);
        Log.d(TAG, "Logging signin");
        signIn();
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
            } else {
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

        //setResult(RESULT_OK);
        //finish();
    }

    public void signIn() {
        Log.e(TAG, "signIn");
        /* TEST */
        progressBar.setProgress(30);
        List<User> users = null;

        try {
            users = loggingApp.getUsers();

            if (users != null && users.size() == 1) {
                /* We have 1 user */
                Log.e(TAG, "signIn2");
                loggingApp.acquireTokenSilentAsync(SCOPES, users.get(0), getAuthSilentCallback());
            } else {
                /* We have no user */
                Log.e(TAG, "signIn3");
                /* Let's do an interactive request */
                loggingApp.acquireToken(getActivity(), SCOPES, getAuthInteractiveCallback());
            }
        } catch (MsalClientException e) {
            Log.d(TAG, "MSAL Exception Generated while getting users: " + e.toString());

        } catch (IndexOutOfBoundsException e) {
            Log.d(TAG, "User at this position does not exist: " + e.toString());
        }
    }

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
                //LoginActivity activity = (LoginActivity) getActivity();
                signIn();
            }

            @Override
            public void onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.");
                LoginActivity activity = (LoginActivity) getActivity();
                activity.quitApp("User cancelled login");
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
                //LoginActivity activity = (LoginActivity) getActivity();
                signIn();
            }

            @Override
            public void onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.");
                LoginActivity activity = (LoginActivity) getActivity();
                activity.quitApp("User cancelled login");
            }
        };
    }
    /* Handles the redirect from the System Browser */

    /* Handles the redirect from the System Browser */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode,resultCode,data);
        if (resultCode == 2001) {
            LoginActivity activity = (LoginActivity) getActivity();
            activity.quitApp("Canceled");
        } else {
            loggingApp.handleInteractiveRequestRedirect(requestCode, resultCode, data);
        }


        Log.e(TAG, "got result " + resultCode);
    }

    /* Use Volley to make an HTTP request to the /me endpoint from MS Graph using an access token */
    private void callGraphAPI() {
        Log.d(TAG, "Starting volley request to graph");
        progressBar.setProgress(50);
        /* Make sure we have a token to send to graph */
        if (authResult.getAccessToken() == null) {
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(getContext());
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
                final ClassApplication application = (ClassApplication) getActivity().getApplication();
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
                }, 3000);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.toString());
                LoginActivity activity = (LoginActivity) getActivity();
                activity.quitApp("Error Response");
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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor spedit = sharedPref.edit();
        spedit.putString("techName", user.getDisplayName());
        spedit.putString("techeMail", user.getEmail());
        spedit.putString("techePhone", user.getPhoneNumber());
        spedit.apply();
        LoginActivity activity = (LoginActivity) getActivity();
        activity.signin();
        //setResult(RESULT_OK);
        //finish();
    }
}
