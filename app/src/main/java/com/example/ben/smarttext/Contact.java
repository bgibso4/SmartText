package com.example.ben.smarttext;

import android.support.annotation.NonNull;

public class Contact {

    public String name;
    public String phoneNumber;

    Contact(@NonNull String name, @NonNull String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
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
