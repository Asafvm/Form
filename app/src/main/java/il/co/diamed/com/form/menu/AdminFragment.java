package il.co.diamed.com.form.menu;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.data_objects.Address;
import il.co.diamed.com.form.data_objects.Location;
import il.co.diamed.com.form.inventory.Part;
import il.co.diamed.com.form.res.providers.PermissionManager;

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
        ClassApplication application;

        if(getActivity()!=null) {
            application = (ClassApplication) getActivity().getApplication();
            final ArrayList<Location> locations = application.getDatabaseProvider(getContext()).getLocDB();

            //v.findViewById(R.id.admin_btnLocation).setOnClickListener(view -> {
            v.findViewById(R.id.adminMainMenu).setVisibility(View.GONE);
            v.findViewById(R.id.admin_subLocationMenu).setVisibility(View.GONE);
            v.findViewById(R.id.admin_locationnDetailsMenu).setVisibility(View.GONE);
            v.findViewById(R.id.admin_btn_confirmNewLocation).setEnabled(false);
            v.findViewById(R.id.adminLocationMenu).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnBack).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnAdd).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.admin_title)).setText("מיקום");


            ((EditText) v.findViewById(R.id.admin_etLocationName)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (locations != null) {
                        String locName = ((EditText) v.findViewById(R.id.admin_etLocationName)).getText().toString().trim();
                        for (Location l : locations){
                            if(l.getName().equals(locName)){
                                //location found
                                v.findViewById(R.id.admin_btn_confirmNewLocation).setEnabled(false);
                                ((EditText) v.findViewById(R.id.admin_etLocationName)).setTextColor(Color.RED);
                                break;

                            }else{
                                v.findViewById(R.id.admin_btn_confirmNewLocation).setEnabled(true);
                                ((EditText) v.findViewById(R.id.admin_etLocationName)).setTextColor(Color.BLACK);
                            }
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            v.findViewById(R.id.admin_btn_confirmNewLocation).setOnClickListener(view1 -> {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                        .setTitle("New Location Alert")
                        .setMessage("Create New Location?")
                        .setPositiveButton("Yes", (dialogInterface, i) ->{
                            v.findViewById(R.id.admin_locationnDetailsMenu).setVisibility(View.VISIBLE);
                            ((EditText)v.findViewById(R.id.admin_etLocationName)).setInputType(InputType.TYPE_NULL);
                            v.findViewById(R.id.admin_etLocationName).setFocusable(false);
                            v.findViewById(R.id.admin_btn_confirmNewLocation).setVisibility(View.GONE);
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {

                        })
                        .setOnCancelListener(dialogInterface -> {

                        })
                        .setCancelable(true);

                AlertDialog dialog = alertDialogBuilder.show();


            });
        }



        if (getContext() != null) {
            v.findViewById(R.id.admin_btnCoordinatesGet).setOnClickListener(view1 -> {
                Log.d(TAG, "Checking Location");

                Activity activity = getActivity();
                Context context = getContext();
                if (activity != null && container != null) {
                    //get location
                    getLocation(activity, context, v);
                }
            });

            //addSubLocation(v);
        }
        //});


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

    private void addSubLocation(View v) {
        //get root layout
        ConstraintLayout root = v.findViewById(R.id.adminLocationMenu);
        //create new layout
        ConstraintLayout layout = new ConstraintLayout(getContext());

        //create elements
        EditText subLocName = new EditText(getContext());
        subLocName.setId(View.generateViewId());
        subLocName.setHint("שם");
        subLocName.setInputType(InputType.TYPE_CLASS_TEXT);

        EditText subLocPhone = new EditText(getContext());
        subLocPhone.setHint("טלפון");
        subLocPhone.setId(View.generateViewId());
        subLocPhone.setInputType(InputType.TYPE_TEXT_VARIATION_PHONETIC);

        ImageButton btn_addSubLoc = new ImageButton(getContext());
        btn_addSubLoc.setId(View.generateViewId());
        btn_addSubLoc.setImageResource(R.drawable.ic_plus_one_black_24dp);


        //add elements to layout
        ConstraintLayout.LayoutParams clpcontactUs = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        subLocName.setLayoutParams(clpcontactUs);
        subLocPhone.setLayoutParams(clpcontactUs);
        btn_addSubLoc.setLayoutParams(clpcontactUs);

        layout.addView(subLocName);
        layout.addView(subLocPhone);
        layout.addView(btn_addSubLoc);

        //add constraints to elements
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        constraintSet.connect(subLocName.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP);
        constraintSet.connect(subLocName.getId(), ConstraintSet.START, layout.getId(), ConstraintSet.START);
        constraintSet.connect(subLocName.getId(), ConstraintSet.END, subLocPhone.getId(), ConstraintSet.START);
        constraintSet.connect(subLocPhone.getId(), ConstraintSet.END, btn_addSubLoc.getId(), ConstraintSet.START);
        constraintSet.connect(btn_addSubLoc.getId(), ConstraintSet.END, layout.getId(), ConstraintSet.END);
        constraintSet.applyTo(layout);

        layout.setLayoutParams(clpcontactUs);

        constraintSet = new ConstraintSet();
        constraintSet.connect(layout.getId(), ConstraintSet.TOP, root.getId(), ConstraintSet.TOP, 0);
        constraintSet.applyTo(layout);
        root.addView(layout);
    }


    private void getLocation(Activity activity, Context context, View v) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                Log.d(TAG, "Location Updated");
                double l_lat = location.getLatitude();
                double l_long = location.getLongitude();
                ((TextView) v.findViewById(R.id.admin_etCoordinatesLat)).setText(String.valueOf(l_lat));
                ((TextView) v.findViewById(R.id.admin_etCoordinatesLong)).setText(String.valueOf(l_long));
                if (location.getAccuracy() != 0) {
                    locationManager.removeUpdates(this);

                    Geocoder geocoder;
                    List<android.location.Address> addresses;
                    geocoder = new Geocoder(getContext(), Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(l_lat, l_long, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressCity)).setText(addresses.get(0).getLocality());
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressStreet)).setText(addresses.get(0).getThoroughfare());
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressSubThoroughfare)).setText(addresses.get(0).getSubThoroughfare());
                    } catch (IOException e) {
                        Log.e(TAG, "Failed getting lat and long from lat and long");
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressCity)).setText("");
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressStreet)).setText("");
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressSubThoroughfare)).setText("");
                    }

                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d(TAG, "Status changed?");

            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d(TAG, "Provider enabled");

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d(TAG, "Provider disabled");

            }
        };


        boolean gotAddress = false;
        String loc_addCity = ((EditText) v.findViewById(R.id.admin_etLocationAddressCity)).getText().toString().trim();
        String loc_addStreet = ((EditText) v.findViewById(R.id.admin_etLocationAddressStreet)).getText().toString().trim();
        String loc_addNumber = ((EditText) v.findViewById(R.id.admin_etLocationAddressSubThoroughfare)).getText().toString().trim();
        if (!loc_addCity.equals("") && !loc_addStreet.equals("") && !loc_addNumber.equals("")) {
            // get lat and long from written location full addresss
            Geocoder geocoder;
            List<android.location.Address> addresses;
            geocoder = new Geocoder(getContext(), Locale.getDefault());

            try {
                addresses = geocoder.getFromLocationName(loc_addStreet + " " + loc_addNumber + ", " + loc_addCity, 1);
                double l_lat = addresses.get(0).getLatitude();
                double l_long = addresses.get(0).getLongitude();
                ((TextView) v.findViewById(R.id.admin_etCoordinatesLat)).setText(String.valueOf(l_lat));
                ((TextView) v.findViewById(R.id.admin_etCoordinatesLong)).setText(String.valueOf(l_long));
                gotAddress = true;
            } catch (IOException e) {
                Log.e(TAG, "Failed getting lat and long from address");
                ((TextView) v.findViewById(R.id.admin_etCoordinatesLat)).setText("");
                ((TextView) v.findViewById(R.id.admin_etCoordinatesLong)).setText("");
                gotAddress = false;
            }
        }


        if (!gotAddress)
            if (PermissionManager.getInstance().checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    PermissionManager.getInstance().checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                v.findViewById(R.id.admin_btnCoordinatesGet).setActivated(true);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, listener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, listener);

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
                String loc_addNumber = ((EditText) v.findViewById(R.id.admin_etLocationAddressSubThoroughfare)).getText().toString().trim();
                String loc_comments = ((EditText) v.findViewById(R.id.admin_etLocationComments)).getText().toString().trim();
                Location l = new Location(loc_name, new Address(loc_addCity, loc_addStreet, loc_addNumber), loc_comments);
                l.setLatitude(Double.parseDouble(((TextView) v.findViewById(R.id.admin_etCoordinatesLat)).getText().toString().trim()));
                l.setLongtitude(Double.parseDouble(((TextView) v.findViewById(R.id.admin_etCoordinatesLong)).getText().toString().trim()));

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


}
