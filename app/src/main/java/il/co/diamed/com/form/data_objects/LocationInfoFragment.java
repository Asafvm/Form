package il.co.diamed.com.form.data_objects;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    LocationInfoAdapter adapter;

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
                ArrayList<ArrayList<FieldDevice>> childValues = new ArrayList<>();
                for (Location location : locations) {
                    if (location.getName().equals(targetLocation)) {
                        ArrayList<SubLocation> groupValues = location.getSubLocation();
                        Collections.sort(groupValues);
                        for (SubLocation subLocation : groupValues) {
                            ArrayList<FieldDevice> child = new ArrayList<>(subLocation.getDevices().values());
                            Collections.sort(child);
                            childValues.add(child);
                        }
                        if(adapter == null) {
                            adapter = new LocationInfoAdapter(groupValues, childValues, getContext());
                            expendableView.setAdapter(adapter);
                        }else{
                            adapter.updateData(groupValues, childValues);
                        }
                        break;
                    }

                }
            }
        }
    }

    private BroadcastReceiver refreshAdapterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (adapter != null) {
                initView();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null)
            getContext().registerReceiver(refreshAdapterReceiver, new IntentFilter(DatabaseProvider.BROADCAST_REFRESH_ADAPTER));

    }

    @Override
    public void onPause() {
        super.onPause();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getContext() != null) {
            getContext().unregisterReceiver(refreshAdapterReceiver);
        }
    }
}
