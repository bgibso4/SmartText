package com.example.ben.smarttext;

import android.annotation.SuppressLint;

import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateNewText extends AppCompatActivity {

    private int year;
    private int month;
    private int day;
    private int hours;
    private int minutes;
    private Context context;
    AppDatabase database;
    private List<Contact> contacts;
    private ContactAdapter adapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_text);
        this.context = this;

        // creates an autocomplete for phone number contacts
        final RecipientEditTextView phoneRetv = findViewById(R.id.phone_retv);
        phoneRetv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        BaseRecipientAdapter baseRecipientAdapter = new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_PHONE, this);

        // Queries for all phone numbers. Includes phone numbers marked as "mobile" and "others".
        // If set as true, baseRecipientAdapter will query only for phone numbers marked as "mobile".
        baseRecipientAdapter.setShowMobileOnly(false);

        phoneRetv.setAdapter(baseRecipientAdapter);

        // setting up searchable contacts from the create new text screen
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        Set<String> contactSet = appSharedPrefs.getStringSet("ContactsList", new HashSet<>());
        contacts = new ArrayList<>();
        for(String contact : contactSet){
            Contact createContact  = gson.fromJson(contact, Contact.class);
            contacts.add(createContact);
        }
        Collections.sort(contacts, new ContactsComparator());
        adapter = new ContactAdapter(this, 0, new ArrayList<>(contacts));
        AutoCompleteTextView searchContacts = findViewById(R.id.contactsSearch);
        searchContacts.setThreshold(0);
        searchContacts.setAdapter(adapter);
        searchContacts.setText("");
        searchContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact selected = (Contact) parent.getItemAtPosition(position);
                searchContacts.setText(selected.getName());
            }
        });
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

        String currentTime = Integer.toString(hour)+" : "+Integer.toString(c.get(Calendar.MINUTE))+" "+ampm;
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
