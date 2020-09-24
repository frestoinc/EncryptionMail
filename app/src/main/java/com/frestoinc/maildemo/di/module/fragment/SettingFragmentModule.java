package com.frestoinc.maildemo.di.module.fragment;

import com.frestoinc.maildemo.ui.setting.SettingsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by frestoinc on 10,January,2020 for MailDemo.
 */
@Module
public abstract class SettingFragmentModule {

    @ContributesAndroidInjector
    abstract SettingsFragment contributeSettingsFragment();
}
