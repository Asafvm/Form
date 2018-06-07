package il.co.diamed.com.form.inventory;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

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
    List<InventoryItem> values;
    ClassApplication application;
    private OnFragmentInteractionListener mListener;

    public InventoryFragment() {
        // Required empty public constructor
    }
    public static InventoryFragment newInstance(String param1, String param2) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        application = (ClassApplication) getActivity().getApplication();
        recyclerView = view.findViewById(R.id.recycler_inventory_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        values = new ArrayList<>();

        /** insert data from firebase **/

        DatabaseProvider provider = application.getDatabaseProvider();
        values = provider.getMyInv();
        if(values == null){
            Log.e(TAG, "My database does not exists");
        }else {
            Collections.sort(values);
            adapter = new InventoryAdapter(values, getContext());
            recyclerView.setAdapter(adapter);
        }
        view.findViewById(R.id.btnUpdateInventory).setOnClickListener(v -> provider.updateDatabase(values));


        view.findViewById(R.id.btnInsertInventory).setOnClickListener(v -> {

            // Create the fragment and show it as a dialog.
            InsertDialogFragment newFragment = InsertDialogFragment.newInstance();
            newFragment.show(getActivity().getFragmentManager(), "dialog");


        });

        return view;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.fragment_inventory_insert, null))
                // Add action buttons
                .setPositiveButton(R.string.insertTitle, (dialog, id) -> {
                    // sign in the user ...
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {

                });
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
