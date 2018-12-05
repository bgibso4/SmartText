package com.example.ben.smarttext;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class SMSJobService extends JobService {
    private String phoneNumber;
    private String message;
    boolean jobCancelled = false;
    boolean isWorking = false;

    private static final int PERMISSION_REQUEST_SMS=0;
    private View mLayout;
    private Context context;

    private BroadcastReceiver sendBroadcastReceiver;
    private BroadcastReceiver deliveryBroadcastReceiver;
    String  SENT = "SMS_SENT";
    String  DELIVERED = "SMS_DELIVERED";


    public SMSJobService(Context c) {
        context = c;

    }

    @Override
    public boolean onStartJob(JobParameters params) {
        //send the sms here
        String[] json = params.getExtras().getStringArray("TextInfo");
//        Gson g =new Gson();
//        String j= g.toJson(json);
//        String phoneNum= j.["phoneNum"];
        isWorking = true;
        sendSMS(json[0], json[1]);
        //sendSMS("5555215554", "WELCOME to the jungle");
        return isWorking;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before being completed.");
        jobCancelled = true;
        boolean needsReschedule = isWorking;
        jobFinished(params, needsReschedule);
        //unregisterReceiver();
        return needsReschedule;
    }


    public void sendSMS(String phoneNumber, String message){
        String  SENT = "SMS_SENT";
        String  DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);
        //---when the SMS has been sent---
        context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);

        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
            Log.e("Message", "Message was not sent");
            //onRequestPermissionsResult(,Manifest.permission.SEND_SMS);
            requestMessageServices();

        }
        else{
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        }

//        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
//        Intent
//        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
//        if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
//            Log.e("Message", "Message was not sent");
//            //requestTextPermission();
//        }
//        else{
//            SmsManager sms = SmsManager.getDefault();
//            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
//        }
        //PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

    }
    public void requestMessageServices(){
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SMS);
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_SMS) {
            // Request for SMS permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Snackbar.make(mLayout, "Camera permission was granted. Starting preview.",
                        Snackbar.LENGTH_SHORT)
                        .show();
                sendSMS("5555215554", "Welcome to the jungle");
            } else {
                // Permission request was denied.
                Snackbar.make(mLayout, "Camera permission request was denied.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }
}
