package com.frestoinc.maildemo.view;

import com.frestoinc.maildemo.data.enums.NetworkState;

/**
 * Created by frestoinc on 23,January,2020 for MailDemo.
 */
public interface LoaderUI {

    void switchToNoNetwork();

    void switchToEmpty();

    void switchToLoading(String text);

    NetworkState getState();
}
