package il.co.diamed.com.form.menu;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.data_objects.Address;
import il.co.diamed.com.form.data_objects.Location;
import il.co.diamed.com.form.data_objects.SubLocation;
import il.co.diamed.com.form.data_objects.SubLocationAdapter;
import il.co.diamed.com.form.inventory.Part;
import il.co.diamed.com.form.res.providers.PermissionManager;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminFragment extends Fragment {

    private static final String TAG = "AdminPage ";
    private Location location = null;
    private ArrayList<Location> locations;
    private ClassApplication application = null;
    private LocationManager locationManager;
    private boolean newLoc = true;
    private Location targetLoc = null;


    private RecyclerView recyclerView;
    private TabLayout tabLayout;


    public AdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_admin_fragmnt, container, false);


        recyclerView = v.findViewById(R.id.admin_subLocationRecyclerView);
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        tabLayout = v.findViewById(R.id.admin_location_tabs);
        tabLayout.addOnTabSelectedListener(locationTabListener);


        /****************************** Location ***********************************/
        //v.findViewById(R.id.admin_btnLocation).setOnClickListener(view -> {
        initLocationView(v);

        v.findViewById(R.id.admin_btn_subLocationAdd).setOnClickListener(view -> {
            String name = ((EditText) v.findViewById(R.id.admin_subLocationName)).getText().toString();
            String comments = ((EditText) v.findViewById(R.id.admin_subLocationComments)).getText().toString();
            if (!name.isEmpty()) {
                //create sublocation in location
                location.addSublocation(new SubLocation(name, comments));
                updateSublocationRecycler();
            }
            ((EditText) v.findViewById(R.id.admin_subLocationName)).setText("");
            ((EditText) v.findViewById(R.id.admin_subLocationComments)).setText("");

        });

