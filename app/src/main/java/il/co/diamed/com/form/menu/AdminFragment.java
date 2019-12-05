package il.co.diamed.com.form.menu;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.transition.Scene;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.data_objects.Address;
import il.co.diamed.com.form.data_objects.FieldDevice;
import il.co.diamed.com.form.data_objects.Location;
import il.co.diamed.com.form.data_objects.PrototypeDevice;
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
    private PrototypeDevice pDevice = null;
    private FieldDevice fDevice = null;
    private ArrayList<Location> locations;
    private ArrayList<PrototypeDevice> pDevices;
    private ClassApplication application = null;
    private LocationManager locationManager;
    private boolean newLoc = true;
    private Location targetLoc = null;


    private RecyclerView sublocationRecyclerView;
    private TabLayout locationTabLayout;
    private RecyclerView reportRecyclerView;
    private RecyclerView deviceRecyclerView;
    private TabLayout deviceTabLayout;

    private FloatingActionButton fab_main;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    Boolean isOpen = false;


    public AdminFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_admin_fragmnt, container, false);
        if (getActivity() != null)
            application = (ClassApplication) getActivity().getApplication();

        initFab(v);
        /****************************** Location ***********************************/


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


        v.findViewById(R.id.admin_btnCoordinatesGet).setOnClickListener(view1 -> {
            Log.d(TAG, "Checking Location");

            Activity activity = getActivity();
            Context context = getContext();
            if (activity != null && container != null) {
                //get location
                getLocation(activity, context, v);
            }
        });
        v.findViewById(R.id.admin_btn_subLocationAdd).setOnClickListener(view -> {
            String name = ((EditText) v.findViewById(R.id.admin_subLocationName)).getText().toString();
            String comments = ((EditText) v.findViewById(R.id.admin_subLocationComments)).getText().toString();
            if (!name.isEmpty()) {
                //create sublocation in location
                location.addSublocation(new SubLocation(name, comments));
            }
            ((EditText) v.findViewById(R.id.admin_subLocationName)).setText("");
            ((EditText) v.findViewById(R.id.admin_subLocationComments)).setText("");

        });

        //Location END

        /****************************** DEVICE ***********************************/


        /****************************** PART ***********************************/


        /****************************** BUTTONS ***********************************/
        //Location
        v.findViewById(R.id.admin_btnLocation).setOnClickListener(view -> {
            sublocationRecyclerView = v.findViewById(R.id.admin_subLocationRecyclerView);
            sublocationRecyclerView.setItemAnimator(new SlideInUpAnimator());
            locationTabLayout = v.findViewById(R.id.admin_location_tabs);
            locationTabLayout.addOnTabSelectedListener(locationTabListener);
            initLocationView(v);
        });
        //Device
        v.findViewById(R.id.admin_btnDevice).setOnClickListener(view -> {
            reportRecyclerView = v.findViewById(R.id.admin_device_ReportRecyclerview);
            reportRecyclerView.setItemAnimator(new SlideInUpAnimator());
            deviceRecyclerView = v.findViewById(R.id.admin_device_DeviceListRecyclerview);
            deviceRecyclerView.setItemAnimator(new SlideInUpAnimator());
            deviceTabLayout = v.findViewById(R.id.admin_device_tablayout);
            deviceTabLayout.addOnTabSelectedListener(deviceTabListener);
            TabLayout.Tab tab = deviceTabLayout.getTabAt(0);
            if (tab != null)
                tab.select();
            deviceTabLayout.setVisibility(View.VISIBLE);
            v.findViewById(R.id.adminMainMenu).setVisibility(View.GONE);
            v.findViewById(R.id.adminDeviceMenu).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnBack).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnFinish).setVisibility(View.GONE);
            v.findViewById(R.id.admin_device_fab).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.admin_title)).setText("מכשירים");
            initDeviceView(v);

        });
        //Part
        v.findViewById(R.id.admin_btnPart).setOnClickListener(view -> {
            v.findViewById(R.id.adminMainMenu).setVisibility(View.GONE);
            v.findViewById(R.id.adminPartMenu).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnBack).setVisibility(View.VISIBLE);
            v.findViewById(R.id.admin_btnFinish).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.admin_title)).setText("חלקים");
        });
        //Back
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
        //Finish
        v.findViewById(R.id.admin_btnFinish).setOnClickListener(view -> {
            if (v.findViewById(R.id.adminPartMenu).isShown())
                addPart();
            else if (v.findViewById(R.id.adminDeviceMenu).isShown())
                addDevice();
            else if (v.findViewById(R.id.admin_location_phase1).isShown()) {
                uploadLocation();
            }
        });

        FloatingActionButton fab = v.findViewById(R.id.admin_location_sublocation_btnAddDevice);
        v.findViewById(R.id.admin_location_sublocation_btnAddDevice).setVisibility(View.GONE);
        fab.setOnClickListener(view -> Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        // Inflate the layout for this fragment
        return v;
    }

    private void initFab(View v) {

        fab_main = v.findViewById(R.id.admin_device_fab_main);
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_anticlock);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isOpen) {
                    fab_main.startAnimation(fab_anticlock);
                    v.findViewById(R.id.admin_layout_device_base_properties).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.admin_device_layout_device_details).setVisibility(View.GONE);
                    isOpen = false;
                } else {
                    v.findViewById(R.id.admin_layout_device_base_properties).setVisibility(View.GONE);
                    v.findViewById(R.id.admin_device_layout_device_details).setVisibility(View.VISIBLE);
                    fab_main.startAnimation(fab_clock);
                    isOpen = true;
                }

            }
        });
    }

    private void initDeviceView(View v) {
        pDevices = application.getDatabaseProvider(getContext()).getPDeviceDB();

        Spinner deviceSpinner = v.findViewById(R.id.admin_device_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<PrototypeDevice> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, pDevices);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        deviceSpinner.setAdapter(adapter);

        deviceSpinner.setOnItemSelectedListener(spinnerListener);


        v.findViewById(R.id.admin_device_spinner).setVisibility(View.GONE);
        v.findViewById(R.id.admin_layout_device_base_properties).setVisibility(View.VISIBLE);
        v.findViewById(R.id.admin_device_layout_BaseDeviceReport).setVisibility(View.GONE);
        v.findViewById(R.id.admin_device_layout_device_details).setVisibility(View.GONE);

        v.findViewById(R.id.admin_device_btnAddBaseDevice).setOnClickListener(view -> {

            String message = "Create new device type?";

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                    .setTitle("New Prototype Alert")
                    .setMessage(message)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        String man = ((EditText) v.findViewById(R.id.admin_etDeviceManufacturer)).getText().toString().trim().toUpperCase();
                        String code = ((EditText) v.findViewById(R.id.admin_etDeviceCodeNumber)).getText().toString().trim().toUpperCase();
                        String name = ((EditText) v.findViewById(R.id.admin_etDeviceCodeName)).getText().toString().trim().toUpperCase();
                        String mod = ((EditText) v.findViewById(R.id.admin_etDeviceModel)).getText().toString().trim().toUpperCase();
                        double price = 0;
                        try {
                            price = Double.valueOf(((EditText) v.findViewById(R.id.admin_etDevicePrice)).getText().toString());
                        } catch (Exception ignored) {
                            price = 0;
                        }
                        if (name.equals("") || code.equals("")) {
                            Toast.makeText(getContext(), "Mandatory fields must not be empty", Toast.LENGTH_SHORT).show();
                            ((EditText) v.findViewById(R.id.admin_etDeviceCodeName)).setError("Mandatory");
                            ((EditText) v.findViewById(R.id.admin_etDeviceCodeNumber)).setError("Mandatory");
                        } else {
                            pDevice = new PrototypeDevice(man, code, name, mod, price);
                            application.getDatabaseProvider(getContext()).uploadPrototypeDevice(pDevice);
                            initTextFields(v, R.id.admin_layout_device_base_properties);
                        }
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {

                    })

                    .setCancelable(true);

            alertDialogBuilder.show();

        });

        v.findViewById(R.id.admin_device_btnAddInstanceDevice).setOnClickListener(view -> {
            String serial = ((EditText) v.findViewById(R.id.admin_etDeviceSerialNumber)).getText().toString().trim().toUpperCase();
            fDevice.setDev_serial(serial);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                    .setCancelable(true)
                    .setTitle("Create Field Device")
                    .setPositiveButton("Confirm", (dialogInterface, i) -> {
                        application.getDatabaseProvider(getContext()).uploadFieldDevice(fDevice);
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    })
                    .setMessage("Create " + serial + "?");
            builder.show();
        });
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

            locations = application.getDatabaseProvider(getContext()).getLocDB();

            TabLayout.Tab tab = locationTabLayout.getTabAt(0);
            if (tab != null)
                tab.select();
            locationTabLayout.setVisibility(View.GONE);
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
                                    sublocationRecyclerView.setAdapter(null);
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
                        v.findViewById(R.id.admin_location_sublocation_tab).setVisibility(View.GONE);
                        v.findViewById(R.id.admin_location_phase3).setVisibility(View.GONE);
                        v.findViewById(R.id.admin_location_phase2).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.admin_location_sublocation_btnAddDevice).setVisibility(View.GONE);
                        break;

                    case 1:
                        v.findViewById(R.id.admin_location_sublocation_tab).setVisibility(View.GONE);
                        v.findViewById(R.id.admin_location_phase3).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.admin_location_phase2).setVisibility(View.GONE);
                        sublocationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        v.findViewById(R.id.admin_location_sublocation_btnAddDevice).setVisibility(View.GONE);
                        break;
                    case 2:
                        v.findViewById(R.id.admin_location_phase3).setVisibility(View.GONE);
                        v.findViewById(R.id.admin_location_phase2).setVisibility(View.GONE);
                        v.findViewById(R.id.admin_location_sublocation_tab).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.admin_location_sublocation_btnAddDevice).setVisibility(View.VISIBLE);
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

    private TabLayout.BaseOnTabSelectedListener deviceTabListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            View v = getView();
            if (v != null)
                switch (tab.getPosition()) {

                    case 0:
                        v.findViewById(R.id.admin_device_layout_BaseDeviceReport).setVisibility(View.GONE);
                        v.findViewById(R.id.admin_device_layout_device_details).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.admin_device_spinner).setVisibility(View.VISIBLE);
                        break;

                    case 1:
                        v.findViewById(R.id.admin_device_layout_BaseDeviceReport).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.admin_device_layout_device_details).setVisibility(View.GONE);
                        v.findViewById(R.id.admin_device_spinner).setVisibility(View.VISIBLE);
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

        SubLocationAdapter recyclerViewAdapter = new SubLocationAdapter(location.getSubLocation(), getContext());
        sublocationRecyclerView.setAdapter(recyclerViewAdapter);
        //recyclerViewAdapter.notifyDataSetChanged();
    }

    private BroadcastReceiver subloctionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && !action.isEmpty()) {
                if (action.equals("sublocation")) {
                    // TODO: display sublocation handling screen
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        if (locationTabLayout.getTabAt(2) != null)
                            locationTabLayout.removeTabAt(2);
                        locationTabLayout.addTab(locationTabLayout.newTab().setText(bundle.getString("sub")), 2, true);
                    }
                }
            }
        }
    };

    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String selected = adapterView.getItemAtPosition(i).toString();
            if (fDevice == null)
                fDevice = new FieldDevice();
            fDevice.setDev_identifier(selected);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null)
            getContext().registerReceiver(subloctionReceiver, new IntentFilter("sublocation"));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getContext() != null)
            getContext().unregisterReceiver(subloctionReceiver);
    }
}

