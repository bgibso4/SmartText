package com.example.ben.smarttext;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public String timeString;
    public View view;

    public String GetTimeString(){
        return timeString;
    }

    public void setView(View v){
        this.view = v;
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
        String ampm = "";
        if(i>=12){
            ampm= "PM";
            if(i!=12){
                i= i-12;
            }
        }
        timeString = hour+" : "+minute+" "+ampm;
        EditText time = view.findViewById(R.id.timeField);
        time.setText(timeString);

    }
}
