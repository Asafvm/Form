package il.co.diamed.com.form.inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

public class TargetInventoryFragment extends Fragment {
    private final String TAG = "InventoryFragment";
    RecyclerView recyclerView;
    RecyclerView.Adapter<InventoryViewerAdapter.ViewHolder> adapter;
    DatabaseProvider provider;
    List<Part> values;
    ClassApplication application;
    SwipeRefreshLayout refreshLayout;
    public TargetInventoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_target_inventory, container, false);

        refreshLayout = view.findViewById(R.id.viewSwipe);
        refreshLayout.setOnRefreshListener(this::refresh);
        if (getActivity() != null && isAdded()) {
            application = (ClassApplication) getActivity().getApplication();
            provider = application.getDatabaseProvider(getContext());
            recyclerView = view.findViewById(R.id.recycler_inventory_view);

        }
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "resume");
        if(getContext()!=null)
            getContext().registerReceiver(databaseReceiver,new IntentFilter(DatabaseProvider.BROADCAST_TARGET_DB_READY));
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        if (getContext() != null && getActivity() != null && isAdded()) {
            //insert data from firebase
            String targetName = getArguments().getString("target");
            values = provider.getTargetInv(targetName);
            if (values == null) {
                Log.e(TAG, "My database does not exists, waiting for broadcast");
            } else {
                refreshLayout.setRefreshing(false);
                Collections.sort(values);
                adapter = new InventoryViewerAdapter(values, getContext());
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            }
        }
    }

    protected void refresh() {
        if (getFragmentManager() != null) {
            initView();

        } else {
            Log.e(TAG, "Fragment manager == null");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if(getContext()!=null)
                getContext().unregisterReceiver(databaseReceiver);
        }catch (Exception ignored){}
    }


    private BroadcastReceiver databaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "Database alert!");
            initView();
        }
    };
}
