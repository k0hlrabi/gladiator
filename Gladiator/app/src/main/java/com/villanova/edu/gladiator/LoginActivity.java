package com.villanova.edu.gladiator;

import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import butterknife.ButterKnife;
import butterknife.Bind;
/**
 * Created by wildcat on 4/13/2016.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final String prefsKey = "TheGladiatorApp";

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Firebase.setAndroidContext(this);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
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

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        final String[] userName = {" "};
        final String[] team = {" "};

        // TODO: Implement your own authentication logic here.
        final Firebase ref = new Firebase("https://blistering-fire-747.firebaseio.com/");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());// 0 - for private mode
         final SharedPreferences.Editor editor = pref.edit();
        ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
             ref.child("users").addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {
                     for (DataSnapshot a : dataSnapshot.getChildren()) {
                         if (a.child("email").getValue().toString().contains(email)) {
                             userName[0] = a.getKey();
                             Log.d("DEBUG", "FOUND USERNAME" + a.getKey());
                             break;
                         }
                     }
                 }

                 @Override
                 public void onCancelled(FirebaseError firebaseError) {

                 }
             });
                //Find the user's team
                ref.child("Teams").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //GO through each team
                        for (DataSnapshot sshot : dataSnapshot.getChildren()) {
                            //Go through each datapoint in team
                            for (DataSnapshot sshot2 : sshot.child("Players").getChildren()) {
                                if (sshot2.getKey().contains(userName[0])) {
                                    team[0] = sshot.child("Info").child("Name").getValue().toString();
                                    Log.d("DEBUG", "FOUND USERS TEAM" + sshot.child("Info").child("Name").getValue().toString());
                                    break;
                                }
                            }


                        }
                        editor.putString("User", userName[0]);
                        editor.putString("Team", team[0]);
                        editor.apply();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });




            }



            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // there was an error
            }

        });

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {

        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

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
}
