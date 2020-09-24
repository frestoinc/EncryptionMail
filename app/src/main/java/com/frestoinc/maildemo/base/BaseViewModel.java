package com.frestoinc.maildemo.base;

import androidx.lifecycle.ViewModel;

import com.frestoinc.maildemo.base.rx.SchedulerProvider;
import com.frestoinc.maildemo.data.local.DataManager;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;


/**
 * Created by frestoinc on 06,December,2019 for MailDemo.
 */
public abstract class BaseViewModel<N> extends ViewModel {

    private final SchedulerProvider schedulerProvider;
    private final DataManager dataManager;
    private CompositeDisposable compositeDisposable;
    private WeakReference<N> navigator;

    public BaseViewModel(SchedulerProvider schedulerProvider, DataManager manager) {
        this.schedulerProvider = schedulerProvider;
        this.dataManager = manager;
        this.compositeDisposable = new CompositeDisposable();
    }

    public abstract void setError(Throwable e);

    public CompositeDisposable getCompositeDisposable() {
        return compositeDisposable;
    }

    public SchedulerProvider getSchedulerProvider() {
        return schedulerProvider;
    }

    public N getNavigator() {
        return navigator.get();
    }

    public void setNavigator(N navigator) {
        this.navigator = new WeakReference<>(navigator);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    @Override
    protected void onCleared() {
        getCompositeDisposable().dispose();
        super.onCleared();
    }
}
