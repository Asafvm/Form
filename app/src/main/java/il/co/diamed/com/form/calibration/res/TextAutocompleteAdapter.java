package il.co.diamed.com.form.calibration.res;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import il.co.diamed.com.form.R;

public class TextAutocompleteAdapter extends ArrayAdapter<String> {


    private List<String> items;

    private final int viewResourceId;
    private final List<String> originalList;

    public TextAutocompleteAdapter(@NonNull Context context, int resource, @NonNull List<String> items) {
        super(context, resource, items);

        this.items = items;
        this.originalList = items;
        this.viewResourceId = resource;

    }

    @Nullable
    @Override
    public String getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getPosition(@Nullable String item) {
        return items.indexOf(item);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(vi).inflate(R.layout.autocomplete_layout, null);
        }
            String s = items.get(position);
            if (s != null) {
                TextView customerNameLabel = convertView.findViewById(R.id.autocomplete);
                if (customerNameLabel != null) {
//              Log.i(MY_DEBUG_TAG, "getView Customer Name:"+customer.getName());
                    customerNameLabel.setText(s);
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
            FilterResults results = new FilterResults();

            if (constraint == null ||constraint.length()==0) {
                items = originalList;
                notifyDataSetChanged();

                results.values = items;
                results.count = items.size();
            }
            else {
                ArrayList<String> suggestions = new ArrayList<>();
                for (String s : originalList) {
                    if (s.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(s);
                    }
                }
                results.values = suggestions;
                results.count = suggestions.size();

            }
            return results;
            /*if (constraint != null && !(constraint.length()==0)) {
                suggestions.clear();
                for (String s : items) {
                    if (s.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(s);
                    }
                }
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
            } else {
                filterResults.values = originalList;
                filterResults.count = originalList.size();
                items = originalList;
                notifyDataSetChanged();

            }
            return filterResults;*/
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //ArrayList<String> filteredList = (ArrayList<String>) results.values;
            if (results.count > 0) {
                items = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }else{
                notifyDataSetInvalidated();
            }
        }
    };
}

