package ncrc.nise.ajou.ac.kr.opa.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ncrc.nise.ajou.ac.kr.opa.R;
import ncrc.nise.ajou.ac.kr.opa.activity.MainActivity;
import ncrc.nise.ajou.ac.kr.opa.activity.UserSettingsActivity;
import ncrc.nise.ajou.ac.kr.opa.data.DBAdapter;
import ncrc.nise.ajou.ac.kr.opa.data.User;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyAvatarFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    // db
    private DBAdapter dbAdapter;
    private Cursor c;

    // elements of layout
    TextView textViewBMI;
    TextView textViewObesityStatus;
    TextView textViewDailyBMR;
    ImageView bmiSet;
    // User
    User user;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MyAvatarFragment newInstance(int sectionNumber) {
        MyAvatarFragment fragment = new MyAvatarFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MyAvatarFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_avatar, container, false);

        textViewBMI = (TextView) rootView.findViewById(R.id.textViewBMI);
        textViewObesityStatus = (TextView) rootView.findViewById(R.id.textViewObesityStatus);
        textViewDailyBMR = (TextView) rootView.findViewById(R.id.textViewDailyBMR);
        bmiSet =  (ImageView) rootView.findViewById(R.id.bmiImage);
        /* DB 처리 */
        //DB Adapter
        dbAdapter = new DBAdapter(getActivity().getApplicationContext());
        c = dbAdapter.read_user_info();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        c = dbAdapter.read_user_info();
        if(c != null) {
            if(c.getCount() != 0) {
                c.moveToFirst();
                Log.i("USER", c.getString(0));

                // set user
                user = new User(c.getDouble(1), c.getDouble(2), c.getInt(3), c.getInt(4));

                // set user info to textview
                textViewBMI.setText(Double.toString(Math.round(user.getBMI() * 100d / 100d)));
                textViewObesityStatus.setText(user.getOverweightStatus());
                textViewDailyBMR.setText(Double.toString(Math.round(user.getDailyBMR() * 100d / 100d)) + " kcal");
                double bmiValue = Math.round(user.getBMI() * 100d / 100d);
                if(bmiValue>35){bmiSet.setImageResource(R.drawable.sixth);}
                else if(bmiValue>30){bmiSet.setImageResource(R.drawable.fifth);}
                else if(bmiValue>25){bmiSet.setImageResource(R.drawable.fourth);}
                else if(bmiValue>23){bmiSet.setImageResource(R.drawable.third);}
                else if(bmiValue>18.5){bmiSet.setImageResource(R.drawable.second);}
                else{bmiSet.setImageResource(R.drawable.first);}


            } else { // if no user info yet
                // Move to User Setting Activity
                startActivity(new Intent(getActivity(), UserSettingsActivity.class));
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}