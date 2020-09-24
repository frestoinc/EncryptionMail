package com.frestoinc.maildemo.data.local.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.frestoinc.maildemo.data.model.AccountUser;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;


/**
 * Created by frestoinc on 13,November,2019 for MailDemo.
 */
@Dao
public interface AccountUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(AccountUser users);

    @Query("DELETE FROM account_user")
    Completable deleteall();

    @Query("DELETE FROM account_user WHERE account_email = :email")
    Completable delete(String email);

    @Query("UPDATE account_user SET account_manager = :i WHERE account_email = :email")
    Completable updatesession(String email, int i);

    @Query("SELECT * FROM account_user WHERE account_manager = 1 LIMIT 1")
    Maybe<AccountUser> getSessionUser();

    @Query("SELECT * FROM account_user ORDER BY account_email ASC")
    Single<List<AccountUser>> getallaccounts();

    @Query("SELECT * FROM account_user WHERE account_email = :email")
    Single<AccountUser> getaccount(String email);
}
