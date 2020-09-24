package com.frestoinc.maildemo.base;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import io.reactivex.Completable;


/**
 * Created by frestoinc on 12,December,2019 for MailDemo.
 */

@Dao
public interface BaseDao<T> {

  //TODO

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  Completable insert(T t);

  @Update
  Completable update(T t);

  @Delete
  Completable delete(T t);

}
