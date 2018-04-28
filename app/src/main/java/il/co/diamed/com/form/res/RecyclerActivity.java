package il.co.diamed.com.form.res;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.IncubatorActivity;

public class RecyclerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Item> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        recyclerView = findViewById(R.id.recycler_view);
        //recyclerView.hasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        list.add(new Item(R.layout.incubator_layout,"test"));
        list.add(new Item(R.layout.centrifuge_layout,"text"));
        list.add(new Item(R.layout.gelstation_layout,"more stuff"));
        list.add(new Item(R.layout.gelstation_layout,"last one"));

        adapter = new MyAdapter(list,this);
        recyclerView.setAdapter(adapter);

    }
}
