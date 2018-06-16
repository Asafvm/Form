package il.co.diamed.com.form.inventory;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

public class InventoryFragment extends Fragment {
    private final String TAG = "InventoryFragment";
    RecyclerView recyclerView;
    RecyclerView.Adapter<InventoryAdapter.ViewHolder> adapter;
    DatabaseProvider provider;
    List<InventoryItem> values;
    ClassApplication application;
    SwipeRefreshLayout refreshLayout;

    public InventoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        refreshLayout = view.findViewById(R.id.viewSwipe);
        refreshLayout.setOnRefreshListener(this::refresh);
        if (getActivity() != null && isAdded()) {
            recyclerView = view.findViewById(R.id.recycler_inventory_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            values = new ArrayList<>();
            application = (ClassApplication) getActivity().getApplication();
            provider = application.getDatabaseProvider();
            /*RecyclerView recyclerView = view.findViewById(R.id.recycler_inventory_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            values = new ArrayList<>();

            // insert data from firebase

            application = (ClassApplication) getActivity().getApplication();
            provider = application.getDatabaseProvider();
            values = provider.getMyInv();
            if (values == null) {
                Log.e(TAG, "My database does not exists");
            } else {
                Collections.sort(values);
                RecyclerView.Adapter<InventoryAdapter.ViewHolder> adapter = new InventoryAdapter(values, getContext());
                recyclerView.setAdapter(adapter);
            }
*/
            view.findViewById(R.id.btnUpdateInventory).setOnClickListener(v -> provider.updateRemoteInv(values));


            view.findViewById(R.id.btnInsertInventory).setOnClickListener(v -> {

                // Create the fragment and show it as a dialog.
                InsertDialogFragment newFragment = InsertDialogFragment.newInstance();
                newFragment.show(getActivity().getFragmentManager(), "dialog");


            });
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void onDestroy() {
        provider.updateRemoteInv(values);
        super.onDestroy();

    }

    private void initView() {
        if (getView() != null && getActivity() != null && isAdded()) {
            // insert data from firebase
            values = provider.getMyInv();
            if (values == null) {
                Log.e(TAG, "My database does not exists");
            } else {
                Collections.sort(values);
                adapter = new InventoryAdapter(values, getContext());
                recyclerView.setAdapter(adapter);
            }
        }
    }

    private void refresh() {
        if (getFragmentManager() != null) {
            initView();
            refreshLayout.setRefreshing(false);
        } else {
            Log.e(TAG, "Fragment manager == null");
        }
    }

}
