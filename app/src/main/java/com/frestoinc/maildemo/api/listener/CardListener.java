package com.frestoinc.maildemo.api.listener;

import com.frestoinc.maildemo.ui.sharedviewmodel.MessageViewModel;

/**
 * Created by frestoinc on 17,January,2020 for MailDemo.
 */
public interface CardListener {

    void onCardDetected(MessageViewModel viewModel, Object provider);

    void onError(MessageViewModel viewModel, Throwable e);
}
