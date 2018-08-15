package il.co.diamed.com.form.inventory;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import il.co.diamed.com.form.R;

public class InventoryViewerAdapter extends RecyclerView.Adapter<InventoryViewerAdapter.ViewHolder> {
    private List<Part> list;
    private Context context;

    public InventoryViewerAdapter(List<Part> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public InventoryViewerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_inventory_item, parent, false);
        v.setClickable(true);

        return new InventoryViewerAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final InventoryViewerAdapter.ViewHolder holder, int position) {
        final Part item = list.get(position);
        holder.serial.setText(item.getSerial());
        holder.description.setText(item.getDescription());
        holder.sub.setVisibility(View.INVISIBLE);
        holder.add.setVisibility(View.INVISIBLE);

        holder.inStock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (item.getInStock().compareTo(item.getMinimum_car()) > 0) {
                    holder.inStock.setBackgroundResource(R.drawable.shape_item_enough);
                } else if (item.getInStock().compareTo(item.getMinimum_car()) == 0) {
                    holder.inStock.setBackgroundResource(R.drawable.shape_item_warning);
                } else{
                    holder.inStock.setBackgroundResource(R.drawable.shape_item_low);
                }
            }
        });
        holder.inStock.setText(item.getInStock());
    }

    @Override
    public int getItemCount() {
        if(list!=null)
            return list.size();
        else
            return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView serial;
        TextView description;
        TextView inStock;
        ImageButton add;
        ImageButton sub;

        ViewHolder(View itemView) {
            super(itemView);
            serial = itemView.findViewById(R.id.item_serial);
            description = itemView.findViewById(R.id.item_description);
            inStock = itemView.findViewById(R.id.item_inStock);
            add = itemView.findViewById(R.id.addItem);
            sub = itemView.findViewById(R.id.subItem);


        }


    }
}

