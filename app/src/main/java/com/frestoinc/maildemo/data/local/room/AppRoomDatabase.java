package com.frestoinc.maildemo.data.local.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.frestoinc.maildemo.data.local.room.dao.AccountUserDao;
import com.frestoinc.maildemo.data.local.room.dao.ContactsDao;
import com.frestoinc.maildemo.data.local.room.dao.EasFoldersDao;
import com.frestoinc.maildemo.data.local.room.dao.EasMessageDao;
import com.frestoinc.maildemo.data.model.AccountUser;
import com.frestoinc.maildemo.data.model.EasFolder;
import com.frestoinc.maildemo.data.model.EasMessage;
import com.frestoinc.maildemo.data.model.GalContact;

/**
 * Created by frestoinc on 11,December,2019 for MailDemo.
 */
@Database(entities = {AccountUser.class, EasFolder.class, EasMessage.class, GalContact.class}, version = 1, exportSchema = false)
public abstract class AppRoomDatabase extends RoomDatabase {

    public abstract AccountUserDao accountDao();

    public abstract EasFoldersDao foldersDao();

    public abstract EasMessageDao messageDao();

    public abstract ContactsDao contactsDao();
}
