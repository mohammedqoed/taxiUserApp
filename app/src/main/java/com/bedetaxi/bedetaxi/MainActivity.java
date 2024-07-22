package com.bedetaxi.bedetaxi;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import org.json.JSONArray;
 import org.json.JSONException;
 import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.ksoap2.serialization.PropertyInfo;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


@SuppressWarnings("WrongConstant")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private MapView mapView;
    MapController mMapController;
    static Context mcontext;
    public static boolean IsInRequest =false;
    public static boolean HaveRequest = false;
    static boolean firstAnimate = true;
    static Context context;
    private boolean isInFront;
    GoogleApiClient mGoogleApiClient;
    GeoPoint latLng;

    LocationRequest mLocationRequest;
    Marker mCurrLocation;
    static SharedPreferencesManager sharedPreferencesManager;
    private static final String FIREBASE_URL = "https://taxihere.firebaseio.com";
    Marker marker;
    Marker markerDriver;
    pickAdapter adapter;
   // CallbackManager callbackManager;
    private static final String TAG = "MainActivity";
    static List<pickUp> My_List = new ArrayList<>();
    static ListView list;
    static  Button bidditaxi;
    static  Button cancel;
    static  Button call;
    static LinearLayout mainlinear;
    static String from;
    static String to;
    static boolean isfrom ;
    Firebase tracking;
    Firebase cancelFirebase;
    Firebase doneRequest;
    static Firebase notification;
    final int PLACE_PICKER_REQUEST = 1;
    Fragment MyFragment = new Fragment();
    Menu My_Menu;
    public static boolean isDestination=false;
    List<TaxisTrackingData> taxis ;
    GifImageView gifImageView;

    //ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
//    public static Road[] road;
//    protected Polyline[] mRoadOverlays;
    protected int mSelectedRoad;
    double taxiLat=0.0;
    double taxiLng=0.0;
    ImageView MyLocation;
    static ImageView UserImage;
    final String IMAGE_URL = "http://bedetaxi.cloudapp.net/Content/Images/";
    static boolean mapTouched = false;
    private static final int PERMISSION_REQUEST_CODE = 200;
    static double userLatFromPicker = 0.0;
    static double userLngFromPicker = 0.0;
    distancetime timeDistance;
    ValueEventListener doneRequestListener;
    ValueEventListener trackingListenr;
    ValueEventListener cancelFirebaseListener;
    ValueEventListener notifcationListernt;
    TextView navUserName;

    Marker taxi1Marker;
    Marker taxi2Marker;
    DelayedMapListener mapListener;
    LinearLayout markerlayout;
    TextView durationNumber;
    static String durationTextNumber ="";
    static  boolean notFirstLoad = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        gifImageView = (GifImageView) findViewById(R.id.loader);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        context = MainActivity.this;
        sharedPreferencesManager = new SharedPreferencesManager(context);

        //setContentView(R.layout.content_main);
// handle the map

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(!checkPermission()){
                requestPermission();
            }
        }
        PutMap();




       // set Action Bar
        setActionBar();
