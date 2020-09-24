package com.frestoinc.maildemo.api;

/**
 * Created by frestoinc on 24,January,2020 for MailDemo.
 */
public interface NetworkReceiver {

    void onNetworkStateChanged(boolean connected);
}
