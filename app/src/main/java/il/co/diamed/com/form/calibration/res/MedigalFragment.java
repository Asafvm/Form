package il.co.diamed.com.form.calibration.res;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import il.co.diamed.com.form.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MedigalFragment extends Fragment {


    public MedigalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.devices_medigal_layout, container, false);
    }

}
