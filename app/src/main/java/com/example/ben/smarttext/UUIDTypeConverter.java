package com.example.ben.smarttext;

import androidx.room.TypeConverter;

import java.util.UUID;

public class UUIDTypeConverter {
    @TypeConverter
    public long convertUUIDToLong(UUID uuid){
        return uuid.getMostSignificantBits();
    }

    @TypeConverter
    public UUID convertLongToUUID(long uuid){
        return new UUID(uuid, uuid);
    }

}
