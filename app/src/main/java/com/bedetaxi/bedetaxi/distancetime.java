package com.bedetaxi.bedetaxi;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.lang.reflect.Field;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link distancetime.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link distancetime#newInstance} factory method to
 * create an instance of this fragment.
 */
public class distancetime extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    View view;
    public TextView Time;
    public TextView Distance;
    Firebase ArrivalTime;
    Firebase ArrivalDistance;
    ValueEventListener durationListener;
    ValueEventListener timeListener;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public distancetime() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment distancetime.
     */
    // TODO: Rename and change types and number of parameters
    public static distancetime newInstance(String param1, String param2) {
        distancetime fragment = new distancetime();
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
        view = inflater.inflate(R.layout.fragment_distancetime, container, false);
        Time = (TextView) view.findViewById(R.id.timeArrival);
        Distance =(TextView) view.findViewById(R.id.distance);
        Bundle b = getArguments();
        if (b != null){
            Time.setText(b.getString("Duration"));
            Distance.setText(b.getString("Distance"));
        }

        ArrivalTime =  new Firebase("https://taxihere.firebaseio.com/Drivers/" + MainActivity.sharedPreferencesManager.getOrderDriverID() + "/Tracking/ArrivalTime");
        ArrivalDistance = new Firebase("https://taxihere.firebaseio.com/Drivers/" + MainActivity.sharedPreferencesManager.getOrderDriverID() + "/Tracking/ArrivalDistance");
        durationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null)
                    if(Distance != null) {
                            Distance.setText(dataSnapshot.getValue().toString().concat(" ").concat(getString(R.string.distance)));
                    }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        ArrivalDistance.addValueEventListener(durationListener);
        timeListener =  new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null)
                    if(Time!=null)
                Time.setText(dataSnapshot.getValue().toString().concat(" ").concat(getString(R.string.time)));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

        ArrivalTime.addValueEventListener(timeListener);

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
