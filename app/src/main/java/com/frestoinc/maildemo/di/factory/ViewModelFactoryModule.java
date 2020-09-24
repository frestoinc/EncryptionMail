package com.frestoinc.maildemo.di.factory;

import androidx.lifecycle.ViewModelProvider;

import com.frestoinc.maildemo.di.scope.AppScope;

import dagger.Binds;
import dagger.Module;

/**
 * Created by frestoinc on 07,December,2019 for MailDemo.
 */
@Module
public abstract class ViewModelFactoryModule {

    @Binds
    @AppScope
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory factory);
}
