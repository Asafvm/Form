package il.co.diamed.com.form;

import android.app.Application;

import il.co.diamed.com.form.devices.Helper;

/**
 * Created by bennya on 04/05/2018.
 */

public class ClassApplication extends Application {

    AnalyticsProvider analyticsProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        analyticsProvider = new AnalyticsProvider(this);
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
}
