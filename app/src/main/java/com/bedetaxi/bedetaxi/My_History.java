package com.bedetaxi.bedetaxi;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class My_History extends Activity {
    ListView HistoryListt;
    List<HistoryList> HistoryData;
    View view ;
    HistoryAdapter adapter;
    SharedPreferencesManager sharedPreferencesManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__history);
        sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(false) ;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        actionBar.setDisplayOptions(actionBar.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.layout_bar);
        ImageView back = (ImageView) actionBar.getCustomView().findViewById(R.id.back);
        String language = Locale.getDefault().getLanguage();
        if(language.equalsIgnoreCase("en")){
            back.setImageResource(R.drawable.backright);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        ImageView imageView = new ImageView(actionBar.getThemedContext());
//        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        imageView.setImageResource(R.drawable.main_logo);
//        ImageView back = new ImageView(actionBar.getThemedContext());
//        back.setScaleType(ImageView.ScaleType.FIT_START);
//        back.setImageResource(R.drawable.back);
//        back.getPaddingEnd();
//        back.getPaddingStart();
//
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
//                ActionBar.LayoutParams.WRAP_CONTENT,
//                ActionBar.LayoutParams.WRAP_CONTENT);
//        imageView.setLayoutParams(layoutParams);
//        back.setLayoutParams(layoutParams);
//
//        actionBar.setCustomView(imageView);
//        actionBar.setCustomView(back);
        new History().execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //
    public void getHistory(JSONArray jsonArray)throws JSONException {
        JSONArray json = jsonArray;
        HistoryData = new ArrayList<HistoryList>();
        HistoryListt = (ListView) findViewById(R.id.list_history);
        adapter = new HistoryAdapter(My_History.this,HistoryData);
        if (json.getJSONObject(0).getString("status").equalsIgnoreCase("Success")) {
            JSONArray jsonArray1 = json.getJSONObject(0).getJSONArray("details");

            for (int i = 0; i<jsonArray1.length();i++){
                JSONObject jsonObject = jsonArray1.getJSONObject(i);
                String name = jsonObject.getString("DriverName");
                String office = jsonObject.getString("Office");
                String pick = jsonObject.getString("Pickup");
                if(pick.trim().isEmpty() || pick.equalsIgnoreCase("غير محدد") || pick.equalsIgnoreCase("null")||pick.equalsIgnoreCase("N/A")){
                    pick = getString(R.string.pickChoice);
                }
                String Dest = jsonObject.getString("Destination");
                if(Dest.equalsIgnoreCase("غير محدد")|| Dest.equalsIgnoreCase("N/A") || Dest.equalsIgnoreCase("null") || Dest.trim().isEmpty()){
                    Dest = getString(R.string.userChoice);
                }
                String date = jsonObject.getString("DateTime");
                name = name + ","+office;
                HistoryList data = new HistoryList(pick,date,Dest,name);

                HistoryData.add(data);
            }




        }
        HistoryListt.setAdapter(adapter);
        if(HistoryData.size() == 0){
            Toast.makeText(this,getString(R.string.historyEmpty),Toast.LENGTH_LONG).show();
        }
    }


    public List<PropertyInfo> getProperty(){
        List<PropertyInfo> propertyInfos = new ArrayList<PropertyInfo>();
        PropertyInfo UserID = new PropertyInfo();
        UserID.setName("UserID");
        UserID.setValue(sharedPreferencesManager.getUserID());// Generally array index starts from 0 not 1
        UserID.setType(String.class);
        propertyInfos.add(UserID);

        return propertyInfos;
    }


    class History extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;
        String output;


        @Override
        protected String doInBackground(Void... params) {
            List<PropertyInfo> prop = getProperty();
            WebAPI api = new WebAPI(getApplicationContext(), "getHistory", prop);
            output = api.call_request();
            return output;
        }
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(My_History.this);
            progressDialog.setTitle(getString(R.string.Loading));
            progressDialog.setMessage(getString(R.string.Wait));
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
                getHistory(data);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),getString(R.string.Error),Toast.LENGTH_LONG).show();
            }

        }
    }



}
