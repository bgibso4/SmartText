package com.gmf.dev;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {TextMessage.class}, version = 3)
@TypeConverters({DateTypeConverer.class, UUIDTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase{
    public abstract TextMessageDAO getTextMessageDAO();
}
