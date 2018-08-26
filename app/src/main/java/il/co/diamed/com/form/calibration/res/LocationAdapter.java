package il.co.diamed.com.form.calibration.res;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import il.co.diamed.com.form.R;

public class LocationAdapter extends ArrayAdapter<String> {


    private final List<String> items;
    private final ArrayList<String> suggestions;
    private final int viewResourceId;

    public LocationAdapter(@NonNull Context context, int resource, @NonNull List<String> items) {
        super(context, resource, items);

        this.items = items;
        this.suggestions = new ArrayList<>();
        this.viewResourceId = resource;

    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(vi).inflate(R.layout.autocomplete_layout, null);

            String s = items.get(position);
            if (s != null) {
                TextView customerNameLabel = convertView.findViewById(R.id.autocomplete);
                if (customerNameLabel != null) {
//              Log.i(MY_DEBUG_TAG, "getView Customer Name:"+customer.getName());
                    customerNameLabel.setText(s);
                }
            }

        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return resultValue.toString();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (String s : items) {
                    if (s.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(s);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<String> filteredList = (ArrayList<String>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (String c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}

