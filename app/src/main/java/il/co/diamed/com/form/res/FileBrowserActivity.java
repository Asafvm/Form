package il.co.diamed.com.form.res;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import il.co.diamed.com.form.BuildConfig;
import il.co.diamed.com.form.R;

public class FileBrowserActivity extends AppCompatActivity {
    private final String TAG = "FileBrowserActivity";
    private String path;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<FileBrowserItem> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);
        registerReceiver(mReceiver, new IntentFilter(FileBrowserAdapter.BROADCAST_FILTER));

        recyclerView = findViewById(R.id.recycler_file_view);
        //recyclerView.hasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Use the current directory as title
        if (getIntent().hasExtra("path")) {
            path = getIntent().getStringExtra("path");
        }
        setTitle(path);

        //ClassApplication application = (ClassApplication)getApplication();
        //application.getDir("MediForms/");
        ((TextView) findViewById(R.id.path_text)).setText(path);

        // Read all files sorted into the values-array
        //List values = new ArrayList();
        values = new ArrayList<>();
        File dir = new File(path);
        if (!dir.canRead()) {
            setTitle(getTitle() + " (inaccessible)");
        }
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    values.add(new FileBrowserItem(file, !file.endsWith("pdf")));
                }
            }
        }

        adapter = new FileBrowserAdapter(values, this);
        recyclerView.setAdapter(adapter);


    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context,intent.getStringExtra("path"),Toast.LENGTH_SHORT).show();
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
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter(FileBrowserAdapter.BROADCAST_FILTER));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
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
            Intent intent = new Intent(this, FileBrowserActivity.class);
            intent.putExtra("path", filename);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else {
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                    Intent.FLAG_ACTIVITY_NO_HISTORY);
            Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file);
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
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file));
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
            files.add(FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID,file));
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
        stringBuilder.append("בתאריך " + date.substring(0, 4) + "/" +
                date.substring(4, 6) + "/" +
                date.substring(6) + " בוצע כיול תקופתי עבור המכשירים הבאים");
        stringBuilder.append('\n');
        String message = stringBuilder.toString();
        return message;

    }
}
