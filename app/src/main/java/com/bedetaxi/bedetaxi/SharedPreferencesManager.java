package com.bedetaxi.bedetaxi;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by Alaa on 12/19/2016.
 */
public class SharedPreferencesManager {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    public SharedPreferencesManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("UserInformation", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }
    public void setOrderDriverID(String driverID){
        editor.putString("DriverID" , driverID);
        editor.commit();
    }




    public void setOrderDetiles(String order){
        editor.putString("Order",order);
        editor.commit();
    }
    public String getOrderDetiles(){return sharedPreferences.getString("Order","");}
    public String getOrderDriverID(){return sharedPreferences.getString("DriverID","");}

    public void insertUserID (String id){


        editor.putString("UserID",id);
        editor.apply();
    }
    public void Verify (){


        editor.putString("verify","true");
        editor.apply();
    }
    public void setUserName (String userName){


        editor.putString("UserName",userName);
        editor.apply();
    }
    public void setUserPhone(String userPhone){


        editor.putString("UserPhone",userPhone);
        editor.apply();
    }
    public void setUserEmail (String userEmail){


        editor.putString("UserEmail",userEmail);
        editor.apply();
    }
    public void setUserImage (String userImage){


        editor.putString("UserImage", userImage);
        editor.apply();
    }

    public void setOrderPosition (String orderPosition){


        editor.putString("OrderPosition", orderPosition);
        editor.apply();
    }

    public void setAppLanguage (String language){


        editor.putString("language", language);
        editor.apply();
    }
    public String getOrderPosition (){
        return sharedPreferences.getString("OrderPosition","");
    }
    public String getAppLanguage (){
        return sharedPreferences.getString("language",null);
    }

    public String getUserID (){
        return sharedPreferences.getString("UserID","NewUser");
    }
    public String getVerify (){
        return sharedPreferences.getString("verify","false");
    }
    public String getUserName (){
        return sharedPreferences.getString("UserName","");
    }
    public String getUserphone (){
        return sharedPreferences.getString("UserPhone","");
    }
    public String getEmail(){
        return sharedPreferences.getString("UserEmail","");
    }
    public String getImage(){return sharedPreferences.getString("UserImage","");}

}
