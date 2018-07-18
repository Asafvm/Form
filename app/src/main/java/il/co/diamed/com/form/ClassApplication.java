package il.co.diamed.com.form;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import il.co.diamed.com.form.res.providers.AnalyticsEventItem;
import il.co.diamed.com.form.res.providers.AnalyticsProvider;
import il.co.diamed.com.form.res.providers.AnalyticsScreenItem;
import il.co.diamed.com.form.res.providers.AuthenticationProvider;
import il.co.diamed.com.form.res.providers.DatabaseProvider;
import il.co.diamed.com.form.res.providers.StorageProvider;
import io.fabric.sdk.android.Fabric;


public class ClassApplication extends Application {
    private final String TAG = "ClassApplication";
    AnalyticsProvider analyticsProvider;
    StorageProvider storageProvider;
    AuthenticationProvider mAuthenticationProvider;
    DatabaseProvider mDatabaseProvider;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        analyticsProvider = new AnalyticsProvider(this);
        storageProvider = new StorageProvider();
        mAuthenticationProvider = new AuthenticationProvider();

        logUser();
    }
    public FirebaseAuth getAuthProvider(){
        return mAuthenticationProvider.getAuth();
    }

    private void logUser() {
        // You can call any combination of these three methods
        if(mAuthenticationProvider.getUser()!=null) {
            FirebaseUser user = mAuthenticationProvider.getUser();
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
        mAuthenticationProvider.signin(email,password);
    }

    public DatabaseProvider getDatabaseProvider(Context context)
    {
        mDatabaseProvider = new DatabaseProvider(context);
        return mDatabaseProvider;
    }




}
