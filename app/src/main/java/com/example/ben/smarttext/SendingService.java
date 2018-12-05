package com.example.ben.smarttext;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import android.content.Context;
import android.util.Log;

public class SendingService extends Service {
    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate(){
        this.context = this;
        this.isRunning= false;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        @Override
        public void run() {
            //do something here
            //TODO : call the sendSMS function
            String s = "";
            Log.i(s, "Another one");
            stopSelf();
        }
    };

    public void onDestroy(){
        this.isRunning = false;
    }

    public int onStartCommand(Intent intent, int flags, int statId){
        if(!this.isRunning){
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }



}
