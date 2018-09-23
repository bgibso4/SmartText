package com.example.ben.smarttext;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface TextMessageDAO {

    @Insert
    public void insert(TextMessage... TextMessages);

    @Update
    public void update(TextMessage... TextMessages);

    @Delete
    public void delete(TextMessage... TextMessages);

    @Query("Select * FROM TextMessages")
    public List<TextMessage> getMessages();

    @Query("Select * FROM TextMessages ")
    public List<TextMessage> getMessagesToSend();
}
