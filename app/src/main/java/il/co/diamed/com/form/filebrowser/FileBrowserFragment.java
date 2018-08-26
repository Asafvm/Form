package il.co.diamed.com.form.filebrowser;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import il.co.diamed.com.form.BuildConfig;
import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

import static il.co.diamed.com.form.filebrowser.FileBrowserAdapter.colorMarked;
import static il.co.diamed.com.form.filebrowser.FileBrowserAdapter.colorUnmarked;


public class FileBrowserFragment extends Fragment {
    private static final String TAG = "FileBrowserFragment";
    private String path;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<FileBrowserItem> values;
    int childCount;

    public FileBrowserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) // Use the current directory as title
            path = getArguments().getString("path");
        if (getActivity() != null && isAdded()) {
            getActivity().setTitle(path);
            childCount = 0;
            //getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getContext() != null) {
            getContext().registerReceiver(mReceiver,
                    new IntentFilter(FileBrowserAdapter.BROADCAST_FILTER));
        }
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_file_browser, container, false);


        recyclerView = view.findViewById(R.id.recycler_file_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new LandingAnimator());
        ((TextView) view.findViewById(R.id.path_text)).setText(path);

        initView(path);


        return view;
    }

    private void initView(String path) {
        // Read all files sorted into the values-array
        values = new ArrayList<>();


        File dir = new File(path);
        if (!dir.canRead()) {
            if (getActivity() != null && isAdded())
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
        if (childCount > 0) {
            values.add(0, new FileBrowserItem("..", true));
        }
        adapter = new FileBrowserAdapter(values, getContext());
        ((FileBrowserAdapter) adapter).clearMarked();
        recyclerView.setAdapter(adapter);
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context,intent.getStringExtra("path"),Toast.LENGTH_SHORT).show();
            Log.e(TAG, "got intent reciever");
            onListItemClick(intent.getStringExtra("filename"));
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
        if (getActivity() != null && isAdded())
            getActivity().overridePendingTransition(0, 0);
    }

    private void onListItemClick(String filename) {
        //String filename = (String) getListAdapter().getItem(position);
        if (filename.equals("..")) {
            childCount--;
            path = path.substring(0, path.lastIndexOf("/"));
            initView(path);
        } else {
            if (path.endsWith(File.separator)) {
                filename = path + filename;
            } else {
                filename = path + File.separator + filename;
            }

            File file = new File(filename);
            if (file.isDirectory()) {
                path = filename;
                if (getView() != null)
                    ((TextView) getView().findViewById(R.id.path_text)).setText(path);
                childCount++;
                initView(path);
            /*
            FileBrowserFragment mFileBrowserFragment = new FileBrowserFragment();
            Bundle bundle = new Bundle();
            bundle.putString("path", filename);
            mFileBrowserFragment.setArguments(bundle);


            FragmentManager mFragmentManager = getFragmentManager();
            FragmentTransaction mFragmentTransaction;
            if (mFragmentManager != null) {
                mFragmentTransaction = mFragmentManager.beginTransaction().remove(mFileBrowserFragment);

                mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.addToBackStack(null);
                mFragmentTransaction.replace(R.id.pager, mFileBrowserFragment).commit();

            }*/
                if (getActivity() != null && isAdded())
                    getActivity().overridePendingTransition(0, 0);
            } else {
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
            }
        }
    }


    private void shareItem(String[] filenames) {

        String[] pathBreak = path.split("/");
        int location = pathBreak.length;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mailSubject) + " - " + pathBreak[location - 2] + ", " + pathBreak[location - 1]);
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
            if (file.isDirectory()) {
                Toast.makeText(getContext(), "לא ניתן לשתף תיקיות, בחר קבצים", Toast.LENGTH_SHORT).show();
                return;
            }
            stringBuilder.append(getMailBody(batchfile[1], batchfile[2]));
            if (getContext() != null)
                files.add(FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID, file));
        }
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getMailHeader(filenames[0].split("_")[0]) + stringBuilder.toString());


        startActivity(Intent.createChooser(shareIntent, "שתף"));
    }

    private String getMailBody(String type, String serial) {
        return ("דגם " + type + " מספר סידורי " + serial.replace(".pdf", "")) +
                '\n';
    }

    private String getMailHeader(String date) {

        return "לקוח יקר," +
                '\n' +
                "בתאריך " + date.substring(6) + "/" +
                date.substring(4, 6) + "/" +
                date.substring(0, 4) + " בוצע כיול תקופתי עבור המכשירים הבאים" +
                '\n';

    }


    private void deleteItem(String[] filenames) {

        for (String filename : filenames) {
            //files = filename.split("_");
            if (path.endsWith(File.separator)) {
                filename = path + filename;
            } else {
                filename = path + File.separator + filename;
            }
            deleteFile(new File(filename));
        }
        refresh();
    }

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            for (String aChildren : children) {
                deleteFile(new File(file, aChildren));
            }
            file.delete();

        } else {
            file.delete();
        }
    }

    private void uploadItem(String[] filenames) {

        for (String filename : filenames) {
            //files = filename.split("_");
            if (path.endsWith(File.separator)) {
                filename = path + filename;
            } else {
                filename = path + File.separator + filename;
            }

            uploadFile(new File(filename));
            //   Toast.makeText(getContext(), path.substring(path.lastIndexOf("MediForms/")) + "/" +
            //   ((TextView) recyclerView.getChildAt(0).findViewById(R.id.file_name)).getText().toString(), Toast.LENGTH_SHORT).show();
        }

    }


    private void uploadFile(File file) {
        if (getActivity() != null) {
            ClassApplication application = (ClassApplication) getActivity().getApplication();
            if (file.isDirectory()) {
                String[] children = file.list();
                for (String aChildren : children) {
                    uploadFile(new File(file, aChildren));
                }
            } else {
                application.uploadFile(file, "MediForms/");
            }
        }
    }


    private void refresh() {
        initView(path);
        /*
        if (getActivity() != null) {
            Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.module_container);
            if (currentFragment instanceof FileBrowserFragment) {
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
        if (getContext() != null)
            getContext().unregisterReceiver(mReceiver);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<String> list = new ArrayList<>();
        switch (item.getItemId()) {
            case R.id.action_delete:
                list.addAll(((FileBrowserAdapter) adapter).getMarkedItems());

                if (!list.isEmpty()) {

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setMessage("מחק " + list.size() + " קבצים?");
                    alertBuilder.setPositiveButton("כן", (dialog, which) -> {
                        String[] arrayList = new String[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            arrayList[i] = list.get(i);
                        }
                        deleteItem(arrayList);

                    });
                    alertBuilder.setNegativeButton("בטל", (dialog, which) -> {

                    });
                    alertBuilder.setCancelable(false);
                    alertBuilder.create().show();


                } else {
                    Toast.makeText(getContext(), "בחר קבצים למחיקה", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_share:
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    if (recyclerView.getChildAt(i).isActivated()) {
                        list.add(((TextView) recyclerView.getChildAt(i).findViewById(R.id.file_name)).getText().toString());
                    }
                }
                if (!list.isEmpty()) {
                    String[] arrayList = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        arrayList[i] = list.get(i);
                    }
                    shareItem(arrayList);

                } else {
                    Toast.makeText(getContext(), "בחר קבצים לשיתוף", Toast.LENGTH_SHORT).show();
                }


                return true;

            case R.id.action_upload:
                list.addAll(((FileBrowserAdapter) adapter).getMarkedItems());

                if (!list.isEmpty()) {
                    String[] arrayList = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        arrayList[i] = list.get(i);
                    }
                    uploadItem(arrayList);

                } else {
                    Toast.makeText(getContext(), "בחר קבצים להעלאה", Toast.LENGTH_SHORT).show();
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
        inflater.inflate(R.menu.my_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
