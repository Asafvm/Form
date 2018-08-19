package il.co.diamed.com.form.data_objects;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationInfoFragment extends Fragment {

    ClassApplication application = null;
    DatabaseProvider provider = null;
    ExpandableListView expendableView = null;
    String targetLocation = "";

    public LocationInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_location_info, container, false);

        if (getArguments() != null) {
            Bundle bundle = getArguments();//getIntent().getExtras();
            targetLocation = bundle.getString("location");
            ((TextView) v.findViewById(R.id.locationTitle)).setText(targetLocation);

            if (getActivity() != null && isAdded()) {
                application = (ClassApplication) getActivity().getApplication();
                provider = application.getDatabaseProvider(getContext());
                expendableView = v.findViewById(R.id.recycler_inventory_view);
                initView();
            }
        }

        return v;
    }


    private void initView() {
        if (getContext() != null && getActivity() != null && isAdded()) {
            ArrayList<Location> locations = provider.getLocDB();
            // insert data from firebase

            if (locations != null) {
                ArrayList<ArrayList<Device>> childValues = new ArrayList<>();
                for (Location location: locations) {
                    if(location.getName().equals(targetLocation)){
                        ArrayList<SubLocation> groupValues = location.getSubLocation();
                        Collections.sort(groupValues);
                        for (SubLocation subLocation: groupValues) {
                            ArrayList<Device> child = new ArrayList<>(subLocation.getDevices().values());
                            Collections.sort(child);
                            childValues.add(child);


                        }

                        ExpandableListAdapter adapter = new LocationInfoAdapter(groupValues, childValues, getContext());
                        expendableView.setAdapter(adapter);
                        break;
                    }

                }





            }
        }
    }

}
