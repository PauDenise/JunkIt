package com.gui.pc1.junkit;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView TextViewLogin;

    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressbar);
        buttonRegister = findViewById(R.id.buttonRegister);
        editTextUsername = findViewById(R.id.EditTextUsername);
        editTextEmail = findViewById(R.id.EditTextEmail);
        editTextPassword = findViewById(R.id.EditTextPassword);
        TextViewLogin = findViewById(R.id.textViewLogin);

        buttonRegister.setOnClickListener(this);
        TextViewLogin.setOnClickListener(this);
    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            //email is empty
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            Toast.makeText(this, "Please Enter Email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email.");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            //password is empty
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            Toast.makeText(this, "Please Enter Password.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length()<6){
            editTextPassword.setError("Minimum length of password should be 6 characters.");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            //user is successfully registered and logged in.
                            //start profile activity
                            Toast.makeText(SignUp.this,"Registered Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),BaseActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }else {
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(SignUp.this,"You are already registered.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(SignUp.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonRegister:
                registerUser();
                break;
            case R.id.textViewLogin:
                startActivity(new Intent(this, LoginActivity.class));
        }

    }
}
