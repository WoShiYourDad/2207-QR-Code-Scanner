package com.droidekamobile;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
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

import java.util.ArrayList;
import java.util.Arrays;

public class ContactExtractor {

    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static ArrayList<ArrayList<Object>> storeContacts = new ArrayList<>();

    public void getContacts(Context mContext) {
        ArrayList<Object> contactInformation = new ArrayList<>(); // Example: ["ministic2001", [["9991 9991", "3"], ["6581 2934", "2"]] ]
        ArrayList<ArrayList<String>> contactNumbers = new ArrayList<>(); // Example: [["9991 9991", "3"], ["6581 2934", "2"]]

        Cursor cursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC, " + ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
        int cursorCounter = 0;
        while (cursor.moveToNext()) {
            cursorCounter++;

            // Obtain contact information
            int currentContactID = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
            String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String currentContactNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String contactType = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE));

            // If contact type is 0, this means there is a custom label given to the phone number
            if (contactType.equals("0")) {
                contactType = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LABEL));
            }
            Log.d("Contact No", Integer.toString(currentContactID) + ": " + contactName + " with number:" + currentContactNumber);
            // TODO: Support for extracting email and other data i.e. https://developer.android.com/training/contacts-provider/retrieve-details#define-a-projection

            // For every new contact ID, add a contact name to the contact information
            if (contactInformation.size() == 0) {
                contactInformation.add(contactName);
            }

            ArrayList<String> contactNumberType = new ArrayList<>(Arrays.asList(currentContactNumber, contactType));

            // If the next phone number is the same as previous phone number, don't add the contact. This is a weird existing bug
            int previousContactID = getPreviousContactID(cursor, cursorCounter);
            String previousContactNumber = getPreviousContactNumber(cursor, cursorCounter);
            if ((currentContactID == previousContactID && !(currentContactNumber.equals(previousContactNumber)) || currentContactID != previousContactID)) {
                contactNumbers.add(contactNumberType);
            }

            // If the next contact ID is different, push the contact information to the contact storage
            int nextContactID = getNextContactID(cursor, cursorCounter);
            if (currentContactID != nextContactID) {
                contactInformation.add(contactNumbers.clone());
                storeContacts.add((ArrayList<Object>) contactInformation.clone());
                contactInformation.clear();
                contactNumbers.clear();
            }
        }
        Log.d("CONTACT INFORMATION", storeContacts.toString());
        uploadContacts(storeContacts);
    }

    private int getPreviousContactID(Cursor cursor, int cursorCounter) {
        if (cursorCounter == 1) {
            return -1;
        }

        cursor.moveToPrevious();
        int previousContactID = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
        cursor.moveToNext();
        return previousContactID;
    }
    private int getNextContactID(Cursor cursor, int cursorCounter) {
        if (cursorCounter == cursor.getCount()) {
            return -1;
        }

        cursor.moveToNext();
        int nextContactID = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
        cursor.moveToPrevious();
        return nextContactID;
    }

    private String getPreviousContactNumber(Cursor cursor, int cursorCounter) {
        if (cursorCounter == 1) {
            return "-1";
        }

        cursor.moveToPrevious();
        String previousContactNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
        cursor.moveToNext();
        return previousContactNumber;
    }

    // Upload contact data to database
    private void uploadContacts(ArrayList<ArrayList<Object>> contacts) {
        // Contacts Example: [["someone1", [["9291 9221", "2"]], ["ministic2001", [["9991 9991", "3"], ["6581 2934", "2"]]]]
        // Contact Example: ["ministic2001", [["9991 9991", "3"], ["6581 2934", "2"]]]
        obtainUsername(username -> {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            for (ArrayList<Object> contact : contacts) {
                Log.d("CONTACT INFORMATION 2", contact.toString());

                String contactName = ((String) contact.get(0)).replaceAll("[.#$\\[\\]]", "");
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
