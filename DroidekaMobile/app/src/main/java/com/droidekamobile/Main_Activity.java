package com.droidekamobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Main_Activity extends AppCompatActivity {

    // Declare button variables
    private Button scanButton, generateQRButton, historyButton, developerButton, contactButton, logoutButton;
    private FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            logout();
            //reload();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Bind variables to element ID on screen
        scanButton = (Button) findViewById(R.id.scanButton);
        generateQRButton = (Button) findViewById(R.id.generateQRButton);
        historyButton = (Button) findViewById(R.id.historyButton);
        developerButton = (Button) findViewById(R.id.developerButton);
        contactButton = (Button) findViewById(R.id.contactButton);
        logoutButton = (Button) findViewById(R.id.logoutButton);

        //Create Listeners to perform action onClick for the different buttons
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Main_Activity.this,ScanQR_Activity.class);
                startActivity(i);
            }
        });

        generateQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Main_Activity.this,GenerateQR_Activity.class);
                startActivity(i);
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Main_Activity.this, Contact_Activity.class);
                startActivity(i);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                finish();
            }
        });
    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();
    }



}