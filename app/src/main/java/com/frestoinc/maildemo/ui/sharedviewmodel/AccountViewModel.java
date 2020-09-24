package com.frestoinc.maildemo.ui.sharedviewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.frestoinc.maildemo.AuthResource;
import com.frestoinc.maildemo.FormState;
import com.frestoinc.maildemo.R;
import com.frestoinc.maildemo.SessionManager;
import com.frestoinc.maildemo.activesync.EasConnection;
import com.frestoinc.maildemo.activesync.EasSyncCommand;
import com.frestoinc.maildemo.base.BaseViewModel;
import com.frestoinc.maildemo.base.rx.SchedulerProvider;
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
 * Created by frestoinc on 09,December,2019 for MailDemo.
 */
public class AccountViewModel extends BaseViewModel {

    /**
     * The Session manager.
     */
    @Inject
    SessionManager sessionManager;
    private MediatorLiveData<List<AccountUser>> accountUsers = new MediatorLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<AccountUser> sessionAccountUser = new MutableLiveData<>();
    private MutableLiveData<FormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<AuthResource> syncResource = new MutableLiveData<>();
    private MutableLiveData<AccountUser> accountUser = new MutableLiveData<>();

    /**
     * Instantiates a new Account view model.
     *
     * @param provider the provider
     * @param manager  the manager
     */
    @Inject
    public AccountViewModel(SchedulerProvider provider, DataManager manager) {
        super(provider, manager);

    }

    @Override
    public void setError(Throwable e) {
        syncResource.setValue(AuthResource.error(e.getMessage()));
        e.printStackTrace();
        error.setValue(e.getMessage());
    }

    public void init() {
        getSessionUser();
    }

    /**
     * Gets account users.
     *
     * @return the account users
     */
    public LiveData<List<AccountUser>> getAccountUsers() {
        return accountUsers;
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
     * Gets session account user.
     *
     * @return the session account user
     */
    public LiveData<AccountUser> getSessionAccountUser() {
        return sessionAccountUser;
    }

    /**
     * Sets account user to global {@link SessionManager}.
     *
     * @param accountUser the account user
     */
    public void setSessionAccountUser(AccountUser accountUser) {
        MutableLiveData<AccountUser> liveData = new MutableLiveData<>();
        liveData.setValue(accountUser);
        sessionManager.setCachedUser(liveData);
    }

    /**
     * Gets login form state.
     *
     * @return the login form state
     */
    public LiveData<FormState> getLoginFormState() {
        return loginFormState;
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
     * Sets account user.
     *
     * @param user the user
     */
    public void setAccountUser(AccountUser user) {
        this.accountUser.setValue(user);
    }

    /**
     * Observe form data changed when user input their details.
     *
     * @param email  the email
     * @param pwd    the pwd
     * @param server the server
     */
    public void observeFormDataChanged(String email, String pwd, String server) {
        loginFormState.setValue(new FormState(false));
        boolean isValid = true;
        if (!Utils.isEmailValid(email)) {
            loginFormState.setValue(new FormState(R.string.error_invalid_email, null, null));
            isValid = false;
        }
        if (Utils.isPasswordNotValid(pwd)) {
            loginFormState.setValue(new FormState(null, R.string.error_field_required, null));
            isValid = false;
        }
        if (Utils.isPasswordNotValid(server)) {
            loginFormState.setValue(new FormState(null, null, R.string.error_field_required));
            isValid = false;
        }
        loginFormState.setValue(new FormState(isValid));
    }

    /**
     * Gets stored room account users.
     */
    public void getRoomAccountUsers() {
        getDataManager().getAllAccounts()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<List<AccountUser>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(List<AccountUser> users) {
                        accountUsers.postValue(users);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    /**
     * Gets account from activesync online.
     *
     * @param user the user
     */
    public void getAccountFromAs(AccountUser user) {
        syncResource.setValue(AuthResource.loading(null));
        Single.fromCallable(() -> {
            EasConnection conn = new EasConnection();
            conn.setAccountUser(user);
            long key = conn.getPolicyKey();
            return key != 0;
        }).subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(Boolean b) {
                        syncResource.setValue(AuthResource.authenticated(null));
                        insertRoomAccountUser(user);
                        syncResource.setValue(null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    private void insertRoomAccountUser(AccountUser user) {
        getDataManager().insertAccount(user)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onComplete() {
                        getRoomAccountUsers();
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    /**
     * Remove all accounts when user click
     * * {@link com.frestoinc.maildemo.ui.account.fragment.AccountFragment}.
     */
    public void removeRoomAccountUsers() {
        getDataManager().deleteAllAccounts()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onComplete() {
                        accountUsers.postValue(null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
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
                        if (accountUser.getValue() != null) {
                            updateSessionAccount(accountUser.getValue());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    private EasConnection getConnection() {
        if (accountUser.getValue() == null) {
            syncResource.setValue(AuthResource.error("account is null"));
        }
        EasConnection connection = new EasConnection();
        connection.setAccountUser(accountUser.getValue());
        return connection;
    }

    //todo review
    private boolean createAsync() throws Exception {
        postStatus("Establishing Connection with server...");
        EasConnection conn = getConnection();

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

    //**************************************************
    // END OF FULL DATA SYNCHRONISATION
    //**************************************************

    /**
     * Update session once synchronisation is complete
     * {@link com.frestoinc.maildemo.ui.account.fragment.AccountFragment}.
     *
     * @param user the user
     */
    private void updateSessionAccount(AccountUser user) {
        getDataManager().updateSession(user.getAccountemail(), 1)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onComplete() {
                        sessionAccountUser.setValue(user);
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

    private void postStatus(String msg) {
        syncResource.postValue(AuthResource.loading(msg));
    }
}
