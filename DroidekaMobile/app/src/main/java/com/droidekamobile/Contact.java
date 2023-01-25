package com.droidekamobile;

import java.util.ArrayList;
import java.util.regex.Pattern;


public class Contact {
    public String name;
    public ArrayList<ArrayList<String>> contactNumbers; // Example: [[9068 1333, MOBILE], [6583 1123, HOME]]
    // TODO: Convert contactNumbers to type ArrayList<ContactNumber>
    // I don't even know half of these labels.
    // Anyways, translation of ContactsContract.CommonDataKinds.Phone.DATA2 (as int) to phone labels (as String)
    // Documentation found here: https://stuff.mit.edu/afs/sipb/project/android/docs/reference/android/provider/ContactsContract.CommonDataKinds.Phone.html#TYPE_HOME
    // TODO: REFACTOR ContactNumber to be a inner class of Contact class https://stackoverflow.com/questions/42041531/save-array-of-object-to-firebase-without-getting-0-1-2-as-key
    enum ContactType {
        HOME,
        MOBILE,
        WORK,
        WORK_FAX,
        HOME_FAX,
        PAGER,
        OTHER,
        CALLBACK,
        CAR,
        COMPANY_MAIN,
        ISDN,
        MAIN,
        OTHER_FAX,
        RADIO,
        TELEX,
        TTY_TDD,
        WORK_MOBILE,
        WORK_PAGER,
        ASSISTANT,
        MMS
    }

    public Contact() {}

    public Contact(String name, ArrayList<ArrayList<String>> contactNumbers) {
        this.name = name;
        for (ArrayList<String> contactNumber : contactNumbers) {
            String contactType = contactNumber.get(1);
            // TODO: If contactType is already string (contactType = 0)-
            // This check is needed as it could be Custom label, which causes the contactType to already be set to something else
            // See here: https://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.BaseTypes#TYPE_CUSTOM
            if (isNumeric(contactType)) {
                contactNumber.set(1, ContactType.values()[Integer.parseInt(contactType) - 1].toString());
            }
        }
        this.contactNumbers = contactNumbers;
    }

    // Takes in a string and see if it's an integer, without the whole try catch.
    private boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        return pattern.matcher(strNum).matches();
    }
}