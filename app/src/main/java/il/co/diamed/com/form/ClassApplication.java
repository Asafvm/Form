package il.co.diamed.com.form;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import il.co.diamed.com.form.res.providers.AnalyticsEventItem;
import il.co.diamed.com.form.res.providers.AnalyticsProvider;
import il.co.diamed.com.form.res.providers.AuthenticationProvider;
import il.co.diamed.com.form.res.providers.StorageProvider;
import io.fabric.sdk.android.Fabric;


public class ClassApplication extends Application {

    AnalyticsProvider analyticsProvider;
    StorageProvider storageProvider;
    AuthenticationProvider authenticationProvider;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        analyticsProvider = new AnalyticsProvider(this);
        storageProvider = new StorageProvider();
        authenticationProvider= new AuthenticationProvider();

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



}
