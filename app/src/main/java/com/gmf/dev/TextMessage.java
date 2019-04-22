package com.gmf.dev;

import android.app.PendingIntent;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import android.content.Context;
import android.content.Intent;

import android.telephony.SmsManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Entity(tableName = "TextMessages")
public class TextMessage implements Comparable<TextMessage>{
    @PrimaryKey
    private UUID uid;
    private String phoneNumber;
    private String message;
    private String name;
    private Date date;
    private String recipientImage;

    private static final int PERMISSION_REQUEST_SMS=0;
    String  SENT = "SMS_SENT";
    String  DELIVERED = "SMS_DELIVERED";



    public UUID getUid(){
        return uid;
    }

    public void setUid(UUID id){
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

    public String getRecipientImage() {return this.recipientImage;}

    public void setRecipientImage(String image){this.recipientImage = image;}

    public Date getDate(){
        return date;
    }
    public String timeAway(){
        String timeAwayText;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        long x = (date.getTime()- System.currentTimeMillis());
        long y = TimeUnit.DAYS.toMillis(1);
        if(x>y ){
            timeAwayText = dateFormat.format(date);
        }
        else{
            timeAwayText = timeFormat.format(date);
        }
        return timeAwayText;
    }

    public void setDate(Date d){
        this.date = d;
    }

    public void sendMessage(Context context){

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    @Override
    public int compareTo(@NonNull TextMessage otherMessage) {
        return (this.getDate().compareTo(otherMessage.getDate()));
    }
}

