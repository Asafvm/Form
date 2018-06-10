package il.co.diamed.com.form.devices;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.res.SimpleFragmentPagerAdapter;
import il.co.diamed.com.form.res.MultiLayoutActivity;


public class DevicesFragment extends Fragment {
    //private static final String TAG = "DeviceFragment";
    private ViewPager viewPager;
    private SimpleFragmentPagerAdapter adapter;

    public DevicesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        /* Tabs */
        // Find the view pager that will allow the user to swipe between fragments
        viewPager = view.findViewById(R.id.pager);
        // Create an adapter that knows which fragment should be shown on each page
        adapter = new SimpleFragmentPagerAdapter(getContext(),getChildFragmentManager());//getActivity().getSupportFragmentManager());
        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = view.findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


    public void deviceSelect(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.idInc:
                intent = new Intent(getContext(), IncubatorActivity.class);
                break;
            case R.id.idCent:
                intent = new Intent(getContext(), CentrifugeActivity.class);
                break;
            case R.id.idDiacent12:
                intent = new Intent(getContext(), Diacent12Activity.class);
                break;
            case R.id.idDiacentCW:
                intent = new Intent(getContext(), DiacentCWActivity.class);
                break;
            case R.id.ultraCW:
                intent = new Intent(getContext(), DiacentUltraCWActivity.class);
                break;
            case R.id.plasma:
                intent = new Intent(getContext(), PlasmaThawerActivity.class);
                break;
            case R.id.idGelstation:
                intent = new Intent(getContext(), GelstationActivity.class);
                break;
            case R.id.ih500:
                intent = new Intent(getContext(), IH500Activity.class);
                break;
            case R.id.ih1000:
                intent = new Intent(getContext(), IH1000Activity.class);
                break;
            case R.id.ib10:
                intent = new Intent(getContext(), GeneralUseActivity.class);
                intent.putExtra("type", R.id.ib10);
                break;
            case R.id.pt10:
                intent = new Intent(getContext(), GeneralUseActivity.class);
                intent.putExtra("type", R.id.pt10);
                break;
            case R.id.docureader:
                intent = new Intent(getContext(), GeneralUseActivity.class);
                intent.putExtra("type", R.id.dr2);
                break;
            case R.id.edan:
                intent = new Intent(getContext(), GeneralUseActivity.class);
                intent.putExtra("type", R.id.edan);
                break;
            case R.id.hc10:
                intent = new Intent(getContext(), HC10Activity.class);
                break;
            case R.id.docon:
                intent = new Intent(getContext(), DoconActivity.class);
                break;
            case R.id.fridge:
                intent = new Intent(getContext(), FridgeActivity.class);
                break;
            case R.id.sepax:
                intent = new Intent(getContext(), SepaxActivity.class);
                break;
            //////////////////////////////
            case R.id.test:
                intent = new Intent(getContext(), MultiLayoutActivity.class);
                break;
        }
        if (intent != null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
            final String techname = sharedPref.getString("techName", "");
            final String signature = sharedPref.getString("signature", "");
            final String thermometer = sharedPref.getString("thermometer", "");
            final String barometer = sharedPref.getString("barometer", "");
            final String timer = sharedPref.getString("timer", "");
            final String speedometer = sharedPref.getString("speedometer", "");


            Bundle calibrationDevices = new Bundle();
            calibrationDevices.putString("thermometer", thermometer);
            calibrationDevices.putString("barometer", barometer);
            calibrationDevices.putString("speedometer", speedometer);
            calibrationDevices.putString("timer", timer);
            calibrationDevices.putString("techName", techname);
            calibrationDevices.putString("signature", signature);
            intent.putExtra("cal", calibrationDevices);
            startActivityForResult(intent, 1);
        } else
            Toast.makeText(getContext(), R.string.noDevice, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        Activity activity = getActivity();
        if (activity != null && isAdded()) {
            super.onResume();
        }else{
            if(activity!=null)
                activity.onBackPressed();
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        viewPager = null;
        adapter = null;
    }


}
