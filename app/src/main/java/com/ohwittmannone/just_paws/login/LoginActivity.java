package com.ohwittmannone.just_paws.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ohwittmannone.just_paws.BaseCompatActivity;
import com.ohwittmannone.just_paws.R;
import com.ohwittmannone.just_paws.MainActivity;
import com.ohwittmannone.just_paws.utils.Cache;

/**
 * Created by Courtney on 2017-02-25.
 */

public class LoginActivity extends BaseCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private String TAG = "SIGN_IN";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = getApplicationContext();
        Button btnLogin;
        ImageView back;
        TextView linkRegister, linkPasswordReset;
        final EditText inputEmail, inputPassword;

        btnLogin = (Button) findViewById(R.id.btn_login);
        back = (ImageView) findViewById(R.id.back_btn);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        linkRegister = (TextView) findViewById(R.id.link_register);
        linkPasswordReset = (TextView) findViewById(R.id.link_pw_reset);

        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged: signed_in" + user.getUid());
                }
                else{
                    Log.d(TAG, "onAuthStateChanged: signed_in");
                }
            }

        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString();
                loginUser(email, password);


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        linkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        linkPasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PasswordResetActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if (authListener != null){
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void loginUser(final String email, String password){
        if(email == null || email.matches("")){
            Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
        }
        else if (password == null || password.matches("")){
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
        }
        else {
            showProgress("Logging in...");
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithEmail:failed", task.getException());
                        Toast.makeText(LoginActivity.this, "Login failed, try again",
                                Toast.LENGTH_SHORT).show();
                    }
                    Cache.getInstance(getApplicationContext()).saveLoginState(true);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    hideProgress();
                    startActivity(intent);
                }
            });
        }
    }
}
