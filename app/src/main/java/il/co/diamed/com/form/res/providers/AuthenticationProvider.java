package il.co.diamed.com.form.res.providers;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.DatePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

public class AuthenticationProvider {
    private final String TAG = "AuthenticationProvider";
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    public AuthenticationProvider() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        updateUI(mFirebaseUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null)
            Log.d(TAG, "UpdateUI: "+currentUser.getDisplayName()+" - "+currentUser.getEmail());
        //SettingsActivity.NotificationPreferenceFragment.setUser(currentUser);

    }

    public void signout(){
        mFirebaseAuth.signOut();
        
        new CountDownTimer(2000,2000){

            @Override
            public void onTick(long millisUntilFinished) {
                //do nothing
            }

            @Override
            public void onFinish() {
                //carryon
            }
        }.start();

    }



    public void signin(final String email, final String password) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signinUserWithEmail:success");
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            updateUI(mFirebaseUser);
                        } else {
                            Log.e(TAG, "signinUserWithEmail:failure", task.getException());
                            Log.w(TAG, "signinUserWithEmail:trying to create");
                            createUser(email,password); //create user if failed to sign in
                            // If sign in fails, display a message to the user.

                            //updateUI(null);
                        }

                        // ...
                    }
                });

    }

    public void createUser(final String email, final String password) {
        Date date = new Date();
        Log.d(TAG, "createUserWithEmail:starting "+date.getTime());
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success ");
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            updateUI(mFirebaseUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
                            //updateUI(null);
                        }

                    }
                });
    }



    public void updateUser(String email, String password) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");
                        }
                    }
                });

        user.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                        }
                    }
                });
    }

    public FirebaseUser getCurrentUser() {
        return mFirebaseUser;
    }
}
