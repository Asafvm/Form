package il.co.diamed.com.form.menu;


import android.app.Activity;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.data_objects.Address;
import il.co.diamed.com.form.data_objects.Location;
import il.co.diamed.com.form.inventory.Part;

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
            ((TextView) v.findViewById(R.id.admin_title)).setText("מיקום");
        });

        v.findViewById(R.id.admin_btnDevice).setOnClickListener(view -> {
            v.findViewById(R.id.adminMainMenu).setVisibility(View.GONE);
            v.findViewById(R.id.adminDeviceMenu).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnBack).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnAdd).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.admin_title)).setText("מכשירים");
        });

        v.findViewById(R.id.admin_btnPart).setOnClickListener(view -> {
            v.findViewById(R.id.adminMainMenu).setVisibility(View.GONE);
            v.findViewById(R.id.adminPartMenu).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnBack).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnAdd).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.admin_title)).setText("חלקים");
        });

        v.findViewById(R.id.admin_btnBack).setOnClickListener(view -> {
            v.findViewById(R.id.adminMainMenu).setVisibility(View.VISIBLE);
            if (v.findViewById(R.id.adminPartMenu).isShown())
                v.findViewById(R.id.adminPartMenu).setVisibility(View.GONE);
            if (v.findViewById(R.id.adminDeviceMenu).isShown())
                v.findViewById(R.id.adminDeviceMenu).setVisibility(View.GONE);
            if (v.findViewById(R.id.adminLocationMenu).isShown())
                v.findViewById(R.id.adminLocationMenu).setVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.admin_title)).setText("תפריט אדמין");
            v.findViewById(R.id.admin_btnAdd).setVisibility(View.GONE);
            v.findViewById(R.id.admin_btnBack).setVisibility(View.GONE);
        });


        v.findViewById(R.id.admin_btnAdd).setOnClickListener(view -> {
            if (v.findViewById(R.id.adminPartMenu).isShown())
                addPart();
            else if (v.findViewById(R.id.adminDeviceMenu).isShown())
                addDevice();
            else if (v.findViewById(R.id.adminLocationMenu).isShown())
                addLocation();


        });

        // Inflate the layout for this fragment
        return v;
    }

    private void addLocation() {
        View v = getView();
        Activity a = getActivity();
        if(v!=null && a != null) {
            try {
                String loc_name = ((EditText) v.findViewById(R.id.admin_etLocationName)).getText().toString().trim();
                String loc_addCity = ((EditText) v.findViewById(R.id.admin_etLocationAddressCity)).getText().toString().trim();
                String loc_addStreet = ((EditText) v.findViewById(R.id.admin_etLocationAddressStreet)).getText().toString().trim();
                String loc_addNumber = ((EditText) v.findViewById(R.id.admin_etLocationAddressNumber)).getText().toString().trim();

                Double loc_lat = Double.valueOf(((EditText) v.findViewById(R.id.admin_etCoordinatesLat)).getText().toString().trim());
                Double loc_long = Double.valueOf(((EditText) v.findViewById(R.id.admin_etCoordinatesLong)).getText().toString().trim());


                String loc_comments = ((EditText) v.findViewById(R.id.admin_etLocationComments)).getText().toString().trim();
                Location l = new Location(loc_name, new Address(loc_addCity, loc_addStreet, loc_addNumber), loc_comments);
                l.setLatitude(loc_lat);
                l.setLongtitude(loc_long);

                ClassApplication application = (ClassApplication) a.getApplication();
                application.getDatabaseProvider(getContext()).uploadNewLocation(l);
            }catch (Exception e){
                //redo form
                Toast.makeText(getContext(),"נא למלא שדות חסרים", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addDevice() {
        View v = getView();
        if(v!=null) {
            Part p = new Part();

        }
    }

    private void addPart() {
        View v = getView();
        if(v!=null) {
            Part p = new Part();

        }
    }


}
