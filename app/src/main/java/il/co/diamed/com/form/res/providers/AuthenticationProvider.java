package il.co.diamed.com.form.res.providers;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class AuthenticationProvider {
    private final String TAG = "AuthenticationProvider";

    public AuthenticationProvider() {

    }

    public FirebaseUser getUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null)
            Log.d(TAG, "UpdateUI: " + currentUser.getDisplayName() + " - " + currentUser.getEmail());
        //SettingsActivity.NotificationPreferenceFragment.setUser(currentUser);

    }


    public void signin(final String email, final String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)

                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signinUserWithEmail:success");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.e(TAG, "signinUserWithEmail:failure", task.getException());
                        Log.w(TAG, "signinUserWithEmail:trying to create");
                        createUser(email, password); //create user if failed to sign in
                        // If sign in fails, display a message to the user.

                        //updateUI(null);
                    }

                    // ...
                });

    }

    private void createUser(final String email, final String password) {
        Date date = new Date();
        Log.d(TAG, "createUserWithEmail:starting " + date.getTime());
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success ");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "createUserWithEmail:failure", task.getException());
                        //updateUI(null);
                    }

                });
    }


    public FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }
}
