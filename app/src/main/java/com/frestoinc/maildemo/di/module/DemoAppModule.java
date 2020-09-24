package com.frestoinc.maildemo.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.frestoinc.maildemo.base.rx.AppSchedulerProvider;
import com.frestoinc.maildemo.base.rx.SchedulerProvider;
import com.frestoinc.maildemo.data.local.AppDataManager;
import com.frestoinc.maildemo.data.local.DataManager;
import com.frestoinc.maildemo.data.local.prefs.AppPreferenceHelper;
import com.frestoinc.maildemo.data.local.prefs.PreferenceHelper;
import com.frestoinc.maildemo.data.local.room.AppRoomDatabase;
import com.frestoinc.maildemo.data.local.room.AppRoomHelper;
import com.frestoinc.maildemo.data.local.room.RoomHelper;
import com.frestoinc.maildemo.data.local.room.dao.AccountUserDao;
import com.frestoinc.maildemo.data.local.room.dao.ContactsDao;
import com.frestoinc.maildemo.data.local.room.dao.EasFoldersDao;
import com.frestoinc.maildemo.data.local.room.dao.EasMessageDao;
import com.frestoinc.maildemo.data.model.AccountUser;
import com.frestoinc.maildemo.di.scope.AppScope;
import com.frestoinc.maildemo.utility.Constants;
import com.google.gson.Gson;

import dagger.Module;
import dagger.Provides;

/**
 * ROOT MODULE
 * Created by frestoinc on 06,December,2019 for MailDemo.
 */
@Module
public class DemoAppModule {

    @AppScope
    @Provides
    Context provideContext(Application application) {
        return application;
    }

    @AppScope
    @Provides
    SchedulerProvider provideSchedulerProvider() {
        return new AppSchedulerProvider();
    }

    @AppScope
    @Provides
    ConnectivityManager provideConnectivityManager(Application context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @AppScope
    @Provides
    Gson provideGson() {
        return new Gson();
    }

    @AppScope
    @Provides
    AccountUser provideAccountUser() {
        return new AccountUser();
    }

    @AppScope
    @Provides
    SharedPreferences provideSharedPreferences(Application context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @AppScope
    @Provides
    PreferenceHelper providePreferenceHelper(AppPreferenceHelper preferenceHelper) {
        return preferenceHelper;
    }

    @AppScope
    @Provides
    RoomHelper provideRoomHelper(AppRoomHelper repository) {
        return repository;
    }

    @AppScope
    @Provides
    DataManager provideDataManager(RoomHelper roomHelper, PreferenceHelper preferenceHelper) {
        return new AppDataManager(roomHelper, preferenceHelper);
    }

    @AppScope
    @Provides
    AppRoomDatabase provideAppRoomDatabase(Application application) {
        return Room.databaseBuilder(application,
                AppRoomDatabase.class, Constants.ROOM_DATABASE)
                .fallbackToDestructiveMigration()
                .build();
    }

    @AppScope
    @Provides
    AccountUserDao provideAccountUserDaos(AppRoomDatabase db) {
        return db.accountDao();
    }

    @AppScope
    @Provides
    EasFoldersDao provideEasFoldersDaos(AppRoomDatabase db) {
        return db.foldersDao();
    }

    @AppScope
    @Provides
    EasMessageDao provideEasMessageDaos(AppRoomDatabase db) {
        return db.messageDao();
    }

    @AppScope
    @Provides
    ContactsDao provideContactsDao(AppRoomDatabase db) {
        return db.contactsDao();
    }
}
