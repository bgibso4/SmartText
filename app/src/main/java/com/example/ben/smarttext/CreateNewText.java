package com.example.ben.smarttext;

import android.annotation.SuppressLint;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.ContactImageCreator;
import com.android.ex.chips.RecipientEditTextView;
import com.android.ex.chips.RecipientEntry;
import com.android.ex.chips.recipientchip.DrawableRecipientChip;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private String currentTime;
    AppDatabase database;
    private boolean recipientCheck;
    private boolean messageBodyCheck;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_text);
        Context context = this;
        recipientCheck = false;
        messageBodyCheck = false;

        // creates an autocomplete for phone number contacts
        final RecipientEditTextView phoneRetv = findViewById(R.id.phone_retv);
        phoneRetv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        BaseRecipientAdapter baseRecipientAdapter = new BaseRecipientAdapter(BaseRecipientAdapter.QUERY_TYPE_PHONE, this);

        // Queries for all phone numbers. Includes phone numbers marked as "mobile" and "others".
        // If set as true, baseRecipientAdapter will query only for phone numbers marked as "mobile".
        baseRecipientAdapter.setShowMobileOnly(false);

        phoneRetv.setAdapter(baseRecipientAdapter);
        phoneRetv.addTextChangedListener(new TextWatcher() {

             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(phoneRetv.getRecipients().length !=0){
                    recipientCheck = true;
                    if(messageBodyCheck){
                        AllowSending();
                    }
                }
                else{
                    recipientCheck=false;
                    DisallowSending();
                }
             }

             @Override
             public void afterTextChanged(Editable s) {

             }
         });

        EditText newMessage = this.findViewById(R.id.newMessage);
        newMessage.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count==0 && start==0){
                    messageBodyCheck= false;
                    DisallowSending();
                }
                else{
                    messageBodyCheck = true;
                    if(recipientCheck){
                        AllowSending();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ImageButton sendBtn = findViewById(R.id.sendBtn);

        database = Room.databaseBuilder(this, AppDatabase.class, "messages")
                .allowMainThreadQueries() //TODO get rid of main thread queries
                .build();



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
        this.currentTime = Integer.toString(hour)+" : "+minutes+" "+ampm;
        timeField.setText(currentTime);
        CreateNewText tempThis = this;

        CardView timeCard = this.findViewById(R.id.timeCard);
        CardView dateCard = this.findViewById(R.id.dateCard);

        

        timeCard.setOnClickListener(view -> {
            TimePickerFragment newFragment = new TimePickerFragment();
            newFragment.setView(view);
            newFragment.setCreateNewText(tempThis);
            newFragment.setTimeString(currentTime);
            newFragment.setHour(c.get(Calendar.HOUR_OF_DAY));
            newFragment.setMinute(c.get(Calendar.MINUTE));

            newFragment.show(getSupportFragmentManager(), "timePicker");


            timeField.setText(newFragment.getTimeString());

        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(CreateNewText.this, MainActivity.class));
        finish();
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


    private Bitmap AvatarImageCreator(RecipientEntry contact){
        long contactId = contact.getContactId();

        byte[] photoBytes = contact.getPhotoBytes();
        // There may not be a photo yet if anything but the first contact address
        // was selected.
//        if (photoBytes == null && contact.getPhotoThumbnailUri() != null) {
//            // TODO: cache this in the recipient entry?
//            getAdapter().fetchPhoto(contact, contact.getPhotoThumbnailUri(), this.getContentResolver());
//            photoBytes = contact.getPhotoBytes();
//        }
        if (photoBytes != null) {
            return BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
        } else {
            // TODO: can the scaled down default photo be cached?
            return ContactImageCreator.getLetterPicture(this, contact);
        }
    }
    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 50;
        int targetHeight = 50;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    public void HandleDateClick(View arg0){
        Calendar c =  Calendar.getInstance();

        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setView(arg0);

        newFragment.setCreateNewText(this);
        newFragment.SetDateString(c.get(Calendar.YEAR) , c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        newFragment.setDay(c.get(Calendar.DAY_OF_MONTH));
        newFragment.setMonth(c.get(Calendar.MONTH));
        newFragment.setYear(c.get(Calendar.YEAR));

        EditText dateField = findViewById(R.id.dateField);
        newFragment.show(getFragmentManager(), "datePicker");
        dateField.setText(newFragment.GetDateString());
    }

    public void HandleTimeClick(View arg0){
        Calendar c =  Calendar.getInstance();
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setView(arg0);

        newFragment.setCreateNewText(this);
        newFragment.setTimeString(currentTime);
        newFragment.setHour(c.get(Calendar.HOUR_OF_DAY));
        newFragment.setMinute(c.get(Calendar.MINUTE));

        newFragment.show(getSupportFragmentManager(), "timePicker");
        EditText timeField = findViewById(R.id.timeField);
        timeField.setText(newFragment.getTimeString());
    }

    public void AllowSending(){
        ImageButton sendBtn = findViewById(R.id.sendBtn);
        final RecipientEditTextView phoneRetv = findViewById(R.id.phone_retv);
        TextMessageDAO textMessageDAO = database.getTextMessageDAO();
        sendBtn.setColorFilter(ContextCompat.getColor(this, R.color.materialLightGreenAccent));
        sendBtn.setOnClickListener(view -> {

            DrawableRecipientChip[] chips = phoneRetv.getSortedRecipients();


            Calendar cal = Calendar.getInstance();
            cal.set(getYear(), getMonth(), getDay(), getHour(), getMinute(), 0);
            Date dateRepresentation = cal.getTime();

            TextInputEditText m = findViewById(R.id.newMessage);
            String message= m.getText().toString();


            for (DrawableRecipientChip chip : chips) {
                TextMessage newText = new TextMessage();
                newText.setDate(dateRepresentation);
                newText.setMessage(message);
                String tempImage = BitmapTypeConverter.BitMapToString(this.AvatarImageCreator(chip.getEntry()));
                newText.setRecipientImage(tempImage);
                newText.setPhoneNumber(chip.getValue().toString());
                newText.setName(chip.getDisplay().toString());
                newText.setUid(java.util.UUID.randomUUID());
                textMessageDAO.insert(newText);
            }


            startActivity(new Intent(CreateNewText.this, MainActivity.class));
            finish();
        });
    }

    public void DisallowSending(){
        ImageButton sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setColorFilter(ContextCompat.getColor(this, R.color.materialGrey));
        sendBtn.setOnContextClickListener(null);
    }
}
