package com.example.ben.smarttext;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener, TimePickerDialog.OnCancelListener {

    public String timeString;
    public View view;
    public CreateNewText createNewText;
    private int hours;
    private int minutes;


    public String getTimeString(){
        return timeString;
    }
    public void setTimeString(String tString){
        this.timeString = tString;
    }

    public void setView(View v){
        this.view = v;
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

    public void setCreateNewText(CreateNewText c){
        this.createNewText = c;
    }
    public CreateNewText getCreateNewText(){
        return this.createNewText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        timeString = "";
        int hour= i;
        int minute = i1;
        String ampm = "AM";
        String minuteZero= "";
        if(i>=12){
            ampm= "PM";
            if(i!=12){
                hour= i-12;
            }
        }
        if(i1<10){
            minuteZero="0";
        }
        setMinute(minute);
        setHour(i);
        timeString = hour+" : "+minuteZero+minute+" "+ampm;
        EditText time = view.findViewById(R.id.timeField);
        time.setText(timeString);
        createNewText.setHour(getHour());
        createNewText.setMinute(getMinute());

    }

    public void onCancel(DialogInterface dialogInterface){
        EditText time = view.findViewById(R.id.timeField);
        time.setText(timeString);
    }

}
