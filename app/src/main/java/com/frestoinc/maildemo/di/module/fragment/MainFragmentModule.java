package com.frestoinc.maildemo.di.module.fragment;

import com.frestoinc.maildemo.ui.main.container.FolderFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by frestoinc on 12,December,2019 for MailDemo.
 */
@Module
public abstract class MainFragmentModule {

    @ContributesAndroidInjector
    abstract FolderFragment contributeFolderFragment();
}
