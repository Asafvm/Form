package il.co.diamed.com.form.res.providers;

import android.util.Log;

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
