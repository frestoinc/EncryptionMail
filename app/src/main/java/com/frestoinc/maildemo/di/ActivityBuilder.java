package com.frestoinc.maildemo.di;

import com.frestoinc.maildemo.di.module.activity.ActivityModule;
import com.frestoinc.maildemo.di.module.activity.ViewModelModule;
import com.frestoinc.maildemo.di.module.fragment.AccountFragmentModule;
import com.frestoinc.maildemo.di.module.fragment.MainFragmentModule;
import com.frestoinc.maildemo.di.module.fragment.SettingFragmentModule;
import com.frestoinc.maildemo.di.scope.AccountActivityScope;
import com.frestoinc.maildemo.di.scope.MainActivityScope;
import com.frestoinc.maildemo.ui.account.AccountActivity;
import com.frestoinc.maildemo.ui.compose.ComposeActivity;
import com.frestoinc.maildemo.ui.contact.AddressBookActivity;
import com.frestoinc.maildemo.ui.main.MainActivity;
import com.frestoinc.maildemo.ui.setting.SettingsActivity;
import com.frestoinc.maildemo.ui.viewer.MessageViewActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * HOUSE ALL ACTIVITIES AND FRAGMENT MODULES
 * Created by frestoinc on 06,December,2019 for MailDemo.
 */
@Module
public abstract class ActivityBuilder {

    @AccountActivityScope
    @ContributesAndroidInjector(modules = {
            ViewModelModule.class, AccountFragmentModule.class})
    abstract AccountActivity contributeAccountActivity();

    @MainActivityScope
    @ContributesAndroidInjector(modules = {
            ViewModelModule.class, ActivityModule.class, MainFragmentModule.class})
    abstract MainActivity contributeMainActivity();

    @MainActivityScope
    @ContributesAndroidInjector(modules = {
            ViewModelModule.class})
    abstract MessageViewActivity contributeMessageViewActivity();

    @MainActivityScope
    @ContributesAndroidInjector(modules = {
            ViewModelModule.class, ActivityModule.class})
    abstract ComposeActivity contributeComposeActivity();

    @MainActivityScope
    @ContributesAndroidInjector(modules = {
            ViewModelModule.class, ActivityModule.class})
    abstract AddressBookActivity contributeAddressBookActivity();

    @MainActivityScope
    @ContributesAndroidInjector(modules = {
            SettingFragmentModule.class
    })
    abstract SettingsActivity contributeSettingsActivity();
}
