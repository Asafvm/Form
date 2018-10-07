package il.co.diamed.com.form.filebrowser;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import il.co.diamed.com.form.BuildConfig;
import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import il.co.diamed.com.form.res.providers.DatabaseProvider;


public class FirebaseBrowserFragment extends Fragment {
    private static final String TAG = "FirebaseBrowserFragment";
    private String path;
    ClassApplication application;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    HashMap<String, String> file_list;
    List<FileBrowserItem> values;
    File localFile;
    int childCount;

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
        int childCount = 0;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file_browser, container, false);
        recyclerView = view.findViewById(R.id.recycler_file_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ((TextView) view.findViewById(R.id.path_text)).setText(path);
        if (getActivity() != null) {
            application = (ClassApplication) getActivity().getApplication();
            application.getDatabaseProvider(getContext()).getFirebaseDir(path);
        }
        return view;
    }

    private void refreshView(HashMap<String, String> files) {
        file_list = files;
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
        if (childCount > 0) {
            values.add(0, new FileBrowserItem("..", true));
        }
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
            HashMap<String, String> files = (HashMap<String, String>) intent.getSerializableExtra("filenameArray");
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
                childCount--;
                path = path.substring(0, path.lastIndexOf("/"));
                if(getView()!=null)
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
                if(getView()!=null)
                    ((TextView) getView().findViewById(R.id.path_text)).setText(path);
                if (getActivity() != null) {
                    application = (ClassApplication) getActivity().getApplication();
                    application.getDatabaseProvider(getContext()).getFirebaseDir(path);
                    childCount++;
                }

/*
            FirebaseBrowserFragment mFileBrowserFragment = new FirebaseBrowserFragment();
            Bundle bundle = new Bundle();
            bundle.putString("path", filename);
            mFileBrowserFragment.setArguments(bundle);

            FragmentManager mFragmentManager = getFragmentManager();
            FragmentTransaction mFragmentTransaction;
            if (mFragmentManager != null) {
                mFragmentTransaction = mFragmentManager.beginTransaction();

                //ft.setCustomAnimations(R.animator, R.animator.fade_in);
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.replace(R.id.pager, mFileBrowserFragment).commit();
            }
            if (getActivity() != null && isAdded())
                getActivity().overridePendingTransition(0, 0);*/
            } else {
                //open item
                if (getContext() != null) {
                    StorageReference islandRef = FirebaseStorage.getInstance().getReference().child(file_list.get(filename));
                    localFile = File.createTempFile("preview", ".pdf", new File(Environment.getExternalStorageDirectory() + "/Documents/MediForms/"));
                    islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Intent target = new Intent(Intent.ACTION_VIEW);
                            target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                                    Intent.FLAG_ACTIVITY_NO_HISTORY);
                            Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, localFile);
                            target.setDataAndType(uri, "application/pdf");
                            try {
                                startActivity(target);
                            } catch (ActivityNotFoundException e) {
                                // Instruct the user to install a PDF reader here, or something
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
            }
        }

        /*} else {
            if (getContext() != null) {
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                        Intent.FLAG_ACTIVITY_NO_HISTORY);
                Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, file);
                target.setDataAndType(uri, "application/pdf");
                try {
                    startActivity(target);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                }
            }
        */
}

    @Override
    public void onResume() {
        super.onResume();
        if(localFile!=null && localFile.exists())
            localFile.delete();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(localFile!=null && localFile.exists())
            localFile.delete();

    }

    private void refresh() {
        refreshView(file_list);
        /*
        if (getActivity() != null) {
            Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.module_container);
            if (currentFragment instanceof FirebaseBrowserFragment) {
                FragmentTransaction fragTransaction = (getActivity()).getSupportFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();
            }
        }*/
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

}
