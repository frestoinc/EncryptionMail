package com.frestoinc.maildemo.data.local.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.frestoinc.maildemo.data.model.EasFolder;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;


/**
 * Created by frestoinc on 16,November,2019 for MailDemo.
 */
@Dao
public interface EasFoldersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertFolder(EasFolder folder);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertFolders(List<EasFolder> folders);

    @Query("DELETE FROM eas_folders")
    Completable deleteall();

    @Query("DELETE FROM eas_folders WHERE id = :id")
    Completable delete(int id);

    @Query("SELECT * FROM eas_folders ORDER BY id DESC")
    Single<List<EasFolder>> getAllFolders();

    @Query("SELECT * FROM eas_folders WHERE id = :id")
    Single<EasFolder> getFolder(int id);
}
