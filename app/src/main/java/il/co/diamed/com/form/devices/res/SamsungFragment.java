package il.co.diamed.com.form.devices.res;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import il.co.diamed.com.form.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SamsungFragment extends Fragment {


    public SamsungFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.devices_samsung_layout, container, false);
        return rootView;
    }

}
