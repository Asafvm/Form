package il.co.diamed.com.form.res;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DialogTitle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import il.co.diamed.com.form.BuildConfig;
import il.co.diamed.com.form.ClassApplication;
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
            if(intent.hasExtra("share"))
                shareItem(intent.getStringExtra("filename"));
            else
                onListItemClick(intent.getStringExtra("filename"));
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
            target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                    Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file);
            target.setDataAndType(uri, "application/pdf");
            try {
                startActivity(target);
                overridePendingTransition(0, 0);
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
        shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "abc@gmail.com" });
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mailSubject));
        shareIntent.putExtra(Intent.EXTRA_TEXT, getMailMessage(files));
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file));
        startActivity(shareIntent);
    }

    private String getMailMessage(String[] files) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("לקוח יקר,");
        stringBuilder.append('\n');
        stringBuilder.append("בתאריך " +files[0].substring(0,4)+"/"+
                                        files[0].substring(4,6)+"/"+
                                        files[0].substring(6)+ " בוצע כיול תקופתי עבור המכשירים הבאים");
        stringBuilder.append('\n');
        stringBuilder.append("דגם "+files[1]+" מספר סידורי "+files[2].replace(".pdf",""));


        String message = stringBuilder.toString();
        return message;

    }
}
