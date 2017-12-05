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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
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

    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
//    private final String GOOGLE_CLIENT_ID = "683661300541-jr57k854iied178o2qdbge9k63j4p5m4.apps.googleusercontent.com";
    private final String GOOGLE_CLIENT_ID = "683661300541-be09k4mtbujvvqag5jedajscqpiv98ci.apps.googleusercontent.com";
    private final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        googleLoginButton = findViewById(R.id.main_button_google);
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignin();
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
//        initializeGoogle();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_CLIENT_ID)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            loginTokenTextView.setText(idToken);
        } catch(ApiException e) {
            e.printStackTrace();
        }
    }

    private void googleSignin() {
        Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(googleSignInIntent, RC_SIGN_IN);
    }

    private void initializeFacebook() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String loginResultString = gson.toJson(loginResult);
                try {
                    JSONObject userData = new JSONObject(loginResultString).getJSONObject("accessToken");
                    String facebookToken = userData.getString("token");
                    loginTokenTextView.setText(facebookToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

}
