package il.co.diamed.com.form;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import il.co.diamed.com.form.menu.LoginActivity;
import il.co.diamed.com.form.res.providers.AnalyticsEventItem;
import il.co.diamed.com.form.res.providers.AnalyticsProvider;
import il.co.diamed.com.form.res.providers.AnalyticsScreenItem;
import il.co.diamed.com.form.res.providers.AuthenticationProvider;
import il.co.diamed.com.form.menu.SettingsActivity;
import il.co.diamed.com.form.res.providers.StorageProvider;
import io.fabric.sdk.android.Fabric;


public class ClassApplication extends Application {
    private final String TAG = "ClassApplication";
    AnalyticsProvider analyticsProvider;
    StorageProvider storageProvider;
    AuthenticationProvider authenticationProvider;
    SettingsActivity settings;
    @Override
    public void onCreate() {
        super.onCreate();
        settings = new SettingsActivity();
        Fabric.with(this, new Crashlytics());
        analyticsProvider = new AnalyticsProvider(this);
        storageProvider = new StorageProvider();
        authenticationProvider = new AuthenticationProvider();

        // TODO: Move this to where you establish a user session
        logUser();
    }
    public FirebaseAuth getAuthProvider(){
        return authenticationProvider.getAuth();
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
            Crashlytics.setUserIdentifier(user.getPhoneNumber());
            Crashlytics.setUserEmail(user.getEmail());
            Crashlytics.setUserName(user.getDisplayName());
        }
    }

    public void logAnalyticsEvent(AnalyticsEventItem eventItem) {
        try {
            analyticsProvider.logAnalyticsEvent(eventItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logAnalyticsScreen(AnalyticsScreenItem analyticsScreenItem) {
        analyticsProvider.logAnalyticsScreen(analyticsScreenItem);
    }

    public void uploadFile(String dest, String destArray){
        storageProvider.uploadFile(dest,destArray);
    }

    public void signin(String email, String password){
        authenticationProvider.signin(email,password);
    }

    public void getDir(String path) {
        storageProvider.getDir(path);
    }

    public void bindPreference(Preference preference){
        SettingsActivity.bindPreferenceSummaryToValue(preference);

    }


}
