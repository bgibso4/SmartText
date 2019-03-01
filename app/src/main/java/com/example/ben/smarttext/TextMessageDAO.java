package com.example.ben.smarttext;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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

    @Query("Select * FROM TextMessages WHERE date = (SELECT MIN(date) FROM TextMessages)")
    public TextMessage getNextText();

    @Query("Select * FROM TextMessages ")
    public List<TextMessage> getMessagesToSend();
}
