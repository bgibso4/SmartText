package com.gmf.dev;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Looper;
import android.os.Handler;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.telephony.SmsManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Context context;
    AppDatabase database;
    RecyclerView pendingMessageView;
    LinearLayoutManager messageLayoutManager;
    MessageLayoutAdapter messageAdapter;
    SharedPreferences pref;
    private List<Contact> contacts;
    private boolean contactsPermissonCheck;
    PendingIntent sentPI;
    PendingIntent deliveredPI;
    BroadcastReceiver sendBroadcastReceiver;
    BroadcastReceiver deliveredBroadcastReceiver;
    String  SENT = "SMS_SENT";
    String  DELIVERED = "SMS_DELIVERED";
    SwipeController swipeController;
    String[] PERMISSIONS = {
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_CONTACTS
    };

    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSION_ALL = 1;

    @SuppressLint("ShortAlarm")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        this.contactsPermissonCheck = false;

        database = Room.databaseBuilder(this, AppDatabase.class, "messages")
                .allowMainThreadQueries() //TODO get rid of main thread queries
                .build();
        TextMessageDAO textMessageDAO = database.getTextMessageDAO();
        List<TextMessage> texts = textMessageDAO.getMessages();
        texts.sort(TextMessage::compareTo);
        TextView pendingMessageTitle = this.findViewById(R.id.pendingMessageTitle);
        pendingMessageTitle.setText("Pending Messages ("+texts.size()+")");

        //Handling swipe actions for the recycler view
        this.swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                List<TextMessage> allMessages = database.getTextMessageDAO().getMessages();
                database.getTextMessageDAO().delete(allMessages.get(position));
                messageAdapter.dataSet.remove(position);
                messageAdapter.notifyItemRemoved(position);
                messageAdapter.notifyItemRangeChanged(position, messageAdapter.getItemCount());
            }

        }, this.getApplicationContext());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);

        pendingMessageView = findViewById(R.id.pendingMessageList);
        itemTouchHelper.attachToRecyclerView(pendingMessageView);

        pendingMessageView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // pendingMessageView.setHasFixedSize(true);


        // use a linear layout manager
        messageLayoutManager = new LinearLayoutManager(this);

        pendingMessageView.setLayoutManager(messageLayoutManager);

        // specify an adapter (see also next example)
        messageAdapter = new MessageLayoutAdapter(texts);
        pendingMessageView.setAdapter(messageAdapter);

        FloatingActionButton createTextBtn= findViewById(R.id.createTextBtn);

        Intent alarm = new Intent(context, MessageSenderRestartReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);



        //---when the SMS has been sent---

        sendBroadcastReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));

        //---when the SMS has been delivered---

        deliveredBroadcastReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        registerReceiver(deliveredBroadcastReceiver, new IntentFilter(DELIVERED));

        //TODO: Instead of sending all of the texts that didnt send before maybe just delete them?
        SendTexts();

//        Intent alarm = new Intent(this.context, MessageSenderRestartReceiver.class);
//        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
//        if(!alarmRunning) {
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarm, 0);
//            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1800, pendingIntent);
//        }

        createTextBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CreateNewText.class));
            finish();
        });

        //creating a thread to run the table queries in the background
        (new Thread(() -> {
            //Creating shared preferences
            Looper.prepare();
            while(!hasPermissions(context, PERMISSIONS)){
                requestPermissions(PERMISSIONS, PERMISSION_ALL);
            }
        })).start();

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {
                    try {
                        TextMessageDAO textMessageDAO = database.getTextMessageDAO();
                        List<TextMessage> texts = textMessageDAO.getMessages();
                        texts.sort(TextMessage::compareTo);
                        messageAdapter.dataSet = texts;
                        messageAdapter.notifyDataSetChanged();
                        String pendingMessage = "Pending Messages ("+texts.size()+")";
                        pendingMessageTitle.setText(pendingMessage);
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        throw new NullPointerException(e.getMessage());
                    }
                });

            }
        };
        timer.schedule(doAsynchronousTask, 1000, 5000);
    }

    @Override
    public void onBackPressed() {
        finish();
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
//                messageAdapter.dataSet.clear();
//                messageAdapter.notifyDataSetChanged();
//                messageAdapter.dataSet.addAll(allMessages);
//                messageAdapter.dataSet.sort(TextMessage::compareTo);
                //messageAdapter.dataSet.remove(index);
//                messageAdapter.notifyDataSetChanged();
                //messageAdapter.notifyItemRangeChanged(index, allMessages.size());
                index--;
            }
            index++;
        }

        TextMessage nextMessage = textMessageDAO.getNextText();
        if(nextMessage!=null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(nextMessage.getDate());
            Intent alarm = new Intent(context, MessageSenderRestartReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }


    }

    public static boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onPause() {
        try{
            unregisterReceiver(sendBroadcastReceiver);
            unregisterReceiver(deliveredBroadcastReceiver);
        }catch(IllegalArgumentException e){
            System.out.print("No receiver is registered");
        };

        super.onPause();

    }


}
