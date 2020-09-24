package com.frestoinc.maildemo.api.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.frestoinc.maildemo.data.model.EasMessage;
import com.frestoinc.maildemo.data.model.GalContact;
import com.frestoinc.maildemo.ui.viewer.MessageViewActivity;
import com.frestoinc.maildemo.utility.Constants;
import com.google.gson.Gson;

import javax.inject.Inject;

/**
 * Created by frestoinc on 20,December,2019 for MailDemo.
 */
public class MainRowListener implements RowListener {

    @Inject
    public Gson gson;

    @Inject
    public MainRowListener() {

    }

    @Override
    public void onMessageRowItemClicked(Context ctx, EasMessage message) {
        Intent intent = new Intent(ctx, MessageViewActivity.class);
        intent.putExtra(Constants.EAS_MESSAGE_VIEWER, gson.toJson(message));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

    @Override
    public void onAddressBookItemClicked(Activity activity, GalContact g) {
        Intent intent = new Intent();
        intent.putExtra(Constants.ADDRESS_BOOK_LIST, gson.toJson(g));
        activity.setResult(Activity.RESULT_OK, intent);
        activity.finish();
    }
}
