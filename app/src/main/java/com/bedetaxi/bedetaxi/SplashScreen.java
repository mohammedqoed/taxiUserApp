package com.bedetaxi.bedetaxi;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Locale;

/**
 * Created by vamsikrishna on 12-Feb-15.
 */
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getApplicationContext());
                    String isVeify = sharedPreferencesManager.getVerify();
                    if (isVeify.equals("true")){
                        if (sharedPreferencesManager.getAppLanguage() != null){
                            changeAppLanguage(sharedPreferencesManager.getAppLanguage());
                        }
                        Intent i = new Intent(getBaseContext(),MainActivity.class);

                        startActivity(i);
//                        overridePendingTransition(R.animator.slide_in_right, R.animator.slide_in_left);
                    }else {
                        Intent i = new Intent(getBaseContext(), Registiration.class);
                        startActivity(i);
                    }


                }

            }

        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    public void changeAppLanguage (String languageToLoad){
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

}