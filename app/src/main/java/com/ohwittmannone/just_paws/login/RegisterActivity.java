package com.ohwittmannone.just_paws.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ohwittmannone.just_paws.BaseCompatActivity;
import com.ohwittmannone.just_paws.R;
import com.ohwittmannone.just_paws.MainActivity;
import com.ohwittmannone.just_paws.models.User;
import com.ohwittmannone.just_paws.utils.Cache;

/**
 * Created by Courtney on 2017-02-24.
 */

public class RegisterActivity extends BaseCompatActivity {

    private EditText inputEmail, inputPassword, inputConfirmPassword, inputName;
    private Button btnCreateUser;
    private FirebaseAuth auth;
    private TextView linkLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputConfirmPassword = (EditText) findViewById(R.id.input_password_confirm);
        btnCreateUser = (Button) findViewById(R.id.btn_signup);
        linkLogin = (TextView) findViewById(R.id.link_login);

        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = inputName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    inputName.setError("Please enter your name");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validatePassword(inputPassword);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        inputConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validatePassword(inputConfirmPassword);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString();
                String confirmPassword = inputConfirmPassword.getText().toString();
                final String name = inputName.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Confirm password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(name)){
                    Toast.makeText(getApplicationContext(), "Enter your name", Toast.LENGTH_SHORT).show();
                }
                showProgress("Loading...");
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                hideProgress();
                                if(!task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "Authentication failed" + task.getException(), Toast.LENGTH_LONG).show();
                                }
                                else {
                                    //create entry in user
                                    createUserEntry(email, name);
                                    Cache.getInstance(getApplicationContext()).saveLoginState(true);
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),"Account was created", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createUserEntry(String email, String name) {
        User userModel = new User();
        userModel.setEmail(email);
        userModel.setName(name);
        userModel.setAdmin(false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(user.getUid());
        userModel.setId(user.getUid());
        reference.setValue(userModel);
    }

    private void validatePassword(EditText passwordInput) {
        String password = passwordInput.getText().toString();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
        }
    }



}
