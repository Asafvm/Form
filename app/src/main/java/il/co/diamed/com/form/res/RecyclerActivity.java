package il.co.diamed.com.form.res;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import il.co.diamed.com.form.R;

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
        list.add(new Item(R.layout.form_header,""));
        list.add(new Item(R.layout.device_centrifuge_layout,"text"));
        list.add(new Item(R.layout.device_diacentcw_layout,"text"));
        list.add(new Item(R.layout.device_general_layout,"test"));
        list.add(new Item(R.layout.device_diacent12_layout,"text"));
        list.add(new Item(R.layout.device_gelstation_layout,"more stuff"));
        list.add(new Item(R.layout.form_footer,""));

        adapter = new MyAdapter(list,this);
        recyclerView.setAdapter(adapter);




    }
}
