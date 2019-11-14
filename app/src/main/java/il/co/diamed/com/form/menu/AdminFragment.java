package il.co.diamed.com.form.menu;


import android.app.Activity;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        View v = inflater.inflate(R.layout.fragment_admin_fragmnt, container, false);

        v.findViewById(R.id.admin_btnLocation).setOnClickListener(view -> {
            v.findViewById(R.id.adminMainMenu).setVisibility(View.GONE);
            v.findViewById(R.id.adminLocationMenu).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnBack).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnAdd).setVisibility(View.VISIBLE);
            ((TextView)v.findViewById(R.id.admin_title)).setText("מיקום");
        });

        v.findViewById(R.id.admin_btnDevice).setOnClickListener(view -> {
            v.findViewById(R.id.adminMainMenu).setVisibility(View.GONE);
            v.findViewById(R.id.adminDeviceMenu).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnBack).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnAdd).setVisibility(View.VISIBLE);
            ((TextView)v.findViewById(R.id.admin_title)).setText("מכשירים");
        });

        v.findViewById(R.id.admin_btnPart).setOnClickListener(view -> {
            v.findViewById(R.id.adminMainMenu).setVisibility(View.GONE);
            v.findViewById(R.id.adminPartMenu).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnBack).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnAdd).setVisibility(View.VISIBLE);
            ((TextView)v.findViewById(R.id.admin_title)).setText("חלקים");
        });

        v.findViewById(R.id.admin_btnBack).setOnClickListener(view -> {
            v.findViewById(R.id.adminMainMenu).setVisibility(View.VISIBLE);
            if(v.findViewById(R.id.adminPartMenu).isShown())
                v.findViewById(R.id.adminPartMenu).setVisibility(View.GONE);
            if(v.findViewById(R.id.adminDeviceMenu).isShown())
                v.findViewById(R.id.adminDeviceMenu).setVisibility(View.GONE);
            if(v.findViewById(R.id.adminLocationMenu).isShown())
                v.findViewById(R.id.adminLocationMenu).setVisibility(View.GONE);
            ((TextView)v.findViewById(R.id.admin_title)).setText("תפריט אדמין");
            v.findViewById(R.id.admin_btnAdd).setVisibility(View.GONE);
            v.findViewById(R.id.admin_btnBack).setVisibility(View.GONE);
        });



        // Inflate the layout for this fragment
        return v;
    }



}
