package il.co.diamed.com.form;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import il.co.diamed.com.form.res.providers.AnalyticsEventItem;
import il.co.diamed.com.form.res.providers.AnalyticsProvider;
import il.co.diamed.com.form.res.providers.AuthenticationProvider;
import il.co.diamed.com.form.res.providers.StorageProvider;
import io.fabric.sdk.android.Fabric;


public class ClassApplication extends Application {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 0;
    private final String TAG = "ClassApplication";
    AnalyticsProvider analyticsProvider;
    StorageProvider storageProvider;
    AuthenticationProvider authenticationProvider;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        analyticsProvider = new AnalyticsProvider(this);
        storageProvider = new StorageProvider();
        authenticationProvider = new AuthenticationProvider();

        // TODO: Move this to where you establish a user session
        logUser();

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

    public void signout() {
        authenticationProvider.signout();
    }

    public FirebaseUser getCurrentUser() {
        return authenticationProvider.getCurrentUser();
    }

    public void getDir(String path) {
        storageProvider.getDir(path);
    }


}
