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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import il.co.diamed.com.form.R;

public class FileBrowserAdapter extends RecyclerView.Adapter<FileBrowserAdapter.ViewHolder> {
    private final String TAG = "FileBrowserAdapter";
    public static final String BROADCAST_FILTER = "FileBrowserAdapter_broadcast_receiver_intent_filter";
    private List<FileBrowserItem> list;
    private List<String> markedList;
    private Context context;
    private String colorMarked = "#173793";
    private String colorUnmarked = "#B9E6EA";


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
        v.setOnClickListener(v1 -> {
            v1.setActivated(!v1.isActivated());
            v1.setBackgroundColor(Color.parseColor(v1.isActivated() ? colorMarked : colorUnmarked));
        });
        return new FileBrowserAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final FileBrowserAdapter.ViewHolder holder, int position) {
        final FileBrowserItem item = list.get(position);
        holder.textView.setText(item.getText());
        holder.download.setVisibility(View.GONE);  //show when viewing online
        if (item.isDirectory()) {
            holder.icon.setImageResource(R.drawable.ic_folder_white_36dp);
            holder.share.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        TextView textView;
        ImageView icon;
        ImageButton download;
        ImageButton share;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            textView = itemView.findViewById(R.id.file_name);
            icon = itemView.findViewById(R.id.file_type_icon);
            download = itemView.findViewById(R.id.file_download_button);
            share = itemView.findViewById(R.id.file_share_button);

            share.setOnClickListener(v -> {
                if(markedList.isEmpty()) {
                    v.setActivated(false);
                    v.setBackgroundColor(Color.parseColor(colorUnmarked));
                    Log.e(TAG, textView.getText().toString());
                    Intent i = new Intent(BROADCAST_FILTER);
                    i.putExtra("share", true);
                    i.putExtra("filename", textView.getText().toString());
                    context.sendBroadcast(i);
                }else{
                    shareBatch();
                }
            });

            /** TODO: implement manual upload to server and delete option **/

        }


        public String getText() {
            return this.textView.getText().toString();
        }

        @Override
        public void onClick(View v) {
            if(v.isActivated()){
                v.setActivated(false);
                markedList.remove(this.textView.getText().toString());
                v.setBackgroundColor(Color.parseColor(colorUnmarked));
            }else if(!markedList.isEmpty()) {
                markedList.add(this.textView.getText().toString());
                v.setActivated(true);
                v.setBackgroundColor(Color.parseColor(colorMarked));
            }else{
                Log.e(TAG, this.textView.getText().toString());
                Intent i = new Intent(BROADCAST_FILTER);
                i.putExtra("filename", this.textView.getText().toString());
                context.sendBroadcast(i);
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if(v.findViewById(R.id.file_share_button).getVisibility()== View.INVISIBLE) {
                return false;
            }else {
                markedList.add(this.textView.getText().toString());
                v.setActivated(true);
                v.setBackgroundColor(Color.parseColor(colorMarked));

                return true;
            }
        }
        private void shareBatch(){
            Log.e(TAG, "preparing batch");
            Intent intent = new Intent(BROADCAST_FILTER);
            String[] arrayList = new String[markedList.size()];
            for(int i=0; i< markedList.size();i++){
                arrayList[i] = markedList.get(i);
            }
            intent.putExtra("share", true);
            intent.putExtra("batch", arrayList);
            context.sendBroadcast(intent);
        }


    }
}

