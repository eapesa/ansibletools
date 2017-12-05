package com.droidtools.eapesa.ansible;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;


/**
 * Created by eapesa on 12/4/17.
 */

public class MainActivity extends AppCompatActivity {

    private Button googleLoginButton;
    private Button facebookLoginButton;
    private TextView loginTokenTextView;
    private CallbackManager callbackManager;

    private final String[] LOGIN_READ_PERMISSIONS = new String[] { "public_profile", "email" };
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        googleLoginButton = findViewById(R.id.main_button_google);
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginTokenTextView.setText("GOOGLE SIGN IN CLICKED!!!");
            }
        });

        facebookLoginButton = findViewById(R.id.main_button_facebook);
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this,
                        Arrays.asList(LOGIN_READ_PERMISSIONS));
            }
        });

        loginTokenTextView = findViewById(R.id.main_textview_token);
        gson = new Gson();

        initializeFacebook();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void initializeFacebook() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String loginResultString = gson.toJson(loginResult);
                Log.d("initializeFacebook:SUCCESS", loginResultString);
//                loginTokenTextView.setText(userData.getString(""));
                try {
                    JSONObject userData = new JSONObject(loginResultString).getJSONObject("accessToken");
                    String facebookToken = userData.getString("token");
                    Log.d("initializeFacebook:SUCCESS", "TOKEN: " + facebookToken);
                    loginTokenTextView.setText(facebookToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {
                Log.d("initializeFacebook:CANCEL", "Canceled login");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("initializeFacebook:ERROR", "Encountered error: " + error.toString());
            }
        });
    }

}
