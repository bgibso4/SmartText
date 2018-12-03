package com.example.ben.smarttext;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class CreateNewText extends AppCompatActivity {

    private int year;
    private int month;
    private int day;
    private int hours;
    private int minutes;
    private Context context;
    AppDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_text);
        this.context = this;


        //final ComponentName componentName = new ComponentName(this, SMSJobService.class);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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


        timeField.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                TimePickerFragment newFragment = new TimePickerFragment();
                newFragment.setView(view);
                newFragment.setCreateNewText(tempThis);
                newFragment.setTimeString(currentTime);
                newFragment.setHour(c.get(Calendar.HOUR_OF_DAY));
                newFragment.setMinute(c.get(Calendar.MINUTE));

                newFragment.show(getSupportFragmentManager(), "timePicker");


                timeField.setText(newFragment.getTimeString());

            }
        });

        dateField.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setView(view);

                newFragment.setCreateNewText(tempThis);
                newFragment.setDay(c.get(Calendar.DAY_OF_MONTH));
                newFragment.setMonth(c.get(Calendar.MONTH));
                newFragment.setYear(c.get(Calendar.YEAR));


                newFragment.show(getFragmentManager(), "datePicker");
                dateField.setText(newFragment.GetDateString());




            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                //newText.sendMessage(context);
                //String time= texts.getString("Time", "InvalidTime");
//                boolean x = false;
//                if(x){
//
//                }
//                //TODO impliment the time feature and then enable this check
////                else if(time.equals("InvalidTime")){
////                    Snackbar.make(view, "Invalid Time set", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
////                }
//                else{
//                    //schedule job
//                    JSONObject textInfo= new JSONObject();
//                    String[] textInf= {num, message};
//                    try{
//                        textInfo.put("phoneNum", num);
//                        textInfo.put("message", message);
//
//                    }
//                    catch(JSONException e){
//                        e.printStackTrace();
//                    }
//
//                    //check these
//                    PersistableBundle bundle = new PersistableBundle();
//                    bundle.putStringArray("TextInfo", textInf);
//                    //bundle.putString("TextInfo", textInfo.toString());
//                    final JobInfo jobInfo = new JobInfo.Builder(12, componentName)
//                            .setMinimumLatency(5000)
//                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
//                            .setExtras(bundle)
//                            .build();
//                    JobScheduler jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
//                    int resultCode = jobScheduler.schedule(jobInfo);
//                    if (resultCode == JobScheduler.RESULT_SUCCESS) {
//                        Log.d("Message", "Job scheduled!");
//                    } else {
//                        Log.d("Message", "Job not scheduled");
//                    }
//
//                }
            }
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
