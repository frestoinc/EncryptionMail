package com.frestoinc.maildemo.data.local.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.frestoinc.maildemo.data.model.EasMessage;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;


/**
 * Created by frestoinc on 17,December,2019 for MailDemo.
 */
@Dao
public interface EasMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMessage(EasMessage message);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMessages(List<EasMessage> messages);

    @Query("DELETE FROM eas_messages WHERE folder_id = :id")
    Completable delete(String id);

    @Query("DELETE FROM eas_messages")
    Completable delete();

    @Update
    Completable update(EasMessage message);

    @Query("SELECT * FROM eas_messages WHERE folder_id = :folderId ORDER BY date_received DESC")
    Single<List<EasMessage>> getFolderMessages(String folderId);

    @Query("SELECT * FROM eas_messages ORDER BY date_received DESC")
    Single<List<EasMessage>> getAllMessages();

    @Query("SELECT * FROM eas_messages WHERE priKey = :key")
    Single<EasMessage> getMessage(int key);
}
