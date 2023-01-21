package com.droidekamobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Activity extends AppCompatActivity {

    //Declare variables for elements on screen
    private Button loginButton, signUpButton;
    private EditText loginEmail, loginPassword;

    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            openMainActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Bind variables to element ID on screen
        loginButton = (Button) findViewById(R.id.login_button);
        signUpButton = (Button) findViewById(R.id.signup_button);

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);

        mAuth = FirebaseAuth.getInstance();


        //Create Listeners to perform action onClick
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignUpActivity();
            }
        });
    }

    public void openMainActivity(){
        Intent intent = new Intent(this, Main_Activity.class);
        startActivity(intent);
    }

    public void openSignUpActivity(){
        Intent intent = new Intent(this, SignUp_Activity.class);
        startActivity(intent);
    }

    public void loginUser(){
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if(email.isEmpty()){
            loginEmail.setError("Email is required!");
            loginEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            loginEmail.setError("Please provide a valid email");
            loginEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            loginPassword.setError("Password required!");
            loginPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ACTIVITY_SERVICE, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            openMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ACTIVITY_SERVICE, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login_Activity.this, "Invalid Username or Password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}