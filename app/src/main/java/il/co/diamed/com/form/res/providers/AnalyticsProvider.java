package il.co.diamed.com.form.res.providers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import il.co.diamed.com.form.AnalyticsScreenItem;
import il.co.diamed.com.form.res.providers.AnalyticsEventItem;

public class AnalyticsProvider {

    private final String TECH_NAME = "Tech_Name";
    private final String REPORT_NAME = "Report_Name";
    private final String IS_SUCCESSFUL = "Is_Successful";
    private final String PDF_ACTIVITY = "PDF_Activity";

    private FirebaseAnalytics mFirebaseAnalytics;



    public AnalyticsProvider(Context context){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @SuppressLint("InvalidAnalyticsName")
    public void logAnalyticsEvent(AnalyticsEventItem item){
        Bundle bundle = new Bundle();
        bundle.putString(TECH_NAME, item.userId);
        bundle.putString(REPORT_NAME,item.reportName);
        bundle.putBoolean(IS_SUCCESSFUL, item.isSuccessful);
        mFirebaseAnalytics.logEvent(PDF_ACTIVITY, bundle);
    }

    public void logAnalyticsScreen(AnalyticsScreenItem item) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,item.screenName);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, FirebaseAnalytics.Event.VIEW_ITEM);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM,bundle);
    }
}
