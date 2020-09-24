package com.frestoinc.maildemo.api;

import android.content.Context;

import com.frestoinc.maildemo.view.LoaderUI;

/**
 * Created by frestoinc on 24,January,2020 for MailDemo.
 */
public interface NetworkLoader {

    Context getContext();

    LoaderUI getNetworkFrameLayout();
}
