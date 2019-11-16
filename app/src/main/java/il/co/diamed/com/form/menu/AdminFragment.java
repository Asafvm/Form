package il.co.diamed.com.form.menu;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.data_objects.Address;
import il.co.diamed.com.form.data_objects.Location;
import il.co.diamed.com.form.inventory.Part;
import il.co.diamed.com.form.res.providers.PermissionManager;

import static android.app.Activity.RESULT_CANCELED;
import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminFragment extends Fragment {

    private static final String TAG = "AdminPage ";

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
            if (getContext() != null)
                v.findViewById(R.id.admin_btnCoordinatesGet).setOnClickListener(view1 -> {
                    Log.d(TAG, "Checking Location");

                    Activity activity = getActivity();
                    Context context = getContext();
                    if (activity != null && container != null) {
                        //get location
                        getLocation(activity, context, v);


                    }
                });


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
            if (v.findViewById(R.id.adminPartMenu).isShown()) {
                v.findViewById(R.id.adminPartMenu).setVisibility(View.GONE);
            }
            if (v.findViewById(R.id.adminDeviceMenu).isShown()) {
                v.findViewById(R.id.adminDeviceMenu).setVisibility(View.GONE);
            }
            if (v.findViewById(R.id.adminLocationMenu).isShown()) {
                v.findViewById(R.id.adminLocationMenu).setVisibility(View.GONE);
            }
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

        //if(getActivity()!=null)
        //SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(getContext());

        // Inflate the layout for this fragment
        return v;
    }

    private void getLocation(Activity activity, Context context, View v) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                Log.d(TAG, "Location Updated");
                ((TextView) v.findViewById(R.id.admin_etCoordinatesLat)).setText(String.valueOf(location.getLatitude()).substring(0,5));
                ((TextView) v.findViewById(R.id.admin_etCoordinatesLong)).setText(String.valueOf(location.getLongitude()).substring(0,5));
                if (location.getAccuracy() != 0)
                    locationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (PermissionManager.getInstance().checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                PermissionManager.getInstance().checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            v.findViewById(R.id.admin_btnCoordinatesGet).setActivated(true);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener);


        } else {
            Log.d(TAG, "Requesting Persmission");
            // Permission is not granted
            v.findViewById(R.id.admin_btnCoordinatesGet).setActivated(false);
            PermissionManager.getInstance().requestPermission(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PermissionManager.MY_LOCATION_REQUEST_CODE);

        }

    }

    private void addLocation() {
        View v = getView();
        Activity a = getActivity();
        if (v != null && a != null) {
            try {
                String loc_name = ((EditText) v.findViewById(R.id.admin_etLocationName)).getText().toString().trim();
                String loc_addCity = ((EditText) v.findViewById(R.id.admin_etLocationAddressCity)).getText().toString().trim();
                String loc_addStreet = ((EditText) v.findViewById(R.id.admin_etLocationAddressStreet)).getText().toString().trim();
                String loc_addNumber = ((EditText) v.findViewById(R.id.admin_etLocationAddressNumber)).getText().toString().trim();


                double loc_lat = Double.parseDouble(((TextView) v.findViewById(R.id.admin_etCoordinatesLat)).getText().toString().trim());
                double loc_long = Double.parseDouble(((TextView) v.findViewById(R.id.admin_etCoordinatesLong)).getText().toString().trim());

                String loc_comments = ((EditText) v.findViewById(R.id.admin_etLocationComments)).getText().toString().trim();

                Location l = new Location(loc_name, new Address(loc_addCity, loc_addStreet, loc_addNumber), loc_comments);
                l.setLatitude(loc_lat);
                l.setLongtitude(loc_long);

                ClassApplication application = (ClassApplication) a.getApplication();
                application.getDatabaseProvider(getContext()).uploadNewLocation(l);
            } catch (Exception e) {
                //redo form
                Toast.makeText(getContext(), "נא למלא שדות חסרים", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addDevice() {
        View v = getView();
        if (v != null) {
            Part p = new Part();

        }
    }

    private void addPart() {
        View v = getView();
        if (v != null) {
            Part p = new Part();

        }
    }


    //Location


    /*---------- Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {


        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }


        @Override
        public void onLocationChanged(android.location.Location loc) {
            Toast.makeText(
                    getContext(),
                    "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                            + loc.getLongitude(), Toast.LENGTH_SHORT).show();
            String longitude = "Longitude: " + loc.getLongitude();
            String latitude = "Latitude: " + loc.getLatitude();

            /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
            List<android.location.Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                    + cityName;
            Toast.makeText(
                    getContext(), s, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
