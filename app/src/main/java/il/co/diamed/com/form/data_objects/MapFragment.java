package il.co.diamed.com.form.data_objects;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.res.providers.DatabaseProvider;
import il.co.diamed.com.form.R;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private GoogleMap mMap;
    private DatabaseProvider provider = null;
    ArrayList<Location> locations = null;

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
        setMapIfNeeded();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setMapIfNeeded();
        if (getContext() != null)
            getContext().registerReceiver(databaseLocDBReceiver, new IntentFilter(DatabaseProvider.BROADCAST_LOCDB_READY));

    }

    @Override
    public void onPause() {
        super.onPause();
        if (getContext() != null)
            getContext().unregisterReceiver(databaseLocDBReceiver);
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

            if(getActivity() != null) {
                ClassApplication application = (ClassApplication) getActivity().getApplication();
                provider = application.getDatabaseProvider(getContext());

                populateMap();
            }
        }
    }

    private void populateMap() {
        mMap.clear();
        locations = provider.getLocDB();
        if (locations != null) {
            Collections.sort(locations);
            for (final Location item : locations) {
                LatLng latLng = null;

                if (item.getLatitude() != -1 && item.getLongtitude() != -1) {
                    latLng = new LatLng(item.getLatitude(),item.getLongtitude());
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
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (latLng != null) {
                    MarkerOptions options = new MarkerOptions().position(latLng);

                    StringBuilder body = new StringBuilder();
                    for (SubLocation subLocation : item.getSubLocation()) {
                        if(subLocation!=null)
                            body.append(subLocation.getName()).append(" - ").append((subLocation.getDevices().size() == 1) ? ("מכשיר " + subLocation.getDevices().size() + "\n") : (subLocation.getDevices().size() + " מכשירים\n"));
                    }
                    Marker m = mMap.addMarker(options);
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
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        //call info fragment and pass location info
        //Toast.makeText(getContext(),"עוד לא מוכן", Toast.LENGTH_SHORT).show();
        if(getFragmentManager() != null) {
            FragmentManager mFragmentManager = getFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
            LocationInfoFragment infoFragment = new LocationInfoFragment();
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.BOTTOM);
            slide.setDuration(500);
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

}
