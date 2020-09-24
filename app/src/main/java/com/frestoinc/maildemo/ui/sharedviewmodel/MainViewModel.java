package com.frestoinc.maildemo.ui.sharedviewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.frestoinc.maildemo.AuthResource;
import com.frestoinc.maildemo.SessionManager;
import com.frestoinc.maildemo.activesync.EasConnection;
import com.frestoinc.maildemo.activesync.EasFolderType;
import com.frestoinc.maildemo.activesync.EasSyncCommand;
import com.frestoinc.maildemo.base.BaseViewModel;
import com.frestoinc.maildemo.base.rx.SchedulerProvider;
import com.frestoinc.maildemo.data.enums.ComposeEnum;
import com.frestoinc.maildemo.data.local.DataManager;
import com.frestoinc.maildemo.data.model.AccountUser;
import com.frestoinc.maildemo.data.model.EasFolder;
import com.frestoinc.maildemo.data.model.EasMessage;
import com.frestoinc.maildemo.utility.Constants;
import com.frestoinc.maildemo.utility.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.MaybeObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;


/**
 * Created by frestoinc on 11,December,2019 for MailDemo.
 */
public class MainViewModel extends BaseViewModel {

    /**
     * The Session manager.
     */
    @Inject
    public SessionManager sessionManager;
    private MutableLiveData<AuthResource> syncResource = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<List<EasFolder>> folders = new MutableLiveData<>();
    private MutableLiveData<AccountUser> sessionAccountUser = new MutableLiveData<>();
    private MutableLiveData<List<EasMessage>> general = new MutableLiveData<>();

    /**
     * Instantiates a new Main view model.
     *
     * @param schedulerProvider the scheduler provider for handling {@link io.reactivex.plugins.RxJavaPlugins}
     * @param manager           the manager for handling data transaction
     */
    @Inject
    public MainViewModel(SchedulerProvider schedulerProvider, DataManager manager) {
        super(schedulerProvider, manager);
        getSessionUser();
        getFolders();
    }

    @Override
    public void setError(Throwable e) {
        syncResource.setValue(AuthResource.error(e.getMessage()));
        e.printStackTrace();
        error.setValue(e.getMessage());
    }

    /**
     * Gets error result.
     *
     * @return the error result
     */
    public LiveData<String> getErrorResult() {
        return error;
    }

    /**
     * Gets app folders.
     *
     * @return the app folders
     */
    public LiveData<List<EasFolder>> getAppFolders() {
        return folders;
    }

    /**
     * Gets session account user.
     *
     * @return the session account user
     */
    public LiveData<AccountUser> getSessionAccountUser() {
        return sessionAccountUser;
    }

    /**
     * Gets general.
     *
     * @return the general
     */
    public LiveData<List<EasMessage>> getGeneral() {
        return general;
    }

    /**
     * Gets sync resource.
     *
     * @return the sync resource
     */
    public LiveData<AuthResource> getSyncResource() {
        return syncResource;
    }


