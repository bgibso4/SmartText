package com.example.ben.smarttext;

import android.annotation.SuppressLint;

import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateNewText extends AppCompatActivity {

    private int year;
    private int month;
    private int day;
    private int hours;
    private int minutes;
    AppDatabase database;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_text);


        // creates an autocomplete for phone number contacts
        final RecipientEditTextView phoneRetv = findViewById(R.id.phone_retv);
        phoneRetv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        BaseRecipientAdapter baseRecipientAdapter = new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_PHONE, this);

        // Queries for all phone numbers. Includes phone numbers marked as "mobile" and "others".
        // If set as true, baseRecipientAdapter will query only for phone numbers marked as "mobile".
        baseRecipientAdapter.setShowMobileOnly(false);

        phoneRetv.setAdapter(baseRecipientAdapter);

        ImageButton sendBtn = findViewById(R.id.sendBtn);

        database = Room.databaseBuilder(this, AppDatabase.class, "textMessages")
                .allowMainThreadQueries() //TODO get rid of main thread queries
                .build();
        TextMessageDAO textMessageDAO = database.getTextMessageDAO();


        EditText timeField = findViewById(R.id.timeField);
        Calendar c =  Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        String ampm;
        if (hour >12){
            hour = hour -12;
            ampm = "PM";
        }
        else{
            ampm = "AM";
        }
        EditText dateField = findViewById(R.id.dateField);
        String month = Integer.toString(c.get(Calendar.MONTH)+1);
        String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        String year = Integer.toString(c.get(Calendar.YEAR));
        String dateToSet = month +"/"+ day+"/"+year;

        setDay(c.get(Calendar.DAY_OF_MONTH));
        setMonth(c.get(Calendar.MONTH));
        setYear(c.get(Calendar.YEAR));
        setHour(c.get(Calendar.HOUR_OF_DAY));
        setMinute(c.get(Calendar.MINUTE));


        dateField.setText(dateToSet);

        String minutes = Integer.toString(c.get(Calendar.MINUTE));
        if(c.get(Calendar.MINUTE)<10){
            minutes = "0" + minutes;
        }
        String currentTime = Integer.toString(hour)+" : "+minutes+" "+ampm;
        timeField.setText(currentTime);
        CreateNewText tempThis = this;


        timeField.setOnClickListener(view -> {
            TimePickerFragment newFragment = new TimePickerFragment();
            newFragment.setView(view);
            newFragment.setCreateNewText(tempThis);
            newFragment.setTimeString(currentTime);
            newFragment.setHour(c.get(Calendar.HOUR_OF_DAY));
            newFragment.setMinute(c.get(Calendar.MINUTE));

            newFragment.show(getSupportFragmentManager(), "timePicker");


            timeField.setText(newFragment.getTimeString());

        });

        dateField.setOnClickListener(view -> {
            DatePickerFragment newFragment = new DatePickerFragment();
            newFragment.setView(view);

            newFragment.setCreateNewText(tempThis);
            newFragment.setDay(c.get(Calendar.DAY_OF_MONTH));
            newFragment.setMonth(c.get(Calendar.MONTH));
            newFragment.setYear(c.get(Calendar.YEAR));


            newFragment.show(getFragmentManager(), "datePicker");
            dateField.setText(newFragment.GetDateString());




        });

        sendBtn.setOnClickListener(view -> {

            String num= "5197028412";

            Calendar cal = Calendar.getInstance();
            cal.set(getYear(), getMonth(), getDay(), getHour(), getMinute(), 0);
            Date dateRepresentation = cal.getTime();

            TextInputEditText m = findViewById(R.id.newMessage);
            String message= m.getText().toString();

            TextMessage newText = new TextMessage();
            newText.setDate(dateRepresentation);
            newText.setMessage(message);
            newText.setPhoneNumber(num);
            newText.setName("Willie");
            newText.setUid(java.util.UUID.randomUUID());
            textMessageDAO.insert(newText);
            startActivity(new Intent(CreateNewText.this, MainActivity.class));
        });
    }

    public void setYear(int y){
        this.year = y;
    }
    public int getYear(){
        return this.year;
    }

    public void setMonth(int m){
        this.month = m;
    }
    public int getMonth(){
        return this.month;
    }

    public void setDay(int d){
        this.day = d;
    }
    public int getDay(){
        return this.day;
    }

    public void setHour(int h){
        this.hours = h;
    }

    public int getHour(){
        return this.hours;
    }

    public void setMinute(int m){
        this.minutes = m;
    }

    public int getMinute(){
        return this.minutes;
    }
}
