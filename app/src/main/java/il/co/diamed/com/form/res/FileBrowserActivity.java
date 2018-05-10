package il.co.diamed.com.form.res;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import il.co.diamed.com.form.ClassApplication;
import il.co.diamed.com.form.R;

public class FileBrowserActivity extends ListActivity {

    private String path;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<FileBrowserItem> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);

        recyclerView = findViewById(R.id.recycler_file_view);
        //recyclerView.hasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // Use the current directory as title
        path = Environment.getExternalStorageDirectory() + "/Documents/MediForms/";
        if (getIntent().hasExtra("path")) {
            path = getIntent().getStringExtra("path");
        }
        setTitle(path);

        values = new ArrayList<>();


        // Read all files sorted into the values-array
        //List values = new ArrayList();
        File dir = new File(path); //application.getDir(path);
        if (!dir.canRead()) {
            setTitle(getTitle() + " (inaccessible)");
        }
        String[] list = dir.list();
        if (list != null) {
            for (String file : list) {
                if (!file.startsWith(".")) {
                    //values.add(file);

                    values.add(new FileBrowserItem(file, !file.endsWith("pdf")));

                }
            }
        }
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        adapter = new FileBrowserAdapter(values, this);
        recyclerView.setAdapter(adapter);
        // Put the data into the list
        //ArrayAdapter adapter = new ArrayAdapter(this,
        //        android.R.layout.simple_list_item_2, android.R.id.text1, values);
        //setListAdapter(adapter);

    }
/*
    @Override
    protected void onListItemClick(RecyclerView l, View v, int position, long id) {
        String filename = (String) getListAdapter().getItem(position);
        if (path.endsWith(File.separator)) {
            filename = path + filename;
        } else {
            filename = path + File.separator + filename;
        }

        if (new File(filename).isDirectory()) {
            Intent intent = new Intent(this, FileBrowserActivity.class);
            intent.putExtra("path", filename);
            startActivity(intent);
        } else {
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(new File(filename)), "application/pdf");
            //target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            //Intent intent = Intent.createChooser(target, "Open File");
            try {
                startActivity(target);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
            }
        }
    }*/
}
