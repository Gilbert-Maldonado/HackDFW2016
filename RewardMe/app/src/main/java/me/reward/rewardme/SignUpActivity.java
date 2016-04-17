package me.reward.rewardme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class SignUpActivity extends AppCompatActivity implements Callback {
    private static final String TAG = "SignupActivity";

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);
//        final Intent intent = new Intent(this, MainActivity.class);
//        final String name = _nameText.getText().toString();
//        final String email = _emailText.getText().toString();
//        final String password = _passwordText.getText().toString();

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UserInfo.setCurrentUser(new UserInfo(name, email, password));
//                startActivity(intent);
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignUpActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.
        UserInfo.setCurrentUser(new UserInfo(name, email));
        try {
            JSONObject newUser = new JSONObject()
                    .put(getString(R.string.name), name)
                    .put(getString(R.string.email), email)
                    .put(getString(R.string.password), password);
            ApiCallLibrary sign_up = new ApiCallLibrary("http://www.utexas.io/register", newUser.toString());
            sign_up.post(this);
        }
        catch(JSONException e ) {
            Log.d("LOGGING!!!", "JSON ERROR");
            e.printStackTrace();
        } catch (IOException e){
            Log.d("LOGGING!!!", "I/O ERROR");
            e.printStackTrace();
        }


//        new android.os.Handler().postDelayed(
//                new Runnable() {
//                    public void run() {
//                        // On complete call either onSignupSuccess or onSignupFailed
//                        // depending on success
//
//                        // onSignupFailed();
//                        progressDialog.dismiss();
//                    }
//                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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
                UserInfo.setCurrentId(result.getString(getString(R.string.id)));
            } catch (JSONException e) {
                e.printStackTrace();
            }

//        Toast.makeText(getBaseContext(), "Sign-Up success!!!!!", Toast.LENGTH_SHORT).show();
            Log.d("LOGGING!!!", "success!!!!");
            progressDialog.dismiss();
            onSignupSuccess();
        }

    }



}