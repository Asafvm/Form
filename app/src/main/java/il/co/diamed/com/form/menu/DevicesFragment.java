package il.co.diamed.com.form.menu;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import il.co.diamed.com.form.R;
import il.co.diamed.com.form.devices.CentrifugeActivity;
import il.co.diamed.com.form.devices.Diacent12Activity;
import il.co.diamed.com.form.devices.DiacentCWActivity;
import il.co.diamed.com.form.devices.DiacentUltraCWActivity;
import il.co.diamed.com.form.devices.DoconActivity;
import il.co.diamed.com.form.devices.FridgeActivity;
import il.co.diamed.com.form.devices.GelstationActivity;
import il.co.diamed.com.form.devices.GeneralUseActivity;
import il.co.diamed.com.form.devices.HC10Activity;
import il.co.diamed.com.form.devices.IH1000Activity;
import il.co.diamed.com.form.devices.IH500Activity;
import il.co.diamed.com.form.devices.IncubatorActivity;
import il.co.diamed.com.form.devices.PlasmaThawerActivity;
import il.co.diamed.com.form.devices.SepaxActivity;
import il.co.diamed.com.form.devices.res.SimpleFragmentPagerAdapter;
import il.co.diamed.com.form.res.MultiLayoutActivity;

import static android.app.Activity.RESULT_OK;


public class DevicesFragment extends Fragment {
    private static final String TAG = "DeviceFragment";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL = 0;
    private Bundle calibrationDevices;
    private ViewPager viewPager;
    private SimpleFragmentPagerAdapter adapter;
    private OnFragmentInteractionListener mListener;

    public DevicesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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


            calibrationDevices = new Bundle();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.e(TAG,"Detached");
        super.onDetach();
        viewPager = null;
        adapter = null;
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
