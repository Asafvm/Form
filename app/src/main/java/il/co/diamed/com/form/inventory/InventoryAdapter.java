package il.co.diamed.com.form.inventory;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import il.co.diamed.com.form.R;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {
    private final String TAG = "InventoryAdapter";
    private List<InventoryItem> list;
    private Context context;
    private View currentView = null;

    InventoryAdapter(List<InventoryItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public InventoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_inventory_item, parent, false);
        v.setClickable(true);

        return new InventoryAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final InventoryAdapter.ViewHolder holder, int position) {
        final InventoryItem item = list.get(position);
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
                try {
                    if (!s.toString().equals("")) {
                        item.setinStock(s.toString());
                        int stock = Integer.valueOf(item.getInStock());
                        int minimum = Integer.valueOf(item.getMinimum());
                        if (stock > minimum) {
                            holder.inStock.setBackgroundResource(R.drawable.shape_item_enough);
                        } else if (stock == minimum) {
                            holder.inStock.setBackgroundResource(R.drawable.shape_item_warning);
                        } else {
                            holder.inStock.setBackgroundResource(R.drawable.shape_item_low);
                        }
                    }
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error converting value at: " + item.getId() + " - " + item.getDescription() +
                            "\nstock: " + item.getInStock() + " minimum: " + item.getMinimum());
                }
            }
        });
        holder.inStock.setText(item.getInStock());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView serial;
        TextView description;
        TextView inStock;
        ImageButton add;
        ImageButton sub;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            serial = itemView.findViewById(R.id.item_serial);
            description = itemView.findViewById(R.id.item_description);
            inStock = itemView.findViewById(R.id.item_inStock);
            add = itemView.findViewById(R.id.addItem);
            sub = itemView.findViewById(R.id.subItem);


        }


        @Override
        public void onClick(View v) {
            if (itemView.isActivated()) {
                itemView.setActivated(false);
                add.setVisibility(View.INVISIBLE);
                sub.setVisibility(View.INVISIBLE);
                currentView = null;
            } else {
                if (currentView != null) {
                    currentView.setActivated(false);
                    currentView.findViewById(R.id.addItem).setVisibility(View.INVISIBLE);
                    currentView.findViewById(R.id.subItem).setVisibility(View.INVISIBLE);
                }
                currentView = itemView;
                itemView.setActivated(true);
                add.setVisibility(View.VISIBLE);
                if (Integer.valueOf(inStock.getText().toString()) > 0)
                    sub.setVisibility(View.VISIBLE);
                add.setOnClickListener(v1 -> {
                    int stock = Integer.valueOf(inStock.getText().toString());
                    inStock.setText(String.valueOf(stock + 1));
                    sub.setVisibility(View.VISIBLE);
                });
                sub.setOnClickListener(v1 -> {
                    int stock = Integer.valueOf(inStock.getText().toString());

                    inStock.setText(String.valueOf(stock - 1));
                    if (stock > 0)
                        sub.setVisibility(View.INVISIBLE);


                });
            }
        }


    }
}

