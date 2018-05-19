package il.co.diamed.com.form;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import il.co.diamed.com.form.res.CaptureSignature;
import il.co.diamed.com.form.res.providers.SettingsActivity;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserSetupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserSetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSetupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String PREFS_NAME = "USER_DATA";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UserSetupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserSetupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserSetupFragment newInstance(String param1, String param2) {
        UserSetupFragment fragment = new UserSetupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_user_setup, container, false);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        //final SharedPreferences sharedPref = getActivity().getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        //final SharedPreferences sharedPref = getActivity().getApplication().getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        final String techname = sharedPref.getString("techName", "");
        final String signature = sharedPref.getString("signature", "");
        final String thermometer = sharedPref.getString("thermometer", "");
        final String barometer = sharedPref.getString("barometer", "");
        final String timer = sharedPref.getString("timer", "");
        final String speedometer = sharedPref.getString("speedometer", "");

        if(!techname.equals("")){
            ((EditText)view.findViewById(R.id.etName)).setText(techname);
        }
        if(!thermometer.equals("")){
            ((EditText)view.findViewById(R.id.etThermometer)).setText(thermometer);
        }
        if(!barometer.equals("")){
            ((EditText)view.findViewById(R.id.etBarometer)).setText(barometer);
        }
        if(!timer.equals("")){
            ((EditText)view.findViewById(R.id.etTimer)).setText(timer);
        }
        if(!speedometer.equals("")){
            ((EditText)view.findViewById(R.id.etSpeedometer)).setText(speedometer);
        }
        if(!signature.equals("")){
            view.findViewById(R.id.signButton).setVisibility(View.INVISIBLE);
        }
        view.findViewById(R.id.signButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),CaptureSignature.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((EditText)view.findViewById(R.id.etName)).getText().toString().equals("")&&
                   !((EditText)view.findViewById(R.id.etThermometer)).getText().toString().equals("")&&
                   !((EditText)view.findViewById(R.id.etBarometer)).getText().toString().equals("")&&
                   !((EditText)view.findViewById(R.id.etTimer)).getText().toString().equals("")&&
                   !((EditText)view.findViewById(R.id.etSpeedometer)).getText().toString().equals("")&&
                   !(sharedPref.getString("signature", "").equals(""))){
                    //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

                    //SharedPreferences pref = getActivity().getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
                    SharedPreferences.Editor spedit = sharedPref.edit();

                    //SharedPreferences.Editor spedit = sharedPref.edit();
                    spedit.putString("techName", ((EditText)view.findViewById(R.id.etName)).getText().toString());
                    spedit.putString("speedometer", ((EditText)view.findViewById(R.id.etSpeedometer)).getText().toString());
                    spedit.putString("thermometer", ((EditText)view.findViewById(R.id.etThermometer)).getText().toString());
                    spedit.putString("barometer", ((EditText)view.findViewById(R.id.etBarometer)).getText().toString());
                    spedit.putString("timer", ((EditText)view.findViewById(R.id.etTimer)).getText().toString());
                    spedit.apply();

                    LoginActivity activity = (LoginActivity)getActivity();
                    activity.moveToMainMenu();
                }else {

                }
            }
        });





        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        super.onDetach();
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
