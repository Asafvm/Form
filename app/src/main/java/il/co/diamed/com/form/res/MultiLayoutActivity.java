package il.co.diamed.com.form.res;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import il.co.diamed.com.form.R;

public class MultiLayoutActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<MultiLayoutItem> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_layout);

        recyclerView = findViewById(R.id.recycler_layout_view);
        //recyclerView.hasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        list.add(new MultiLayoutItem(R.layout.form_header,""));
        list.add(new MultiLayoutItem(R.layout.device_centrifuge_layout,"text"));
        list.add(new MultiLayoutItem(R.layout.device_diacentcw_layout,"text"));
        list.add(new MultiLayoutItem(R.layout.device_general_layout,"test"));
        list.add(new MultiLayoutItem(R.layout.device_diacent12_layout,"text"));
        list.add(new MultiLayoutItem(R.layout.device_gelstation_layout,"more stuff"));
        list.add(new MultiLayoutItem(R.layout.form_footer,""));

        adapter = new MultiLayoutAdapter(list,this);
        recyclerView.setAdapter(adapter);




    }
}
