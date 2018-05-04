package il.co.diamed.com.form.res;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class AnalyticsProvider {
    private FirebaseAnalytics mFirebaseAnalytics;
    public AnalyticsProvider(Context context){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle analytics = new Bundle();
        analytics.putString(FirebaseAnalytics.Param.CONTENT,"App Opened");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT,analytics);
    }
}
