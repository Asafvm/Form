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


    public UserManagmentProvider(){

    }





    public void signout() {
        FirebaseAuth.getInstance().signOut();
    }

    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null)
            Log.d(TAG, "UpdateUI: "+currentUser.getDisplayName()+" - "+currentUser.getEmail());
        //SettingsActivity.NotificationPreferenceFragment.setUser(currentUser);

    }
}
