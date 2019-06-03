package il.co.diamed.com.form.filebrowser;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import il.co.diamed.com.form.R;

public class BrowserFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private FirebaseBrowserFragment mFirebaseBrowserFragment;
    private FileBrowserFragment mFileBrowserFragment;

    BrowserFragmentPagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);
        mContext = context;

    }


    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (mFileBrowserFragment == null) {
                mFileBrowserFragment = new FileBrowserFragment();
                Bundle bundle = new Bundle();
                bundle.putString("path", Environment.getExternalStorageDirectory() + "/Documents/MediForms/");
                mFileBrowserFragment.setArguments(bundle);

            }
            return mFileBrowserFragment;

        } else {
            if (mFirebaseBrowserFragment == null) {
                mFirebaseBrowserFragment = new FirebaseBrowserFragment();
                Bundle bundle = new Bundle();
                bundle.putString("path", "Files/MediForms");
                mFirebaseBrowserFragment.setArguments(bundle);
            }
            return mFirebaseBrowserFragment;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.localBrowser);
            case 1:
                return mContext.getString(R.string.remoteBrowser);

            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof FileBrowserFragment && mFileBrowserFragment != null) {
            return POSITION_UNCHANGED;
        }else if (object instanceof FirebaseBrowserFragment && mFirebaseBrowserFragment != null)
            return POSITION_UNCHANGED;
        else
            return POSITION_NONE;

    }
}
