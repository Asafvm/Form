package il.co.diamed.com.form.inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.providers.DatabaseProvider;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class InventoryFragment extends Fragment {
    private final String TAG = "InventoryFragment";
    RecyclerView recyclerView;
    RecyclerView.Adapter<InventoryAdapter.ViewHolder> adapter;
    DatabaseProvider provider;
    List<Part> values;
    ClassApplication application;
    SwipeRefreshLayout refreshLayout;
    SharedInventoryFragment mUsersInventoryFragment;
    String userName = "";

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
            application = (ClassApplication) getActivity().getApplication();
            userName = application.getAuthProvider().getUser().getDisplayName();
            ((TextView)view.findViewById(R.id.tvTitle)).setText("מלאי ברכב של " + userName);
            provider = application.getDatabaseProvider(getContext());
            recyclerView = view.findViewById(R.id.recycler_inventory_view);
            recyclerView.setItemAnimator(new SlideInUpAnimator());

            view.findViewById(R.id.btnUpdateInventory).setOnClickListener(v -> showUsersInventory());

            view.findViewById(R.id.btnInsertInventory).setOnClickListener(v -> {
                // Create the fragment and show it as a dialog.
                InsertDialogFragment newFragment = InsertDialogFragment.newInstance();
                newFragment.show(getActivity().getFragmentManager(), "dialog");
            });

            view.findViewById(R.id.btnDownloadList).setOnClickListener(v -> {
                // Create a csv file from the list at download folder
                if (values != null && !values.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Serial,Description,Amount\n");
                    for (Part p : values)
                        sb.append(p.getSerial()).append(",").append(p.getDescription()).append(",").append(p.getInStock()).append("\n");

                    File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MediApp");
                    if (!dir.exists())
                        dir.mkdir();

                    try {
                        File csv = new File(dir, "PartList - " + userName + ".csv");
                        FileWriter fw = new FileWriter(csv);
                        fw.append(sb.toString());
                        fw.flush();
                        fw.close();
                        Toast.makeText(getContext(),"Saved to Downloads\\MediApp folder",Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
        }
        return view;
    }

    private void showUsersInventory() {
        FragmentManager mFragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        if (mUsersInventoryFragment == null)
            mUsersInventoryFragment = new SharedInventoryFragment();

        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.END);
        slide.setDuration(500);
        mUsersInventoryFragment.setEnterTransition(slide);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.replace(R.id.module_container, mUsersInventoryFragment).commit();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "resume");
        if (getContext() != null)
            getContext().registerReceiver(databaseReceiver, new IntentFilter(DatabaseProvider.BROADCAST_DB_READY));
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        if (getContext() != null && getActivity() != null && isAdded()) {
            // insert data from firebase
            values = provider.getMyInv();
            if (values == null) {
                Log.e(TAG, "My database does not exists, waiting for broadcast");
            } else {
                refreshLayout.setRefreshing(false);
                Collections.sort(values);
                adapter = new InventoryAdapter(values, getContext());
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
        if (values != null)
            provider.uploadMyInv(values);
        try {
            if (getContext() != null)
                getContext().unregisterReceiver(databaseReceiver);
        } catch (Exception ignored) {
        }
    }


    private BroadcastReceiver databaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "Database alert!");
            initView();
        }
    };
}
