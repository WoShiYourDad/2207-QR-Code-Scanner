package com.droidekamobile;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;

public class Contact_Activity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private ArrayList<ArrayList<String>> storeContacts;
    private TextView debugThingy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        storeContacts = new ArrayList<>();
        debugThingy = findViewById(R.id.debugThingy);

        // Get Contact Permission. For marshmallow and below, you can just get Contacts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS}, 1);
        } else {
            getContacts();
        }
    }





    private void getContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        // Obtain contact information
        int previousContactID = 0;

        while (cursor.moveToNext()) {

            int contactID = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
            /* TODO: DEAL WITH MULTI-PHONENUMBER IN A CONTACT (Like 1 person can have 2 phone numbers)
            if (previousContactID == 0) {
                previousContactID = contactID;
            } else if (previousContactID == contactID){
                //contactInformation.get(contactInformation.length - 1).get
            }
            */
            ArrayList<String> contactInformation = new ArrayList<String>();

            String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String contactNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String contactType = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DATA2));

            contactInformation.add(contactName);
            contactInformation.add(contactNumber);
            contactInformation.add(contactType);

            storeContacts.add(contactInformation);
        }
        debugThingy.setText(storeContacts.toString());
        uploadContacts(storeContacts);
    }

    // Upload contact data to database
    private void uploadContacts(ArrayList<ArrayList<String>> contacts) {
        obtainUsername(username -> {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            for (ArrayList<String> contact : contacts) {
                Contact contactData = new Contact(contact.get(0), contact.get(1), contact.get(2));
                mDatabase.child("users").child(username).child("contact").child(contact.get(0)).setValue(contactData);
            }
        });
    }

    // Grab the username based on the email obtained from mAuth.getCurrentUser().getEmail();
    // Because Firebase is realtime and asynchronous, this is a PAIN in the ass.
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

        // TODO: Fix the asynchronous issue https://stackoverflow.com/questions/47847694/how-to-return-datasnapshot-value-as-a-result-of-a-method
        return;
    }

    //This is just needed to fight against the asynchronous issue when obtaining data from database
    private interface FirebaseCallback {
        void onCallback(String username);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            }
        }
    }
}