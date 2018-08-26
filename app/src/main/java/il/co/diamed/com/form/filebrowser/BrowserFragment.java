package il.co.diamed.com.form.filebrowser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.CubeInTransformer;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.calibration.CentrifugeActivity;
import il.co.diamed.com.form.calibration.Diacent12Activity;
import il.co.diamed.com.form.calibration.DiacentCWActivity;
import il.co.diamed.com.form.calibration.DiacentUltraCWActivity;
import il.co.diamed.com.form.calibration.DoconActivity;
import il.co.diamed.com.form.calibration.FridgeActivity;
import il.co.diamed.com.form.calibration.GelstationActivity;
import il.co.diamed.com.form.calibration.GeneralUseActivity;
import il.co.diamed.com.form.calibration.HC10Activity;
import il.co.diamed.com.form.calibration.IH1000Activity;
import il.co.diamed.com.form.calibration.IH500Activity;
import il.co.diamed.com.form.calibration.IncubatorActivity;
import il.co.diamed.com.form.calibration.PlasmaThawerActivity;
import il.co.diamed.com.form.calibration.SepaxActivity;
import il.co.diamed.com.form.calibration.res.DeviceDialogFragment;
import il.co.diamed.com.form.res.MultiLayoutActivity;


public class BrowserFragment extends Fragment {
    //private static final String TAG = "DeviceFragment";
    private ViewPager viewPager;
    private BrowserFragmentPagerAdapter adapter;

    public BrowserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browsers, container, false);
        /* Tabs */
        // Find the view pager that will allow the user to swipe between fragments
        viewPager = view.findViewById(R.id.pager);
        // Create an adapter that knows which fragment should be shown on each page
        adapter = new BrowserFragmentPagerAdapter(getContext(), getChildFragmentManager());//getActivity().getSupportFragmentManager());
        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new CubeOutTransformer());

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = view.findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);


        return view;
    }


    @Override
    public void onResume() {
        Activity activity = getActivity();
        if (activity != null && isAdded()) {
            super.onResume();
        } else {
            if (activity != null)
                activity.onBackPressed();
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        viewPager = null;
        adapter = null;
    }

    public void dataChanged() {
        if (viewPager.getAdapter() != null)
            viewPager.getAdapter().notifyDataSetChanged();
    }


}
