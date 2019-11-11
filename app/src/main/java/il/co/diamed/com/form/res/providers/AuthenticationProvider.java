package il.co.diamed.com.form.res.providers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class AuthenticationProvider {
    private final String TAG = "AuthenticationProvider";
    final static String CLIENT_ID = "074d69f8-eed5-46ed-b577-13a834d0a716";
    public static final String BROADCAST_MESSAGE = "login_message";
    private static AuthenticationProvider _firebase;

    private AuthenticationProvider() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
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
                            broadcastMessage(task.getException().getMessage(), currentContext);


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
                            user.sendEmailVerification().addOnCompleteListener(task1 -> {
                                Log.d(TAG, "Confimation Email sent.");
                                broadcastMessage("Confimation Email sent", currentContext);


                            });
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        if(task.getException() != null) {
                            Log.e(TAG, "createUserWithEmail:failure");
                            Log.e(TAG, "Message: " + task.getException().getMessage());
                            broadcastMessage(task.getException().getMessage(), currentContext);


                        }
                        //updateUI(null);
                    }


                });
    }

    private void broadcastMessage(String message, Context currentContext) {
        Intent i = new Intent(BROADCAST_MESSAGE);
        i.putExtra("message", message);
        currentContext.sendBroadcast(i);
    }

    public void forgotPassword(String email, Context currentContext){

      FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Reset Email sent");
                        broadcastMessage("Reset Email sent", currentContext);

                        if(task.getException() != null) {
                            broadcastMessage(task.getException().getMessage(), currentContext);
                        }
                    }

                });


    }

    public FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }





}
