package com.ohwittmannone.just_paws.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.ohwittmannone.just_paws.BaseCompatActivity;
import com.ohwittmannone.just_paws.R;

/**
 * Created by Courtney on 2017-02-27.
 */

public class PasswordResetActivity extends BaseCompatActivity {

    private EditText inputEmailResetPw;
    private ImageView back;
    private Button btnReset;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        auth = FirebaseAuth.getInstance();

        back = (ImageView) findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        inputEmailResetPw = (EditText) findViewById(R.id.input_email_reset);

        btnReset = (Button) findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress("Loading...");
                String email = inputEmailResetPw.getText().toString().trim();
                if(!email.equals("")){
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PasswordResetActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                                hideProgress();
                                Intent intent = new Intent(PasswordResetActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(PasswordResetActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
                                hideProgress();
                            }
                        }
                    });

                }
                else {
                    inputEmailResetPw.setError("Enter email");
                    hideProgress();
                }

            }
        });


    }
}
