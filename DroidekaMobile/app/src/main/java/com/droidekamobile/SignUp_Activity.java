package com.droidekamobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUp_Activity extends AppCompatActivity {

    //Declare variables for elements on screen
    private Button signUpButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Bind variables to element ID on screen
        signUpButton = (Button) findViewById(R.id.signup_button);
        backButton = (Button) findViewById(R.id.back_button);

        //Create Listeners to perform action onClick
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginActivity();
            }
        });
    }

    public void openMainActivity(){
        Intent intent = new Intent(this, Main_Activity.class);
        startActivity(intent);
    }

    public void openLoginActivity(){
        //Destroys itself and returns to previous Login_Activity
        finish();
    }
}