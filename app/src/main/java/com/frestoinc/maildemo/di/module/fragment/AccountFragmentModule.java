package com.frestoinc.maildemo.di.module.fragment;

import com.frestoinc.maildemo.ui.account.fragment.AccountFragment;
import com.frestoinc.maildemo.ui.account.fragment.AuthFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by frestoinc on 13,December,2019 for MailDemo.
 */
@Module
public abstract class AccountFragmentModule {

    @ContributesAndroidInjector
    abstract AccountFragment contributeAccountFragment();

    @ContributesAndroidInjector
    abstract AuthFragment contributeAuthFragment();
}
