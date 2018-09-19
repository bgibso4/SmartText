package com.example.ben.smarttext;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "TextMessages")
public class TextMessage {
    @PrimaryKey
    private int uid;

    private String phoneNumber;
    private String message;
    private String name;
    private Date date;

    public int getUid(){
        return uid;
    }

    public void setUid(int id){
        this.uid = id;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNum){
        this.phoneNumber = phoneNum;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String msg){
        this.message = msg;
    }

    public String getName(){
        return name;
    }

    public void setName(String n){
        this.name = n;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date d){
        this.date = d;
    }



}
