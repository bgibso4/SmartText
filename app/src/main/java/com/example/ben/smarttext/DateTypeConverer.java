package com.example.ben.smarttext;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateTypeConverer {
    @TypeConverter
    public long convertDateToLong(Date date){
        return date.getTime();
    }

    @TypeConverter
    public Date convertLongTodate(long time){
        return new Date(time);
    }

}
