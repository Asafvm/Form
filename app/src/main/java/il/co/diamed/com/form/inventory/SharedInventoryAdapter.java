package il.co.diamed.com.form.inventory;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import il.co.diamed.com.form.R;

class SharedInventoryAdapter extends BaseExpandableListAdapter {

    public static final String BROADCAST_TARGET_SELECTED = "target_user_selected";
    private Context mContext;
    private List<Part> mGroupList;
    private List<ArrayList<String>> mChildList;

    public SharedInventoryAdapter(List<Part> list, List<ArrayList<String>> childValues, Context context) {
        this.mContext = context;
        this.mGroupList = list;
        this.mChildList = childValues;
    }

    @Override
    public int getGroupCount() {
        return mGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildList.get(groupPosition).size()/2;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_inventory_item_users, parent, false);
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_inventory_item, parent, false);
        //v.setClickable(true);
        ((TextView) convertView.findViewById(R.id.item_serial)).setText(((Part)getGroup(groupPosition)).getSerial());
        ((TextView) convertView.findViewById(R.id.item_description)).setText(((Part)getGroup(groupPosition)).getDescription());
        //((TextView) convertView.findViewById(R.id.item_inStock)).setText(((Part)getGroup(groupPosition)).getInStock());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        int total = 0;
        String name = "", count = "";
        if(convertView==null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_inventory_user, parent, false);
        convertView.setClickable(true);
            name = mChildList.get(groupPosition).get(childPosition*2);
            count = mChildList.get(groupPosition).get(childPosition*2 + 1);
            ((TextView) convertView.findViewById(R.id.user_name)).setText(name);
            ((TextView) convertView.findViewById(R.id.user_inStock)).setText(count);
            total += Integer.valueOf(mChildList.get(groupPosition).get(childPosition*2 + 1));



        String finalName = name;
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(BROADCAST_TARGET_SELECTED);
            intent.putExtra("target", finalName);
            mContext.sendBroadcast(intent);
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);

    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }


}
