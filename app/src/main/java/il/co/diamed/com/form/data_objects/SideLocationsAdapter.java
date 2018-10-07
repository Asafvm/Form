package il.co.diamed.com.form.data_objects;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import il.co.diamed.com.form.R;

public class SideLocationsAdapter extends RecyclerView.Adapter<SideLocationsAdapter.ViewHolder> {
    public static final String BROADCASE_FOCUS = "broadcast_location_focus";
    private final String TAG = "InventoryAdapter";
    private List<Location> list;
    private Context context;
    private View currentView = null;

    SideLocationsAdapter(List<Location> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SideLocationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.side_locations_item, parent, false);
        v.setClickable(true);
        //v.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new SideLocationsAdapter.ViewHolder(v);

    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        if(holder.itemView.isActivated()){
            holder.itemView.setBackgroundColor(Color.CYAN);
        }else{
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final SideLocationsAdapter.ViewHolder holder, int position) {
        final Location item = list.get(position);
        holder.loc_lat.setText(String.valueOf(item.getLatitude()));
        holder.loc_long.setText(String.valueOf(item.getLongtitude()));
        holder.loc_name.setText(item.getName());
        if(item.getLatitude() == -1 || item.getLongtitude() == -1){
            holder.itemView.setBackgroundColor(Color.CYAN);
            holder.itemView.setActivated(true);
        }else{
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.itemView.setActivated(false);
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<Location> getList() {
        return list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView loc_name;
        TextView loc_lat;
        TextView loc_long;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            loc_name = itemView.findViewById(R.id.location_name);
            loc_lat = itemView.findViewById(R.id.location_lat);
            loc_long = itemView.findViewById(R.id.location_long);




        }


        @Override
        public void onClick(View v) {
            Intent intent = new Intent(BROADCASE_FOCUS);
            intent.putExtra("lat",Double.valueOf(loc_lat.getText().toString()));
            intent.putExtra("long",Double.valueOf(loc_long.getText().toString()));
            intent.putExtra("name",(loc_name.getText().toString()));
            context.sendBroadcast(intent);

        }


        @Override
        public boolean onLongClick(View v) {


            return true;
        }
    }
}

