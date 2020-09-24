package com.frestoinc.maildemo.data.local.room;

import com.frestoinc.maildemo.data.model.AccountUser;
import com.frestoinc.maildemo.data.model.EasFolder;
import com.frestoinc.maildemo.data.model.EasMessage;
import com.frestoinc.maildemo.data.model.GalContact;
import com.frestoinc.maildemo.di.scope.AppScope;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;


/**
 * Created by frestoinc on 11,December,2019 for MailDemo.
 */
@AppScope
public class AppRoomHelper implements RoomHelper {

    private final AppRoomDatabase appDatabase;

    @Inject
    public AppRoomHelper(AppRoomDatabase database) {
        this.appDatabase = database;
    }

    @Override
    public Single<List<AccountUser>> getAllAccounts() {
        return appDatabase.accountDao().getallaccounts();
    }

    @Override
    public Single<AccountUser> getAccount(String email) {
        return appDatabase.accountDao().getaccount(email);
    }

    @Override
    public Maybe<AccountUser> getSessionAccount() {
        return appDatabase.accountDao().getSessionUser();
    }

    @Override
    public Completable insertAccount(AccountUser user) {
        return appDatabase.accountDao().insert(user);
    }

    @Override
    public Completable deleteAllAccounts() {
        return appDatabase.accountDao().deleteall();
    }

    @Override
    public Completable updateSession(String email, int i) {
        return appDatabase.accountDao().updatesession(email, i);
    }

    @Override
    public Single<List<EasFolder>> getAllFolders() {
        return appDatabase.foldersDao().getAllFolders();
    }

    @Override
    public Single<EasFolder> getFolder(int id) {
        return appDatabase.foldersDao().getFolder(id);
    }

    @Override
    public Completable insertFolder(EasFolder folder) {
        return appDatabase.foldersDao().insertFolder(folder);
    }

    @Override
    public Completable insertFolders(List<EasFolder> folders) {
        return appDatabase.foldersDao().insertFolders(folders);
    }

    @Override
    public Completable deleteFolder(int id) {
        return appDatabase.foldersDao().delete(id);
    }

    @Override
    public Completable deleteAllFolder() {
        return appDatabase.foldersDao().deleteall();
    }

    @Override
    public Single<List<EasMessage>> getFolderMessages(String id) {
        return appDatabase.messageDao().getFolderMessages(id);
    }

    @Override
    public Single<List<EasMessage>> getAllMessages() {
        return appDatabase.messageDao().getAllMessages();
    }

    @Override
    public Single<EasMessage> getEasMessage(int key) {
        return appDatabase.messageDao().getMessage(key);
    }

    @Override
    public Completable insertMessages(List<EasMessage> messages) {
        return appDatabase.messageDao().insertMessages(messages);
    }

    @Override
    public Completable updateMessage(EasMessage message) {
        return appDatabase.messageDao().update(message);
    }

    @Override
    public Completable deleteMessage(String id) {
        return appDatabase.messageDao().delete(id);
    }

    @Override
    public Completable deleteAllMessage() {
        return appDatabase.messageDao().delete();
    }

    @Override
    public Completable insertContact(GalContact contact) {
        return appDatabase.contactsDao().insertContact(contact);
    }

    @Override
    public Completable deleteContact(String email) {
        return appDatabase.contactsDao().delete(email);
    }

    @Override
    public Single<GalContact> getContact(String email) {
        return appDatabase.contactsDao().getContact(email);
    }

    @Override
    public Single<List<GalContact>> getAllContacts() {
        return appDatabase.contactsDao().getAllContacts();
    }
}
