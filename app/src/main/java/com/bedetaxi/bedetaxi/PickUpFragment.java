package com.bedetaxi.bedetaxi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PickUpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PickUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickUpFragment extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView list;
    public static String From = "";
    List<pickUp> My_List = new ArrayList<>() ;
    public static Context context;
    ListAdapter adapter;
    String URL ;
    View view;
    private OnFragmentInteractionListener mListener;

    public PickUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PickUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PickUpFragment newInstance(String param1, String param2) {
        PickUpFragment fragment = new PickUpFragment();
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

        view = inflater.inflate(R.layout.fragment_pick_up, container, false);
        context = getActivity();
        Bundle b = getArguments();
        URL = "https://maps.googleapis.com/maps/api/place/search/json?key=AIzaSyDbQSxXaU3wPX1J-6HZMl4JJn36A_TZvws&location="+b.getDouble("lat")+","+b.getDouble("lng")+"&radius=1000&sensor=false";
        new RetrieveFeedTask().execute();


        // Inflate the layout for this fragment
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
    }
    private void notifyAdapter()  {
        getActivity().runOnUiThread(new Runnable()  {
            public void run() {
                list.setAdapter(adapter);
                if(adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
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



    class RetrieveFeedTask extends AsyncTask<Void,Void,String> {
        ProgressDialog progressDialog;
        String stringData;


        @Override
        protected String doInBackground(Void... params) {
            HttpRequest httpRequest = new HttpRequest();
            stringData = httpRequest.sendGetRequest(URL);


            return stringData;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Please Wait ... ");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {

            if (!stringData.trim().isEmpty()) {
                JSONObject data = null;
                try {
                    data = new JSONObject(stringData);
                    String status = data.getString("status");
                    if (status.equals("OK")) {


                        JSONArray array = data.getJSONArray("results");

                        for (int i = 0; i < array.length(); i++) {
                            String name = array.getJSONObject(i).getString("name");
                            My_List.add(new pickUp(name, 0));
                        }
                    } else {
                        Toast.makeText(context, "We were unable to find any nearby places", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(context,"Network connection Error",Toast.LENGTH_LONG).show();
            }
            progressDialog.dismiss();
            list=(ListView)view.findViewById(R.id.list1);
            adapter = new ListAdapter(context,My_List);

            notifyAdapter();
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    From = My_List.get(position).getName();
                    Bundle bundle = new Bundle();
                    MainActivity.from = From;
                    ((MainActivity)getActivity()).FullMainList();
                    getActivity().getFragmentManager().beginTransaction().remove(PickUpFragment.this).commit();
                    ((MainActivity)getActivity()).showButtonAndList();

                }
            });

        }
    }
}
