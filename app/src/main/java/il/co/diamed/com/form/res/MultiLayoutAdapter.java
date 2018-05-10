package il.co.diamed.com.form.res;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import il.co.diamed.com.form.R;

public class MultiLayoutAdapter extends RecyclerView.Adapter<MultiLayoutAdapter.ViewHolder> {

    private List<MultiLayoutItem> list;
    private Context context;

    public MultiLayoutAdapter(List<MultiLayoutItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_view, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MultiLayoutItem item = list.get(position);
        holder.textView.setText(item.getText());
        //holder.devView = item.getView();
        View layout = holder.devView;
        try {


            ViewGroup parent = (ViewGroup) layout.getParent();
            int index = parent.indexOfChild(layout);
            parent.removeView(layout);
            layout = LayoutInflater.from(context).inflate(item.getLayout_res(), (ViewGroup) holder.devView.getParent());
            parent.addView(layout, index);
        } catch (NullPointerException e) {

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public View devView;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textview_item);
            devView = itemView.findViewById(R.id.view_item);

        }
    }


}
