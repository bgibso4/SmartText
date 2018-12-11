package com.example.ben.smarttext;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import androidx.room.Room;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import androidx.annotation.Nullable;

import android.content.Context;
import android.util.Log;

import java.util.Date;
import java.util.List;

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
            AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, "messages")
                    .allowMainThreadQueries() //TODO get rid of main thread queries
                    .build();
            TextMessageDAO textMessageDAO = database.getTextMessageDAO();
            List<TextMessage> allMessages = textMessageDAO.getMessages();
            List<TextMessage> tempMessages = textMessageDAO.getMessages();
            int index = 0;
            for (TextMessage t: tempMessages) {
                if(t.getDate().before(new Date()) || t.getDate().equals(new Date())){
                    //TODO call the send messages function
                    t.sendMessage(context);
                    textMessageDAO.delete(t);
                }
            }
            //stopSelf();
        }
    };

    public void onDestroy(){

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent alarm = new Intent(this.context, MessageSenderRestartReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if(!alarmRunning) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            assert alarmManager != null;
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime(),  60000, pendingIntent);
        }
        sendBroadcast(alarm);
    }

    public int onStartCommand(Intent intent, int flags, int statId){
        if(!this.isRunning){
            this.isRunning = true;



        }
        myTask.run();
        //startTimer();
        return START_STICKY;
    }

}