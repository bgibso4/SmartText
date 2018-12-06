package com.example.ben.smarttext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Context context;
    AppDatabase database;
    RecyclerView pendingMessageView;
    LinearLayoutManager messageLayoutManager;
    MessageLayoutAdapter messageAdapter;
    SharedPreferences pref;
    private List<Contact> contacts;
    private boolean contactsPermissonCheck;


    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @SuppressLint("ShortAlarm")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        this.contactsPermissonCheck = false;

        database = Room.databaseBuilder(this, AppDatabase.class, "textMessages")
                .allowMainThreadQueries() //TODO get rid of main thread queries
                .build();
        TextMessageDAO textMessageDAO = database.getTextMessageDAO();
        List<TextMessage> texts = textMessageDAO.getMessages();
        texts.sort(TextMessage::compareTo);
        TextView pendingMessageTitle = this.findViewById(R.id.pendingMessageTitle);
        pendingMessageTitle.setText("Pending Messages ("+texts.size()+")");


        pendingMessageView = findViewById(R.id.pendingMessageList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //pendingMessageView.setHasFixedSize(true);


        // use a linear layout manager
        messageLayoutManager = new LinearLayoutManager(this);

        pendingMessageView.setLayoutManager(messageLayoutManager);

        // specify an adapter (see also next example)
        messageAdapter = new MessageLayoutAdapter(texts);
        pendingMessageView.setAdapter(messageAdapter);


        FloatingActionButton createTextBtn= findViewById(R.id.createTextBtn);


        Intent alarm = new Intent(this.context, MessageSenderRestartReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if(!alarmRunning) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1800, pendingIntent);
        }

        createTextBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CreateNewText.class)));


        //creating a thread to run the table queries in the background
        (new Thread(){
            public void run(){
                //Creating shared preferences
                Looper.prepare();
                while(!contactsPermissonCheck){
                    showContacts();
                }

            }
        }).start();



        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    try {
                        SendTexts();
                        messageAdapter.notifyDataSetChanged();
                        pendingMessageTitle.setText("Pending Messages ("+texts.size()+")");
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        throw new NullPointerException(e.getMessage());
                    }
                });

            }
        };
        timer.schedule(doAsynchronousTask, 1000, 10000);




    }

    public void SendTexts(){

        TextMessageDAO textMessageDAO = database.getTextMessageDAO();
        List<TextMessage> allMessages = textMessageDAO.getMessages();
        List<TextMessage> tempMessages = textMessageDAO.getMessages();
        int index = 0;
        for (TextMessage t: tempMessages) {
            if(t.getDate().before(new Date()) || t.getDate().equals(new Date())){
                //TODO call the send messages function
                t.sendMessage(this);
                //pendingMessageView.removeViewAt(index);
                textMessageDAO.delete(t);
                allMessages.remove(index);

                messageAdapter.dataSet.clear();
                messageAdapter.notifyDataSetChanged();
                messageAdapter.dataSet.addAll(allMessages);
                messageAdapter.dataSet.sort(TextMessage::compareTo);
                //messageAdapter.dataSet.remove(index);
                messageAdapter.notifyDataSetChanged();
                //messageAdapter.notifyItemRangeChanged(index, allMessages.size());
                index--;
            }
            index++;
        }

    }

    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if(grantResults.length!=0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    this.contactsPermissonCheck = true;
                    showContacts();
                }
                else {
                    Toast.makeText(this, "Until you grant the permission, we cannot display your contacts", Toast.LENGTH_SHORT).show();

                }

            }

        }
    }


}
