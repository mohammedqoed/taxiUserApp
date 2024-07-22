package com.bedetaxi.bedetaxi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistertionFragment extends Fragment {

    private EditText nameText;
    private EditText phoneText;
    private EditText emailText;
    private TextView Privacy;
    Context context;
    SharedPreferencesManager sharedPreferencesManager;
    View rootView;

    public RegistertionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_registertion, container, false);


        nameText = (EditText) rootView.findViewById(R.id.nameTextFields);
        phoneText = (EditText) rootView.findViewById(R.id.phoneTextField);
        emailText = (EditText) rootView.findViewById(R.id.emailTextField);
        Privacy = (TextView) rootView.findViewById(R.id.Privacy);
        Bundle bundle = getArguments();
        if (bundle != null){
            nameText.setText(bundle.getString("first_name") + " "+bundle.getString("last_name"));
            emailText.setText(bundle.getString("email"));
        }

        context = rootView.getContext();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        sharedPreferencesManager = new SharedPreferencesManager(context);

        Button goButton = (Button) rootView.findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               GOButton();
            }
        });

        Privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkNetworkConnection()) {
                    Toast.makeText(context, R.string.NoInternet, Toast.LENGTH_LONG).show();
                    return;
                }
                Uri uri = Uri.parse("http://bedetaxi.com/privacy"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    public void GOButton (){

        if (!checkIfFieldsIsEmpty()) {
            Toast.makeText(context, R.string.FieldsEmpty, Toast.LENGTH_LONG).show();
            return;
        }
        if (!checkEmailPattren()) {
            Toast.makeText(context, R.string.InValidEmail, Toast.LENGTH_LONG).show();
            return;
        }

        if (!checkPhoneNumberPattren()) {
            Toast.makeText(context, R.string.InValidPhone, Toast.LENGTH_LONG).show();
            return;

        }
        if (!checkNetworkConnection()) {
            Toast.makeText(context, R.string.NoInternet, Toast.LENGTH_LONG).show();
            return;
        }
        dialog();

    }

    public boolean checkIfFieldsIsEmpty() {
        if (nameText.getText().toString().trim().isEmpty() ||
                phoneText.getText().toString().trim().isEmpty()) {
            return false;

        }
        return true;
    }

    public boolean checkEmailPattren() {
        if(emailText.getText().toString().trim().isEmpty()){
            return true;
        }
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailText.getText().toString());
        if (matcher.find()) {
            return true;
        }
        return false;

    }

    public boolean checkPhoneNumberPattren() {
        Pattern VALID_PHONE_NUMBER = Pattern.compile("^0?5[0-9]{8}$");
        Matcher matcher = VALID_PHONE_NUMBER.matcher(phoneText.getText().toString());
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        } else
            return false;


    }
    public List<PropertyInfo> getPropertyInfo(){
        List<PropertyInfo> propertyInfos = new ArrayList<PropertyInfo>();

        PropertyInfo name = new PropertyInfo();
        name.setName("name");
        name.setValue(nameText.getText().toString());// Generally array index starts from 0 not 1
        name.setType(String.class);
        propertyInfos.add(name);
        PropertyInfo phone = new PropertyInfo();
        phone.setName("phone");
        phone.setValue(phoneText.getText().toString());// Generally array index starts from 0 not 1
        phone.setType(String.class);
        propertyInfos.add(phone);
        PropertyInfo email = new PropertyInfo();
        email.setName("email");
        if(emailText.getText().toString().isEmpty()){
            email.setValue("");
        }else
        email.setValue(emailText.getText().toString());// Generally array index starts from 0 not 1
        email.setType(String.class);
        propertyInfos.add(email);
        PropertyInfo role = new PropertyInfo();
        role.setName("role");
        role.setValue(1);// Generally array index starts from 0 not 1
        role.setType(int.class);
        propertyInfos.add(role);

        return propertyInfos;
    }


    public void add (){
        FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
                R.animator.fragment_slide_left_exit,
                R.animator.fragment_slide_right_enter,
                R.animator.fragment_slide_right_exit);
        VerificationFragment f = new VerificationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("phone",phoneText.getText().toString());
        f.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment, f);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();

    }

    class ConfirmTask extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;
        String output;


        @Override
        protected String doInBackground(Void... params) {
            List<PropertyInfo> prop = getPropertyInfo();
            WebAPI api = new WebAPI(context, "RegisterUser", prop);
            output = api.call_request();
            return output;
        }
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
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

            String status = data.getJSONObject(0).getString("status");

            if (status.equals("success")) {
                String id = data.getJSONObject(0).getString("userid");
                sharedPreferencesManager.insertUserID(id);
                sharedPreferencesManager.setUserName(nameText.getText().toString());
                sharedPreferencesManager.setUserEmail(emailText.getText().toString());
                sharedPreferencesManager.setUserPhone(phoneText.getText().toString());
                add();
            }else {

                Toast.makeText(context,data.getJSONObject(0).getString("message"),Toast.LENGTH_LONG).show();

            }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context,getString(R.string.Error),Toast.LENGTH_LONG).show();
            }

        }
    }

    public void dialog() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.main_dialog);

        TextView title = (TextView) dialog.findViewById(R.id.dialog_title);
        TextView phone = (TextView) dialog.findViewById(R.id.dialog_phone);
        title.setText(getString(R.string.PhoneNumberConfirmation));
        phone.setText(phoneText.getText());
        Button dialog_no = (Button) dialog.findViewById(R.id.dialog_button_No);
        dialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button dialog_yes = (Button) dialog.findViewById(R.id.dialog_button_Yes);
        dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConfirmTask().execute();
                dialog.dismiss();
            }
        });

        dialog.show();

    }

}
