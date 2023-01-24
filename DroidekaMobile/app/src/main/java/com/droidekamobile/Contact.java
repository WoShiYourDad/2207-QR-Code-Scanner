package com.droidekamobile;

import java.util.ArrayList;

public class Contact {
    public String name;
    public ArrayList<ArrayList<String>> contactNumbers; // Example: [[9068 1333, MOBILE], [6583 1123, HOME]]
    // TODO: Convert contactNumbers to type ArrayList<ContactNumber>
    // I don't even know half of these labels.
    // Anyways, translation of ContactsContract.CommonDataKinds.Phone.DATA2 (as int) to phone labels (as String)
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
            contactNumber.set(1, ContactType.values()[Integer.parseInt(contactType) - 1].toString());
        }
        this.contactNumbers = contactNumbers;
    }
}