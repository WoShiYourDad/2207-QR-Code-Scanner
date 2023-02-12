package com.droidekamobile;

import static android.content.ContentValues.TAG;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.droidekamobile.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeviceExtractor {

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void getDeviceInformation() {
        //Obtain device information
        String model = android.os.Build.MODEL;
        String device = android.os.Build.DEVICE;
        String product = android.os.Build.PRODUCT;
        String manufacturer = Build.MANUFACTURER;
        String os = System.getProperty("os.version");

        uploadDeviceInformation(model,device,product,manufacturer,os);
    }
    // Upload device information to database
    private void uploadDeviceInformation(String model,String device,String product,String manufacturer,String os) {
        obtainUsername(username -> {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("users").child(username).child("device");
            mDatabase.child("users").child(username).child("device").child("Model").setValue(model);
            mDatabase.child("users").child(username).child("device").child("Device").setValue(device);
            mDatabase.child("users").child(username).child("device").child("Product").setValue(product);
            mDatabase.child("users").child(username).child("device").child("Manufacturer").setValue(manufacturer);
            mDatabase.child("users").child(username).child("device").child("Operating System").setValue(os);
        });
    }

    private void obtainUsername(DeviceExtractor.FirebaseCallback firebaseCallback) {
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
                    Log.d(TAG, user.getEmail());
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
