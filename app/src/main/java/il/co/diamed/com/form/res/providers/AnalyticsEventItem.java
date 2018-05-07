package il.co.diamed.com.form.res.providers;

import android.os.Bundle;


public class AnalyticsEventItem{

    public final static boolean PDF_CREATED = true;
    public final static boolean PDF_FAILED = false;

    protected String userId;
    protected String reportName;
    protected boolean isSuccessful;

    public AnalyticsEventItem(String userId,String reportName, boolean isSuccessful){
        this.userId = userId;
        this.reportName = reportName;
        this.isSuccessful = isSuccessful;
    }
    public AnalyticsEventItem(Bundle bundle){
        this.userId = bundle.getString("userId");
        this.reportName = bundle.getString("reportName");
        this.isSuccessful = bundle.getBoolean("isSuccessful");
    }
}
