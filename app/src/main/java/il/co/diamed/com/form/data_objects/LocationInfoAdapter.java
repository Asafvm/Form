package il.co.diamed.com.form.data_objects;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.menu.MainMenuAcitivity;

class LocationInfoAdapter extends BaseExpandableListAdapter {

    private static final String BROADCAST_TARGET_DEVICE_SELECTED = "LocationInfo_device_selected";
    private Context mContext;
    private ArrayList<SubLocation> mGroupList;
    private ArrayList<ArrayList<Device>> mChildList;
    private FragmentManager manager;
    private DeviceEditorFragment newFragment;

    LocationInfoAdapter(ArrayList<SubLocation> list, ArrayList<ArrayList<Device>> childValues, Context context) {
        this.mContext = context;
        this.mGroupList = list;
        this.mChildList = childValues;
    }


    public void updateData(ArrayList<SubLocation> groupValues, ArrayList<ArrayList<Device>> childValues) {
        this.mGroupList = groupValues;
        this.mChildList = childValues;
        notifyDataSetChanged();
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
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
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
        TextView next_m = convertView.findViewById(R.id.device_nextmaintenance);
        ((TextView) convertView.findViewById(R.id.device_name)).setText(String.format("%s %s", target.getDev_codename(), target.getDev_model()));
        ((TextView) convertView.findViewById(R.id.device_serial)).setText(target.getDev_serial());
        //SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(target.getDev_next_maintenance());

        int next_month = cal.get(Calendar.MONTH);
        int next_year = cal.get(Calendar.YEAR);
        next_m.setText("כיול הבא: " + (next_month + 1) + "/" + next_year);

        //today
        Date d = new Date();
        cal.setTime(d);

        Date date1 = new java.util.Date();
        Date date2 = target.getDev_next_maintenance();
        long diff = (date2.getTime() - date1.getTime()) / 1000 / 60 / 60 / 24; //next - today in days


        if (diff > 60) { //sometime
            String CAL_OK = "#00cc00";
            next_m.setTextColor(Color.parseColor(CAL_OK));
        }
        if (diff > 30 && diff < 60) { //next month
            String NEXT_MONTH = "#cccc00";
            next_m.setTextColor(Color.parseColor(NEXT_MONTH));
        }
        if (diff > 0 && diff < 30) { //this month
            String CAL_NOW = "#ffa500";
            next_m.setTextColor(Color.parseColor(CAL_NOW));
        }
        if (diff <= 0) {    //due
            String CAL_LATE = "#cc0000";
            next_m.setTextColor(Color.parseColor(CAL_LATE));
        }

        if (target.getDev_under_warranty()) {  //check under lifetime warranty
            ((TextView) convertView.findViewById(R.id.device_warrenty)).setText("באחריות"); //set text
            ((TextView) convertView.findViewById(R.id.device_warrenty)).setTextColor(Color.parseColor("#00cc00")); //set color
        } else { //check by install and end of warranty dates
            ((TextView) convertView.findViewById(R.id.device_warrenty)).setText(target.getEnd_of_warranty().getTime() - date1.getTime() > 0 ? "באחריות עד "+new SimpleDateFormat("MM/YYYY").format(target.getEnd_of_warranty()) : "לא באחריות"); //set text
            ((TextView) convertView.findViewById(R.id.device_warrenty)).setTextColor(Color.parseColor(target.getEnd_of_warranty().getTime() - date1.getTime() > 0 ? "#00cc00" : "#cc0000")); //set color
        }
        if (!target.getDev_comments().isEmpty())
            ((TextView) convertView.findViewById(R.id.device_comments)).setText(target.getDev_comments());
        else{
            ((TextView) convertView.findViewById(R.id.device_comments)).setText("");
        }


        convertView.setOnClickListener(v -> {
            manager = ((MainMenuAcitivity) mContext).getSupportFragmentManager();
            if (manager != null) {

                newFragment = new DeviceEditorFragment();
                Bundle bundle = new Bundle();
                bundle.putString("serial", target.getDev_serial());
                bundle.putString("type", target.getDev_codename());
                bundle.putLong("ins_date", target.getDev_install_date().getTime());
                bundle.putLong("eow_date", target.getEnd_of_warranty().getTime());
                bundle.putBoolean("under_warranty",target.getDev_under_warranty());
                bundle.putString("comments",target.getDev_comments());
                newFragment.setArguments(bundle);
                newFragment.show(manager, "dialog");
                //Intent intent = new Intent(BROADCAST_TARGET_DEVICE_SELECTED);
                //intent.putExtra("target", target.getDev_serial());
                //mContext.sendBroadcast(intent);
            }
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
