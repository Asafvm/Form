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

public class InventoryFragment extends Fragment{
    private final String TAG = "InventoryFragment";

    RecyclerView recyclerView;
    RecyclerView.Adapter<InventoryAdapter.ViewHolder> adapter;
    List<InventoryItem> values;
    ClassApplication application;

    public InventoryFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.viewSwipe);
        refreshLayout.setOnRefreshListener(() -> {
            refresh();
            refreshLayout.setRefreshing(false);
        });

        recyclerView = view.findViewById(R.id.recycler_inventory_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        values = new ArrayList<>();

        // insert data from firebase
        if (getActivity() != null && isAdded()) {
            application = (ClassApplication) getActivity().getApplication();
            DatabaseProvider provider = application.getDatabaseProvider();
            values = provider.getMyInv();
            if (values == null) {
                Log.e(TAG, "My database does not exists");
            } else {
                Collections.sort(values);
                adapter = new InventoryAdapter(values, getContext());
                recyclerView.setAdapter(adapter);
            }

            view.findViewById(R.id.btnUpdateInventory).setOnClickListener(v -> provider.updateRemoteInv(values));


            view.findViewById(R.id.btnInsertInventory).setOnClickListener(v -> {

                // Create the fragment and show it as a dialog.
                InsertDialogFragment newFragment = InsertDialogFragment.newInstance();
                newFragment.show(getActivity().getFragmentManager(), "dialog");


            });
        }
        return view;
    }

    private void refresh() {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        } else {
            Log.e(TAG, "Fragment manager == null");
        }
    }

}
