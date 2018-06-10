package il.co.diamed.com.form.devices.res;

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import il.co.diamed.com.form.R;

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter{

    private Context mContext;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            Log.e("Adapter","0");
            return new DiamedFragment();

        } else if (position == 1){
            Log.e("Adapter","1");
            return new MedigalFragment();

        } else{// (position == 2){
            Log.e("Adapter","2");
            return new SamsungFragment();
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 3;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        Log.e("adapter",String.valueOf(position));

        switch (position) {
            case 0:
                return mContext.getString(R.string.diamed);
            case 1:
                return mContext.getString(R.string.medigal);
            case 2:
                return mContext.getString(R.string.samsung);

            default:
                return null;
        }
    }

}
