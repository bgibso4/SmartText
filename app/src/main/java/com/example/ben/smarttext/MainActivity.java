package com.example.ben.smarttext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
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

import android.view.View;
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

    Context context;
    AppDatabase database;
    RecyclerView pendingMessageView;
    LinearLayoutManager messageLayoutManager;
    MessageLayoutAdapter messageAdapter;
    SharedPreferences pref;
    SwipeController swipeController;
    boolean contactsPermissonCheck;
    String[] PERMISSIONS = {
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_PHONE_STATE
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

        if(!alarmRunning) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1800, pendingIntent);
        }

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
                        SendTexts();
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
        timer.schedule(doAsynchronousTask, 1000, 10000);
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

}
