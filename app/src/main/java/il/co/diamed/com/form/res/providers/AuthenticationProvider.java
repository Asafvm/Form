package il.co.diamed.com.form.res.providers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.AuthenticationResult;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuthenticationProvider {
    private final String TAG = "AuthenticationProvider";
    final static String CLIENT_ID = "074d69f8-eed5-46ed-b577-13a834d0a716";
    public static final String BROADCAST_MESSAGE = "login_message";
    private static AuthenticationProvider _firebase;
    private static PublicClientApplication sampleApp;
    private FirebaseAuth firebaseAuth;

    private AuthenticationProvider() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static AuthenticationProvider GetAuthenticationProvider() {
        if (_firebase != null)
            return _firebase;
        else
            return new AuthenticationProvider();
    }

    public FirebaseUser getUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null)
            Log.d(TAG, "UpdateUI: " + currentUser.getDisplayName() + " - " + currentUser.getEmail());
        //SettingsActivity.NotificationPreferenceFragment.setUser(currentUser);

    }


    public void signin(final String email, final String password, Context currentContext) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)

                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signinUserWithEmail:success");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        updateUI(user);
                    } else {
                        if(task.getException() != null) {
                            Log.e(TAG, "signinUserWithEmail:failure");
                            Log.e(TAG, "Message: " + task.getException().getMessage());
                            Intent i = new Intent(BROADCAST_MESSAGE);
                            i.putExtra("message", task.getException().getMessage());
                            currentContext.sendBroadcast(i);

                        }
                        else
                            Log.e(TAG, "signinUserWithEmail:failure" + task.getException());

                        //Log.w(TAG, "signinUserWithEmail:trying to create");
                        //createUser(email, password); //create user if failed to sign in
                        // If sign in fails, display a message to the user.

                        //updateUI(null);
                    }

                    // ...
                });

    }

    public void createUser(final String email, final String password, Context currentContext) {
        Date date = new Date();
        Log.d(TAG, "createUserWithEmail:starting " + date.getTime());
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success ");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user != null) {
                            updateUI(user);
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "Confimation Email sent.");
                                }
                            });
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        if(task.getException() != null) {
                            Log.e(TAG, "createUserWithEmail:failure");
                            Log.e(TAG, "Message: " + task.getException().getMessage());
                            Intent i = new Intent(BROADCAST_MESSAGE);
                            i.putExtra("message", task.getException().getMessage());
                            currentContext.sendBroadcast(i);

                        }
                        //updateUI(null);
                    }

                });
    }

    public void forgotPassword(String email, Context currentContext){

      FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Reset Email sent.");
                        if(task.getException() != null) {
                            Intent i = new Intent(BROADCAST_MESSAGE);
                            i.putExtra("message", task.getException().getMessage());
                            currentContext.sendBroadcast(i);
                        }


                    }
                });


    }

    public FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }




    public void signinToMicrosoftFirebase(Activity activity) {
        Log.e(TAG, "Signing with microsoft");
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("microsoft.com");

        List<String> scopes =
                new ArrayList<String>() {
                    {
                        add("user.read");
                    }
                };
        provider.setScopes(scopes);

        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Log.e(TAG, "Pending Success");
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Pending Failed: " + e.getMessage());
                                    // Handle failure.
                                }
                            });
        } else {
            Log.e(TAG, "No Pending");
            // There's no pending result so you need to start the sign-in flow.
            // See below.
        }

        firebaseAuth
                .startActivityForSignInWithProvider(activity, provider.build())
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Log.e(TAG, "Auth Success");
                                // User is signed in.
                                // IdP data available in
                                // authResult.getAdditionalUserInfo().getProfile().
                                // The OAuth access token can also be retrieved:
                                // authResult.getCredential().getAccessToken().
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Auth Failed: " + e.getMessage() + "\nTrace: " + e.getClass().getCanonicalName());
                                // Handle failure.
                            }
                        });

// The user is already signed-in.
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Log.e(TAG, "User already logged in");
            firebaseUser
                    .startActivityForLinkWithProvider(/* activity= */ activity, provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // Microsoft credential is linked to the current user.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                }
                            });
        }
        /*
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MicrosoftSigninFragment mMicrosoftSigninFragment = new MicrosoftSigninFragment();)
        fragmentTransaction.replace(R.id.fragment_container, mMicrosoftSigninFragment).commitAllowingStateLoss();*/

    }

    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call graph now */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "ID Token: " + authenticationResult.getIdToken());
                /* Store the auth result */
                //authResult = authenticationResult;
                /* call Graph */
                //callGraphAPI();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Interactive Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                }
                //LoginActivity activity = (LoginActivity) getActivity();
            }

            @Override
            public void onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }

    private AuthenticationCallback getAuthSilentCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(AuthenticationResult authenticationResult) {
                /* Successfully got a token, call Graph now */
                Log.d(TAG, "Successfully authenticated");

                /* Store the authResult */
                //authResult = authenticationResult;

                /* call graph */
                //callGraphAPI();

            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Silent Authentication failed: " + exception.toString());

                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                } else if (exception instanceof MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                }
                //LoginActivity activity = (LoginActivity) getActivity();
                //signIn();
            }

            @Override
            public void onCancel() {
                /* User cancelled the authentication */
                Log.d(TAG, "User cancelled login.");
                //LoginActivity activity = (LoginActivity) getActivity();
                //if (activity != null) {
                //    activity.quitApp("User cancelled login");
                //}
            }
        };
    }
}
