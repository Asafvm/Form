package il.co.diamed.com.form;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

import il.co.diamed.com.form.res.providers.AnalyticsEventItem;
import il.co.diamed.com.form.res.providers.AnalyticsProvider;
import il.co.diamed.com.form.res.providers.AnalyticsScreenItem;
import il.co.diamed.com.form.res.providers.AuthenticationProvider;
import il.co.diamed.com.form.res.providers.DatabaseProvider;
import il.co.diamed.com.form.res.providers.StorageProvider;
import io.fabric.sdk.android.Fabric;


public class ClassApplication extends Application {
    private static final String TAG = "ClassApplication";
    AnalyticsProvider analyticsProvider;
    StorageProvider storageProvider;
    AuthenticationProvider mAuthenticationProvider;
    DatabaseProvider mDatabaseProvider;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        analyticsProvider = new AnalyticsProvider(this);
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


    public void signin(String email, String password){
        mAuthenticationProvider.signin(email,password);
    }


    public StorageProvider getStorageProvider(Context context)
    {
        storageProvider = new StorageProvider(this);
        return storageProvider;
    }

    public DatabaseProvider getDatabaseProvider(Context context)
    {
        mDatabaseProvider = new DatabaseProvider(context);
        return mDatabaseProvider;
    }



    public static void deleteCache(Context context) {
        try {
            Log.e(TAG, "Deleting cache");
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception ignored) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }

    public String getAppVer() {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
