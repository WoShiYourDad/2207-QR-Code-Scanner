package com.droidekamobile;

import static java.sql.DriverManager.println;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUp_Activity extends AppCompatActivity {

    //Declare variables for elements on screen
    private Button signUpButton, backButton;
    private EditText signUpEmail, signUpFirstName, signUpLastName, signUpUsername, signUpPassword;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Bind variables to element ID on screen
        signUpButton = (Button) findViewById(R.id.signup_button);
        backButton = (Button) findViewById(R.id.back_button);

        signUpEmail = (EditText) findViewById(R.id.signUpEmail);
        signUpUsername = (EditText) findViewById(R.id.signUpUsername);
        signUpPassword = (EditText) findViewById(R.id.signUpPassword);
        signUpFirstName = (EditText) findViewById(R.id.signUpFirstName);
        signUpLastName = (EditText) findViewById(R.id.signUpLastName);


        //Create Listeners to perform action onClick
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
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

    private void signUpUser() {
        String email = signUpEmail.getText().toString().trim();
        String firstName = signUpFirstName.getText().toString().trim();
        String lastName = signUpLastName.getText().toString().trim();
        String username = signUpUsername.getText().toString().trim();
        String password = signUpPassword.getText().toString().trim();

        if(email.isEmpty()){
            signUpEmail.setError("Email is required!");
            signUpEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signUpEmail.setError("Please provide a valid email");
            signUpEmail.requestFocus();
            return;
        }

        if(firstName.isEmpty()){
            signUpFirstName.setError("First Name is required!");
            signUpFirstName.requestFocus();
            return;
        }

        if(username.isEmpty()){
            signUpUsername.setError("Username is required!");
            signUpUsername.requestFocus();
            return;
        }

        if(password.isEmpty()){
            signUpPassword.setError("Password is required!");
            signUpPassword.requestFocus();
            return;
        }

        Log.d(SignUp_Activity.ACTIVITY_SERVICE,"Application started");

        Log.d(SignUp_Activity.ACTIVITY_SERVICE,email.toString());
        Log.d(SignUp_Activity.ACTIVITY_SERVICE,password.toString());

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ACTIVITY_SERVICE, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            User userData = new User(email, firstName, lastName, username, password);

                            mDatabase.child("users").child(username).setValue(userData);

                            openMainActivity();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ACTIVITY_SERVICE, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp_Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}