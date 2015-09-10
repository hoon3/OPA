package ncrc.nise.ajou.ac.kr.opa.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ncrc.nise.ajou.ac.kr.opa.R;
import ncrc.nise.ajou.ac.kr.opa.activity.MainActivity;
import ncrc.nise.ajou.ac.kr.opa.data.ManboValues;

/**
 * A placeholder fragment containing a simple view.
 */
public class ManboFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    // CountText
    TextView CountText;

    // service data
    String serviceData;

    // 리시버
    BroadcastReceiver receiver;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ManboFragment newInstance(int sectionNumber) {
        ManboFragment fragment = new ManboFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ManboFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manbo, container, false);

        CountText = (TextView) rootView.findViewById(R.id.stepCount);
        CountText.setText(ManboValues.Step+"");

        receiver = new MyMainLocalReceiver();

        try {
            IntentFilter mainFilter = new IntentFilter("ncrc.nise.ajou.ac.kr.opa.step");
            getActivity().registerReceiver(receiver, mainFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
    class MyMainLocalReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.i("lee", "123");
            serviceData = intent.getStringExtra("serviceData");
            Log.i("lee", serviceData);
            CountText.setText(serviceData);
        }
    }

}