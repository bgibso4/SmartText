package com.example.ben.smarttext;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppDatabase database = Room.databaseBuilder(this, AppDatabase.class, "db-test")
                .allowMainThreadQueries() //TODO get rid of main thread queries
                .build();

        TextMessageDAO textMessageDAO = database.getTextMessageDAO();
//
//        TextMessage textMessage = new TextMessage();
//        textMessage.setUid(2);
//        textMessage.setName("Bengy");
//        textMessage.setMessage("Welcome to the jungle");
//        textMessage.setPhoneNumber("5197028412");
//        textMessage.setDate(new Date());
//
//        textMessageDAO.insert(textMessage);

        List<TextMessage> texts = textMessageDAO.getMessages();

        FloatingActionButton createTextBtn= findViewById(R.id.createTextBtn);
        this.context = this;
        Intent alarm = new Intent(this.context, MessageSenderRestartReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if(alarmRunning == false) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1800, pendingIntent);
        }

        createTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateNewText.class));
            }
        });


    }
}
