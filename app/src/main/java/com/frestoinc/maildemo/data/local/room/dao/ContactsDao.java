package com.frestoinc.maildemo.data.local.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.frestoinc.maildemo.data.model.GalContact;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;


/**
 * Created by frestoinc on 16,January,2020 for MailDemo.
 */
@Dao
public interface ContactsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertContact(GalContact contact);

    @Query("DELETE FROM contacts WHERE email = :email")
    Completable delete(String email);

    @Query("SELECT * FROM contacts WHERE email =:email")
    Single<GalContact> getContact(String email);

    @Query("SELECT * FROM contacts")
    Single<List<GalContact>> getAllContacts();
}
