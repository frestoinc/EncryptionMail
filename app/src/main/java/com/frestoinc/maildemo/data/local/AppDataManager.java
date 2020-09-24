package com.frestoinc.maildemo.data.local;

import com.frestoinc.maildemo.data.enums.ClassificationEnum;
import com.frestoinc.maildemo.data.enums.CryptoEnum;
import com.frestoinc.maildemo.data.local.prefs.PreferenceHelper;
import com.frestoinc.maildemo.data.local.room.RoomHelper;
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
 * Created by frestoinc on 09,January,2020 for MailDemo.
 */
@AppScope
public class AppDataManager implements DataManager {

    private final RoomHelper roomHelper;

    private final PreferenceHelper prefHelper;

    @Inject
    public AppDataManager(RoomHelper roomHelper, PreferenceHelper prefHelper) {
        this.roomHelper = roomHelper;
        this.prefHelper = prefHelper;
    }

    @Override
    public void clearAll() {
        prefHelper.clearAll();
    }

    @Override
    public ClassificationEnum getClassification() {
        return prefHelper.getClassification();
    }

    @Override
    public void setClassification(ClassificationEnum classification) {
        prefHelper.setClassification(classification);
    }

    @Override
    public CryptoEnum getEncryption() {
        return prefHelper.getEncryption();
    }

    @Override
    public void setEncryption(CryptoEnum encryption) {
        prefHelper.setEncryption(encryption);
    }

    @Override
    public boolean getIsEcc() {
        return prefHelper.getIsEcc();
    }

    @Override
    public void setisEcc(boolean bool) {
        prefHelper.setisEcc(bool);
    }

    @Override
    public Single<List<AccountUser>> getAllAccounts() {
        return roomHelper.getAllAccounts();
    }

    @Override
    public Single<AccountUser> getAccount(String email) {
        return roomHelper.getAccount(email);
    }

    @Override
    public Maybe<AccountUser> getSessionAccount() {
        return roomHelper.getSessionAccount();
    }

    @Override
    public Completable insertAccount(AccountUser user) {
        return roomHelper.insertAccount(user);
    }

    @Override
    public Completable deleteAllAccounts() {
        return roomHelper.deleteAllAccounts();
    }

    @Override
    public Completable updateSession(String email, int i) {
        return roomHelper.updateSession(email, i);
    }

    @Override
    public Single<List<EasFolder>> getAllFolders() {
        return roomHelper.getAllFolders();
    }

    @Override
    public Single<EasFolder> getFolder(int id) {
        return roomHelper.getFolder(id);
    }

    @Override
    public Completable insertFolder(EasFolder folder) {
        return roomHelper.insertFolder(folder);
    }

    @Override
    public Completable insertFolders(List<EasFolder> folders) {
        return roomHelper.insertFolders(folders);
    }

    @Override
    public Completable deleteFolder(int id) {
        return roomHelper.deleteFolder(id);
    }

    @Override
    public Completable deleteAllFolder() {
        return roomHelper.deleteAllFolder();
    }

    @Override
    public Single<List<EasMessage>> getFolderMessages(String id) {
        return roomHelper.getFolderMessages(id);
    }

    @Override
    public Single<List<EasMessage>> getAllMessages() {
        return roomHelper.getAllMessages();
    }

    @Override
    public Single<EasMessage> getEasMessage(int key) {
        return roomHelper.getEasMessage(key);
    }

    @Override
    public Completable insertMessages(List<EasMessage> messages) {
        return roomHelper.insertMessages(messages);
    }

    @Override
    public Completable updateMessage(EasMessage message) {
        return roomHelper.updateMessage(message);
    }

    @Override
    public Completable deleteMessage(String id) {
        return roomHelper.deleteMessage(id);
    }

    @Override
    public Completable deleteAllMessage() {
        return roomHelper.deleteAllMessage();
    }

    @Override
    public Completable insertContact(GalContact contact) {
        return roomHelper.insertContact(contact);
    }

    @Override
    public Completable deleteContact(String email) {
        return roomHelper.deleteContact(email);
    }

    @Override
    public Single<GalContact> getContact(String email) {
        return roomHelper.getContact(email);
    }

    @Override
    public Single<List<GalContact>> getAllContacts() {
        return roomHelper.getAllContacts();
    }
}
