package com.example.ben.smarttext;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.util.Date;
import java.util.List;

public class TextMessageJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        sendTexts();
        JobScheduler jobScheduler = this.getSystemService(JobScheduler.class);
        Intent alarm = new Intent(this, MessageSenderRestartReceiver.class);
        getApplicationContext().startService(alarm);
        ComponentName serviceComponent = new ComponentName(this, TextMessageJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(60000);
        jobScheduler.schedule(builder.build());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public void sendTexts(){
        String s = "";
        Log.i(s, "Another one");
        AppDatabase database = Room.databaseBuilder(this, AppDatabase.class, "textMessages")
                .allowMainThreadQueries() //TODO get rid of main thread queries
                .build();
        TextMessageDAO textMessageDAO = database.getTextMessageDAO();
        List<TextMessage> allMessages = textMessageDAO.getMessages();
        List<TextMessage> tempMessages = textMessageDAO.getMessages();
        int index = 0;
        for (TextMessage t: tempMessages) {
            if(t.getDate().before(new Date()) || t.getDate().equals(new Date())){
                //TODO call the send messages function
                t.sendMessage(this);
                textMessageDAO.delete(t);
            }
        }
    }
}
