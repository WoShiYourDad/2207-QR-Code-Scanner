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
import java.util.UUID;


public class GalleryExtractor extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    String photoDir;
    private boolean mSaved;
    FileInputStream fi;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        uploadGallery();
//        StorageReference photoref = storageRef.child("images"); // + /images?
//        StorageReference testimgref = storageRef.child(getGalleryPath() + "image1.jpg");
//        storageRef.getName().equals(testimgref.getName());
//        storageRef.getName().equals(testimgref.getPath());

    }

    //get images
    private String getGalleryPath() {
        return photoDir = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/" + "image1.jpg";
    }

    //upload images
    public void uploadGallery(){
        Log.d(ACTIVITY_SERVICE, getGalleryPath());
        Uri localFile = Uri.fromFile(new File("/storage/emulated/0/Download/-6224201455460527966_121.jpg")); //Change this part
        //Uri l = test(localFile);
        //Log.d(ACTIVITY_SERVICE, "This is test l : " + l.toString());
        //Uri sessionUri = null;
        UploadTask uploadTask;
        StorageReference photoref = storageRef.child("images/" + UUID.randomUUID().toString());
//        InputStream stream = new FileInputStream(new File("storage/14F1-3C1C/DCIM/image1.jpg"));
//        uploadTask = storageRef.putStream(stream);
        // start save before restart
        uploadTask = photoref.putFile(localFile);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Uri sessionUri = taskSnapshot.getUploadSessionUri();
                if (sessionUri != null && !mSaved) {
                    mSaved = true;
                    // A persisted session has begun with the server.
                    // Save this to persistent storage in case the process dies.
                }
            }
        });
        // [END save_before_restart]

        // [START restore_after_restart]
        //resume the upload task from where it left off when the process died.
        //to do this, pass the sessionUri as the last parameter
        //uploadTask = storageRef.putFile(localFile,
         //       new StorageMetadata.Builder().build(), sessionUri);
        // [END restore_after_restart]
    }

    public Uri test(Uri p) {
        String filePath = p.toString();
        File imageFile = new File(filePath);
        Cursor cursor = getApplicationContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver resolver = getContentResolver();
                    Uri picCollection = MediaStore.Images.Media
                            .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                    ContentValues picDetail = new ContentValues();
                    picDetail.put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.getName());
                    picDetail.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                    picDetail.put(MediaStore.Images.Media.RELATIVE_PATH,"DCIM/" + UUID.randomUUID().toString());
                    picDetail.put(MediaStore.Images.Media.IS_PENDING,1);
                    Uri finaluri = resolver.insert(picCollection, picDetail);
                    picDetail.clear();
                    picDetail.put(MediaStore.Images.Media.IS_PENDING, 0);
                    resolver.update(picCollection, picDetail, null, null);
                    return finaluri;
                }else {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, filePath);
                    return getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                }

            } else {
                return null;
            }
        }
    }


}
