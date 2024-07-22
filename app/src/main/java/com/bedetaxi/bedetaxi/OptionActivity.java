package com.bedetaxi.bedetaxi;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;
/*
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
*/

/**
 * A simple {@link Fragment} subclass.
 */
public class OptionActivity extends Fragment {

    private ProfileTracker mProfileTracker;
    private CallbackManager callbackManager;
    View root;
    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            System.out.println("onSuccess");
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            String accessToken = loginResult.getAccessToken().getToken();
            Log.i("accessToken", accessToken);

            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Log.i("LoginActivity", response.toString());
                    // Get facebook data from login
                    Bundle bFacebookData = getFacebookData(object);
                    progressDialog.dismiss();
                    add(bFacebookData);
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            System.out.println("onCancel");
        }

        @Override
        public void onError(FacebookException exception) {
            System.out.println("onError");
//            Log.v("LoginActivity", exception.getCause().toString());
        }
    };

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
            LoginManager.getInstance().logOut();
            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (JSONException e) {
            Log.d(TAG, "Error parsing JSON");
            return null;
        }
    }


    public OptionActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton) root.findViewById(R.id.facebook);
        loginButton.setReadPermissions("email");
        // If using in a fragment
        loginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager,mCallback );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //AppEventsLogger.activateApp(getActivity().getApplication());
        root = inflater.inflate(R.layout.fragment_option, container, false);





        Button c = (Button) root.findViewById(R.id.phone);



        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                add(null);


            }
        });

        // Inflate the layout for this fragment
        return root;
    }

    public void add (Bundle bundle){
        FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
                R.animator.fragment_slide_left_exit,
                R.animator.fragment_slide_right_enter,
                R.animator.fragment_slide_right_exit);
        RegistertionFragment f = new RegistertionFragment();
        if(bundle != null) {
            f.setArguments(bundle);
        }
        fragmentTransaction.replace(R.id.fragment, f);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

}
