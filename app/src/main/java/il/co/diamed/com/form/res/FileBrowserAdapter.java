package il.co.diamed.com.form.res;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import il.co.diamed.com.form.R;

public class FileBrowserAdapter extends RecyclerView.Adapter<FileBrowserAdapter.ViewHolder> {
    private final String TAG = "FileBrowserAdapter";
    public static final String BROADCAST_FILTER = "FileBrowserAdapter_broadcast_receiver_intent_filter";
    private List<FileBrowserItem> list;
    private Context context;

    FileBrowserAdapter(List<FileBrowserItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public FileBrowserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_file_browser_item, parent, false);
        v.setClickable(true);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setActivated(!v.isActivated());
                v.setBackgroundColor(v.isActivated() ? Color.BLUE : Color.parseColor("#eeeeee"));
            }
        });
        return new FileBrowserAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull final FileBrowserAdapter.ViewHolder holder, int position) {
        final FileBrowserItem item = list.get(position);
        holder.textView.setText(item.getText());
        holder.download.setVisibility(View.INVISIBLE);  //show when viewing online
        if (item.isDirectory()) {
            holder.icon.setImageResource(R.drawable.ic_folder_white_36dp);
            holder.share.setVisibility(View.INVISIBLE);

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnClickListener {
        TextView textView;
        ImageView icon;
        ImageButton download;
        ImageButton share;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);
            textView = itemView.findViewById(R.id.file_name);
            icon = itemView.findViewById(R.id.file_type_icon);
            download = itemView.findViewById(R.id.file_download_button);
            share = itemView.findViewById(R.id.file_share_button);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    v.setActivated(false);
                    v.setBackgroundColor(Color.parseColor("#dddddd"));
                    Log.e(TAG, textView.getText().toString());
                    Intent i = new Intent(BROADCAST_FILTER);
                    i.putExtra("share", true);
                    i.putExtra("filename", textView.getText().toString());
                    context.sendBroadcast(i);
                }
            });

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
        /*    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setActivated(true);
                v.setBackgroundColor(Color.parseColor("#28BCFB"));

                return true;

            } else {
                v.setActivated(false);
                v.setBackgroundColor(Color.parseColor("#dddddd"));
                Log.e(TAG, this.textView.getText().toString());
                Intent i = new Intent(BROADCAST_FILTER);
                i.putExtra("filename", this.textView.getText().toString());
                context.sendBroadcast(i);
*/
            return false;

        }


        public String getText() {
            return this.textView.getText().toString();
        }

        @Override
        public void onClick(View v) {

            v.setActivated(false);
            v.setBackgroundColor(Color.parseColor("#dddddd"));
            Log.e(TAG, this.textView.getText().toString());
            Intent i = new Intent(BROADCAST_FILTER);
            i.putExtra("filename", this.textView.getText().toString());
            context.sendBroadcast(i);

        }
    }
}

