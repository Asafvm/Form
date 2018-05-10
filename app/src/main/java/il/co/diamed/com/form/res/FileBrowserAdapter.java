package il.co.diamed.com.form.res;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import il.co.diamed.com.form.R;

public class FileBrowserAdapter extends RecyclerView.Adapter<FileBrowserAdapter.ViewHolder> {

    private List<FileBrowserItem> list;
    private Context context;

    public FileBrowserAdapter(List<FileBrowserItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public FileBrowserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_file_browser_item, parent, false);

        return new FileBrowserAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final FileBrowserAdapter.ViewHolder holder, int position) {
        final FileBrowserItem item = list.get(position);
        holder.textView.setText(item.getText());
        if(item.isDirectory()){
            holder.icon.setImageResource(R.drawable.ic_folder_white_36dp);
            holder.share.setVisibility(View.INVISIBLE);
            holder.download.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        ImageView icon;
        ImageButton download;
        ImageButton share;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.file_name);
            icon = itemView.findViewById(R.id.file_type_icon);
            download = itemView.findViewById(R.id.file_download_button);
            share = itemView.findViewById(R.id.file_share_button);
        }
    }
}
