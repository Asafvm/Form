package il.co.diamed.com.form.filebrowser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import il.co.diamed.com.form.R;

public class FileBrowserAdapter extends RecyclerView.Adapter<FileBrowserAdapter.ViewHolder> {
    public static final String BROADCAST_FILTER = "FileBrowserAdapter_broadcast_receiver_intent_filter";
    private List<FileBrowserItem> list;
    private List<String> markedList;
    private Context context;

    static String colorMarked ="#11ccff";
    static String colorUnmarked = "#ffffff";
    private int selectedPos = RecyclerView.NO_POSITION;

    FileBrowserAdapter(List<FileBrowserItem> list, Context context) {
        this.list = list;
        this.context = context;
        this.markedList = new ArrayList<>();
    }

    @NonNull
    @Override
    public FileBrowserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_file_browser_item, parent, false);
        v.setClickable(true);


        return new FileBrowserAdapter.ViewHolder(v);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        if(holder.itemView.isActivated()){
            holder.itemView.setBackgroundColor(Color.parseColor(colorMarked));
        }else{
            holder.itemView.setBackgroundColor(Color.parseColor(colorUnmarked));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final FileBrowserAdapter.ViewHolder holder, int position) {
        final FileBrowserItem item = list.get(position);
        holder.textView.setText(item.getText());
        if (item.isDirectory()) {
            holder.icon.setImageResource(R.drawable.ic_folder_black_24dp);
        }
        holder.itemView.setActivated(markedList.contains(holder.textView.getText().toString()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addToMarked(View childAt) {

        if(markedList.add(((TextView)childAt.findViewById(R.id.file_name)).getText().toString())){
            childAt.setBackgroundColor(Color.parseColor(colorMarked));
            childAt.setActivated(true);
        }
    }
    public void removeFromMarked(View childAt) {

        if(markedList.remove(((TextView)childAt.findViewById(R.id.file_name)).getText().toString())){
            childAt.setActivated(false);
            childAt.setBackgroundColor(Color.parseColor(colorUnmarked));
        }

    }

    public void clearMarked() {
        markedList.clear();
    }


    public void selectAll() {
        for(FileBrowserItem item : list)
            markedList.add(item.getText());
        markedList.remove(".."); //if exists
    }

    public void removeAll() {

    }

    public List<String> getMarkedItems() {
        return markedList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        TextView textView;
        ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            textView = itemView.findViewById(R.id.file_name);
            icon = itemView.findViewById(R.id.file_type_icon);

        }

        public String getText() {
            return this.textView.getText().toString();
        }


        @Override
        public void onClick(View v) {
            if(!((TextView)v.findViewById(R.id.file_name)).getText().toString().equals("..")) {
                if (v.isActivated()) {

                    removeFromMarked(v);
                } else if (!markedList.isEmpty()) {
                    addToMarked(v);
                } else {
                    Intent i = new Intent(BROADCAST_FILTER);
                    i.putExtra("filename", this.textView.getText().toString());
                    context.sendBroadcast(i);
                }
            }else if(markedList.isEmpty()){
                Intent i = new Intent(BROADCAST_FILTER);
                i.putExtra("filename", this.textView.getText().toString());
                context.sendBroadcast(i);
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if(!((TextView)v.findViewById(R.id.file_name)).getText().toString().equals("..")) {
                if (v.isActivated()) {
                    removeFromMarked(v);

                    return true;
                } else {
                    addToMarked(v);

                    return true;
                }
            }
            return false;

        }
    }
}

