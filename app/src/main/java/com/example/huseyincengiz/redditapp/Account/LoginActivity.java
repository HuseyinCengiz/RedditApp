package com.example.huseyincengiz.redditapp.Account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.huseyincengiz.redditapp.FeedApi;
import com.example.huseyincengiz.redditapp.FeedClient;
import com.example.huseyincengiz.redditapp.R;

import java.util.HashMap;

import javax.security.auth.login.LoginException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by HuseyinCengiz on 2.09.2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private ProgressBar mProgress;
    private EditText mUsername, mPassword;
    private Button mbtnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Started");

        mProgress = (ProgressBar) findViewById(R.id.loginRequestLoadingProgressBar);
        mUsername = (EditText) findViewById(R.id.input_username);
        mPassword = (EditText) findViewById(R.id.input_password);
        mbtnLogin = (Button) findViewById(R.id.btn_login);
        mProgress.setVisibility(View.GONE);

        mbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Username = mUsername.getText().toString();
                String Password = mPassword.getText().toString();
                if (!Username.equals("") && !Password.equals("")) {
                    mProgress.setVisibility(View.VISIBLE);
                    login(Username, Password);
                }
            }
        });
    }

    private void login(final String username, String password) {
        FeedApi feedApi = FeedClient.getFeedApiImplements(FeedClient.LOGIN_URL, GsonConverterFactory.create());

        HashMap<String, String> headersMap = new HashMap<String, String>();
        headersMap.put("Content-Type", "application/json");

        Call<CheckLogin> call = feedApi.signIn(headersMap, username, username, password, "json");

        call.enqueue(new Callback<CheckLogin>() {
            @Override
            public void onResponse(Call<CheckLogin> call, Response<CheckLogin> response) {

                try {
                    Log.d(TAG, "Onresponse:Server Response " + response.toString());
                    //  Log.d(TAG, "onResponse:" + response.body().toString());
                    String modhash = response.body().getJson().getData().getModhash();
                    String cookie = response.body().getJson().getData().getCookie();
                    if (!modhash.equals("")) {
                        setSessionParams(username, modhash, cookie);
                        mProgress.setVisibility(View.GONE);
                        mUsername.setText("");
                        mPassword.setText("");
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        //navigate back to previous activity
                        finish();
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "onResponse:NullPointerException:" + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<CheckLogin> call, Throwable t) {
                Log.d(TAG, "Onfailure:Unable to retrieve Json " + t.getMessage());
                Toast.makeText(LoginActivity.this, "An Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSessionParams(String username, String modhash, String cookie) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Log.d(TAG, "setSessionParams: Storing session variables" + "\n" +
                "Username:" + username + "\n" +
                "Modhash:" + modhash + "\n" +
                "Cookie:" + cookie + "\n"
        );
        editor.putString("@string/SessionUsername", username);
        editor.putString("@string/SessionModhash", modhash);
        editor.putString("@string/SessionCookie", cookie);
        editor.commit();
    }
}
