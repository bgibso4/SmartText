package com.example.ben.smarttext;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

@Database(entities = {TextMessage.class}, version = 2)
@TypeConverters({DateTypeConverer.class, UUIDTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase{
    public abstract TextMessageDAO getTextMessageDAO();
}
