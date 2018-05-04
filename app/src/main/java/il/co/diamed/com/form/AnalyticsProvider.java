package il.co.diamed.com.form;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by bennya on 04/05/2018.
 */

public class AnalyticsProvider {

    private FirebaseAnalytics mFirebaseAnalytics;



    public AnalyticsProvider(Context context){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void logAnalyticsEvent(AnalyticsEventItem item){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, item.itemId);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,item.itemName);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, item.contentType);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void logAnalyticsScreen(AnalyticsScreenItem item) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,item.screenName);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, FirebaseAnalytics.Event.VIEW_ITEM);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM,bundle);
    }
}
