package com.droidekamobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Developer_Activity extends AppCompatActivity {

    // Declare button variables
    private Button jhButton, zafButton, sxButton, elyButton, qhButton, syButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        //Bind variables to element ID on screen
        jhButton = (Button) findViewById(R.id.jhButton);
        zafButton = (Button) findViewById(R.id.zafButton);
        sxButton = (Button) findViewById(R.id.sxButton);
        elyButton = (Button) findViewById(R.id.elyButton);
        qhButton = (Button) findViewById(R.id.qhButton);
        syButton = (Button) findViewById(R.id.syButton);

        //Create Listeners to perform action onClick for the different buttons
        jhButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Developer_Activity.this,Junhong_Activity.class);
                startActivity(i);
            }
        });
        zafButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Developer_Activity.this,Zafran_Activity.class);
                startActivity(i);
            }
        });
        sxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Developer_Activity.this,Shaoxuan_Activity.class);
                startActivity(i);
            }
        });
        elyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Developer_Activity.this,Elysia_Activity.class);
                startActivity(i);
            }
        });
        qhButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Developer_Activity.this,Qihui_Activity.class);
                startActivity(i);
            }
        });
        syButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Developer_Activity.this,Szeying_Activity.class);
                startActivity(i);
            }
        });


    }
}