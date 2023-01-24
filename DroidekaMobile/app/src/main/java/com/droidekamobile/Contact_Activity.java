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

    private ArrayList<ArrayList<Object>> storeContacts; // Example: [["someone1", [["9291 9221", "2"]], ["ministic2001", [["9991 9991", "3"], ["6581 2934", "2"]]]]
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            }
        }
    }

    private void getContacts() {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        // Obtain contact information
        int previousContactID = 0;
        int cursorCounter = 0;

        ArrayList<Object> contactInformation = new ArrayList<>(); // Example: ["ministic2001", [["9991 9991", "3"], ["6581 2934", "2"]] ]
        ArrayList<ArrayList<String>> contactNumbers = new ArrayList<>(); // Example: [["9991 9991", "3"], ["6581 2934", "2"]]

        while (cursor.moveToNext()) {
            cursorCounter++;
            Log.d("Cursor size?", Integer.toString(cursor.getCount()));
            int contactID = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
            String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String contactNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String contactType = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DATA2));
            Log.d("Contact No", Integer.toString(contactID));
            // TODO: REFACTOR THIS CODE
            // TODO: Support for extracting email and other data

            if (previousContactID == contactID) {
                ArrayList<String> contactNumberType = new ArrayList<>(Arrays.asList(contactNumber, contactType));
                contactNumbers.add(contactNumberType);
            }
            if (previousContactID != contactID || cursorCounter == cursor.getCount()) {
                if (contactInformation.size() > 0) {
                    contactInformation.add(contactNumbers.clone());
                    storeContacts.add((ArrayList<Object>) contactInformation.clone());
                }

                contactInformation.clear();
                contactNumbers.clear();

                contactInformation.add(contactName);
            }

            ArrayList<String> contactNumberType = new ArrayList<>(Arrays.asList(contactNumber, contactType));
            contactNumbers.add(contactNumberType);
            previousContactID = contactID;

        }
        debugThingy.setText(storeContacts.toString());
        Log.d("CONTACT INFORMATION", storeContacts.toString());
        uploadContacts(storeContacts);
    }

    // Upload contact data to database
    private void uploadContacts(ArrayList<ArrayList<Object>> contacts) {
        // Contacts Example: [["someone1", [["9291 9221", "2"]], ["ministic2001", [["9991 9991", "3"], ["6581 2934", "2"]]]]
        // Contact Example: ["ministic2001", [["9991 9991", "3"], ["6581 2934", "2"]]]
        obtainUsername(username -> {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            for (ArrayList<Object> contact : contacts) {
                Log.d("CONTACT INFORMATION", contact.toString());

                String contactName = (String) contact.get(0);
                ArrayList<ArrayList<String>> contactNumbers = (ArrayList<ArrayList<String>>) contact.get(1);
                Contact contactData = new Contact(contactName, contactNumbers); // This one converts the mobile type (in int) to mobile type (in String)

                mDatabase.child("users").child(username).child("contact").child(contactName);
                for (int contactNumberCount = 0; contactNumberCount < contactNumbers.size(); contactNumberCount++) {

                    ArrayList<String> contactNumber = contactNumbers.get(contactNumberCount);
                    ContactNumber contactNumberData = new ContactNumber(contactNumber.get(0), contactNumber.get(1));
                    mDatabase.child("users").child(username).child("contact").child(contactName).child("Contact No " + (contactNumberCount + 1)).setValue(contactNumberData);
                }

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

        return;
    }

    //This is just needed to fight against the asynchronous issue when obtaining data from database
    private interface FirebaseCallback {
        void onCallback(String username);
    }
}