package com.example.ben.smarttext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;

import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
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
    SMSJobService service;
    RecyclerView pendingMessageView;
    LinearLayoutManager messageLayoutManager;
    MessageLayoutAdapter messageAdapter;
    SharedPreferences pref;
    private List<Contact> contacts;


    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;


        database = Room.databaseBuilder(this, AppDatabase.class, "db-test")
                .allowMainThreadQueries() //TODO get rid of main thread queries
                .build();
        TextMessageDAO textMessageDAO = database.getTextMessageDAO();
        List<TextMessage> texts = textMessageDAO.getMessages();



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
        Button contactsButton = findViewById((R.id.contactsButton));


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

        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ContactsScreen.class));
            }
        });

        //creating a thread to run the table queries in the background
//        (new Thread(){
//            public void run(){
//                //Creating shared preferences
//                pref = PreferenceManager.getDefaultSharedPreferences(context);
//                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = pref.edit();
//                showContacts();
//                Gson gson = new Gson();
//                Set<String> ContactSet = new HashSet<>();
//                for(Contact c : contacts) {
//                    ContactSet.add(gson.toJson(c));
//                }
//                editor.putStringSet("ContactsList", ContactSet);
//                editor.apply();
//            }
//        }).start();



        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    try {
                        SendTexts();
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        throw new NullPointerException(e.getMessage());
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 5000, 10000);



    }

    public void SendTexts(){

        TextMessageDAO textMessageDAO = database.getTextMessageDAO();
        List<TextMessage> allMessages = textMessageDAO.getMessages();
        for (TextMessage t: allMessages) {
            if(t.getDate().before(new Date()) || t.getDate().equals(new Date())){
                //TODO call the send messages function
                t.sendMessage(this);
                //service.sendSMS(t.getPhoneNumber(), t.getMessage());
            }
        }

    }

    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            contacts = getContactNames();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Read the name of all the contacts.
     *
     * @return a list of names.
     */
    private List<Contact> getContactNames() {
        List<Contact> contacts = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME.toUpperCase()+" ASC");
        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                while (cursor.moveToNext()) {
                    contacts.add(new Contact(cursor.getString(nameIndex), cursor.getString(numberIndex)));
                }
            } finally {
                cursor.close();
            }
        }
        return contacts;
    }

}
