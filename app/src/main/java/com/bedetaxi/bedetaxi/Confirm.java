package com.bedetaxi.bedetaxi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bedetaxi.bedetaxi.Routing.SignalRHubConnection;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler3;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Confirm.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Confirm#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Confirm extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static int seatesType = 0;
    String[] arraySpinner;
    static SignalRHubConnection mSignalRHubConnection;
    String orderPosition= "";
    HashMap<String,String> map = new HashMap<String,String>();
    View view;
    Button x;
    //    ImageView Xlarge;
//    ImageView Large;
//    ImageView Medium;
    Spinner officeSpinner;
    TextView From;
    TextView To;
    EditText notesUser;
    Button cancelConfirm;
    double userLat;
    double userLng;
    AsyncTask task;
    Dialog dialog;
    JSONObject details;
    private OnFragmentInteractionListener mListener;
    SubscriptionHandler2<Integer,Object> handler;
    Dialog progressDialog;
    String value;
    boolean isBindResponseSet = false;
    public Confirm() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Confirm.
     */
    // TODO: Rename and change types and number of parameters
    public static Confirm newInstance(String param1, String param2) {
        Confirm fragment = new Confirm();
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

        view = inflater.inflate(R.layout.fragment_confirm, container, false);
        new getOffices().execute();
//        Xlarge = (ImageView)view.findViewById(R.id.XLarge);
//        Large = (ImageView)view.findViewById(R.id.Large);
//        Medium = (ImageView)view.findViewById(R.id.Medium);
//        Xlarge.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                seatesType = 2;
//                Xlarge.setBackgroundColor(R.color.lightGrey);
//                Large.setBackgroundColor(Color.TRANSPARENT);
//                Medium.setBackgroundColor(Color.TRANSPARENT);
//            }
//        });
//        Large.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                seatesType = 1;
//                Large.setBackgroundColor(R.color.lightGrey);
//                Xlarge.setBackgroundColor(Color.TRANSPARENT);
//                Medium.setBackgroundColor(Color.TRANSPARENT);
//            }
//        });
//        Medium.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                seatesType = 0;
//                Medium.setBackgroundColor(R.color.lightGrey);
//                Xlarge.setBackgroundColor(Color.TRANSPARENT);
//                Large.setBackgroundColor(Color.TRANSPARENT);
//            }
//        });
        dialog = new Dialog(MainActivity.context, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.animatedialog);
        dialog.setCancelable(false);


        Button cancel = (Button) dialog.findViewById(R.id.cancelInRequest);
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignalRHubConnection.mHubProxy.invoke("cancelUserRequest",mSignalRHubConnection.getConnectionID(),((MainActivity) getActivity()).sharedPreferencesManager.getUserID());
             dialog.dismiss();

            }
        });
        final Handler h = new Handler();
        handler = new SubscriptionHandler2<Integer, Object>() {
            @Override
            public void run(final Integer status, final Object data) {
                h.post(new Runnable(){
                    public void run() {

                        if (status == 1) {
                            Gson gson = new Gson();
                            try {
                                value = gson.toJson(data);
                                details = new JSONObject(value);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            MainActivity.sharedPreferencesManager.setOrderDetiles(value);
                            MainActivity.sharedPreferencesManager.setOrderPosition(orderPosition);
                            MainActivity.HaveRequest = true;
                            MainActivity.from = "";
                            MainActivity.userLngFromPicker = 0.0;
                            MainActivity.userLatFromPicker = 0.0;
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            // get your custom_toast.xml ayout
                            LayoutInflater inflater = getActivity().getLayoutInflater();

                            View layout = inflater.inflate(R.layout.toast_layout,
                                    (ViewGroup) getActivity().findViewById(R.id.custom_toast_layout_id));

                            // set a dummy image
                            ImageView image = (ImageView) layout.findViewById(R.id.image);
                            image.setImageResource(R.drawable.smile);

                            // set a message
                            TextView text = (TextView) layout.findViewById(R.id.text);
                            text.setText("التكسي في الطريق اليك! ");

                            // Toast...
                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();


                            try {
                                hashMap.put("driverID", details.getString("DriverID"));
                                hashMap.put("DriverName", details.getString("DriverName"));
                                hashMap.put("DriverPhone", details.getString("DriverPhone"));
                                hashMap.put("VehicleType", details.getString("VehicleType"));
                                hashMap.put("Distance", details.getString("Distance"));
                                hashMap.put("Duration", details.getString("Duration"));
                                hashMap.put("taxiLat", details.getString("lat"));
                                hashMap.put("taxiLng", details.getString("lng"));
                                hashMap.put("Rating", details.getString("Rating"));
                                if (details.has("DriverImage")){
                                    hashMap.put("DriverImage", details.getString("DriverImage"));

                                }else{
                                    hashMap.put("DriverImage",null);

                                }
                                dialog.dismiss();
                                ((MainActivity) getActivity()).initRequestData(hashMap);
//                                if(proccessRequest != null){
//                                    getActivity().getFragmentManager().beginTransaction().remove(proccessRequest).commitAllowingStateLoss();
//                                }
                                getActivity().getFragmentManager().beginTransaction().remove(Confirm.this).commitAllowingStateLoss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else if (status == 2) {
                            dialog.dismiss();

                            showAlertDialog(getString(R.string.sorry), getString(R.string.sorrymessage));
                        } else if (status == 3) {
                            dialog.dismiss();

                            showAlertDialog(getString(R.string.sorry), getString(R.string.blocked));
                        } else if (status == 4) {
                            dialog.dismiss();

                            showAlertDialog(getString(R.string.Cancel), getString(R.string.cancelDone));
                        }
                        if (dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                });

            }
        };
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        officeSpinner = (Spinner)view.findViewById(R.id.officeSpinner);
        From = (TextView) view.findViewById(R.id.From);
        To = (TextView) view.findViewById(R.id.To);
        notesUser = (EditText)view.findViewById(R.id.notes);
        cancelConfirm = (Button)view.findViewById(R.id.cancelConfirm);
        Bundle b = getArguments();
        userLat = b.getDouble("userLat");
        userLng = b.getDouble("userLng");
        if (b.getString("From") == null){
            From.setText(getString(R.string.unknwon));
        }else {
            From.setText(b.getString("From"));
        }

        if (b.getString("To") == null){
            To.setText(getString(R.string.unknwon));
        }else {
            To.setText(b.getString("To"));
        }


        x= (Button) view.findViewById(R.id.ConfirmOffice);
        x.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<String, String> detiles = getTaxiButtonClicked();
            }
        });


        cancelConfirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        return view;
    }

    public HashMap<String,String> getTaxiButtonClicked (){

        if (!((MainActivity)getActivity()).checkNetworkConnection()){
            Toast.makeText(getActivity(), getString(R.string.internetproblem), Toast.LENGTH_LONG).show();
            return null;
        }

//        if (!((MainActivity)getActivity()).checkGPSEnabled()){
//            ((MainActivity)getActivity()).GPSAlert();
//            return null;
//        }
        //((MainActivity)getActivity()).latLng = new GeoPoint(31.950678, 35.184773);


        if (((MainActivity)getActivity()).latLng == null){
            Toast.makeText(getActivity(),"Unable to get location",Toast.LENGTH_LONG).show();
            return null;
        }
        HashMap<String,String> hashMap = null;
//        JSONArray data = new sendTaxiRequest().execute().get();

        try{
//            dialog.show();
//           sendRequest();
            new sendRequest().execute();
        }catch (Exception e){
//            dialog.dismiss();
            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();


        }


        return null;
    }

    private void sendRequest(){

        if (mSignalRHubConnection.getConnectionID() == null){
            Toast.makeText(MainActivity.context,getString(R.string.internetproblem),Toast.LENGTH_LONG).show();
            return;
        }
        String officeID;
        String lat;
        String lng;
        String fromText;
        if(From.getText().toString().equalsIgnoreCase(getString(R.string.currentposition)) || From.getText().toString().equalsIgnoreCase(getString(R.string.maplocation))){
            fromText = "";// Generally array index starts from 0 not 1
        }else {
            fromText = From.getText().toString();// Generally array index starts from 0 not 1
        }
        if(userLng != 0.0){
            lng = String.valueOf(userLng);
        }else {
            lng = String.valueOf(((MainActivity) getActivity()).latLng.getLongitude());// Generally array index starts from 0 not 1
        }
        if(userLat != 0.0){
            lat = String.valueOf(userLat);
        }else {
            lat = String.valueOf(((MainActivity) getActivity()).latLng.getLatitude());// Generally array index starts from 0 not 1

        }
        if(officeSpinner != null) {
            Object selected = officeSpinner.getSelectedItem();
            if (map.get(selected) == null) {
                officeID = "";// Generally array index starts from 0 not 1
            } else {
                officeID = map.get(officeSpinner.getSelectedItem().toString());// Generally array index starts from 0 not 1
            }
        }else
        {
            officeID = "";
        }
        SignalRHubConnection.mHubProxy.invoke("Request",mSignalRHubConnection.getConnectionID(),((MainActivity) getActivity()).sharedPreferencesManager.getUserID()
                ,officeID,"",lat,lng,fromText,"","",To.getText().toString(),String.valueOf(seatesType),notesUser.getText().toString());


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

    class getOffices extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;
        String output;


        @Override
        protected String doInBackground(Void... params) {
            mSignalRHubConnection = new SignalRHubConnection();
            SignalRHubConnection.startSignalR();
            List<PropertyInfo> props = new ArrayList<PropertyInfo>();
            WebAPI webAPI = new WebAPI(MainActivity.context,"getOffices",props);
            output = webAPI.call_request();
            return output;
        }
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.context);
            progressDialog.setTitle(getString(R.string.loading));
            progressDialog.setMessage(getString(R.string.Wait));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (output == null || output.trim().isEmpty()){

            }else {

                JSONArray data = null;
                try {
                    data = new JSONArray(output);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String status = null;
                try {
                    status = data.getJSONObject(0).getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (status.equalsIgnoreCase("success")) {
                    try {
                        JSONArray jsonArray = data.getJSONObject(0).getJSONArray("details");
                        arraySpinner = new String[jsonArray.length()+1];
                        arraySpinner[0] = getString(R.string.all_offices);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            String id = object.getString("OfficeID");
                            String name = object.getString("OfficName");
                            map.put(name,id);
                            arraySpinner[i+1] = name;


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    OfficeListAdapter adapter = new OfficeListAdapter (MainActivity.context, arraySpinner);
                    adapter.notifyDataSetChanged();
                    officeSpinner.setAdapter(adapter);


                }else if(status.equalsIgnoreCase("failed"))
                {
                    Toast.makeText(MainActivity.context, "failed", Toast.LENGTH_LONG).show();

                }
            }


        }
    }

    public List<PropertyInfo> getPropertyCancel(){
        List<PropertyInfo> propertyInfos = new ArrayList<PropertyInfo>();

        PropertyInfo name = new PropertyInfo();
        name.setName("UserID");
        name.setValue(MainActivity.sharedPreferencesManager.getUserID());// Generally array index starts from 0 not 1
        name.setType(String.class);
        propertyInfos.add(name);

        return propertyInfos;
    }


    public List<PropertyInfo> getPropertyInfo(){
        List<PropertyInfo> propertyInfos = new ArrayList<PropertyInfo>();


        PropertyInfo ConntectionID = new PropertyInfo();
        ConntectionID.setName("connectionID");
        ConntectionID.setValue(mSignalRHubConnection.getConnectionID());// Generally array index starts from 0 not 1
        ConntectionID.setType(String.class);
        propertyInfos.add(ConntectionID);


        PropertyInfo name = new PropertyInfo();
        name.setName("UserID");
        name.setValue(((MainActivity) getActivity()).sharedPreferencesManager.getUserID());// Generally array index starts from 0 not 1
        name.setType(String.class);
        propertyInfos.add(name);

        PropertyInfo officeID = new PropertyInfo();
        officeID.setName("OfficeID");
        if(officeSpinner != null) {
            Object selected = officeSpinner.getSelectedItem();
            if (map.get(selected) == null) {
                officeID.setValue("");// Generally array index starts from 0 not 1
            } else {
                officeID.setValue(map.get(officeSpinner.getSelectedItem().toString()));// Generally array index starts from 0 not 1
            }
        }else
        {
            officeID.setValue("");// Generally array index starts from 0 not 1
        }
        officeID.setType(String.class);
        propertyInfos.add(officeID);

        PropertyInfo DriverID = new PropertyInfo();
        DriverID.setName("DriverID");
        DriverID.setValue("");// Generally array index starts from 0 not 1
        DriverID.setType(String.class);
        propertyInfos.add(DriverID);

        orderPosition = "";
        PropertyInfo lat = new PropertyInfo();
        lat.setName("FromLat");
        if(userLat != 0.0){
            lat.setValue(String.valueOf(userLat));
            orderPosition = String.valueOf(userLat);
        }else {
            lat.setValue(String.valueOf(((MainActivity) getActivity()).latLng.getLatitude()));// Generally array index starts from 0 not 1}
            orderPosition = String.valueOf(String.valueOf(((MainActivity) getActivity()).latLng.getLatitude()));

        }
        lat.setType(String.class);
        propertyInfos.add(lat);

        PropertyInfo lng = new PropertyInfo();
        lng.setName("FromLng");
        if(userLng != 0.0){
            lng.setValue(String.valueOf(userLng));
            orderPosition = orderPosition +","+String.valueOf(userLng);
        }else {
            lng.setValue(String.valueOf(((MainActivity) getActivity()).latLng.getLongitude()));// Generally array index starts from 0 not 1
            orderPosition = orderPosition +","+String.valueOf(((MainActivity) getActivity()).latLng.getLongitude());
        }
        lng.setType(String.class);
        propertyInfos.add(lng);

        PropertyInfo FromText = new PropertyInfo();
        FromText.setName("FromText");
        if(From.getText().toString().equalsIgnoreCase(getString(R.string.currentposition)) || From.getText().toString().equalsIgnoreCase(getString(R.string.maplocation))){
            FromText.setValue("");// Generally array index starts from 0 not 1
        }else {
            FromText.setValue(From.getText().toString());// Generally array index starts from 0 not 1
        }
        FromText.setType(String.class);
        propertyInfos.add(FromText);

        PropertyInfo ToLat = new PropertyInfo();
        ToLat.setName("ToLat");
        ToLat.setValue("");
        ToLat.setType(String.class);
        propertyInfos.add(ToLat);

        PropertyInfo ToLng = new PropertyInfo();
        ToLng.setName("ToLng");
        ToLng.setValue("");
        ToLng.setType(String.class);
        propertyInfos.add(ToLng);

        PropertyInfo ToText = new PropertyInfo();
        ToText.setName("ToText");
        ToText.setValue(To.getText().toString());
        ToText.setType(String.class);
        propertyInfos.add(ToText);

        PropertyInfo Seats = new PropertyInfo();
        Seats.setName("Seats");
        Seats.setValue(String.valueOf(seatesType));
        Seats.setType(String.class);
        propertyInfos.add(Seats);

        PropertyInfo notes = new PropertyInfo();
        notes.setName("notes");
        notes.setValue(notesUser.getText().toString());
        notes.setType(String.class);
        propertyInfos.add(notes);

        PropertyInfo type = new PropertyInfo();
        type.setName("type");
        type.setValue(1);
        type.setType(Integer.class);
        propertyInfos.add(type);

        return propertyInfos;
    }

    class sendRequest extends AsyncTask<Void, Void, String> {

        //        FragmentManager fragmentManager = getFragmentManager();
//        CancelWithinOrder cancelWithinOrder = new CancelWithinOrder();
//        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        WebAPI webAPI;

        Button cancel;
        String output;

        @Override
        protected String doInBackground(Void... params) {
            if (!isBindResponseSet) {
                SignalRHubConnection.mHubProxy.on("BindResponse", handler, Integer.class, Object.class);
                isBindResponseSet = true;
            }
            List<PropertyInfo> props = getPropertyInfo();
            webAPI = new WebAPI(MainActivity.context,"RequestUser",props);
            output = webAPI.call_request();
            return output;
        }
        @Override
        protected void onPreExecute() {
            dialog.show();

//            FragmentManager fragmentManager = getFragmentManager();
//            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
//                    R.animator.fragment_slide_left_exit,
//                    R.animator.fragment_slide_right_enter,
//                    R.animator.fragment_slide_right_exit);
//            proccessRequest = new proccessRequest();
//            fragmentTransaction.replace(R.id.ConfirmFrame, proccessRequest);
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.commitAllowingStateLoss();



        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }


    }


    public void showAlertDialog (String title,String message){

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.Ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
        alertDialog.show();
    }









    class cancelOrder extends AsyncTask<Void, Void, String> {
        String output;
        @Override
        protected String doInBackground(Void... params) {
            List<PropertyInfo> props = getPropertyCancel();
            WebAPI webAPI = new WebAPI(MainActivity.context,"cancelUserRequest",props);
            output = webAPI.call_request();
            return output;
        }
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


        }


    }
}






