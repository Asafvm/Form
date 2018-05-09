package il.co.diamed.com.form.res.providers;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.signin.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserManagmentProvider{ 
    private final String TAG = "UserManagmentProvider";
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;

    public UserManagmentProvider(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }


    public FirebaseUser getUser(){
        return mFirebaseAuth.getCurrentUser();
    }


    public void signout() {
        mFirebaseAuth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return mFirebaseAuth.getCurrentUser();
    }



    public FirebaseUser signin(final String email, final String password) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            //updateUI(null);
                        }

                        // ...
                    }
                });
        return mFirebaseAuth.getCurrentUser();
    }

    public void createUser(final String email, final String password) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            signin(email, password);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //updateUI(null);
                        }

                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null)
            Log.d(TAG, "UpdateUI: "+currentUser.getDisplayName()+" - "+currentUser.getEmail());
        //SettingsActivity.NotificationPreferenceFragment.setUser(currentUser);

    }
}