/*        v.findViewById(R.id.admin_btn_locationAdd).setOnClickListener(view -> {
            v.findViewById(R.id.admin_location_phase3).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_location_phase2).setVisibility(View.GONE);
        });
        v.findViewById(R.id.admin_btn_locationContinue).setOnClickListener(view -> {
            v.findViewById(R.id.admin_location_phase2).setVisibility(View.GONE);
            v.findViewById(R.id.admin_location_phase3).setVisibility(View.VISIBLE);

        });*/

        v.findViewById(R.id.admin_btnCoordinatesGet).setOnClickListener(view1 -> {
            Log.d(TAG, "Checking Location");

            Activity activity = getActivity();
            Context context = getContext();
            if (activity != null && container != null) {
                //get location
                getLocation(activity, context, v);
            }
        });
        //Location END

        /****************************** DEVICE ***********************************/
        v.findViewById(R.id.admin_btnDevice).setOnClickListener(view -> {
            v.findViewById(R.id.adminMainMenu).setVisibility(View.GONE);
            v.findViewById(R.id.adminDeviceMenu).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnBack).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnFinish).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.admin_title)).setText("מכשירים");
        });

        /****************************** PART ***********************************/
        v.findViewById(R.id.admin_btnPart).setOnClickListener(view -> {
            v.findViewById(R.id.adminMainMenu).setVisibility(View.GONE);
            v.findViewById(R.id.adminPartMenu).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnBack).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnFinish).setVisibility(View.VISIBLE);
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
            if (v.findViewById(R.id.admin_location_phase1).isShown()) {
                v.findViewById(R.id.admin_location_phase1).setVisibility(View.GONE);
            }
            ((TextView) v.findViewById(R.id.admin_title)).setText("תפריט אדמין");
            v.findViewById(R.id.admin_btnFinish).setVisibility(View.GONE);
            v.findViewById(R.id.admin_btnBack).setVisibility(View.GONE);
        });


        v.findViewById(R.id.admin_btnFinish).setOnClickListener(view -> {
            if (v.findViewById(R.id.adminPartMenu).isShown())
                addPart();
            else if (v.findViewById(R.id.adminDeviceMenu).isShown())
                addDevice();
            else if (v.findViewById(R.id.admin_location_phase1).isShown()) {
                uploadLocation();

            }


        });

        v.findViewById(R.id.admin_btn_subLocationAdd).setOnClickListener(view -> {
            String name = ((EditText) v.findViewById(R.id.admin_subLocationName)).getText().toString();
            String comments = ((EditText) v.findViewById(R.id.admin_subLocationComments)).getText().toString();
            if (!name.isEmpty()) {
                //create sublocation in location
                location.addSublocation(new SubLocation(name, comments));
                //updateSublocationRecycler();


            }
            ((EditText) v.findViewById(R.id.admin_subLocationName)).setText("");
            ((EditText) v.findViewById(R.id.admin_subLocationComments)).setText("");

        });
        //addSubLocation(v);

        //});

        // Inflate the layout for this fragment
        return v;
    }

    private void initTextFields(View v, int layout) {
        ViewGroup viewGroup = v.findViewById(layout);
        for (View view : viewGroup.getTouchables()) {
            if (view instanceof EditText) {
                ((EditText) view).setText("");
                view.setFocusable(true);
            }
        }

    }

    private void initLocationView(View v) {
        if (v != null) {
            if (getActivity() != null) {
                application = (ClassApplication) getActivity().getApplication();
                locations = application.getDatabaseProvider(getContext()).getLocDB();
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


            TabLayout.Tab tab = tabLayout.getTabAt(0);
            if (tab != null)
                tab.select();
            tabLayout.setVisibility(View.GONE);
            initTextFields(v, R.id.admin_location_phase1);
            initTextFields(v, R.id.admin_location_phase2);
            initTextFields(v, R.id.admin_location_phase3);
            ((TextView) v.findViewById(R.id.admin_etCoordinatesLat)).setText("");
            ((TextView) v.findViewById(R.id.admin_etCoordinatesLong)).setText("");

            v.findViewById(R.id.adminMainMenu).setVisibility(View.GONE);
            v.findViewById(R.id.admin_location_phase1).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_location_phase2).setVisibility(View.GONE);
            v.findViewById(R.id.admin_location_phase3).setVisibility(View.GONE);

            ((TextView) v.findViewById(R.id.admin_title)).setText("מיקום");

            ((EditText) v.findViewById(R.id.admin_etLocationName)).setText("");
            ((EditText) v.findViewById(R.id.admin_etLocationName)).addTextChangedListener(nameWatcher);
            v.findViewById(R.id.admin_etLocationName).setFocusableInTouchMode(true);
            v.findViewById(R.id.admin_btn_confirmNewLocation).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btn_confirmNewLocation).setEnabled(false);

            v.findViewById(R.id.admin_btnBack).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnFinish).setVisibility(View.GONE);

            v.findViewById(R.id.admin_btn_confirmNewLocation).setOnClickListener(view1 -> {
                String message = "";
                if (newLoc)
                    message = "Create New Location?";
                else
                    message = "Edit Location?";
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                        .setTitle("New Location Alert")
                        .setMessage(message)
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            if (((EditText) v.findViewById(R.id.admin_etLocationName)).getText().toString().equals("")) {
                                Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                            } else {
                                v.findViewById(R.id.admin_etLocationName).setFocusable(false);
                                v.findViewById(R.id.admin_location_phase2).setVisibility(View.VISIBLE);
                                v.findViewById(R.id.admin_btn_confirmNewLocation).setVisibility(View.GONE);
                                v.findViewById(R.id.admin_btnFinish).setVisibility(View.VISIBLE);

                                if (!newLoc && targetLoc != null) {
                                    location = targetLoc;
                                    //import location settings
                                    ((EditText) v.findViewById(R.id.admin_etLocationAddressCountry)).setText(location.getAddress().getLoc_Country());
                                    ((EditText) v.findViewById(R.id.admin_etLocationAddressArea)).setText(location.getAddress().getLoc_Area());
                                    ((EditText) v.findViewById(R.id.admin_etLocationAddressLocality)).setText(location.getAddress().getloc_Locality());
                                    ((EditText) v.findViewById(R.id.admin_etLocationAddressThoroughfare)).setText(location.getAddress().getloc_Thoroughfare());
                                    ((EditText) v.findViewById(R.id.admin_etLocationAddressSubThoroughfare)).setText(location.getAddress().getloc_SubThoroughfare());
                                    ((TextView) v.findViewById(R.id.admin_etCoordinatesLat)).setText(String.valueOf(location.getLatitude()));
                                    ((TextView) v.findViewById(R.id.admin_etCoordinatesLong)).setText(String.valueOf(location.getLongtitude()));
                                } else {
                                    //create new location
                                    location = new Location();
                                    location.setName(((EditText) v.findViewById(R.id.admin_etLocationName)).getText().toString().trim());
                                    recyclerView.setAdapter(null);
                                }
                                updateSublocationRecycler();
                                v.findViewById(R.id.admin_location_tabs).setVisibility(View.VISIBLE);
                            }
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {

                        })
                        .setOnCancelListener(dialogInterface -> {

                        })
                        .setCancelable(true);

                alertDialogBuilder.show();

            });
        }
    }


    private void uploadLocation() {
        if (location == null) {
            Toast.makeText(getContext(), "Operation Canceled", Toast.LENGTH_SHORT).show();
            initLocationView(getView());
        } else {
            setLocationAddress();

            String title = getString(R.string.create) + location.getName() + getString(R.string.q_mark);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(location.getName());
            if (location.getSubLocation().size() == 0) {
                stringBuilder.append(" does not contain sub locations\n");

            } else {
                stringBuilder.append(" contains the following:\n");
                for (SubLocation s : location.getSubLocation()) {
                    stringBuilder.append(s.getName());
                    stringBuilder.append('\n');
                }
            }
            String question = (newLoc) ? "Confirm" : "Save Changes?";
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setCancelable(true)
                    .setTitle(title)
                    .setPositiveButton(question, (dialogInterface, i) -> {
                        application.getDatabaseProvider(getContext()).uploadNewLocation(location);
                        initLocationView(getView());

                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    })
                    .setMessage(stringBuilder);

            builder.show();
        }
    }


    private void getLocation(Activity activity, Context context, View v) {
        //get location from gps / network
        locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);

        boolean gotAddress = false;
        String loc_addCountry = ((EditText) v.findViewById(R.id.admin_etLocationAddressCountry)).getText().toString().trim();
        String loc_addArea = ((EditText) v.findViewById(R.id.admin_etLocationAddressArea)).getText().toString().trim();
        String loc_addCity = ((EditText) v.findViewById(R.id.admin_etLocationAddressLocality)).getText().toString().trim();
        String loc_addStreet = ((EditText) v.findViewById(R.id.admin_etLocationAddressThoroughfare)).getText().toString().trim();
        String loc_addNumber = ((EditText) v.findViewById(R.id.admin_etLocationAddressSubThoroughfare)).getText().toString().trim();
        if (!loc_addCountry.equals("") || !loc_addArea.equals("") || !loc_addCity.equals("") || !loc_addStreet.equals("") || !loc_addNumber.equals("")) {
            // get lat and long from written location full addresss
            Geocoder geocoder;
            List<android.location.Address> addresses;
            geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocationName(loc_addCountry + " " + loc_addArea + " " + loc_addStreet + " " + loc_addNumber + " " + loc_addCity, 1);
                if (addresses.size() == 0) {
                    gotAddress = false;
                } else {
                    double l_lat = addresses.get(0).getLatitude();
                    double l_long = addresses.get(0).getLongitude();
                    ((TextView) v.findViewById(R.id.admin_etCoordinatesLat)).setText(String.valueOf(l_lat));
                    ((TextView) v.findViewById(R.id.admin_etCoordinatesLong)).setText(String.valueOf(l_long));
                    gotAddress = true;
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed getting lat and long from address");
                ((TextView) v.findViewById(R.id.admin_etCoordinatesLat)).setText("");
                ((TextView) v.findViewById(R.id.admin_etCoordinatesLong)).setText("");
                gotAddress = false;
            }
        }


        if (!gotAddress)
            //get current location from gps
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

    private void setLocationAddress() {
        View v = getView();
        Activity a = getActivity();
        if (v != null && a != null) {
            try {

                String loc_addCountry = ((EditText) v.findViewById(R.id.admin_etLocationAddressCountry)).getText().toString().trim();
                String loc_addArea = ((EditText) v.findViewById(R.id.admin_etLocationAddressArea)).getText().toString().trim();
                String loc_addCity = ((EditText) v.findViewById(R.id.admin_etLocationAddressLocality)).getText().toString().trim();
                String loc_addStreet = ((EditText) v.findViewById(R.id.admin_etLocationAddressThoroughfare)).getText().toString().trim();
                String loc_addNumber = ((EditText) v.findViewById(R.id.admin_etLocationAddressSubThoroughfare)).getText().toString().trim();
                String loc_comments = ((EditText) v.findViewById(R.id.admin_etLocationComments)).getText().toString().trim();
                location.setAddress(new Address(loc_addCountry, loc_addArea, loc_addCity, loc_addStreet, loc_addNumber));
                location.setComments(loc_comments);
                try {   //TODO: find a more elegant way to dead with this
                    location.setLatitude(Double.parseDouble(((TextView) v.findViewById(R.id.admin_etCoordinatesLat)).getText().toString().trim()));
                    location.setLongtitude(Double.parseDouble(((TextView) v.findViewById(R.id.admin_etCoordinatesLong)).getText().toString().trim()));
                } catch (Exception ignored) {
                }

                //incomplete address warning
                if (loc_addCountry.equals("") || loc_addArea.equals("") || loc_addCity.equals("") || loc_addStreet.equals("") || loc_addNumber.equals("")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                            .setTitle("Address Alert")
                            .setMessage("Address is not complete, Continue?")
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                            })
                            .setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(getContext(), "נא למלא שדות חסרים", Toast.LENGTH_SHORT).show())
                            .setCancelable(true);
                    alertDialogBuilder.show();
                } else {
                    //address is complete
                    //v.findViewById(R.id.admin_location_phase3).setVisibility(View.VISIBLE);
                    //v.findViewById(R.id.admin_location_phase2).setVisibility(View.GONE);
                }
            } catch (Exception e) {
                //redo form
                Toast.makeText(getContext(), "נא למלא שדות חסרים", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Location
    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            Log.d(TAG, "Location Updated");
            double l_lat = location.getLatitude();
            double l_long = location.getLongitude();
            View v = getView();
            if (v != null) {
                ((TextView) v.findViewById(R.id.admin_etCoordinatesLat)).setText(String.valueOf(l_lat));
                ((TextView) v.findViewById(R.id.admin_etCoordinatesLong)).setText(String.valueOf(l_long));
                if (location.getAccuracy() != 0) {
                    locationManager.removeUpdates(this);
                    Geocoder geocoder;
                    List<android.location.Address> addresses;
                    geocoder = new Geocoder(getContext(), Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(l_lat, l_long, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressCountry)).setText(addresses.get(0).getCountryName());
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressArea)).setText(addresses.get(0).getAdminArea());
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressLocality)).setText(addresses.get(0).getLocality());
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressThoroughfare)).setText(addresses.get(0).getThoroughfare());
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressSubThoroughfare)).setText(addresses.get(0).getSubThoroughfare());
                    } catch (IOException e) {
                        Log.e(TAG, "Failed getting lat and long from lat and long");
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressCountry)).setText("");
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressArea)).setText("");
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressLocality)).setText("");
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressThoroughfare)).setText("");
                        ((EditText) v.findViewById(R.id.admin_etLocationAddressSubThoroughfare)).setText("");
                    }

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

    private TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            newLoc = true;
            targetLoc = null;
            if (getView() != null) {

                if (charSequence.length() > 0)
                    getView().findViewById(R.id.admin_btn_confirmNewLocation).setEnabled(true);

                if (locations != null) {
                    String locName = charSequence.toString().trim();
                    if (locations.isEmpty()) {
                        newLoc = true;
                        targetLoc = null;
                    } else {
                        for (Location l : locations) {
                            if (l.getName().equals(locName)) {
                                //location found
                                newLoc = false;
                                targetLoc = l;
                                break;
                            } else {
                                newLoc = true;
                                targetLoc = null;

                            }
                        }
                    }
                }

                if (newLoc) {
                    ((EditText) getView().findViewById(R.id.admin_etLocationName)).setTextColor(Color.BLACK);
                    if (getContext() != null)
                        ((ImageButton) getView().findViewById(R.id.admin_btn_confirmNewLocation)).setImageDrawable(getResources().getDrawable(R.drawable.ic_add_circle_white_36dp, getContext().getTheme()));
                } else {
                    ((EditText) getView().findViewById(R.id.admin_etLocationName)).setTextColor(Color.RED);
                    if (getContext() != null)
                        ((ImageButton) getView().findViewById(R.id.admin_btn_confirmNewLocation)).setImageDrawable(getResources().getDrawable(R.drawable.ic_content_copy_white_24dp, getContext().getTheme()));
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TabLayout.BaseOnTabSelectedListener locationTabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            View v = getView();
            if (v != null)
                switch (tab.getPosition()) {
                    case 0:
                        v.findViewById(R.id.admin_location_phase3).setVisibility(View.GONE);
                        v.findViewById(R.id.admin_location_phase2).setVisibility(View.VISIBLE);
                        break;

                    case 1:
                        v.findViewById(R.id.admin_location_phase3).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.admin_location_phase2).setVisibility(View.GONE);
                        break;
                }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void updateSublocationRecycler() {

        SubLocationAdapter recyclerViewAdapter = new SubLocationAdapter(location.getSubLocation(),getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    BroadcastReceiver subloctionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action!=null && !action.isEmpty()){
                if(action.equals("sublocation")) {
                    //display sublocation handling screen

                }
            }
        }
    };

}

