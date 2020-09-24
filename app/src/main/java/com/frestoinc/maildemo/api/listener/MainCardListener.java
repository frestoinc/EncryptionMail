package com.frestoinc.maildemo.api.listener;

import com.frestoinc.maildemo.ui.sharedviewmodel.MessageViewModel;

import javax.inject.Inject;

/**
 * Created by frestoinc on 17,January,2020 for MailDemo.
 */
public class MainCardListener implements CardListener {

    @Inject
    public MainCardListener() {

    }

    @Override
    public void onCardDetected(MessageViewModel viewModel, Object provider) {
        viewModel.setKeyStore(provider);
    }

    @Override
    public void onError(MessageViewModel viewModel, Throwable e) {
        viewModel.setError(e);
    }


}
