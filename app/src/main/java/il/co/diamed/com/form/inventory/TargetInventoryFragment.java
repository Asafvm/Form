package il.co.diamed.com.form.inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    String userName = "";
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


            view.findViewById(R.id.btnUpdateInventory).setOnClickListener(v -> getActivity().onBackPressed());


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
                        File csv = new File(dir, "PartList - "+ userName + ".csv");
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
            userName = getArguments().getString("name");
            ((TextView)getView().findViewById(R.id.tvTitle)).setText("מלאי ברכב של " + userName);
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
