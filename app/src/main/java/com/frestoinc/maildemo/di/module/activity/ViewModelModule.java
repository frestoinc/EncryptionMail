package com.frestoinc.maildemo.di.module.activity;

import androidx.lifecycle.ViewModel;

import com.frestoinc.maildemo.di.ViewModelKey;
import com.frestoinc.maildemo.ui.contact.AddressBookViewModel;
import com.frestoinc.maildemo.ui.sharedviewmodel.AccountViewModel;
import com.frestoinc.maildemo.ui.sharedviewmodel.MainViewModel;
import com.frestoinc.maildemo.ui.sharedviewmodel.MessageViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by frestoinc on 08,January,2020 for MailDemo.
 */
@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel.class)
    public abstract ViewModel bindAuthViewModel(AccountViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    public abstract ViewModel bindMainViewModel(MainViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MessageViewModel.class)
    public abstract ViewModel bindMessageViewModel(MessageViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AddressBookViewModel.class)
    public abstract ViewModel bindAddressBookViewModel(AddressBookViewModel viewModel);
}
