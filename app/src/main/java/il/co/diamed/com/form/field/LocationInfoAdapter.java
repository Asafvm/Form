package il.co.diamed.com.form.field;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import il.co.diamed.com.form.R;

class LocationInfoAdapter extends BaseExpandableListAdapter {

    private static final String BROADCAST_TARGET_DEVICE_SELECTED = "LocationInfo_device_selected";
    private Context mContext;
    private ArrayList<SubLocation> mGroupList;
    private ArrayList<ArrayList<Device>> mChildList;

    LocationInfoAdapter(ArrayList<SubLocation> list, ArrayList<ArrayList<Device>> childValues, Context context) {
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
        return mChildList.get(groupPosition).size();
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
        if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_location_info_list_sublocation, parent, false);

        ((TextView) convertView.findViewById(R.id.sublocatonTitle)).setText(((SubLocation) getGroup(groupPosition)).getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_location_info_list_device, parent, false);
        convertView.setClickable(true);
        Device target = mChildList.get(groupPosition).get(childPosition);

        ((TextView) convertView.findViewById(R.id.device_name)).setText(String.format("%s %s", target.getDev_codename(), target.getDev_model()));
        ((TextView) convertView.findViewById(R.id.device_serial)).setText(target.getDev_serial());
        ((TextView) convertView.findViewById(R.id.device_nextmaintenance)).setText(target.getDev_next_maintenance().toString());
        ((TextView) convertView.findViewById(R.id.device_warrenty)).setText(String.format("באחריות: %s", target.getDev_under_warranty() ? "כן" : "לא"));

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(BROADCAST_TARGET_DEVICE_SELECTED);
            intent.putExtra("target",  target.getDev_serial());
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
