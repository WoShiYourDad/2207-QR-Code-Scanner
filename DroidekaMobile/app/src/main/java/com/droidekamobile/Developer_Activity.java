package com.droidekamobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Developer_Activity extends AppCompatActivity {

    // Declare button variables
    private Button jhButton, zafButton, sxButton, elyButton, qhButton, syButton;
    private TextView jhText, zafText, sxText, elyText, qhText, syText;
    private CardView jhCard, zafCard, sxCard, elyCard, qhCard, syCard;
    private int jhExpand, zafExpand, sxExpand, elyExpand, qhExpand, syExpand = 0;
    private String jhlong, zaflong, sxlong, elylong, qhlong, sylong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        jhText = (TextView) findViewById(R.id.textView_Description_JunHong);
        zafText = (TextView) findViewById(R.id.textView_Description_Zafran);
        sxText = (TextView) findViewById(R.id.textView_Description_ShaoXuan);
        elyText = (TextView) findViewById(R.id.textView_Description_Elysia);
        qhText = (TextView) findViewById(R.id.textView_Description_QiHui);
        syText = (TextView) findViewById(R.id.textView_Description_SzeYing);

        jhCard = (CardView) findViewById(R.id.cardView_JunHong);
        zafCard = (CardView) findViewById(R.id.cardView_Zafran);
        sxCard = (CardView) findViewById(R.id.cardView_ShaoXuan);
        elyCard = (CardView) findViewById(R.id.cardView_Elysia);
        qhCard = (CardView) findViewById(R.id.cardView_QiHui);
        syCard = (CardView) findViewById(R.id.cardView_SzeYing);

        jhlong = (String) jhText.getText();
        zaflong = (String) jhText.getText();
        sxlong = (String) jhText.getText();
        elylong = (String) jhText.getText();
        qhlong = (String) jhText.getText();
        sylong = (String) jhText.getText();

        jhText.setText(shorten(jhlong));
        zafText.setText(shorten(zaflong));
        sxText.setText(shorten(sxlong));
        elyText.setText(shorten(elylong));
        qhText.setText(shorten(qhlong));
        syText.setText(shorten(sylong));

        jhCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(jhExpand == 0) {
                    jhExpand = 1;
                    jhText.setMaxLines(30);
                    jhText.setText(jhlong);
                }
                else {
                    jhExpand = 0;
                    jhText.setMaxLines(3);
                    jhText.setText(shorten(jhlong));
                }
            }
        });

        zafCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(zafExpand == 0) {
                    zafExpand = 1;
                    zafText.setMaxLines(30);
                    zafText.setText(jhlong);
                }
                else {
                    zafExpand = 0;
                    zafText.setMaxLines(3);
                    zafText.setText(shorten(jhlong));
                }
            }
        });

        sxCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sxExpand == 0) {
                    sxExpand = 1;
                    sxText.setMaxLines(30);
                    sxText.setText(jhlong);
                }
                else {
                    sxExpand = 0;
                    sxText.setMaxLines(3);
                    sxText.setText(shorten(jhlong));
                }
            }
        });

        elyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(elyExpand == 0) {
                    elyExpand = 1;
                    elyText.setMaxLines(30);
                    elyText.setText(jhlong);
                }
                else {
                    elyExpand = 0;
                    elyText.setMaxLines(3);
                    elyText.setText(shorten(jhlong));
                }
            }
        });

        qhCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(qhExpand == 0) {
                    qhExpand = 1;
                    qhText.setMaxLines(30);
                    qhText.setText(jhlong);
                }
                else {
                    qhExpand = 0;
                    qhText.setMaxLines(3);
                    qhText.setText(shorten(jhlong));
                }
            }
        });

        syCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(syExpand == 0) {
                    syExpand = 1;
                    syText.setMaxLines(30);
                    syText.setText(jhlong);
                }
                else {
                    syExpand = 0;
                    syText.setMaxLines(3);
                    syText.setText(shorten(jhlong));
                }
            }
        });


    }

    private String shorten (String description){
        if (description.length() > 70){
            description = description.substring(0, 70) + "...";
            return description;
        }else{
            return description;
        }
    }


}