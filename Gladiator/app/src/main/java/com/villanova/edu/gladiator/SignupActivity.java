package com.villanova.edu.gladiator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Bind;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;
    boolean goodSignUp = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                if(goodSignUp) {
                    finish();
                }else {
                    onSignupFailed();
                }
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
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();
        final String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        // TODO: Implement your own signup logic here.
        final Firebase ref = new Firebase("https://blistering-fire-747.firebaseio.com/");
        ref.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                ref.child("users").child(name).child("email").setValue(email);
            }
            @Override
            public void onError(FirebaseError firebaseError) {
            }
        });

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                            onSignupSuccess();
                            //onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        final String[] userName = {" "};
        final String[] team = {" "};
        final Firebase ref = new Firebase("https://blistering-fire-747.firebaseio.com/");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());// 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();
        ref.child("Teams").addListenerForSingleValueEvent(new ValueEventListener() {
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

            finish();
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
}
