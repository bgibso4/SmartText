package com.gmf.dev;

import androidx.annotation.NonNull;

public class Contact {

    public String name;
    public String phoneNumber;

    Contact(@NonNull String name, @NonNull String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    Contact(@NonNull Contact contact){
        this.name = contact.getName();
        this.phoneNumber = contact.getPhoneNumber();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)  {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
