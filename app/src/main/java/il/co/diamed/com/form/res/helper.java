package il.co.diamed.com.form.res;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.transition.Scene;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.GelstationActivity;

public class helper extends AppCompatActivity {


    public void setLayout(Activity activity, int resLayout) {
        activity.setContentView(R.layout.activity_gelstation);
        View lowLayout = activity.findViewById(R.id.lowLayout);
        ViewGroup parent = (ViewGroup) lowLayout.getParent();
        int index = parent.indexOfChild(lowLayout);
        parent.removeView(lowLayout);
        lowLayout = activity.getLayoutInflater().inflate(resLayout, parent, false);
        parent.addView(lowLayout, index);

        helper.setButtons((ViewGroup)lowLayout);

    }

    public static void setButtons(ViewGroup layout) {

        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (view instanceof ViewGroup)
                setButtons((ViewGroup)view);
            else {
                if (view instanceof CheckBox) {
                    ((CheckBox) view).setChecked(true);

                }


                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }
    }





}
