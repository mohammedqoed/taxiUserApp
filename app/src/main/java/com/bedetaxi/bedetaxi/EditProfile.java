package com.bedetaxi.bedetaxi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.ksoap2.serialization.PropertyInfo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class EditProfile extends AppCompatActivity {

    Button confirm;
    EditText name;
    EditText phone;
    EditText email;
    String nameText;
    String phoneText;
    String emailText;
    ImageView userImage;
    String Extention;
    Bitmap bitmap;
    Bitmap resized;
    ImageView back;
    private static final int PERMISSION_REQUEST_CODE = 200;


    SharedPreferencesManager sharedPreferencesManager;
    private int PICK_IMAGE_REQUEST = 1;
    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(!checkPermission()){
                requestPermission();
            }
        }

        name = (EditText) findViewById(R.id.UserName);
        phone =(EditText) findViewById(R.id.PhoneNumber);
        email =(EditText) findViewById(R.id.Email);
        back = (ImageView) findViewById(R.id.back);
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
        sharedPreferencesManager = new SharedPreferencesManager(this);
        confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<PropertyInfo> prop = getProperty();
                WebAPI webApi = new WebAPI(EditProfile.this,"updateUserInfo",prop);
                String result = webApi.call();
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Update_Profile(jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


               // Intent i = new Intent(EditProfile.this,MainActivity.class);
              //  startActivity(i);
            }
        });

            userImage = (ImageView) findViewById(R.id.UserImage);
            getPic();
            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        if(!checkPermission()){
                            requestPermission();
                        }else{
                            Intent intent = new Intent();
                            // Show only images, no videos or anything else
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            // Always show the chooser (if there are multiple options available)
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        }
                    }else{
                        Intent intent = new Intent();
                        // Show only images, no videos or anything else
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        // Always show the chooser (if there are multiple options available)
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                    }


                }
            });



        Initialization();



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void Update_Profile(JSONArray jsonArray)throws JSONException {
        JSONArray json= jsonArray;
        if(json == null ||  json.getJSONObject(0) != null|| json.getJSONObject(0).getString("status").trim().isEmpty() ||json.getJSONObject(0).getString("status").equalsIgnoreCase("failed") ){
            super.onBackPressed();
        }else if (json.getJSONObject(0).getString("status").equalsIgnoreCase("Success")) {
            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

            super.onBackPressed();

        }

    }

    public List<PropertyInfo> getProperty() {

        List<PropertyInfo> propertyInfos = new ArrayList<PropertyInfo>();

        nameText = name.getText().toString();
        phoneText = phone.getText().toString();
        emailText = email.getText().toString();


        PropertyInfo UserID = new PropertyInfo();
        UserID.setName("UserID");
        UserID.setValue(sharedPreferencesManager.getUserID());// Generally array index starts from 0 not 1
        UserID.setType(String.class);
        propertyInfos.add(UserID);

        sharedPreferencesManager.setUserName(nameText);

        PropertyInfo nameInfo = new PropertyInfo();
        nameInfo.setName("name");
        nameInfo.setValue(nameText);// Generally array index starts from 0 not 1
        nameInfo.setType(String.class);
        propertyInfos.add(nameInfo);

        sharedPreferencesManager.setUserEmail(emailText);

        PropertyInfo emailInfo = new PropertyInfo();
        emailInfo.setName("email");
        emailInfo.setValue(emailText);// Generally array index starts from 0 not 1
        emailInfo.setType(String.class);
        propertyInfos.add(emailInfo);

        sharedPreferencesManager.setUserPhone(phoneText);

        PropertyInfo phoneInfo = new PropertyInfo();
        phoneInfo.setName("phone");
        phoneInfo.setValue(phoneText);// Generally array index starts from 0 not 1
        phoneInfo.setType(String.class);
        propertyInfos.add(phoneInfo);

        PropertyInfo Image = new PropertyInfo();
        Image.setName("img");
        Image.setValue("data:image/"+Extention+";base64,"+sharedPreferencesManager.getImage());// Generally array index starts from 0 not 1
        Image.setType(String.class);
        propertyInfos.add(Image);

        return propertyInfos;
    }


    private String getRealPathFromURI(Uri contentURI) {
        try{
            String result;
            Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);

                result = cursor.getString(idx);
                cursor.close();
            }
            return result;
        }catch (Exception e){
            return null;
        }

    }

    private boolean checkPermission() {
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(),READ_EXTERNAL_STORAGE);

        return  result2==PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean StorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (StorageAccepted) {
                        Toast.makeText(this, "Permission Granted, Now you can access  Read Storage.", Toast.LENGTH_LONG).show();
                    }
                    else {

                        Toast.makeText(this, "Permission Denied, You cannot access Read Storage.", Toast.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE},
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
        new AlertDialog.Builder(EditProfile.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();


    }

@RequiresApi(api = Build.VERSION_CODES.FROYO)
public void getPic(){
    if(sharedPreferencesManager.getImage().equalsIgnoreCase("")){

        userImage.setImageResource(R.drawable.profile);
    }else {
        String image = sharedPreferencesManager.getImage();

            byte [] encodeByte=Base64.decode(image,Base64.DEFAULT);
            Bitmap bitmap1= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

        userImage.setImageBitmap(bitmap1);
    }
}

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                 bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                String FilePath = getRealPathFromURI(uri);
                if(FilePath == null){
                    Toast.makeText(this,getString(R.string.pathError),Toast.LENGTH_LONG).show();
                    return;
                }
                Extention = FilePath.substring(FilePath.lastIndexOf(".") + 1);
                if(!Extention.toLowerCase().matches("(png|jpg|jpeg)")) {
                    Toast.makeText(this,getString(R.string.imageSelectionError),Toast.LENGTH_LONG).show();
                    return;
                }
                resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.50), (int) (bitmap.getHeight() * 0.50), true);
                resized.compress(Bitmap.CompressFormat.JPEG,50, baos);
                byte [] b=baos.toByteArray();
                String temp= Base64.encodeToString(b, Base64.NO_WRAP );
                // Log.d(TAG, String.valueOf(bitmap));

                userImage.setImageBitmap(bitmap);
                MainActivity.UserImage.setImageBitmap(bitmap);
                sharedPreferencesManager.setUserImage(temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

public void Initialization(){

    name.setText(sharedPreferencesManager.getUserName());
    phone.setText(sharedPreferencesManager.getUserphone());
    email.setText(sharedPreferencesManager.getEmail());
}





}
