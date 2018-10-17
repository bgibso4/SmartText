package com.example.ben.smarttext;

import java.util.Comparator;

public class ContactsComparator implements Comparator<Contact> {
    @Override
    public int compare(Contact contact, Contact t1) {
        return contact.getName().toUpperCase().compareTo(t1.getName().toUpperCase());
    }
}
