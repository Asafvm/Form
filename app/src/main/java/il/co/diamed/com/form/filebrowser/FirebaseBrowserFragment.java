package il.co.diamed.com.form.filebrowser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.providers.DatabaseProvider;
import il.co.diamed.com.form.res.providers.StorageProvider;


public class FirebaseBrowserFragment extends Fragment {
    private static final String TAG = "FirebaseBrowserFragment";
    private String path;
    private HashMap<String, String> files;
    private ClassApplication application;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<FileBrowserItem> values;
    private StorageProvider storageProvider;

    public FirebaseBrowserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) // Use the current directory as title
            path = getArguments().getString("path");
        if (getActivity() != null && isAdded())
            getActivity().setTitle(path);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //LocalBroadcastManager.getInstance(getActivity())
        if (getContext() != null) {
            getContext().registerReceiver(mReceiver,
                    new IntentFilter(FileBrowserAdapter.BROADCAST_FILTER));

            getContext().registerReceiver(mFirebaseReceiver,
                    new IntentFilter(DatabaseProvider.BROADCAST_FIREBASE_DIR));
        }
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file_browser, container, false);
        recyclerView = view.findViewById(R.id.recycler_file_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ((EditText) view.findViewById(R.id.et_searchtext)).setText("");
        ((EditText) view.findViewById(R.id.et_searchtext)).addTextChangedListener(filter);
        ((TextView) view.findViewById(R.id.path_text)).setText(path);
        if (getActivity() != null) {
            application = (ClassApplication) getActivity().getApplication();
            application.getDatabaseProvider(getContext()).getFirebaseDir(path);
        }


        return view;
    }

    private void refreshView(HashMap<String, String> files) {
        // Read all files sorted into the values-array
        //List values = new ArrayList();
        values = new ArrayList<>();

        for (String s : files.keySet()) {
            if (files.get(s).equals("")) {
                values.add(new FileBrowserItem(s, files.get(s).equals("")));
            } else {
                values.add(new FileBrowserItem(s, files.get(s).equals("")));
            }
        }
        Collections.sort(values);
        //if (childCount > 0) {
        values.add(0, new FileBrowserItem("..", true));
        //}
        adapter = new FileBrowserAdapter(values, getContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    private void refreshView(HashMap<String, String> files, String filter) {
        // Read all files sorted into the values-array
        //List values = new ArrayList();
        values = new ArrayList<>();

        for (String s : files.keySet()) {
            if (s.toLowerCase().contains(filter.toLowerCase())) {
                if (files.get(s).equals("")) {
                    values.add(new FileBrowserItem(s, files.get(s).equals("")));
                } else {
                    values.add(new FileBrowserItem(s, files.get(s).equals("")));
                }
            }
        }
        Collections.sort(values);
        //if (childCount > 0) {
        values.add(0, new FileBrowserItem("..", true));
        //}
        adapter = new FileBrowserAdapter(values, getContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context,intent.getStringExtra("path"),Toast.LENGTH_SHORT).show();
                Log.e(TAG, "got intent reciever");
                if (getUserVisibleHint()) {
                    try {
                        onListItemClick(intent.getStringExtra("filename"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


        }
    };
    private BroadcastReceiver mFirebaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context,intent.getStringExtra("path"),Toast.LENGTH_SHORT).show();
            Log.e(TAG, "got intent reciever");
            files = (HashMap<String, String>) intent.getSerializableExtra("filenameArray");
            refreshView(files);


        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && isAdded())
            getActivity().overridePendingTransition(0, 0);
    }

    private void onListItemClick(String filename) throws IOException {

        if (filename.equals("..")) {

            if (getActivity() != null) {
                path = path.substring(0, path.lastIndexOf("/"));
                if (getView() != null)
                    ((TextView) getView().findViewById(R.id.path_text)).setText(path);
                application = (ClassApplication) getActivity().getApplication();
                application.getDatabaseProvider(getContext()).getFirebaseDir(path);
            }
        } else {
            if (!filename.contains(".")) {
                if (path.endsWith(File.separator)) {
                    filename = path + filename;
                } else {
                    filename = path + File.separator + filename;
                }
                path = filename;
                if (getView() != null)
                    ((TextView) getView().findViewById(R.id.path_text)).setText(path);
                if (getActivity() != null) {
                    application = (ClassApplication) getActivity().getApplication();
                    application.getDatabaseProvider(getContext()).getFirebaseDir(path);
                }


            } else {
                //open item
                application.getStorageProvider(getContext()).downloadFile(path, filename);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<String> list = new ArrayList<>();
        switch (item.getItemId()) {

            case R.id.action_download:

                list.addAll(((FileBrowserAdapter) adapter).getMarkedItems());

                if (!list.isEmpty()) {
                    if (getActivity() != null) {
                        ClassApplication application = (ClassApplication) getActivity().getApplication();
                        storageProvider = application.getStorageProvider(getContext());
                    }

                    for (int i = 0; i < list.size(); i++) {
                        storageProvider.downloadFile(path, list.get(i));
                    }
                    //uploadItem(arrayList);
                    ((FileBrowserAdapter) adapter).clearMarked();
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "בחר קבצים להורדה", Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.action_select_all:
                if (recyclerView.getChildCount() > 1) {
                    boolean mark = recyclerView.getChildAt(recyclerView.getChildCount() - 1).isActivated();

                    if (!mark)
                        ((FileBrowserAdapter) adapter).selectAll();//.addToMarked(recyclerView.getChildAt(i));
                    else
                        ((FileBrowserAdapter) adapter).clearMarked();//.removeFromMarked(recyclerView.getChildAt(i));
                    adapter.notifyDataSetChanged();


                    return true;
                }


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.toolbar_firebasebrowser, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getContext() != null) {
            getContext().unregisterReceiver(mReceiver);
            getContext().unregisterReceiver(mFirebaseReceiver);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }


    private TextWatcher filter = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count > 2)
                refreshView(files, s.toString());
            else
                refreshView(files);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
