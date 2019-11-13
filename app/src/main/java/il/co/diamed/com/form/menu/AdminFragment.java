package il.co.diamed.com.form.menu;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import il.co.diamed.com.form.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminFragment extends Fragment {


    public AdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_fragmnt, container, false);
    }

}
