package me.reward.rewardme;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.reward.rewardme.DataObjects.UserInfo;
import me.reward.rewardme.ApiCallLibrary.Callback;


public class LoginActivity extends AppCompatActivity implements Callback {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private ProgressDialog progressDialog;

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                UserInfo.setCurrentUser(new UserInfo("Gilbert", "gilbert_maldonado@utexas.edu"));
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        try {
            JSONObject newUser = new JSONObject().put(getString(R.string.email), email)
                    .put(getString(R.string.password), password);
            Log.d("Loggin!!!", newUser.toString());
            ApiCallLibrary sign_up = new ApiCallLibrary("http://www.utexas.io/login", newUser.toString());
            sign_up.post(this);
        }
        catch(JSONException e ) {
            Log.d("Loggin!!!", "JSON ERROR");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("Loggin!!!", "IO ERROR");
            e.printStackTrace();
        }
    }
    

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Invalid Username or Password", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    @Override
    public void onResponse(String response){
        if(response != null) {
            try {
                JSONObject result = new JSONObject(response);
                Log.d("LOGGING RESULT", result.toString());
                UserInfo.setCurrentUser(new UserInfo(result));
                if(result.has("tasks")){
                    UserInfo.addHabitsJson(result.getJSONArray("tasks"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            onLoginSuccess();
            progressDialog.dismiss();
        } else {
            onLoginFailed();
            progressDialog.dismiss();
        }
    }
}