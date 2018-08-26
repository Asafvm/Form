package il.co.diamed.com.form.calibration.res;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.data_objects.Location;
import il.co.diamed.com.form.data_objects.SubLocation;
import il.co.diamed.com.form.inventory.Part;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

public class DeviceDialogFragment extends DialogFragment {
    private final String TAG = "DeviceDialogFragment";
    ClassApplication application;
    DatabaseProvider provider;
    AutoCompleteTextView textLocation;
    AutoCompleteTextView textSublocation;
    HashMap<String, ArrayList<String>> sublocations = null;

    public interface OnLocationSelected {
        void sendLocation(String location, String sublocation);
    }

    public OnLocationSelected mOnLocationSelected;

    static DeviceDialogFragment newInstance() {
        return new DeviceDialogFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

            textLocation = v.findViewById(R.id.et_device_locaion);
            textSublocation = v.findViewById(R.id.et_device_sublocation);


            getLocDB();

            v.findViewById(R.id.insertSubmit).setOnClickListener(v1 -> {
                String location = textLocation.getText().toString();
                String sublcation = textSublocation.getText().toString();
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
        /** use this function to lock list for specific locations **/
        return true;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnLocationSelected = (OnLocationSelected) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e(TAG, "Error attaching interface - " + e.getMessage());
        }
    }


    public void getLocDB() {

        if (getContext() != null) {
            ArrayList<Location> locations = provider.getLocDB();
            if (locations != null) {
                try {
                    try {
                        getContext().unregisterReceiver(databaseReceiver);
                    } catch (Exception ignored) {
                    }


                    ArrayList<String> location_names = new ArrayList<>();
                    sublocations = new HashMap<>();
                    for (Location location : locations) {
                        location_names.add(location.getName());
                        ArrayList<String> sublocation_names = new ArrayList<>();
                        for (SubLocation subLocation : location.getSubLocation()) {
                            sublocation_names.add(subLocation.getName());
                        }
                        sublocations.put(location.getName(), sublocation_names);
                    }
                    //String[] NAMES = location_names.toArray(new String[location_names.size()]);
/*
                    ArrayAdapter<String> arrayadapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item, NAMES);
*/
                    LocationAdapter arrayadapter = new LocationAdapter(getContext(),
                            android.R.layout.simple_spinner_dropdown_item, location_names);

                    textLocation.setAdapter(arrayadapter);
                    textLocation.setOnItemClickListener((parent, view, position, id) -> {
                        ArrayList<String> target = sublocations.get(arrayadapter.getItem(position));
                        if (target != null) {
                            /*ArrayAdapter<String> arrayadapter1 = new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_spinner_dropdown_item, target.toArray(new String[target.size()]));*/
                            LocationAdapter arrayadapter1 = new LocationAdapter(getContext(),
                                    android.R.layout.simple_spinner_dropdown_item, target);
                            textSublocation.setAdapter(arrayadapter1);
                            textSublocation.showDropDown();
                        }
                    });
                } catch (Exception ignored) {
                }
            } else {
                getContext().registerReceiver(databaseReceiver, new IntentFilter(DatabaseProvider.BROADCAST_LOCDB_READY));
            }

        }
    }


    private BroadcastReceiver databaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "Database alert!");
            getLocDB();
        }
    };


}

