package il.co.diamed.com.form.contacts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.res.providers.DatabaseProvider;
import il.co.diamed.com.form.R;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
            LatLng cameraPos;
            ClassApplication application = (ClassApplication) getActivity().getApplication();
            DatabaseProvider provider = application.getDatabaseProvider(getContext());
/*            List<UserItem> users = provider.getMyInv();
            if (users != null) {
                Collections.sort(users);
                for (final UserItem item : users) {
                    if (!(item.getLat() == -1 && item.getLng() == -1)) {
                        LatLng latLng = new LatLng(item.getLat(), item.getLng());
                        Geocoder geocoder = new Geocoder(getContext());
                        MarkerOptions options = null;
                        try {
                            options = new MarkerOptions().position(latLng)
                                    .title(item.getName() + " - " + item.getScore() + "\n")
                                    .snippet(geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0));
                        } catch (IOException e) {
                            options = new MarkerOptions().position(latLng).title(item.getName() + " - " + item.getScore());
                        }
                        if (users.indexOf(item) == 0) {
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        } else if (users.indexOf(item) == 1) {
                            options.icon(BitmapDescriptorFactory.defaultMarker(300));

                        } else if (users.indexOf(item) == 2) {
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        }
                        mMap.addMarker(options);
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {

                                marker.showInfoWindow();
                                return true;
                            }
                        });
                        cameraPos = latLng;
                        //}
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(cameraPos));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraPos, 8f));
                    }
                }
            }*/
        }
    }
}
