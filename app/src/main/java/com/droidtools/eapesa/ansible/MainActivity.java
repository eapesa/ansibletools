package com.droidtools.eapesa.ansible;

import android.app.Activity;
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
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
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

public class MainActivity extends AppCompatActivity {
    private TextView loginTokenTextView;
    private CallbackManager callbackManager;

    private final String[] LOGIN_READ_PERMISSIONS = new String[] { "public_profile", "email" };
    private Gson gson;

    private GoogleSignInClient mGoogleSignInClient;
    private final String GOOGLE_CLIENT_ID = "683661300541-be09k4mtbujvvqag5jedajscqpiv98ci.apps.googleusercontent.com";
    private final int REQUEST_CODE_GOOGLE = 1;
    private final int REQUEST_CODE_ACCOUNTKIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button googleLoginButton;
        Button facebookLoginButton;
        final Button accountKitLoginButton;
        final Activity activity = this;

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

        accountKitLoginButton = findViewById(R.id.main_button_accountkit);
        accountKitLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loginTokenTextView.setText("AK CLICKED!");
                accountKitPhoneLogin(activity);
            }
        });

        initializeFacebook();
        initializeGoogle();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else if (requestCode == REQUEST_CODE_ACCOUNTKIT) {
            loginTokenTextView.setText(AccountKit.getCurrentAccessToken().getToken());
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
        startActivityForResult(googleSignInIntent, REQUEST_CODE_GOOGLE);
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

    private void initializeGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GOOGLE_CLIENT_ID)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void accountKitPhoneLogin(Activity activity) {
        final Intent intent = new Intent(activity, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN
                ).setReceiveSMS(true);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, REQUEST_CODE_ACCOUNTKIT);
    }
}
