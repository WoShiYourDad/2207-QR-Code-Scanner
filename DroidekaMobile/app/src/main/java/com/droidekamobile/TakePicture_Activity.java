package com.droidekamobile;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TakePicture_Activity extends AppCompatActivity{
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    private static FirebaseStorage mStorage = FirebaseStorage.getInstance();
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepicture);

        previewView = findViewById(R.id.previewView);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
                previewView.setVisibility(View.VISIBLE);
                previewView.setVisibility(View.INVISIBLE);
                capturePhoto();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, getExecutor());

    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {

        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        Preview preview = new Preview.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    private void capturePhoto() {
        if (imageCapture != null) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            getCacheDir();
            imageCapture.takePicture(new ImageCapture.OutputFileOptions.Builder(result).build(),
                    Executors.newSingleThreadExecutor(),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            Long timeStampLong = System.currentTimeMillis()/1000;
                            String timeStamp = timeStampLong.toString();
                            String dir = getCacheDir().getAbsolutePath();
                            String path = dir + "/" + timeStamp + ".png";
                            Uri pathUri = Uri.parse(path);
                            File cache = new File(path);
                            try (FileOutputStream stream = new FileOutputStream(cache)) {
                                stream.write(result.toByteArray());
//                                upload function here
                                sendImage(path);
//                                cache.delete();
                                finish();
                            } catch (Exception e) {
                                Log.e("TAG", "onImageSaved: Exception occurred", e);
                            }
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Log.i("TAG", "onError: ", exception);
                        }
                    });
        }
    }

    public void sendImage(String path) { //https://stackoverflow.com/questions/40885860/how-to-save-bitmap-to-firebase
        obtainUsername(username -> {
            Uri file = Uri.fromFile(new File(path));
            StorageReference imageRef = mStorage.getReference().child(username).child("Images").child("images/"+ UUID.randomUUID().toString());
            Log.e("HERE3", "HERE3");
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.e("HEREFAIL", "HEREFAIL");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = Uri.parse(taskSnapshot.getMetadata().getReference().getDownloadUrl().toString()); //https://stackoverflow.com/questions/50585334/tasksnapshot-getdownloadurl-method-not-working
                    Log.d("HERE", downloadUrl.toString());
                }
            });
        });
    }

    private void obtainUsername(FirebaseCallback firebaseCallback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Incase you do not understand this part: https://www.youtube.com/watch?v=OvDZVV5CbQg
        // This solves the asynchronous issue
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getEmail().equals(email)) {
                        String username = user.getUsername();
                        firebaseCallback.onCallback(username);
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, error.getMessage());
                return;
            }
        };
        mDatabase.addListenerForSingleValueEvent(valueEventListener);

        return;
    }

    //This is just needed to fight against the asynchronous issue when obtaining data from database
    private interface FirebaseCallback {
        void onCallback(String username);
    }
}