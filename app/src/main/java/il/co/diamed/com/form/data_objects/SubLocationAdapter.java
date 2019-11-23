package il.co.diamed.com.form.data_objects;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import il.co.diamed.com.form.R;

public class SubLocationAdapter extends RecyclerView.Adapter<SubLocationAdapter.ViewHolder> {
    private List<SubLocation> list;

    public SubLocationAdapter(List<SubLocation> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public SubLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_sublocation_item, parent, false);
        return new SubLocationAdapter.ViewHolder(v);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

    }

    @Override
    public void onBindViewHolder(@NonNull final SubLocationAdapter.ViewHolder holder, int position) {
        final SubLocation item = list.get(position);
        holder.tvComments.setText(item.getComments());
        holder.tvName.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void clear() {
        int size = list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                list.remove(0);
                notifyItemRangeRemoved(0, size);
            }
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName;
        TextView tvPhone;
        TextView tvComments;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvName = itemView.findViewById(R.id.sublocation_item_name);
            tvPhone = itemView.findViewById(R.id.sublocation_item_phone);
            tvComments = itemView.findViewById(R.id.sublocation_item_desc);

        }


        @Override
        public void onClick(View v) {

        }


    }
}

