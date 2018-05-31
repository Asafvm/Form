package il.co.diamed.com.form.res;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import il.co.diamed.com.form.BuildConfig;
import il.co.diamed.com.form.R;


public class FileBrowserFragment extends Fragment {
    private static final String TAG = "FileBrowserFragment";
    private String path;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<FileBrowserItem> values;
    private OnFragmentInteractionListener mListener;

    public FileBrowserFragment() {
        // Required empty public constructor
    }

    public static FileBrowserFragment newInstance(String param1, String param2) {
        FileBrowserFragment fragment = new FileBrowserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) // Use the current directory as title
            path = getArguments().getString("path");

        getActivity().setTitle(path);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //LocalBroadcastManager.getInstance(getActivity())
        getContext().registerReceiver(mReceiver,
                new IntentFilter(FileBrowserAdapter.BROADCAST_FILTER));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file_browser, container, false);


        //getActivity().registerReceiver(mReceiver, new IntentFilter(FileBrowserAdapter.BROADCAST_FILTER));

        recyclerView = view.findViewById(R.id.recycler_file_view);
        //recyclerView.hasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        //ClassApplication application = (ClassApplication)getApplication();
        //application.getDir("MediForms/");
        ((TextView) view.findViewById(R.id.path_text)).setText(path);

        // Read all files sorted into the values-array
        //List values = new ArrayList();
        values = new ArrayList<>();
        File dir = new File(path);
        if (!dir.canRead()) {
            getActivity().setTitle(getActivity().getTitle() + " (inaccessible)");
        }
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    values.add(new FileBrowserItem(file, !file.endsWith("pdf")));
                }
            }
        }
        Collections.sort(values);
        adapter = new FileBrowserAdapter(values, getContext());
        recyclerView.setAdapter(adapter);


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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context,intent.getStringExtra("path"),Toast.LENGTH_SHORT).show();
            Log.e(TAG, "got intent reciever");

            if (intent.hasExtra("share")) {
                if (intent.hasExtra("filename"))
                    shareItem(intent.getStringExtra("filename"));
                if (intent.hasExtra("batch"))
                    shareItem(intent.getStringArrayExtra("batch"));
            } else {
                onListItemClick(intent.getStringExtra("filename"));
            }
            Log.e(TAG, "Invalid intent");
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        //getActivity().registerReceiver(mReceiver, new IntentFilter(FileBrowserAdapter.BROADCAST_FILTER));
    }

    @Override
    public void onPause() {
        super.onPause();
        //getActivity().unregisterReceiver(mReceiver);
        getActivity().overridePendingTransition(0, 0);
    }

    private void onListItemClick(String filename) {
        //String filename = (String) getListAdapter().getItem(position);
        if (path.endsWith(File.separator)) {
            filename = path + filename;
        } else {
            filename = path + File.separator + filename;
        }
        File file = new File(filename);
        if (file.isDirectory()) {

            FileBrowserFragment mFileBrowserFragment = new FileBrowserFragment();
            Bundle bundle = new Bundle();
            bundle.putString("path", filename);
            mFileBrowserFragment.setArguments(bundle);
            FragmentManager mFragmentManager = getFragmentManager();
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            //ft.setCustomAnimations(R.animator, R.animator.fade_in);
            mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.replace(R.id.module_container, mFileBrowserFragment).commit();


            getActivity().overridePendingTransition(0, 0);
        } else {
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
    }

    private void shareItem(String filename) {
        String[] files = new String[0];
        files = filename.split("_");
        if (path.endsWith(File.separator)) {
            filename = path + filename;
        } else {
            filename = path + File.separator + filename;
        }
        File file = new File(filename);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mailSubject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getMailHeader(files[0]) + getMailBody(files[1],files[2]));
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, file));
        startActivity(shareIntent);
    }

    private void shareItem(String[] filenames) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mailSubject));
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<Uri> files = new ArrayList<>();
        String[] batchfile;
        for (String filename : filenames) {
            //files = filename.split("_");
            if (path.endsWith(File.separator)) {
                filename = path + filename;
            } else {
                filename = path + File.separator + filename;
            }
            batchfile = filename.split("_");
            File file = new File(filename);

            stringBuilder.append(getMailBody(batchfile[1],batchfile[2]));
            files.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID,file));
        }
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getMailHeader(filenames[0].split("_")[0])+stringBuilder.toString());


        startActivity(shareIntent);
    }

    private String getMailBody(String type, String serial) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("דגם " + type + " מספר סידורי " + serial.replace(".pdf", ""));
        stringBuilder.append('\n');
        String message = stringBuilder.toString();
        return message;
    }

    private String getMailHeader(String date) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("לקוח יקר,");
        stringBuilder.append('\n');
        stringBuilder.append("בתאריך " + date.substring(6) + "/" +
                date.substring(4, 6) + "/" +
                date.substring(0, 4) + " בוצע כיול תקופתי עבור המכשירים הבאים");
        stringBuilder.append('\n');
        String message = stringBuilder.toString();
        return message;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unregisterReceiver(mReceiver);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
