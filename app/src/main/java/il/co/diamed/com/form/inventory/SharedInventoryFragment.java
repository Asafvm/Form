package il.co.diamed.com.form.inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import java.util.List;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.providers.DatabaseProvider;

public class SharedInventoryFragment extends Fragment {
    private final String TAG = "InventoryFragment";
    private ExpandableListView expendableView;
    private DatabaseProvider provider;
    private ClassApplication application;
    private SwipeRefreshLayout refreshLayout;
    private TargetInventoryFragment mTargetInventoryFragment;
    private List<ArrayList<InventoryUser>> users;

    public SharedInventoryFragment() {
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
            getContext().registerReceiver(targetUserReceiver, new IntentFilter(SharedInventoryAdapter.BROADCAST_TARGET_SELECTED));
        }
        initView();
    }


    private void initView() {
        if (getContext() != null && getActivity() != null && isAdded()) {
            users = provider.getAllUsersInv();
            List<Part> groupValues = provider.getGlobalInv();
            // insert data from firebase

            if (groupValues != null && users != null) {
                //HashMap<String,String> values = new HashMap<>();
                List<ArrayList<String>> childValues = new ArrayList<>();
                //for (Part item : groupValues) {

                for (ArrayList<InventoryUser> group : users) {
                    ArrayList<String> child = new ArrayList<>();
                    for (InventoryUser user : group) {
                        child.add(user.getName());
                        child.add(user.getInStock());
                    }
                    childValues.add(child);
                }

                ExpandableListAdapter adapter = new SharedInventoryAdapter(groupValues, childValues, getContext());
                expendableView.setAdapter(adapter);
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
            FirebaseUser firebaseUser = application.getAuthProvider().getUser();
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
                            showTargetInventory(user.getId(),user.getName());
                    }
        }
    };

    private void showTargetInventory(String target, String name) {
        if(getActivity()!=null) {
            FragmentManager mFragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
            if (mTargetInventoryFragment == null)
                mTargetInventoryFragment = new TargetInventoryFragment();
            Bundle bundle = new Bundle();
            bundle.putString("target", target);
            bundle.putString("name", name);
            mTargetInventoryFragment.setArguments(bundle);
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.END);
            slide.setDuration(500);
            mTargetInventoryFragment.setEnterTransition(slide);
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.replace(R.id.module_container, mTargetInventoryFragment).commit();
        }
    }



}
