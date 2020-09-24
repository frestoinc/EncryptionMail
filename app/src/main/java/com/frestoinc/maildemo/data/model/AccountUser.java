package com.frestoinc.maildemo.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Created by frestoinc on 13,November,2019 for MailDemo.
 */
@Entity(tableName = "account_user", indices = {@Index(value = {"account_email"}, unique = true)})
public class AccountUser {

    @PrimaryKey(autoGenerate = true)
    private int priKey;

    @NonNull
    @ColumnInfo(name = "account_email")
    private String accountemail = "";

    @NonNull
    @ColumnInfo(name = "account_pwd")
    private String accountpwd = "";

    @NonNull
    @ColumnInfo(name = "account_server")
    private String accountserver = "";

    @ColumnInfo(name = "account_manager")
    private int accountmanager = 0;

    @Ignore
    public AccountUser() {

    }

    public AccountUser(
            @NonNull String accountemail, @NonNull String accountpwd, @NonNull String accountserver) {
        this.accountemail = accountemail;
        this.accountpwd = accountpwd;
        this.accountserver = accountserver;
        this.accountmanager = 0;
    }

    public int getPriKey() {
        return priKey;
    }

    public void setPriKey(int priKey) {
        this.priKey = priKey;
    }

    @NonNull
    public String getAccountemail() {
        return accountemail;
    }

    @NonNull
    public String getAccountpwd() {
        return accountpwd;
    }

    @NonNull
    public String getAccountserver() {
        return accountserver;
    }

    public int getAccountmanager() {
        return accountmanager;
    }

    public void setAccountmanager(int accountmanager) {
        this.accountmanager = accountmanager;
    }
}
