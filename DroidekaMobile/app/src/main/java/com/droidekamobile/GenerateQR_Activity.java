package com.droidekamobile;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.WriterException;

import java.io.Writer;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GenerateQR_Activity extends AppCompatActivity {

    // Declare button variables
    private TextView qrCodeTV;
    private ImageView qrCodeIV;
    private TextInputEditText dataEdit;
    private Button generateQRBtn;
    private QRGEncoder qrgEncoder;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        //Bind variables to element ID on screen
        qrCodeTV = findViewById(R.id.idTVGenerateQR);
        qrCodeIV = findViewById(R.id.idIVQRCode);
        dataEdit = findViewById(R.id.idEditData);
        generateQRBtn = findViewById(R.id.idBtnGenerateQR);

        //Create Listeners to perform action onClick for the different buttons
        generateQRBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String data = dataEdit.getText().toString();
                if(data.isEmpty()){
                    Toast.makeText(GenerateQR_Activity.this,"Please enter some data to generate QR Code...", Toast.LENGTH_SHORT).show();
                }else{
                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                    Display display = manager.getDefaultDisplay();
                    Point point = new Point();
                    display.getSize(point);
                    int width = point.x;
                    int height = point.y;
                    int dimen = width<height ? width:height;
                    dimen = dimen * 3/4;

                    qrgEncoder = new QRGEncoder(dataEdit.getText().toString(),null, QRGContents.Type.TEXT,dimen);
                    qrgEncoder.setColorBlack(Color.WHITE);
                    qrgEncoder.setColorWhite(Color.BLACK);
                    bitmap = qrgEncoder.getBitmap();
                    qrCodeTV.setVisibility(View.GONE);
                    qrCodeIV.setImageBitmap(bitmap);
                }
            }
        });
    }
}