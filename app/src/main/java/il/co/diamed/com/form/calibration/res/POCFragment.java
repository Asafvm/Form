package il.co.diamed.com.form.calibration.res;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import il.co.diamed.com.form.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class POCFragment extends Fragment {


    public POCFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.devices_samsung_layout, container, false);
    }

}
