package il.co.diamed.com.form.calibration.res;

import androidx.fragment.app.FragmentManager;
import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.Log;

import il.co.diamed.com.form.R;

public class DevicesFragmentPagerAdapter extends FragmentPagerAdapter{

    private Context mContext;


    public DevicesFragmentPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;

    }


    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        //return new DiamedFragment();

        if (position == 0) {
            Log.e("Adapter","0");
            return new DiamedFragment();

        }

        else if (position == 1){
            Log.e("Adapter","1");
            return new MedigalFragment();

        } else{// (position == 2){
            Log.e("Adapter","2");
            return new POCFragment();
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
                return mContext.getString(R.string.poc);

            default:
                return null;
        }
    }

}
