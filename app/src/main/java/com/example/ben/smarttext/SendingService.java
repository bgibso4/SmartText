package com.example.ben.smarttext;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import androidx.core.app.NotificationCompat;
import androidx.room.Room;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import androidx.annotation.Nullable;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;
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

    @Override
    public void onCreate(){
        this.context = this;
        this.isRunning= false;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Sending Text Message")
                    .setContentText("Your delayed text is being sent").build();

            startForeground(1, notification);
            sendTexts();
        }
        //this.backgroundThread = new Thread(myTask);
    }

//    private Runnable myTask = new Runnable() {
//        @Override
//        public void run() {
//            //do something here
//            //TODO : call the sendSMS function
//            String s = "";
//            Log.i(s, "Another one");
//            AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, "messages")
//                    .allowMainThreadQueries() //TODO get rid of main thread queries
//                    .build();
//            TextMessageDAO textMessageDAO = database.getTextMessageDAO();
//            List<TextMessage> allMessages = textMessageDAO.getMessages();
//            List<TextMessage> tempMessages = textMessageDAO.getMessages();
//            int index = 0;
//            for (TextMessage t: tempMessages) {
//                if(t.getDate().before(new Date()) || t.getDate().equals(new Date())){
//                    //TODO call the send messages function
//                    t.sendMessage(context);
//                    textMessageDAO.delete(t);
//                }
//            }
//            //stopSelf();
//        }
//    };

    private void sendTexts(){
        String s = "";
        Log.i(s, "Another one");
        AppDatabase database = Room.databaseBuilder(context, AppDatabase.class, "messages")
                .allowMainThreadQueries() //TODO get rid of main thread queries
                .build();
        TextMessageDAO textMessageDAO = database.getTextMessageDAO();
        List<TextMessage> allMessages = textMessageDAO.getMessages();

        int index = 0;
        for (TextMessage t: allMessages) {
            if(t.getDate().before(new Date()) || t.getDate().equals(new Date())){
                //TODO call the send messages function
                t.sendMessage(context);
                textMessageDAO.delete(t);
                TextMessage nextMessage = textMessageDAO.getNextText();

                Calendar cal = Calendar.getInstance();
                cal.setTime(nextMessage.getDate());
                Intent alarm = new Intent(context, MessageSenderRestartReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarm, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
            }
        }
        stopSelf();
    }

    public void onDestroy(){

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        Intent alarm = new Intent(this.context, MessageSenderRestartReceiver.class);
//        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
//        if(!alarmRunning) {
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarm, 0);
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            assert alarmManager != null;
//            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime(),  60000, pendingIntent);
//        }
//        sendBroadcast(alarm);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(!this.isRunning){
            this.isRunning = true;



        }
        sendTexts();
        //myTask.run();
        //startTimer();
        return START_STICKY;
    }

}