package il.co.diamed.com.form.calibration.res;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

public class DeviceDialogFragment extends DialogFragment {
    private final String TAG = "DeviceDialogFragment";
    ClassApplication application;
    DatabaseProvider provider;
    AutoCompleteTextView textLoction;
    AutoCompleteTextView textSubloction;


    public interface OnLocationSelected {
        void sendLocation(String location, String sublocation);
    }

    public OnLocationSelected mOnLocationSelected;

    static DeviceDialogFragment newInstance() {
        return new DeviceDialogFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        }
        if (getContext() != null) {
            View v = inflater.inflate(R.layout.fragment_device_dialog, container, false);

            this.setCancelable(false);


            //get parts serial list
            application = (ClassApplication) getActivity().getApplication();
            provider = application.getDatabaseProvider(getContext());

            textLoction = v.findViewById(R.id.et_device_locaion);
            textSubloction = v.findViewById(R.id.et_device_sublocation);


            //getPartsDB();

            v.findViewById(R.id.insertSubmit).setOnClickListener(v1 -> {
                String location = textLoction.getText().toString();
                String sublcation = textSubloction.getText().toString();
                if (verify(location, sublcation)) {
                    mOnLocationSelected.sendLocation(location, sublcation);
                    dismiss();

                } else {
                    Log.e(TAG, "View == null");
                    dismiss();
                }
            });


            v.findViewById(R.id.insertCancel).setOnClickListener(v1 -> {
                mOnLocationSelected.sendLocation("", "");
                this.dismiss();
            });

            return v;
        } else
            return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog().getWindow() != null && isAdded()) {
            Window window = getDialog().getWindow();
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
    }

    private boolean verify(String location, String sublocation) {

        return true;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void getLocationDB() {

        if (getContext() != null) {


        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnLocationSelected = (OnLocationSelected) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG,"Error attaching interface - "+e.getMessage());
        }
    }
}

