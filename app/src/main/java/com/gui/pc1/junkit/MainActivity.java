package com.gui.pc1.junkit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=findViewById (R.id.start_button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openSignUpActivity();
            }
        });
    }

    public void openSignUpActivity(){
        Intent intent =  new Intent(this, SignUp.class);
        startActivity(intent);
    }


}
