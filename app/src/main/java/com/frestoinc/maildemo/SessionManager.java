package com.frestoinc.maildemo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.frestoinc.maildemo.activesync.EasConnection;
import com.frestoinc.maildemo.data.enums.ComposeEnum;
import com.frestoinc.maildemo.data.model.AccountUser;
import com.frestoinc.maildemo.data.model.EasMessage;
import com.frestoinc.maildemo.di.scope.AppScope;

import javax.inject.Inject;

/**
 * Created by frestoinc on 12,December,2019 for MailDemo.
 */
@AppScope
public class SessionManager {

    private MediatorLiveData<AccountUser> cachedUser = new MediatorLiveData<>();

    private MediatorLiveData<EasMessage> cachedMessage = new MediatorLiveData<>();

    private MediatorLiveData<ComposeEnum> cachedMessageType = new MediatorLiveData<>();

    private EasConnection easConnection;

    @Inject
    public SessionManager() {

    }

    public LiveData<AccountUser> getAccountUser() {
        return cachedUser;
    }

    public void setCachedUser(final LiveData<AccountUser> user) {
        cachedUser.setValue(user.getValue());
        setEasConnection();
    }

    public LiveData<EasMessage> getCachedMessage() {
        return cachedMessage;
    }

    public void setCachedMessage(final LiveData<EasMessage> data) {
        cachedMessage.setValue(data.getValue());
    }

    public LiveData<ComposeEnum> getCachedMessageType() {
        return cachedMessageType;
    }

    public void setCachedMessageType(final LiveData<ComposeEnum> data) {
        cachedMessageType.setValue(data.getValue());
    }

    public void setEasConnection() {
        easConnection = new EasConnection();
        easConnection.setAccountUser(getAccountUser().getValue());
    }

    public EasConnection getEasConnection() {
        return easConnection;
    }
}
