package il.co.diamed.com.form.data_objects;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.res.providers.DatabaseProvider;
import il.co.diamed.com.form.R;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerDragListener {
    private GoogleMap mMap;
    private DatabaseProvider provider = null;
    private boolean redMarker = true, orgMarker = true, yelMarker = true, grnMarker = true;
    private ArrayList<Location> display_locations = null;
    double originLat = -1, originLong = -1;
    private Location targetLocation = null;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_score_map, container, false);

        view.findViewById(R.id.mapRedMarker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (redMarker) {
                    redMarker = false;
                    v.setBackgroundColor(Color.WHITE);
                } else {
                    redMarker = true;
                    v.setBackgroundColor(Color.parseColor("#bbff0000"));
                }
                populateMap();
            }
        });
        view.findViewById(R.id.mapOrgMarker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orgMarker) {
                    orgMarker = false;
                    v.setBackgroundColor(Color.WHITE);
                } else {
                    orgMarker = true;
                    v.setBackgroundColor(Color.parseColor("#bbFDBB1C"));

                }
                populateMap();
            }
        });
        view.findViewById(R.id.mapYelMarker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yelMarker) {
                    yelMarker = false;
                    v.setBackgroundColor(Color.WHITE);
                } else {
                    yelMarker = true;
                    v.setBackgroundColor(Color.parseColor("#bbffff00"));
                }
                populateMap();
            }
        });
        view.findViewById(R.id.mapGrnMarker).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grnMarker) {
                    grnMarker = false;
                    v.setBackgroundColor(Color.WHITE);
                } else {
                    grnMarker = true;
                    v.setBackgroundColor(Color.parseColor("#bb00ff00"));
                }
                populateMap();
            }
        });

        setMapIfNeeded();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setMapIfNeeded();
        if (getContext() != null) {
            getContext().registerReceiver(databaseLocDBReceiver, new IntentFilter(DatabaseProvider.BROADCAST_LOCDB_READY));
            getContext().registerReceiver(locationFocusDBReceiver, new IntentFilter(SideLocationsAdapter.BROADCASE_FOCUS));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getContext() != null) {
            getContext().unregisterReceiver(databaseLocDBReceiver);
            getContext().unregisterReceiver(locationFocusDBReceiver);
        }
    }

    private void setMapIfNeeded() {
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView)).getMapAsync(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            mMap.setOnInfoWindowClickListener(this);
            mMap.setOnMarkerDragListener(this);

            if (getActivity() != null) {
                ClassApplication application = (ClassApplication) getActivity().getApplication();
                provider = application.getDatabaseProvider(getContext());

                populateMap();
            }
        }
    }

    private void populateMap() {
        mMap.clear();

        ArrayList<Location> locations = provider.getLocDB();
        display_locations = new ArrayList<>();
        if (locations != null) {
            Collections.sort(locations);
            for (final Location item : locations) {
                LatLng latLng = null;

                if (item.getLatitude() != -1 && item.getLongtitude() != -1) {
                    latLng = new LatLng(item.getLatitude(), item.getLongtitude());

                } else {
                    Geocoder geocoder = new Geocoder(getContext());
                    List<Address> address;
                    try {
                        address = geocoder.getFromLocationName(item.getName(), 1);
                        if (address != null && address.size() > 0) {
                            latLng = new LatLng(address.get(0).getLatitude(), address.get(0).getLongitude());
                            item.setLatitude(latLng.latitude);
                            item.setLongtitude(latLng.longitude);
                            provider.updateLocation(item);
                        } else {
                            display_locations.add(item);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (latLng != null) {
                    MarkerOptions options = new MarkerOptions().position(latLng);
                    long min_diff = 0;
                    Marker m = null;

                    StringBuilder body = new StringBuilder();
                    for (SubLocation subLocation : item.getSubLocation()) {
                        if (subLocation != null) {
                            body.append(subLocation.getName()).append(" - ").append((subLocation.getDevices().size() == 1) ? ("מכשיר " + subLocation.getDevices().size() + "\n") : (subLocation.getDevices().size() + " מכשירים\n"));
                            if (subLocation.getDevices() != null) {
                                Date date1 = new java.util.Date();
                                min_diff = 100;
                                for (FieldDevice device : subLocation.getDevices().values()) {
                                    if (device.isDev_under_warranty()) {
                                        Date date2 = device.getDev_next_maintenance();
                                        long diff = (date2.getTime() - date1.getTime()) / 1000 / 60 / 60 / 24; //next - today in days
                                        if (min_diff == -1) {
                                            min_diff = diff;
                                        } else if (diff < min_diff) {
                                            min_diff = diff;
                                        }
                                    }
                                }


                                if (min_diff > 60) { //sometime
                                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                }
                                if (min_diff > 30 && min_diff < 60) { //next month
                                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                }
                                if (min_diff > 0 && min_diff < 30) { //this month
                                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                }
                                if (min_diff <= 0) {    //due
                                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                }


                            }
                        }
                    }

                    if (redMarker && min_diff <= 0 ||
                            orgMarker && (min_diff > 0 && min_diff < 30) ||
                            yelMarker && (min_diff > 30 && min_diff < 60) ||
                            grnMarker && min_diff > 60) {
                        display_locations.add(item);


                        m = mMap.addMarker(options);
                        m.setDraggable(true);
                        m.setTitle(item.getName());
                        m.setTag(body.toString());


                        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                            public View getInfoWindow(Marker arg0) {
                                View v = getLayoutInflater().inflate(R.layout.custom_infowindow, null);

                                String title = arg0.getTitle();
                                String finalBody = (String) arg0.getTag();
                                ((TextView) v.findViewById(R.id.markerTitle)).setText(title);
                                ((TextView) v.findViewById(R.id.markerText)).setText(finalBody);
                                return v;
                            }

                            public View getInfoContents(Marker arg0) {

                                //View v = getLayoutInflater().inflate(R.layout.custom_infowindow, null);

                                return null;

                            }
                        });

                        mMap.setOnMarkerClickListener(marker -> {
                            marker.showInfoWindow();
                            return true;
                        });

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8f));
                    }

                }
            }

            /*
               update side list here
             */
            if (getView() != null && !display_locations.isEmpty()) {
                RecyclerView recyclerView = getView().findViewById(R.id.recycler_locationList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                RecyclerView.Adapter<SideLocationsAdapter.ViewHolder> adapter = new SideLocationsAdapter(display_locations, getContext());
                recyclerView.setAdapter(adapter);
            }

        }

    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        //call info fragment and pass location info
        //Toast.makeText(getContext(),"עוד לא מוכן", Toast.LENGTH_SHORT).show();
        if (getFragmentManager() != null) {
            FragmentManager mFragmentManager = getFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
            LocationInfoFragment infoFragment = new LocationInfoFragment();
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.BOTTOM);
            slide.setDuration(1000);
            infoFragment.setEnterTransition(slide);
            Bundle bundle = new Bundle();
            bundle.putString("location", marker.getTitle());

            infoFragment.setArguments(bundle);
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.replace(R.id.module_container, infoFragment).commit();
        }
    }


    private BroadcastReceiver databaseLocDBReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            populateMap();
        }
    };

    private BroadcastReceiver locationFocusDBReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            double loc_lat = bundle.getDouble("lat");
            double loc_long = bundle.getDouble("long");
            String name = bundle.getString("name");
            if (loc_lat != -1 && loc_long != -1) {

                LatLng latLng = new LatLng(loc_lat, loc_long);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));

            } else {

                MarkerOptions options = new MarkerOptions().position(mMap.getCameraPosition().target);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                Marker m = mMap.addMarker(options);
                m.setDraggable(true);
                m.setTitle(name);

            }

        }
    };

    @Override
    public void onMarkerDragStart(Marker marker) {
        for (Location location : display_locations) {
            if (location.getName().equals(marker.getTitle())) {
                targetLocation = location;
            }
        }
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        if (getContext() != null && targetLocation != null) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
            alertBuilder.setMessage("הזז מיקום?");
            alertBuilder.setPositiveButton("כן", (dialog, which) -> {

                targetLocation.setLatitude(marker.getPosition().latitude);
                targetLocation.setLongtitude(marker.getPosition().longitude);
                provider.updateLocation(targetLocation);
            });

            alertBuilder.setNegativeButton("בטל", (dialog, which) -> {
                if (targetLocation == null || (targetLocation.getLatitude() == -1 || targetLocation.getLongtitude() == -1)) {
                    marker.remove();
                } else {
                    marker.setPosition(new LatLng(targetLocation.getLatitude(), targetLocation.getLongtitude()));
                }
            });
            alertBuilder.setCancelable(false);
            alertBuilder.create().show();
        }
    }

}
