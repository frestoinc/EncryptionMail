package com.frestoinc.maildemo.ui.contact;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.frestoinc.maildemo.AuthResource;
import com.frestoinc.maildemo.SessionManager;
import com.frestoinc.maildemo.activesync.EasConnection;
import com.frestoinc.maildemo.base.BaseViewModel;
import com.frestoinc.maildemo.base.rx.SchedulerProvider;
import com.frestoinc.maildemo.data.local.DataManager;
import com.frestoinc.maildemo.data.model.GalContact;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;


/**
 * Created by frestoinc on 08,January,2020 for MailDemo.
 */
public class AddressBookViewModel extends BaseViewModel {

    @Inject
    SessionManager sessionManager;
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<AuthResource> syncResource = new MutableLiveData<>();
    private MutableLiveData<GalContact> isInserted = new MutableLiveData<>();
    private MutableLiveData<List<GalContact>> contacts = new MutableLiveData<>();

    @Inject
    public AddressBookViewModel(SchedulerProvider schedulerProvider, DataManager manager) {
        super(schedulerProvider, manager);
        getContactFromDb();
    }

    @Override
    public void setError(Throwable e) {
        syncResource.setValue(AuthResource.error(e.getMessage()));
        e.printStackTrace();
        error.setValue(e.getMessage());
    }

    public LiveData<List<GalContact>> getContacts() {
        return contacts;
    }

    public LiveData<AuthResource> getSyncResource() {
        return syncResource;
    }

    LiveData<String> getErrorResult() {
        return error;
    }

    private SessionManager getSessionManager() {
        return sessionManager;
    }

    LiveData<GalContact> getIsInserted() {
        return isInserted;
    }

    void getContactsFromAS(String query) {
        syncResource.setValue(AuthResource.loading("Retrieving GAL from server..."));
        Single.fromCallable(
                () -> getAddress(query))
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<List<GalContact>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(List<GalContact> galContacts) {
                        syncResource.setValue(AuthResource.authenticated(null));
                        List<GalContact> list = getContacts().getValue();
                        if (list != null && !list.isEmpty()) {
                            galContacts.addAll(list);
                            galContacts = galContacts.stream().distinct().collect(Collectors.toList());
                        }
                        contacts.setValue(galContacts);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    void getContactFromDb() {
        syncResource.setValue(AuthResource.loading("Loading GAL contacts..."));
        getDataManager().getAllContacts()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new SingleObserver<List<GalContact>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onSuccess(List<GalContact> galContacts) {
                        syncResource.setValue(AuthResource.authenticated(null));
                        contacts.setValue(galContacts);
                    }

                    @Override
                    public void onError(Throwable e) {
                        setError(e);
                    }
                });
    }

    private List<GalContact> getAddress(String query) throws Exception {
        EasConnection conn = getSessionManager().getEasConnection();
        long policyKey = conn.getPolicyKey();
        return conn.getGalCommand(policyKey, query);
    }

    void insertContacts(GalContact contact) {
        syncResource.setValue(AuthResource.loading("Updating GAL contacts..."));
        getDataManager().insertContact(contact)
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
                        isInserted.setValue(contact);
                    }

                    @Override
                    public void onError(Throwable e) {
                        isInserted.setValue(null);
                        setError(e);
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (getCompositeDisposable() != null) {
            getCompositeDisposable().clear();
        }
    }
}
