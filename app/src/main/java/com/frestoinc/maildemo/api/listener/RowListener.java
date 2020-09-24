package com.frestoinc.maildemo.api.listener;

import android.app.Activity;
import android.content.Context;

import com.frestoinc.maildemo.data.model.EasMessage;
import com.frestoinc.maildemo.data.model.GalContact;

/**
 * Created by frestoinc on 20,December,2019 for MailDemo.
 */
public interface RowListener {

    void onMessageRowItemClicked(Context ctx, EasMessage message);

    void onAddressBookItemClicked(Activity activity, GalContact contact);
}
