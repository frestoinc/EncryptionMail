package com.frestoinc.maildemo.data.local.room;

import com.frestoinc.maildemo.data.model.AccountUser;
import com.frestoinc.maildemo.data.model.EasFolder;
import com.frestoinc.maildemo.data.model.EasMessage;
import com.frestoinc.maildemo.data.model.GalContact;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;


/**
 * Created by frestoinc on 11,December,2019 for MailDemo.
 */
public interface RoomHelper {

    /**
     * ACCOUNT USERS.
     */
    Single<List<AccountUser>> getAllAccounts();

    Single<AccountUser> getAccount(String email);

    Maybe<AccountUser> getSessionAccount();

    Completable insertAccount(AccountUser user);

    Completable deleteAllAccounts();

    Completable updateSession(String email, int i);

    /**
     * EAS FOLDERS.
     */
    Single<List<EasFolder>> getAllFolders();

    Single<EasFolder> getFolder(int id);

    Completable insertFolder(EasFolder folder);

    Completable insertFolders(List<EasFolder> folders);

    Completable deleteFolder(int id);

    Completable deleteAllFolder();

    /**
     * EAS MESSAGES.
     */
    Single<List<EasMessage>> getFolderMessages(String id);

    Single<List<EasMessage>> getAllMessages();

    Single<EasMessage> getEasMessage(int key);

    Completable insertMessages(List<EasMessage> messages);

    Completable updateMessage(EasMessage message);

    Completable deleteMessage(String id);

    Completable deleteAllMessage();

    /**
     * Contacts.
     */
    Completable insertContact(GalContact contact);

    Completable deleteContact(String email);

    Single<GalContact> getContact(String email);

    Single<List<GalContact>> getAllContacts();
}
