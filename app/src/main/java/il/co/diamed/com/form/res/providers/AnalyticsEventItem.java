package il.co.diamed.com.form.res.providers;

import android.os.Bundle;


public class AnalyticsEventItem{

    public final static boolean PDF_CREATED = true;
    public final static boolean PDF_FAILED = false;

    protected String activityType;
    protected String userId;
    protected String reportName;
    protected boolean isSuccessful;
    protected String addInfo;

    public AnalyticsEventItem(String activityType, String userId,String reportName, boolean isSuccessful, String addInfo){
        this.activityType = activityType;
        this.userId = userId;
        this.reportName = reportName;
        this.isSuccessful = isSuccessful;
        this.addInfo = addInfo;
    }
}
