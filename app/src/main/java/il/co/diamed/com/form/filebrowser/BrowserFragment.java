package il.co.diamed.com.form.filebrowser;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;

import il.co.diamed.com.form.R;


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
