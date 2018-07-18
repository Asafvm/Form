package il.co.diamed.com.form.inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.HashMap;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

public class UsersInventoryFragment extends Fragment {
    private final String TAG = "InventoryFragment";
    ExpandableListView expendableView;
    ExpandableListAdapter adapter;
    DatabaseProvider provider;
    List<InventoryItem> groupValues;
    List<ArrayList<String>> childValues;
    ClassApplication application;
    SwipeRefreshLayout refreshLayout;
    TargetInventoryFragment mTargetInventoryFragment;
    List<ArrayList<InventoryUser>> users;

    public UsersInventoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_inventory, container, false);

        refreshLayout = view.findViewById(R.id.viewSwipe);
        refreshLayout.setOnRefreshListener(this::refresh);

        if (getActivity() != null && isAdded()) {
            application = (ClassApplication) getActivity().getApplication();
            provider = application.getDatabaseProvider(getContext());
            expendableView = view.findViewById(R.id.recycler_inventory_view);
            initView();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "resume");
        if (getContext() != null) {
            getContext().registerReceiver(databaseReceiver, new IntentFilter(DatabaseProvider.BROADCAST_USER_DB_READY));
            getContext().registerReceiver(targetUserReceiver, new IntentFilter(UsersInventoryAdapter.BROADCAST_TARGET_SELECTED));
        }
        initView();
    }


    private void initView() {
        if (getContext() != null && getActivity() != null && isAdded()) {
            users = provider.getUsersInv();
            groupValues = provider.getLabInv();
            // insert data from firebase

            if (groupValues != null && users != null) {
                HashMap<String,String> values = new HashMap<>();
                childValues = new ArrayList<>();
                //for (InventoryItem item : groupValues) {

                for (ArrayList<InventoryUser> group : users) {
                    ArrayList<String> child = new ArrayList<>();
                    for (InventoryUser user : group) {
                        child.add(user.getName());
                        child.add(user.getInStock());
                    }
                    childValues.add(child);
                }

                adapter = new UsersInventoryAdapter(groupValues, childValues, getContext());
                expendableView.setAdapter(adapter);
            }
        }
    }

    protected void refresh() {
        if (getFragmentManager() != null) {
            initView();
            refreshLayout.setRefreshing(false);
        } else {
            Log.e(TAG, "Fragment manager == null");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (getContext() != null) {
                getContext().unregisterReceiver(databaseReceiver);
                getContext().unregisterReceiver(targetUserReceiver);
            }
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
    private BroadcastReceiver targetUserReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "User selected alert!");
            FirebaseUser firebaseUser = application.getAuthProvider().getCurrentUser();
            String target = null;
            Bundle bundle = intent.getExtras();
            if(bundle!=null)
                target = bundle.getString("target");
            if(firebaseUser!=null && target!=null && users!=null && getActivity() != null && isAdded())
                for(InventoryUser user : users.get(0))
                    if(user.getName().equals(target)) {
                        if (user.getId().equals(firebaseUser.getUid()))
                            getActivity().onBackPressed();
                        else
                            showTargetInventory(user.getId());
                    }
        }
    };

    private void showTargetInventory(String target) {
        FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        if(mTargetInventoryFragment==null)
            mTargetInventoryFragment = new TargetInventoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("target",target);
        mTargetInventoryFragment.setArguments(bundle);
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.END);
        slide.setDuration(500);
        mTargetInventoryFragment.setEnterTransition(slide);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.replace(R.id.module_container, mTargetInventoryFragment).commit();
    }


}
