package com.gui.pc1.junkit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.signup_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(firebaseAuth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(this, BaseActivity.class));
        }
    }

    public void onClick (View view){
        switch (view.getId()){
            case R.id.signup_button:
                finish();
                startActivity(new Intent(this, SignUp.class));
                break;
            case R.id.login_button:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }

}
