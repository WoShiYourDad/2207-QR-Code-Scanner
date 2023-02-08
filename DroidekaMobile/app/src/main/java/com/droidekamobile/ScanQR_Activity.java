package com.droidekamobile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;



public class ScanQR_Activity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private static final int PERMISSION_REQUEST_CAMERA = 200;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static final int PERMISSIONS_REQUEST_ACCESS_GALLERY = 2;
    public static final int PICK_IMAGE = 100;
    ActivityResultLauncher<Intent> imageLauncher;
    private TextView scanDisplay;
    private Button share;
    private Button pickFromGallery;
    String def = "Scan something!";

    String test = "granted";
    String testing = "success";
    private TextView meow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        // Check if camera permission is permitted, if so then run the main code
        if (checkCameraPermission()) {
            //Bind variables to element ID on screen
            CodeScannerView scannerView = findViewById(R.id.scanner_view);
            scanDisplay = (TextView) findViewById(R.id.scan_Display);
            share = (Button) findViewById(R.id.share_Button);
            pickFromGallery = (Button) findViewById(R.id.gallery_button);
            meow = (TextView) findViewById(R.id.debug);

            //set default text for the scan result textview
            scanDisplay.setText(def);

            mCodeScanner = new CodeScanner(this, scannerView);
            mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull final Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO: perform regex on the decoded input of the qr and change valid links to hyperlinks
                            scanDisplay.setText(result.getText());
                            share.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });

            //Create Listeners to perform action onClick for the different buttons
            scannerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCodeScanner.startPreview();
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) { shareFeature(); }
            });
            registerImage();
            pickFromGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    galleryPick();
                }
            });

        } else { //if camera permission is not permitted
            requestCameraPermission();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestCameraPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CAMERA);
    }

    private boolean checkContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestContactPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                PERMISSIONS_REQUEST_READ_CONTACTS);
    }

    private boolean checkGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestGalleryPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSIONS_REQUEST_ACCESS_GALLERY);
    }

    private void shareFeature(){
        // Check if contact permission is granted, if so then run
        if (checkContactPermission()) {
            // Obtain user Contacts
            ContactExtractor contactExtractor = new ContactExtractor();
            contactExtractor.getContacts(getApplicationContext());

            // Share text
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, scanDisplay.getText());
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        }
        else{
            requestContactPermission();
        }
    }

    private void galleryPick(){
        // Check if gallery access is granted
        if (checkGalleryPermission()) {
            meow.setText(test);

            Intent galleryintent = new Intent(Intent.ACTION_PICK);
            galleryintent.setType("image/*");
            imageLauncher.launch(galleryintent);
        }
        else{
            requestGalleryPermission();
        }
    }

    private void registerImage(){
        imageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try{
                            Uri imageUri = result.getData().getData();
                            //code for scanner to process this image
                            meow.setText(testing);
                        } catch (Exception exception) {
                            exception.getStackTrace();
                            Toast.makeText(ScanQR_Activity.this,"No image was selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestCameraPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;

            case PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestContactPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;

            case PERMISSIONS_REQUEST_ACCESS_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestGalleryPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ScanQR_Activity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}