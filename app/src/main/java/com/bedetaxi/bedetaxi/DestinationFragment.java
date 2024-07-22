package com.bedetaxi.bedetaxi;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DestinationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DestinationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinationFragment extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ListView list;
    public static String To;
    View view;
    public static Context context;
    String URL ;
    SearchView search;

    destAdapter adapter;
    List<destinationList> My_List = new ArrayList<>() ;
    private OnFragmentInteractionListener mListener;
    Set<String> set = new HashSet<String>();

    public DestinationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DestinationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DestinationFragment newInstance(String param1, String param2) {
        DestinationFragment fragment = new DestinationFragment();
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
       view = inflater.inflate(R.layout.fragment_destination, container, false);
        context = getActivity();
        search = (SearchView) view .findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setIconified(false);
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().length() >=3) {
                    try {
                        My_List.clear();
                        set.clear();
                        String newString = URLEncoder.encode(newText, "utf-8");
                        URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+newString+"&key=AIzaSyDbQSxXaU3wPX1J-6HZMl4JJn36A_TZvws&location";
                        new RetrieveFeedTask().execute();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
                return false;

            }
        });


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
//            progressDialog = new ProgressDialog(context);
//            progressDialog.setTitle("Loading...");
//            progressDialog.setMessage("Please Wait ... ");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {

            if (!stringData.trim().isEmpty()) {
                JSONObject data = null;
                try {
                    data = new JSONObject(stringData);
                    String status = data.getString("status");
                    if (status.equals("OK")) {
                        JSONArray array = data.getJSONArray("predictions");

                        for (int i = 0; i < array.length(); i++) {
                            String name = array.getJSONObject(i).getJSONObject("structured_formatting").getString("main_text");
                            if (set.contains(name.trim())){
                                continue;
                            }
                            set.add(name.trim());
                            My_List.add(new destinationList(name, 0));
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
//            progressDialog.dismiss();
            list=(ListView)view.findViewById(R.id.textView9);
            adapter = new destAdapter(context,My_List);

            notifyAdapter();
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    To = My_List.get(position).getName();
                    MainActivity.to = To;
                    ((MainActivity)getActivity()).FullMainList();
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    getActivity().getFragmentManager().beginTransaction().remove(DestinationFragment.this).commit();
                    ((MainActivity)getActivity()).showButtonAndList();

                }
            });

        }
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

}