    /**
     * Gets session manager.
     *
     * @return the session manager
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }


    //**************************************************
    // START OF FULL DATA SYNCHRONISATION
    //**************************************************

    /**
     * Synchronise data with activesync online.
     */
    public void synchroniseData() {
        syncResource.setValue(AuthResource.loading("Establishing Connection with server..."));
        Completable.fromCallable(
                this::createAsync)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onComplete() {
                        syncResource.setValue(AuthResource.authenticated(null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    //todo review
    private boolean createAsync() throws Exception {
        postStatus("Establishing Connection with server...");
        EasConnection conn = getSessionManager().getEasConnection();

        postStatus("Retrieving policy key...");
        long policyKey = conn.getPolicyKey();

        postStatus("Getting folder lists...");
        List<EasFolder> folderList = conn.getFolders(policyKey);

        postStatus("Saving folder lists...");
        List<EasFolder> newFolderList = parseFolderList(folderList);
        refreshFolders(newFolderList);

        postStatus("Downloading folder messages...");
        List<EasMessage> newMessageList = parseMessageList(conn, policyKey, newFolderList);

        postStatus("Saving folder messages...");
        refreshMessages(newMessageList);

        return true;
    }

    private List<EasFolder> parseFolderList(List<EasFolder> list) {
        List<EasFolder> newList = new ArrayList<>();
        for (EasFolder e : list) {
            if (Constants.FILTERED_FOLDERS.contains(e.getName())) {
                newList.add(e);
            }
        }
        return newList;
    }

    private List<EasMessage> parseMessageList(EasConnection conn, long policyKey, List<EasFolder> list)
            throws Exception {
        List<EasMessage> newMessageList = new ArrayList<>();
        for (EasFolder easFolder : list) {
            postStatus(String.format("Downloading %s folder contents...", easFolder.getName()));
            if (Utils.isStringInteger(easFolder.getId())) {
                String syncKey = conn.getFolderSyncKey(policyKey, easFolder.getId(), 10);
                EasSyncCommand syncCommand =
                        conn.getMailSyncCommands(policyKey, easFolder.getId(), syncKey);
                if (syncCommand != null) {
                    for (EasSyncCommand.Command ec : syncCommand.getAdded()) {
                        EasMessage message = ec.getMessage();
                        message.setFolderId(easFolder.getId());
                        message.setSync(false);
                        newMessageList.add(message);
                    }
                }
            }
        }
        return newMessageList;
    }

    private void refreshFolders(List<EasFolder> folderList) {
        getDataManager().deleteAllFolder()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onComplete() {
                        getDataManager().insertFolders(folderList)
                                .subscribeOn(getSchedulerProvider().io())
                                .observeOn(getSchedulerProvider().ui()).subscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    private void refreshMessages(List<EasMessage> messageList) {
        getDataManager().deleteAllMessage()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onComplete() {
                        getDataManager().insertMessages(messageList)
                                .subscribeOn(getSchedulerProvider().io())
                                .observeOn(getSchedulerProvider().ui()).subscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    private void getFolders() {
        getDataManager().getAllFolders()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<List<EasFolder>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(List<EasFolder> easFolders) {
                        folders.setValue(easFolders);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    //**************************************************
    // END OF FULL DATA SYNCHRONISATION
    //**************************************************

    private List<EasMessage> setupFolderAS(EasFolderType type) throws Exception {
        List<EasMessage> messageList = new ArrayList<>();
        EasConnection conn = getSessionManager().getEasConnection();
        long policyKey = conn.getPolicyKey();
        String folderId = getFolderType(type);
        String syncKey = conn.getFirstSyncKeyCommand(policyKey, folderId);
        if (syncKey != null) {
            EasSyncCommand syncCommand = conn.getMailSyncCommands(policyKey, folderId, syncKey);
            if (syncCommand.getAdded() != null) {
                for (EasSyncCommand.Command ec : syncCommand.getAdded()) {
                    EasMessage message = ec.getMessage();
                    message.setFolderId(folderId);
                    message.setSync(false);
                    messageList.add(message);
                }
            }
        }
        return messageList;
    }

    private void updateMessage(List<EasMessage> messageList, EasFolderType type) {
        syncResource.setValue(AuthResource.loading(String.format("Updating %s folder", type.toString())));
        getDataManager().deleteMessage(getFolderType(type))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onComplete() {
                        getDataManager().insertMessages(messageList)
                                .subscribeOn(getSchedulerProvider().io())
                                .observeOn(getSchedulerProvider().ui())
                                .subscribe(new CompletableObserver() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        getCompositeDisposable().add(d);
                                    }

                                    @Override
                                    public void onComplete() {
                                        getFolder(type);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        setError(e);
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    /**
     * Gets folder from ActiveSync online by supplying the {@link EasFolderType} param.
     *
     * @param type of {@link EasFolderType}
     */
    public void getFolderAS(EasFolderType type) {
        syncResource.setValue(AuthResource.loading(String.format("Synchronising %s folder", type.toString())));
        Single.fromCallable(
                () -> setupFolderAS(type))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<List<EasMessage>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(List<EasMessage> messageList) {
                        general.setValue(messageList);
                        updateMessage(messageList, type);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    /**
     * Gets folder from room by supplying the {@link EasFolderType} param.
     *
     * @param type of {@link EasFolderType}
     */
    public void getFolder(EasFolderType type) {
        syncResource.setValue(AuthResource.loading(String.format("Loading %s folder", type.toString())));
        general.setValue(new ArrayList<>());
        getDataManager().getFolderMessages(getFolderType(type))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<List<EasMessage>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(List<EasMessage> messageList) {
                        general.setValue(messageList);
                        syncResource.setValue(AuthResource.authenticated(null));
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    /**
     * Update room database by assigning value 0 to {@link AccountUser} account manager.
     */
    public void signOut() {
        //todo delete folders and messages
        AccountUser accountUser = getSessionAccountUser().getValue();
        getDataManager().updateSession(accountUser.getAccountemail(), 0)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onComplete() {
                        sessionAccountUser.setValue(null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    private void getSessionUser() {
        getDataManager().getSessionAccount()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new MaybeObserver<AccountUser>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(AccountUser accountUser) {
                        sessionAccountUser.setValue(accountUser);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }

                    @Override
                    public void onComplete() {
                        //nth
                    }
                });
    }

    private String getFolderType(EasFolderType type) {
        if (folders.getValue() == null) {
            return null;
        }
        for (EasFolder f : folders.getValue()) {
            if (f.getType() == type) {
                return f.getId();
            }
        }
        return null;
    }

    /**
     * Sets and store {@link ComposeEnum} to the global state list.
     */
    public void setGlobalMessage() {
        MutableLiveData<ComposeEnum> liveData1 = new MutableLiveData<>();
        liveData1.setValue(ComposeEnum.NEW);
        getSessionManager().setCachedMessageType(liveData1);
        getSessionManager().setCachedMessage(new MutableLiveData<>());
    }

    private void postStatus(String msg) {
        syncResource.postValue(AuthResource.loading(msg));
    }
}
