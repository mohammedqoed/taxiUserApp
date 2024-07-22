package com.bedetaxi.bedetaxi;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.ksoap2.serialization.PropertyInfo;

import java.util.ArrayList;
import java.util.List;

public class Rating extends Activity {

    RatingBar ratingBar;
    String DriverID;
    int rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);


        ActionBar actionBar = getActionBar();
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

        DriverID = getIntent().getExtras().getString("DriverID");

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
       ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
           @Override
           public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            rate = (int)rating;
              new RatingDriver().execute();
               MainActivity.to="";
                   Intent i = new Intent(Rating.this,MainActivity.class);
                   startActivity(i);

           }
       });



    }

    class RatingDriver extends AsyncTask<Void, Void, String>{

        ProgressDialog progressDialog;
        String output;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Rating.this);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Please Wait ... ");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

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
                    Toast.makeText(MainActivity.context, "success", Toast.LENGTH_LONG).show();

                }else if(status.equalsIgnoreCase("failed"))
                {
                    Toast.makeText(MainActivity.context, "failed", Toast.LENGTH_LONG).show();

                }
            }


        @Override
        protected String doInBackground(Void... params) {
            List<PropertyInfo> props;
            props = getRate(DriverID,rate);
            WebAPI webAPI = new WebAPI(Rating.this,"Rate",props);
            output = webAPI.call_request();
            return output;
        }
    }



    public List<PropertyInfo> getRate(String id,int rate){
        List<PropertyInfo> info = new ArrayList<PropertyInfo>();
        PropertyInfo DriverID = new PropertyInfo();
        DriverID.setName("DriverID");
        DriverID.setValue(id);// Generally array index starts from 0 not 1
        DriverID.setType(String.class);

        info.add(DriverID);
        PropertyInfo UserPhone = new PropertyInfo();
        UserPhone.setName("Rating");
        UserPhone.setValue(rate);// Generally array index starts from 0 not 1
        UserPhone.setType(Integer.class);

        info.add(UserPhone);

        return info;
    }

}
