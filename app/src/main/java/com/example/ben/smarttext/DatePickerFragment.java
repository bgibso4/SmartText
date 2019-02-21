package com.example.ben.smarttext;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public String dateString;
    public View view;
    public CreateNewText createNewText;
    private int year;
    private int month;
    private int day;




    public void setView(View v){
        this.view = v;
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

    public void setCreateNewText(CreateNewText c){
        this.createNewText = c;
    }
    public CreateNewText getCreateNewText(){
        return this.createNewText;
    }

    public String GetDateString(){
        return dateString;
    }
    public void SetDateString(int year, int month, int day){
        this.dateString = (month+1)+"/"+day+"/"+year;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        dateString = month+1+"/"+day+"/"+year;
        setDay(day);
        setMonth(month);
        setYear(year);
        EditText time = this.view.findViewById(R.id.dateField);
        time.setText(dateString);
        createNewText.setDay(getDay());
        createNewText.setMonth(getMonth());
        createNewText.setYear(getYear());
    }

    public void onCancel(DialogInterface dialogInterface){
        EditText time = view.findViewById(R.id.dateField);
        time.setText(dateString);
    }
}
