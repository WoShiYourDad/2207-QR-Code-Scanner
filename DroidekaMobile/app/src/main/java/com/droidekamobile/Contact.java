package com.droidekamobile;

import java.util.ArrayList;

public class Contact {
    public String name, number, type;

    // TODO: Multipe-Contacts and Emails
    //public ArrayList<ArrayList<String>> contact_number; // Example: [[9068 1333, MOBILE], [6583 1123, HOME]]

    // I don't even know half of these labels.
    // Anyways, translation of ContactsContract.CommonDataKinds.Phone.DATA2 (as int) to phone labels (as String)
    enum ContactType {
        MOBILE,
        HOME,
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

    public Contact(String name, String number, String type) {
        this.name = name;
        this.number = number;
        this.type = ContactType.values()[Integer.parseInt(type)].toString();
    }
}