// fill the main list
        FullMainList();



        markerlayout = (LinearLayout) findViewById(R.id.markerLayout);

        mapView.setOnTouchListener(new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(IsInRequest){
            mapTouched = true;
        }
        return false;
    }
});
        MyLocation = (ImageView) findViewById(R.id.MyLocation);
        MyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoPoint startPoint = null;
                try{
                    Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLastLocation != null) {
                        startPoint = new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//                        addMarker(startPoint,getString(R.string.currentposition));
                        mMapController.animateTo(startPoint);
                        latLng.setLatitude(startPoint.getLatitude());
                        latLng.setLongitude(startPoint.getLongitude());
                        new LoadTaxis().execute();
                    }else{
                        Toast.makeText(context,getString(R.string.unableLocation),Toast.LENGTH_LONG).show();
                    }

                }catch (Exception error){
                    Toast.makeText(MainActivity.context,"Get Start Location Error",Toast.LENGTH_LONG).show();
                }

            }

        });

        if ((!checkGPSEnabled())){
            GPSAlert();
        }
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            pickUp selected = My_List.get(position);
                String selectedName = selected.getBName();
                if (selectedName.equalsIgnoreCase(My_List.get(0).getBName())) {
                    if (latLng !=null) {
                     PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                        Intent intent;
                        try {
                            intent = builder.build(MainActivity.this);
                            startActivityForResult(intent,PLACE_PICKER_REQUEST);

                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                        }
//                        hideButtonAndList();

                    }else if (!checkNetworkConnection()){
                            Toast.makeText(getApplicationContext(), getString(R.string.internetproblem), Toast.LENGTH_LONG).show();
                        }else if (!checkGPSEnabled()){
                            GPSAlert();
                    }else{
                        Toast.makeText(getApplicationContext(), "Unable to get location", Toast.LENGTH_LONG).show();
                    }


                } else if (selectedName.equalsIgnoreCase(My_List.get(1).getBName())) {

                    if (latLng != null) {

                        FragmentManager fragmentManager = getFragmentManager();
                        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
                                R.animator.fragment_slide_left_exit,
                                R.animator.fragment_slide_right_enter,
                                R.animator.fragment_slide_right_exit);
                        DestinationFragment fragment = new DestinationFragment();
                        Bundle b = new Bundle();
                        b.putDouble("lat", latLng.getLatitude());
                        b.putDouble("lng", latLng.getLongitude());
                        fragment.setArguments(b);
//                        startActivity(i);
                        fragmentTransaction.add(R.id.ConfirmFrame, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commitAllowingStateLoss();
                        hideButtonAndList();


                        My_List.clear();
                    } else if (!checkNetworkConnection()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.internetproblem), Toast.LENGTH_LONG).show();
                    } else if (!checkGPSEnabled()) {
                        GPSAlert();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to get location", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        bidditaxi = (Button) findViewById(R.id.bidditaxi);
        bidditaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             // dialog after press biddi taxi



                FragmentManager fragmentManager = getFragmentManager();
                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
                        R.animator.fragment_slide_left_exit,
                        R.animator.fragment_slide_right_enter,
                        R.animator.fragment_slide_right_exit);
                Confirm fragment = new Confirm();
                Bundle bundle = new Bundle();
                if(userLatFromPicker==0.0 && userLngFromPicker == 0.0){
                    from = getString(R.string.currentposition);
                }
                bundle.putString("From",from);
                bundle.putString("To",to);
                bundle.putDouble("userLat",userLatFromPicker);
                bundle.putDouble("userLng",userLngFromPicker);
                fragment.setArguments(bundle);
                fragmentTransaction.add(R.id.ConfirmFrame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commitAllowingStateLoss();
                hideButtonAndList();

               // dialog();

            }
        });

        if (sharedPreferencesManager.getOrderDriverID() != null && !sharedPreferencesManager.getOrderDriverID().trim().isEmpty()){
            if(checkNetworkConnection()) {
                new OrderStatus().execute();
            }else{
                mapView.setMapListener(mapListener);
            }
        }else{
            mapView.setMapListener(mapListener);
        }

    }
    public List<PropertyInfo> getPropertyInfo(String driverID){
                 List<PropertyInfo> propertyInfos = new ArrayList<PropertyInfo>();

                 PropertyInfo userID = new PropertyInfo();
                userID.setName("UserID");
                userID.setValue(sharedPreferencesManager.getUserID());// Generally array index starts from 0 not 1
                 userID.setType(String.class);
                 propertyInfos.add(userID);
                 PropertyInfo DriverID = new PropertyInfo();
                 DriverID.setName("DriverID");
                 DriverID.setValue(driverID);// Generally array index starts from 0 not 1
                DriverID.setType(String.class);
                 propertyInfos.add(DriverID);

                 return propertyInfos;
             }


    public void addMarkerDriver (GeoPoint geoPoint,String title, float rotation){

        if(markerDriver!= null){
            mapView.getOverlays().remove(markerDriver);
        }
        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.rsz_taxi, null);
        markerDriver = new Marker(mapView);
        markerDriver.setPosition(geoPoint);
        markerDriver.setIcon(icon);
        markerDriver.setRotation(rotation);
        // mapView.getOverlayManager().clear();
        mapView.getOverlays().add(markerDriver);
        mapView.invalidate();

    }

    public Marker addMarkerDriverInLoading (Marker oldMarker ,GeoPoint geoPoint, float rotation){
        if(oldMarker!= null){
            mapView.getOverlays().remove(oldMarker);
        }
        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.rsz_taxi, null);
        Marker marker = new Marker(mapView);
        marker.setPosition(geoPoint);
        marker.setIcon(icon);
        marker.setRotation(rotation);
        marker.setTitle(getString(R.string.taxilocation));
        // mapView.getOverlayManager().clear();
        mapView.getOverlays().add(marker);
        mapView.invalidate();
        return marker;
    }
    public void addMarker (GeoPoint geoPoint,String title){
        if(marker!= null){
            mapView.getOverlays().remove(marker);
        }
        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.userlocation, null);
        marker = new Marker(mapView);
        marker.setPosition(geoPoint);
        marker.setTitle(title);
        marker.setIcon(icon);
       // mapView.getOverlayManager().clear();
        mapView.getOverlays().add(marker);
        mapView.invalidate();

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode==RESULT_OK){
                Place place = PlacePicker.getPlace(data, this);
                if(place.getName().toString().contains("°")){
                    from = getString(R.string.maplocation);
                }else {
                    from = place.getName().toString();
                }
                LatLng location = place.getLatLng();
                userLatFromPicker = location.latitude;
                userLngFromPicker = location.longitude;
                GeoPoint userLocation = new GeoPoint(userLatFromPicker,userLngFromPicker);
                latLng = userLocation;
                mMapController.animateTo(latLng);
                addMarker(latLng, getString(R.string.currentposition));
                FullMainList();
                showButtonAndList();
               new LoadTaxis().execute();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted) {
                        Toast.makeText(this, "Permission Granted, Now you can access location data.", Toast.LENGTH_LONG).show();
                    }
                    else {

                        Toast.makeText(this, "Permission Denied, You cannot access location data.", Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();


}




    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;


    }

    public boolean checkGPSEnabled (){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);


        try {
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}
        return false;

    }

    public void hideButtonAndList(){
        bidditaxi.setVisibility(View.INVISIBLE);
        list.setVisibility(View.INVISIBLE);
    }
    public void showButtonAndList(){
        bidditaxi.setVisibility(View.VISIBLE);
        list.setVisibility(View.VISIBLE);
    }
    public void GPSAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.GPS_Setting));

        // Setting Dialog Message
        alertDialog
                .setMessage(getString(R.string.location_alert));

        // On pressing Settings button
        alertDialog.setPositiveButton(getString(R.string.setting),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(intent);

                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton(getString(R.string.Cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    public enum TYPE{
        DONE,CANCEL,CANCELFROMUSER
    }

    public void removeListeners(TYPE type){
        if (type == TYPE.CANCEL){
            tracking.removeEventListener(trackingListenr);
            doneRequest.removeEventListener(doneRequestListener);
            notification.removeEventListener(notifcationListernt);
        }else if (type == TYPE.DONE){
            tracking.removeEventListener(trackingListenr);
            cancelFirebase.removeEventListener(cancelFirebaseListener);
            notification.removeEventListener(notifcationListernt);
        }else if (type == TYPE.CANCELFROMUSER){
            tracking.removeEventListener(trackingListenr);
            cancelFirebase.removeEventListener(cancelFirebaseListener);
            notification.removeEventListener(notifcationListernt);
            doneRequest.removeEventListener(doneRequestListener);
        }

    }

    public void initRequestData(final HashMap<String, String> detiles){
        mapView.setMapListener(null);

        if (detiles != null) {
            removeTaxisLocations();
            if(markerlayout != null){
                markerlayout.setVisibility(View.INVISIBLE);
            }
            IsInRequest = true;
            sharedPreferencesManager.setOrderDriverID(detiles.get("driverID"));
            taxiLat = Double.parseDouble(detiles.get("taxiLat"));
            taxiLng = Double.parseDouble(detiles.get("taxiLng"));
            String orderPosition =sharedPreferencesManager.getOrderPosition();
            String [] coords = orderPosition.split(",");
            GeoPoint position = new GeoPoint(Double.valueOf(coords[0]),Double.valueOf(coords[1]));

            addMarker(position, getString(R.string.currentposition));
            addMarkerDriver((new GeoPoint(taxiLat, taxiLng)), getString(R.string.taxilocation),0);

//            MenuItem item = My_Menu.findItem(R.id.action_Done);
//            item.setVisible(true);
            doneRequestListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getValue().toString().equals("1")) {
                        IsInRequest = false;
                        sharedPreferencesManager.setOrderDriverID(null);
                        sharedPreferencesManager.setOrderDetiles(null);
                        doneRequest.removeEventListener(this);
                        mapView.setMapListener(mapListener);
                        try {
                            removeListeners(TYPE.DONE);
                        }catch (Exception e){

                        }
                        Intent i = new Intent(MainActivity.this, Rating.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("DriverID",detiles.get("driverID"));
                        i.putExtras(bundle);
                        markerlayout.setVisibility(View.VISIBLE);
                        startActivity(i);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };

            trackingListenr =  new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String lat = dataSnapshot.child("lat").getValue(String.class);
                    String lng = dataSnapshot.child("lng").getValue(String.class);
                    float rotation = dataSnapshot.child("rotation").getValue(Float.class);

                    if (latLng != null && detiles.get("driverID")!=null&& !detiles.get("driverID").trim().isEmpty() ) {
                        //addMarker(latLng, "Your Location");
                        addMarkerDriver(new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lng)), getString(R.string.taxilocation),rotation);
//                        waypoints.clear();
//                        waypoints.add(new GeoPoint(latLng));
//                        waypoints.add(new GeoPoint(new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lng))));
                        // new UpdateRoadTask().execute(waypoints);

                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };

            cancelFirebaseListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getValue().toString().equals("1")){
                        IsInRequest = false;
                        sharedPreferencesManager.setOrderDriverID(null);
                        sharedPreferencesManager.setOrderDetiles(null);
                        markerlayout.setVisibility(View.VISIBLE);
                        mapView.setMapListener(mapListener);
                        cancelFirebase.removeEventListener(this);
                        try {
                            removeListeners(TYPE.CANCEL);
                        }catch (Exception e){

                        }
                        MainActivity.to="";
                        showAlertDialog(getString(R.string.cancelOrder), getString(R.string.drivercancel));
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };


            notifcationListernt = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.getValue() != null && !dataSnapshot.getValue().toString().isEmpty()) {
                        if ((dataSnapshot.getValue(Integer.class)) == 1) {
                            ShowNot(getString(R.string.notify), getString(R.string.arraival));
                            notification.setValue(0);
                            if(timeDistance != null){
                                if ( timeDistance.ArrivalDistance != null && timeDistance.durationListener != null)
                                timeDistance.ArrivalDistance.removeEventListener(timeDistance.durationListener);
                                if (timeDistance.ArrivalTime != null && timeDistance.timeListener != null)
                                timeDistance.ArrivalTime.removeEventListener(timeDistance.timeListener);
                                try {
                                    getFragmentManager().beginTransaction().remove(timeDistance).commitAllowingStateLoss();
                                }catch (Exception e){

                                }
                                FragmentManager fragmentManager = getFragmentManager();
                                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
                                        R.animator.fragment_slide_left_exit,
                                        R.animator.fragment_slide_right_enter,
                                        R.animator.fragment_slide_right_exit);
                                ArrivalMessage arrivalMessage = new ArrivalMessage();
                                fragmentTransaction.add(R.id.distancetime, arrivalMessage);
                                fragmentTransaction.commitAllowingStateLoss();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };

            tracking = new Firebase("https://taxihere.firebaseio.com/Drivers/" + detiles.get("driverID") + "/Tracking");
            tracking.addValueEventListener(trackingListenr);

            cancelFirebase = new Firebase(FIREBASE_URL+"/Drivers/"+detiles.get("driverID")+"/RequestDetails/Cancel");
            cancelFirebase.addValueEventListener(cancelFirebaseListener);

            notification = new Firebase(FIREBASE_URL+"/Drivers/"+detiles.get("driverID")+"/Notification");
            notification.addValueEventListener(notifcationListernt);

            doneRequest = new Firebase(FIREBASE_URL+"/Drivers/"+detiles.get("driverID")+"/RequestDetails/Done");
            doneRequest.addValueEventListener(doneRequestListener);


            cancel = (Button) findViewById(R.id.cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    // Setting Dialog Message
                    alertDialog.setMessage(getString(R.string.usercancel));
                    // On pressing Settings button
                    alertDialog.setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    IsInRequest = false;
                                    mapTouched=false;
                                    cancelFirebase.setValue(2);
                                    try {
                                        removeListeners(TYPE.CANCELFROMUSER);
                                    }catch (Exception e){

                                    }
                                    sharedPreferencesManager.setOrderDriverID(null);
                                    Intent i = new Intent(MainActivity.this, cancel.class);
                                    markerlayout.setVisibility(View.VISIBLE);
                                    startActivity(i);
                                }
                            });

                    // on pressing cancel button
                    alertDialog.setNegativeButton(getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    // Showing Alert Message
                    alertDialog.show();

                }
            });

            call = (Button) findViewById(R.id.call);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+detiles.get("DriverPhone")));
                    startActivity(intent);
                }
            });
            TextView DriverName = (TextView) findViewById(R.id.DriverNmae);
            if(detiles.get("DriverName").length()>10)
                DriverName.setTextSize(16);
            DriverName.setText(detiles.get("DriverName"));
            TextView CarType = (TextView) findViewById(R.id.CarType);
            CarType.setText(detiles.get("VehicleType"));
            if(detiles.containsKey("DriverImage")) {
                if (detiles.get("DriverImage") != null  && !detiles.get("DriverImage").equalsIgnoreCase("null") && !detiles.get("DriverImage").equalsIgnoreCase("")) {
                    new DownloadImageTask((ImageView) findViewById(R.id.DriverImage))
                            .execute(IMAGE_URL+detiles.get("DriverImage"));
                }
            }
            RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
            String RatingValueString = detiles.get("Rating");
            Double RatingValue = Double.parseDouble(RatingValueString);
            ratingBar.setRating(RatingValue.intValue());
            bidditaxi.setVisibility(View.INVISIBLE);
            list.setVisibility(View.INVISIBLE);
            mainlinear = (LinearLayout) findViewById(R.id.mainlinear);
            mainlinear.setVisibility(View.VISIBLE);


            FragmentManager fragmentManager = getFragmentManager();
            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
                    R.animator.fragment_slide_left_exit,
                    R.animator.fragment_slide_right_enter,
                    R.animator.fragment_slide_right_exit);
            timeDistance = new distancetime();
            Bundle b = new Bundle();
            b.putString("Duration",detiles.get("Duration"));
            b.putString("Distance",detiles.get("Distance"));
            timeDistance.setArguments(b);
            fragmentTransaction.add(R.id.distancetime, timeDistance);
            fragmentTransaction.commitAllowingStateLoss();

        }


    }
    public void showAlertDialog (String title,String message){

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.Ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(i);
                        dialog.dismiss();

                    }
                });
        alertDialog.show();
    }

    public List<PropertyInfo> getCancelInfo (String id){
        List<PropertyInfo> info = new ArrayList<PropertyInfo>();
        PropertyInfo orderID = new PropertyInfo();
        orderID.setName("OrderID");
        orderID.setValue(id);// Generally array index starts from 0 not 1
        orderID.setType(String.class);

        info.add(orderID);
        return info;
    }

    public void FullMainList(){
        My_List.clear();
        list = (ListView) findViewById(R.id.pick);

        if(from!=null && !from.isEmpty()){
            pickUp one = new pickUp(from,durationTextNumber,false);
            My_List.add(one);
            isfrom=true;

        }else {
            pickUp one = new pickUp(getString(R.string.PickChoose),getString(R.string.PickArt),false);
            My_List.add(one);
            isDestination = false;
        }
        if(to!=null && !to.isEmpty()){
            pickUp two = new pickUp(to,"",true);
            My_List.add(two);

        }else {
            pickUp two = new pickUp(getString(R.string.Dest),getString(R.string.DestArt), true);
            My_List.add(two);

        }

        adapter = new pickAdapter(getApplicationContext(), My_List);
        notifyAdapter();

    }

    private void notifyAdapter() {
        runOnUiThread(new Runnable() {
            public void run() {
                list.setAdapter(adapter);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
    public void setActionBar(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(false) ;

        actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_CUSTOM);
        ImageView imageView = new ImageView(actionBar.getThemedContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setImageResource(R.drawable.main_logo);

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
        actionBar.setCustomView(imageView);
    }

    private void removeTaxisLocations(){
        mapView.getOverlays().clear();

        if(taxis != null) {

            for (Iterator<TaxisTrackingData> iterator = taxis.iterator(); iterator.hasNext(); ) {

                TaxisTrackingData taxi = iterator.next();
                taxi.setIsRemoved(true);
                if (taxi.getFirebase() != null && taxi.getValueEventListener() != null) {
                    taxi.getFirebase().removeEventListener(taxi.getValueEventListener());
                }
                if (taxi.getTaxiMarker() != null) {
                    mapView.getOverlays().remove(taxi.getTaxiMarker());
                    taxi.setTaxiMarker(null);
                    iterator.remove();
                }
            }
        }
    }

    private void removeTaxiNotAvailabile(Marker notAvailableTaxi, TaxisTrackingData taxiInfo){
        if(notAvailableTaxi != null){
            mapView.getOverlays().remove(notAvailableTaxi);
            taxiInfo.setTaxiMarker(null);
        }

    }

    private void loadTaxisLocations(){
        String driversFirebaseURL = FIREBASE_URL+"/Drivers/";
        String TRACKING = "/Tracking";

        for (final Iterator<TaxisTrackingData> iterator = taxis.iterator(); iterator.hasNext();) {
            final TaxisTrackingData taxi = iterator.next();
            final Firebase taxiFirebase = new Firebase(driversFirebaseURL+taxi.getTaxiID()+TRACKING);
            ValueEventListener valueEventListener =  new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String lat = dataSnapshot.child("lat").getValue(String.class);
                    String lng = dataSnapshot.child("lng").getValue(String.class);
                    float rotation = dataSnapshot.child("rotation").getValue(Float.class);
                    int isTaxiAvailabile = dataSnapshot.child("isAvailable").getValue(int.class);
                    double latDouble = Double.parseDouble(lat);
                    double lngDouble = Double.parseDouble(lng);
                    GeoPoint geoPoint = new GeoPoint(latDouble,lngDouble);
                    taxi.setIsAvailable(isTaxiAvailabile);
                    taxi.setFirebase(taxiFirebase);
                    if(isTaxiAvailabile == 1) {
                        if (taxi.isRemoved == false) {
                            Marker m = addMarkerDriverInLoading(taxi.getTaxiMarker(), geoPoint, rotation);
                            taxi.setTaxiMarker(m);
                        }else{
                            if (taxi.getTaxiMarker() != null){
                                mapView.getOverlays().remove(taxi.getTaxiMarker());
                            }
                            try {
                                iterator.remove();
                            }catch (Exception e){

                            }
                        }
                    }else{

                        removeTaxiNotAvailabile(taxi.getTaxiMarker() ,taxi);

                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            };

            taxiFirebase.addValueEventListener(valueEventListener);
            taxi.setValueEventListener(valueEventListener);

        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void PutMap(){
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.setMultiTouchControls(true);
        mMapController = (MapController) mapView.getController();
        mMapController.setZoom(18);
        GeoPoint center = new GeoPoint(31.905060, 35.204456);
        mMapController.animateTo(center);

        mapListener = new DelayedMapListener(new MapListener() {

            @Override
            public boolean onZoom(ZoomEvent arg0) {
                return false;
            }

            @Override
            public boolean onScroll(ScrollEvent arg0) {
//               Toast.makeText(getApplicationContext(), String.valueOf(mapView.getMapCenter().getLatitude()) + "  " + String.valueOf(mapView.getMapCenter().getLongitude()), Toast.LENGTH_LONG).show();
                userLatFromPicker = mapView.getMapCenter().getLatitude();
                userLngFromPicker = mapView.getMapCenter().getLongitude();
                GeoPoint userLocation = new GeoPoint(userLatFromPicker,userLngFromPicker);
                latLng = userLocation;
                mMapController.animateTo(latLng);
//                addMarker(latLng,getString(R.string.currentposition));
//                FullMainList();
//                showButtonAndList();
                if(checkNetworkConnection()) {
                    new LoadTaxis().execute();
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.NoInternet),Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        }, 100);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
//        Toast.makeText(this, "buildGoogleApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(MainActivity.this)
                .addApi(LocationServices.API)
                .build();
    }



    @Override
    public void onBackPressed() {
        if(!IsInRequest) {
            showButtonAndList();
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
                FullMainList();
                return;

            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if(IsInRequest){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            // Setting Dialog Message
            alertDialog.setMessage(getString(R.string.usercancel));
            // On pressing Settings button
            alertDialog.setPositiveButton(getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mapTouched=false;
                            IsInRequest = false;
                            cancelFirebase.setValue(2);
                            try {
                                removeListeners(TYPE.CANCELFROMUSER);
                            }catch (Exception e){

                            }
                            sharedPreferencesManager.setOrderDetiles(null);
                            sharedPreferencesManager.setOrderDriverID(null);
                            Intent intent = new Intent(MainActivity.context, com.bedetaxi.bedetaxi.cancel.class);
                            startActivity(intent);
                        }
                    });

            // on pressing cancel button
            alertDialog.setNegativeButton(getString(R.string.no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            // Showing Alert Message
            alertDialog.show();

        }else
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            System.exit(0);
        }
        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);

        UserImage = (ImageView) findViewById(R.id.imageView);
        navUserName = (TextView) findViewById(R.id.navUserName);

        String Image="";
        if(sharedPreferencesManager.getImage()!=null) {
            Image = sharedPreferencesManager.getImage();
        }
        if(sharedPreferencesManager.getUserName()!=null && !sharedPreferencesManager.getUserName().trim().isEmpty()){
            if (navUserName != null) {
                navUserName.setText(sharedPreferencesManager.getUserName());
            }
        }
        byte [] encodeByte=Base64.decode(Image, Base64.NO_WRAP);
        Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        if (bitmap!=null) {
            try {
                UserImage.setImageBitmap(bitmap);
            } catch (Exception e) {

            }
        }
        getMenuInflater().inflate(R.menu.main, menu);
        this.My_Menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.HomeItem) {
            if(isInFront){

            }else {
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
            }
        }else if (id == R.id.Edit_Profile_item){

            Intent i = new Intent(this,EditProfile.class);
            startActivity(i);
        }else if (id == R.id.History){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent i = new Intent(this,My_History.class);
            startActivity(i);
        }else if (id == R.id.language){
            final String CurrentLang = Locale.getDefault().getLanguage();

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getString(R.string.language));
            // Add the buttons
            builder.setPositiveButton(getString(R.string.en), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String languageToLoad = "en"; // your language
                    if (CurrentLang.equals(languageToLoad)){
                        Toast.makeText(getApplicationContext(),getString(R.string.usingEnglish),Toast.LENGTH_LONG).show();
                        return;
                    }
                    changeAppLanguage(languageToLoad);
                    sharedPreferencesManager.setAppLanguage(languageToLoad);
                    dialog.dismiss();




                    Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(refresh);
                    finish();

                }
            });
            builder.setNegativeButton(getString(R.string.ar), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog

                    String languageToLoad = "ar"; // your language
                    if (CurrentLang.equals(languageToLoad)){
                        Toast.makeText(getApplicationContext(),getString(R.string.usingArabic),Toast.LENGTH_LONG).show();
                        return;
                    }
                    changeAppLanguage(languageToLoad);
                    sharedPreferencesManager.setAppLanguage(languageToLoad);
                    dialog.dismiss();



                    Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(refresh);
                    finish();

                }
            });

            builder.create().show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void changeAppLanguage (String languageToLoad){
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }


    @Override
    public void onConnected(Bundle bundle) {
        try{
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mapView.getOverlayManager().clear();
                latLng = new GeoPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude());
//                if (latLng != null) {
//                    new LoadTaxis().execute();
//                }

//                addMarker(latLng, getString(R.string.currentposition));
                mMapController.animateTo(latLng);

            }

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5000); //5 seconds
            mLocationRequest.setFastestInterval(3000); //3 seconds
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } catch (SecurityException e) {

        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        FullMainList();
//        if (!checkGPSEnabled()){
//            GPSAlert();
//        }
        super.onResume();
    }

    @Override
    public void onLocationChanged(Location location) {
//       latLng = new GeoPoint(location.getLatitude(), location.getLongitude());
        if(firstAnimate){
//            addMarker(latLng,getString(R.string.currentposition));
            mMapController.animateTo(latLng);
            firstAnimate=false;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        isInFront = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isInFront = false;
    }

//    private class UpdateRoadTask extends AsyncTask<Object, Void, Road[]> {
//
//        protected Road[] doInBackground(Object... params) {
//            @SuppressWarnings("unchecked")
//            ArrayList<GeoPoint> waypoints = (ArrayList<GeoPoint>) params[0];
//            RoadManager roadManager = new OSRMRoadManager(context);
//
//
//            return roadManager.getRoads(waypoints);
//        }
//
//        @Override
//        protected void onPostExecute(Road[] result) {
//            road = result;
//           updateUIWithRoads(result);
////            if (road.mStatus != Road.STATUS_OK)
////                Toast.makeText(context, "Error when loading the road - status=" + road.mStatus, Toast.LENGTH_SHORT).show();
////            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
////            mapView.getOverlays().add(roadOverlay);
////
////            //3. Showing the Route steps on the map
////            FolderOverlay roadMarkers = new FolderOverlay();
////            mapView.getOverlays().add(roadMarkers);
////            Drawable nodeIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_node, null);
////            for (int i = 0; i < road.mNodes.size(); i++) {
////                RoadNode node = road.mNodes.get(i);
////                Marker nodeMarker = new Marker(mapView);
////                nodeMarker.setPosition(node.mLocation);
////                nodeMarker.setIcon(nodeIcon);
////
////                //4. Filling the bubbles
////                nodeMarker.setTitle("Step " + i);
////                nodeMarker.setSnippet(node.mInstructions);
////                nodeMarker.setSubDescription(Road.getLengthDurationText(context, node.mLength, node.mDuration));
////                Drawable iconContinue = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_continue, null);
////                nodeMarker.setImage(iconContinue);
////                //4. end
////
////                roadMarkers.add(nodeMarker);
////                //updateUIWithRoad(result);
//        }
//    }
//
//    void updateUIWithRoads(Road[] roads){
//
//        List<Overlay> mapOverlays = mapView.getOverlays();
//        if (mRoadOverlays != null){
//            for (int i=0; i<mRoadOverlays.length; i++)
//                mapOverlays.remove(mRoadOverlays[i]);
//            mRoadOverlays = null;
//        }
//        if (roads == null)
//            return;
//        if (roads[0].mStatus == Road.STATUS_TECHNICAL_ISSUE)
//            Toast.makeText(mapView.getContext(), "Technical issue when getting the route", Toast.LENGTH_SHORT).show();
//        else if (roads[0].mStatus > Road.STATUS_TECHNICAL_ISSUE) //functional issues
//            Toast.makeText(mapView.getContext(), "No possible route here", Toast.LENGTH_SHORT).show();
//        mRoadOverlays = new Polyline[roads.length];
//        for (int i=0; i<roads.length; i++) {
//            Polyline roadPolyline = RoadManager.buildRoadOverlay(roads[i]);
//            mRoadOverlays[i] = roadPolyline;
//
//            String routeDesc = roads[i].getLengthDurationText(this, -1);
//            roadPolyline.setRelatedObject(i);
//            roadPolyline.setOnClickListener(new RoadOnClickListener());
//            mapOverlays.add(1, roadPolyline);
//            //we insert the road overlays at the "bottom", just above the MapEventsOverlay,
//            //to avoid covering the other overlays.
//        }
//        selectRoad(0);
//    }
//
//    class RoadOnClickListener implements Polyline.OnClickListener{
//        @Override public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos){
//            int selectedRoad = (Integer)polyline.getRelatedObject();
//            selectRoad(selectedRoad);
//            polyline.showInfoWindow(eventPos);
//            return true;
//        }
//    }
//
//
//    void selectRoad(int roadIndex){
//        mSelectedRoad = roadIndex;
//        //Set route info in the text view:
//        for (int i=0; i<mRoadOverlays.length; i++){
//            Paint p = mRoadOverlays[i].getPaint();
//            if (i == roadIndex)
//                p.setColor(0x800000FF); //blue
//            else
//                p.setColor(0x90666666); //grey
//        }
//        mapView.invalidate();
//    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void ShowNot (String Title,String Not){
        NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this);
        mBuilder .setSmallIcon(R.drawable.logo);
        mBuilder  .setContentTitle(Title);
        mBuilder   .setContentText(Not);
        mBuilder.setAutoCancel(true);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        Class activity = context.getClass();
        Intent intent = new Intent(this, activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        mBuilder.setLights(Color.YELLOW, 3000, 3000);
        NM.notify(0, mBuilder.build());

    }
    class OrderStatus extends AsyncTask<Void, Void, String> {
        String output;


        @Override
        protected String doInBackground(Void... params) {
            List<PropertyInfo> prop = getPropertyInfo(sharedPreferencesManager.getOrderDriverID());
            WebAPI api = new WebAPI(context, "getOrderStatus", prop);
            output = api.call_request();
            return output;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONArray data = null;
            try{
                data = new JSONArray(output);

                String status = data.getJSONObject(0).getString("status");

                if (status.equals("success")) {
                    int OrderStatus = data.getJSONObject(0).getInt("value");
                    if (OrderStatus == 0) {
                        String dataOrder;
                        dataOrder = sharedPreferencesManager.getOrderDetiles();
                        Gson gson = new Gson();
                        JSONObject details = null;
                        try {
                             details =new JSONObject(dataOrder.replaceAll("=",":"));
//                            value= value.replaceAll("=",":");
//                            details = new JSONObject(value);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(details == null)
                            return;
                        HaveRequest = true;
                        HashMap<String, String> hashMap = new HashMap<String, String>();

                        hashMap.put("driverID", details.getString("DriverID"));
                        hashMap.put("DriverName", details.getString("DriverName"));
                        hashMap.put("DriverPhone", details.getString("DriverPhone"));
                        hashMap.put("VehicleType", details.getString("VehicleType"));
                        hashMap.put("Distance", details.getString("Distance"));
                        hashMap.put("Duration", details.getString("Duration"));
                        hashMap.put("taxiLat", details.getString("lat"));
                        hashMap.put("taxiLng", details.getString("lng"));
                        if (details.has("DriverImage")){
                            hashMap.put("DriverImage", details.getString("DriverImage"));

                        }else{
                            hashMap.put("DriverImage",null);

                        }
                        hashMap.put("Rating", details.getString("Rating"));
                        initRequestData(hashMap);

                    } else {
                        sharedPreferencesManager.setOrderDetiles(null);
                        sharedPreferencesManager.setOrderDriverID(null);
                        mapView.setMapListener(mapListener);
                    }

                } else {
                    mapView.setMapListener(mapListener);
                    Toast.makeText(context, data.getJSONObject(0).getString("message"), Toast.LENGTH_LONG).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
//                Toast.makeText(context, getString(R.string.Error), Toast.LENGTH_LONG).show();
            }

        }

    }

    class LoadTaxis extends AsyncTask<Void, Void, String> {
        String output;


        @Override
        protected String doInBackground(Void... params) {
            List<PropertyInfo> prop = getLoadingTaxisProp();
            WebAPI api = new WebAPI(context, "getInRangeDrivers", prop);
            output = api.call_request();
            return output;
        }

        @Override
        protected void onPreExecute() {
        gifImageView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(taxis != null && taxis.size()>0) {
                removeTaxisLocations();
            }
            JSONArray data = null;
            try {
                data = new JSONArray(output);
if(!data.toString().trim().isEmpty()) {
    String status = data.getJSONObject(0).getString("status");

    if (status.equals("success")) {

        JSONArray details = data.getJSONObject(0).getJSONArray("details");
        taxis = new ArrayList<TaxisTrackingData>();
        for (int i = 0; i < details.length(); i++) {
            taxis.add(new TaxisTrackingData((String) details.get(i)));
        }
        if (taxis.size() > 0) {
            loadTaxisLocations();

        }
        String  title = data.getJSONObject(0).getString("title");
        if(title != null && !title.trim().isEmpty() && !title.equalsIgnoreCase("null")) {
            from = title;

        }else {
            from = getString(R.string.maplocation);
        }

        int duration = data.getJSONObject(0).getInt("duration");
        if((duration > 0 && duration <=2) || duration >= 11) {
            durationTextNumber = duration +" "+ getString(R.string.durationtext);
        } else if (duration >= 3 && duration <= 10){
            durationTextNumber = duration +" "+ getString(R.string.mins);

        }
        else{
            durationTextNumber = getString(R.string.unavilable);
        }
        FullMainList();

        gifImageView.setVisibility(View.INVISIBLE);

    } else {

        Toast.makeText(context, data.getJSONObject(0).getString("message"), Toast.LENGTH_LONG).show();

    }
}
            } catch (JSONException e) {
                e.printStackTrace();


            }
        }
    }

    public List<PropertyInfo> getLoadingTaxisProp(){
            List<PropertyInfo> propertyInfos = new ArrayList<PropertyInfo>();

            PropertyInfo lat = new PropertyInfo();
            lat.setName("lat");
            lat.setValue(String.valueOf(latLng.getLatitude()));// Generally array index starts from 0 not 1
            lat.setType(String.class);
            propertyInfos.add(lat);

        PropertyInfo lng = new PropertyInfo();
        lng.setName("lng");
        lng.setValue(String.valueOf(latLng.getLongitude()));// Generally array index starts from 0 not 1
        lng.setType(String.class);
        propertyInfos.add(lng);

        return propertyInfos;
    }
    }



