package com.droidekamobile;

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

    ArrayList<ArrayList<String>> storeContacts;
    TextView debugThingy;

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

    private void uploadContacts(ArrayList<ArrayList<String>> contacts) {
        String username = obtainUsername();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        for (ArrayList<String> contact : contacts) {
            Contact contactData = new Contact(contact.get(0), contact.get(1), contact.get(2));
            mDatabase.child("users").child(username).child("contact").child(contact.get(0)).setValue(contactData);
        }
        // TODO: Retrieve owner's phone number - https://www.geeksforgeeks.org/how-to-obtain-the-phone-number-of-the-android-phone-programmatically/
    }

    private String obtainUsername() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String email = currentUser.getEmail();
        final String[] username = new String[1];

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    User user = dataSnapshot.getValue(User.class);
                    if (user.getEmail().equals(email)) {
                        username[0] = user.getUsername(); // Yeah, u cant just declare username. Dunno why
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Contact_Activity.this, "Shit.", Toast.LENGTH_SHORT).show();
            }
        });

        // TODO: Fix the asynchronous issue https://stackoverflow.com/questions/47847694/how-to-return-datasnapshot-value-as-a-result-of-a-method
        return "ministic2001";
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